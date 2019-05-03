package problems.gvrp.constructive_heuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problems.gvrp.GVRP;
import problems.gvrp.GVRP_Inverse;
import solutions.Solution;

public class MCWS {
	public Solution<List<Integer>> construct(GVRP_Inverse gvrp){
		List<List<Integer>> feasibleRoutes = new ArrayList<List<Integer>>(),
				infeasibleRoutes = new ArrayList<List<Integer>>();
//		build feasible and infeasible routes
		for (int i = 1; i <= gvrp.customersSize; i++) {
			List<Integer> route = new ArrayList<Integer> ();
			route.add(0);
			route.add(i);
			route.add(0);
			if (gvrp.getFuelConsumption(0, i, 0) <= gvrp.vehicleAutonomy &&
				gvrp.getTimeConsumption(0, i, 0) <= gvrp.vehicleOperationTime) 
				feasibleRoutes.add(route);			
			else
				infeasibleRoutes.add(route);
		}
//		calculate savings for insert an AFS for each infeasible routes
		for (int i = 0; i < infeasibleRoutes.size(); i++) {
			List<Integer> infeasibleRoute = infeasibleRoutes.get(i);
			Integer customer = infeasibleRoute.get(1); 
			Integer bestStationBefore = null, 
					bestStationAfter = null;
			Double bestStationSaving = Double.MAX_VALUE;
			for (int f = gvrp.customersSize + 1; f < gvrp.size; f++) {
//				check insert before
				if (gvrp.getFuelConsumption(0, f, customer, 0) <= gvrp.vehicleAutonomy &&
					gvrp.getTimeConsumption(0, f, customer, 0) <= gvrp.vehicleOperationTime) {
					Double saving = gvrp.getDistance(0, f, customer) - gvrp.getDistance(0, customer);
					if (saving < bestStationSaving) {
						bestStationSaving = saving;
						bestStationBefore = f;
					}
				}
//				check insert after
				if (gvrp.getFuelConsumption(0, customer, f, 0) <= gvrp.vehicleAutonomy &&
					gvrp.getTimeConsumption(0, customer, f, 0) <= gvrp.vehicleOperationTime) {
					Double saving = gvrp.getDistance(customer, f, 0) - gvrp.getDistance(customer, 0);
					if (saving < bestStationSaving) {
						bestStationSaving = saving;
						bestStationAfter = f;
						bestStationBefore = null;
					}
				}
//				check insert before and after at same time
				for (int _f = gvrp.customersSize + 1; _f < gvrp.size; _f++) {					
					if (gvrp.getFuelConsumption(0, f, customer, _f, 0) <= gvrp.vehicleAutonomy &&
						gvrp.getTimeConsumption(0, f, customer, _f, 0) <= gvrp.vehicleOperationTime) {
						Double saving = gvrp.getDistance(0, f, customer, _f, 0) - gvrp.getDistance(0, customer, 0);
						if (saving < bestStationSaving) {
							bestStationSaving = saving;
							bestStationBefore = f;
							bestStationAfter = _f;
						}
					}					
				}
			}
//			add AFS to infeasible route
			if (bestStationBefore != null) 
				infeasibleRoute.add(1, bestStationBefore);
			if (bestStationAfter != null) 
				infeasibleRoute.add(infeasibleRoute.size() - 1, bestStationAfter);
			feasibleRoutes.add(infeasibleRoute);
		}
//		calculate merge cost
		Map<Integer[], Double> savingsPairList = new HashMap<Integer[], Double>();
		for (int i = 0; i < feasibleRoutes.size(); i++) {
			List<Integer> routeI = feasibleRoutes.get(i);
			List<Integer> routeI_inverse = new ArrayList<Integer> (routeI);
			Double routeI_cost = gvrp.getDistance(routeI),
					routeI_inverse_cost = gvrp.getDistance(routeI_inverse);
			Collections.reverse(routeI_inverse);
			Integer adjacentI = routeI.get(1),
					_adjacentI = routeI.get(routeI.size() - 2);
			for (int j = i + 1; j < feasibleRoutes.size(); j++) {
				List<Integer> routeJ = feasibleRoutes.get(j);
				List<Integer> routeJ_inverse = new ArrayList<Integer> (routeJ);
				Double routeJ_cost = gvrp.getDistance(routeJ),
						routeJ_inverse_cost = gvrp.getDistance(routeJ_inverse);
				Collections.reverse(routeJ_inverse);
				Integer adjacentJ = routeJ.get(1),
						_adjacentJ = routeJ.get(routeJ.size() - 2);								
//				savingsPairList.put(new Integer[]{adjacentI, adjacentJ}, 
//						routeI_cost + routeJ_cost - (routeI_inverse_cost - gvrp.getDistance(adjacentI, 0) + 
//						gvrp.getDistance(adjacentI, adjacentJ) - gvrp.getDistance(0, adjacentJ) + routeJ_cost));
				savingsPairList.put(new Integer[]{adjacentI, adjacentJ}, 
						routeI_cost - routeI_inverse_cost + gvrp.getDistance(adjacentI, 0) -
						gvrp.getDistance(adjacentI, adjacentJ) + gvrp.getDistance(0, adjacentJ));
//				savingsPairList.put(new Integer[]{adjacentI, _adjacentJ}, 
//						routeI_cost + routeJ_cost - (routeI_inverse_cost - gvrp.getDistance(adjacentI, 0) +
//						gvrp.getDistance(adjacentI, _adjacentJ) - gvrp.getDistance(0, _adjacentJ) + routeJ_inverse_cost));
				savingsPairList.put(new Integer[]{adjacentI, _adjacentJ}, 
						routeI_cost + routeJ_cost - routeI_inverse_cost + gvrp.getDistance(adjacentI, 0) -
						gvrp.getDistance(adjacentI, _adjacentJ) + gvrp.getDistance(0, _adjacentJ) - routeJ_inverse_cost);
				savingsPairList.put(new Integer[]{_adjacentI, adjacentJ}, gvrp.getDistance(_adjacentI, 0) +
						gvrp.getDistance(0, adjacentJ) - gvrp.getDistance(_adjacentI, adjacentJ));
//				savingsPairList.put(new Integer[]{_adjacentI, _adjacentJ}, routeI_cost + routeJ_cost -
//						(routeI_cost - gvrp.getDistance(_adjacentI, 0) + gvrp.getDistance(_adjacentI, _adjacentJ) -
//						gvrp.getDistance(0, _adjacentJ) + routeJ_inverse_cost));
				savingsPairList.put(new Integer[]{_adjacentI, _adjacentJ}, routeJ_cost + gvrp.getDistance(_adjacentI, 0) - 
						gvrp.getDistance(_adjacentI, _adjacentJ) + gvrp.getDistance(0, _adjacentJ) - routeJ_inverse_cost);
			}
		}
//		sort savings
		
		return null;
	}
}
