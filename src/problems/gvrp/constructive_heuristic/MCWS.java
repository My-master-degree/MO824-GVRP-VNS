package problems.gvrp.constructive_heuristic;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import problems.gvrp.GVRP;
import problems.gvrp.GVRP_Inverse;
import solutions.Solution;

public class MCWS {
	
	public static final Integer MERGE_FIRST_FIRST = 1;
	public static final Integer MERGE_FIRST_LAST = 2;
	public static final Integer MERGE_LAST_FIRST = 3;
	public static final Integer MERGE_LAST_LAST = 4;
	
	public static class Saving {
		Integer node1;
		Integer node2;
		List<Integer> route1;
		List<Integer> route2;
		Integer merge_type;
		Double saving;
		
		Saving (Integer node1, Integer node2, List<Integer> route1, List<Integer> route2, Integer merge_type, Double saving){
			this.node1 = node1;
			this.node2 = node2;
			this.route1 = route1;
			this.route2 = route2;
			this.saving = saving;
			this.merge_type = merge_type;
		}
	}
	
	public static Solution<List<Integer>> construct(GVRP_Inverse gvrp){
		Solution<List<Integer>> feasibleRoutes = new Solution<List<Integer>>(),
				infeasibleRoutes = new Solution<List<Integer>>();
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
//		compute savings
		List<Saving> savingsPairList = computeSavings(feasibleRoutes, gvrp);
//      merge routes
        while(savingsPairList.size() > 0) {
        	Saving saving = savingsPairList.remove(0);
        	List<Integer> newRoute = new ArrayList<Integer>(saving.route1.size() + saving.route2.size() - 2);
//        	perform merge
        	if (saving.merge_type == MCWS.MERGE_FIRST_FIRST) {
        		List<Integer> route1_inverse = new ArrayList<Integer> (saving.route1);
        		Collections.reverse(route1_inverse);
        		newRoute.addAll(route1_inverse.subList(0, route1_inverse.size() - 1));
        		newRoute.addAll(saving.route2.subList(1, saving.route2.size()));
        	}else if (saving.merge_type == MCWS.MERGE_FIRST_LAST) {
        		List<Integer> route1_inverse = new ArrayList<Integer> (saving.route1);
        		List<Integer> route2_inverse = new ArrayList<Integer> (saving.route2);
        		Collections.reverse(route1_inverse);
        		Collections.reverse(route2_inverse);
        		newRoute.addAll(route1_inverse.subList(0, route1_inverse.size() - 1));
        		newRoute.addAll(route2_inverse.subList(1, route2_inverse.size()));
        	}else if (saving.merge_type == MCWS.MERGE_LAST_FIRST) {
        		newRoute.addAll(saving.route1.subList(0, saving.route1.size() - 1));
        		newRoute.addAll(saving.route2.subList(1, saving.route2.size()));
        	}else if (saving.merge_type == MCWS.MERGE_LAST_LAST) {
        		List<Integer> route2_inverse = new ArrayList<Integer> (saving.route2);
        		Collections.reverse(route2_inverse);
        		newRoute.addAll(saving.route1.subList(0, saving.route1.size() - 1));
        		newRoute.addAll(route2_inverse.subList(1, route2_inverse.size()));
        	}
        	if (gvrp.getCapacityConsumption(newRoute) > gvrp.vehicleCapacity) {        		
        		continue;
        	}        		
//        	check feasibility
        	if (gvrp.getFuelConsumption(newRoute) > gvrp.vehicleAutonomy && 
        			gvrp.getTimeConsumption(newRoute) <= gvrp.vehicleOperationTime) {
//        		insert station
        		Double cost = Double.MAX_VALUE;
        		Integer bestFuelStation = gvrp.customersSize + 1;
        		for (int i = gvrp.customersSize + 1; i < gvrp.size; i++) {
					Double currentCost = gvrp.getDistance(saving.node1, i, saving.node2);
					if (currentCost < cost) {
						cost = currentCost;
						bestFuelStation = i;
					}
				}
        		newRoute.add(newRoute.indexOf(saving.node2), bestFuelStation);
        	} 
//        	solutions keeps infeasible
        	if (gvrp.getFuelConsumption(newRoute) > gvrp.vehicleAutonomy)
    			break;
        	if (gvrp.getTimeConsumption(newRoute) <= gvrp.vehicleOperationTime) {
//        		remove redundant stations
        		for (Integer vertex : newRoute) {
					if (gvrp.rechargeStationsRefuelingTime.get(vertex) != null) {
						List<Integer> newRouteTest = new ArrayList<Integer> (newRoute);
						newRouteTest.remove(vertex);
						if (gvrp.getFuelConsumption(newRouteTest) <= gvrp.vehicleAutonomy && 
								gvrp.getTimeConsumption(newRouteTest) <= gvrp.vehicleOperationTime) {
							newRoute = newRouteTest;
							break;
						}
					}
				}
        	}
//        	solutions keeps infeasible
        	if (gvrp.getTimeConsumption(newRoute) > gvrp.vehicleOperationTime)	
        		break;        	
//        	calculate again        	
        	feasibleRoutes.remove(saving.route1);
        	feasibleRoutes.remove(saving.route2);
        	feasibleRoutes.add(newRoute);
        	savingsPairList = computeSavings(feasibleRoutes, gvrp);
        }
        
		return feasibleRoutes;
	}
	
	public static List<Saving> computeSavings(List<List<Integer>> feasibleRoutes, GVRP_Inverse gvrp){
//		calculate merge cost
		List<Saving> savingsPairList = new ArrayList<Saving>(feasibleRoutes.size() * feasibleRoutes.size() * 4);
		for (int i = 0; i < feasibleRoutes.size(); i++) {
			List<Integer> routeI = feasibleRoutes.get(i);
			List<Integer> routeI_inverse = new ArrayList<Integer> (routeI);
			Collections.reverse(routeI_inverse);
			Double routeI_cost = gvrp.getDistance(routeI),
					routeI_inverse_cost = gvrp.getDistance(routeI_inverse);
			Integer adjacentI = routeI.get(1),
					_adjacentI = routeI.get(routeI.size() - 2);
			for (int j = i + 1; j < feasibleRoutes.size(); j++) {
				List<Integer> routeJ = feasibleRoutes.get(j);
				List<Integer> routeJ_inverse = new ArrayList<Integer> (routeJ);
				Collections.reverse(routeJ_inverse);
				Double routeJ_cost = gvrp.getDistance(routeJ),
						routeJ_inverse_cost = gvrp.getDistance(routeJ_inverse);
				Integer adjacentJ = routeJ.get(1),
						_adjacentJ = routeJ.get(routeJ.size() - 2);			
//				Merge type I
//				savingsPairList.put(new Integer[]{adjacentI, adjacentJ, routeI, routeJ, 1}, 
//						routeI_cost + routeJ_cost - (routeI_inverse_cost - gvrp.getDistance(adjacentI, 0) + 
//						gvrp.getDistance(adjacentI, adjacentJ) - gvrp.getDistance(0, adjacentJ) + routeJ_cost));								
				savingsPairList.add(new Saving (adjacentI, adjacentJ, routeI, routeJ, MCWS.MERGE_FIRST_FIRST, 
						routeI_cost - routeI_inverse_cost + gvrp.getDistance(adjacentI, 0) -
						gvrp.getDistance(adjacentI, adjacentJ) + gvrp.getDistance(0, adjacentJ)));
//				Merge type II
//				savingsPairList.add(new Saving (adjacentI, _adjacentJ, routeI, routeJ, MCWS.MERGE_FIRST_LAST, 
//						routeI_cost + routeJ_cost - (routeI_inverse_cost - gvrp.getDistance(adjacentI, 0) +
//						gvrp.getDistance(adjacentI, _adjacentJ) - gvrp.getDistance(0, _adjacentJ) + routeJ_inverse_cost)));
				savingsPairList.add(new Saving (adjacentI, _adjacentJ, routeI, routeJ, MCWS.MERGE_FIRST_LAST,
						routeI_cost + routeJ_cost - routeI_inverse_cost + gvrp.getDistance(adjacentI, 0) -
						gvrp.getDistance(adjacentI, _adjacentJ) + gvrp.getDistance(0, _adjacentJ) - routeJ_inverse_cost));
//				Merge type III
				savingsPairList.add(new Saving(_adjacentI, adjacentJ, routeI, routeJ, MCWS.MERGE_LAST_FIRST, 
						gvrp.getDistance(_adjacentI, 0) + gvrp.getDistance(0, adjacentJ) - 
						gvrp.getDistance(_adjacentI, adjacentJ)));
//				Merge type IV
//				savingsPairList.add(new Saving(_adjacentI, _adjacentJ, routeI, routeJ, MCWS.MERGE_LAST_LAST, routeI_cost + routeJ_cost -
//						(routeI_cost - gvrp.getDistance(_adjacentI, 0) + gvrp.getDistance(_adjacentI, _adjacentJ) -
//						gvrp.getDistance(0, _adjacentJ) + routeJ_inverse_cost));
				savingsPairList.add(new Saving(_adjacentI, _adjacentJ, routeI, routeJ, MCWS.MERGE_LAST_LAST, 
						routeJ_cost + gvrp.getDistance(_adjacentI, 0) - gvrp.getDistance(_adjacentI, _adjacentJ) + 
						gvrp.getDistance(0, _adjacentJ) - routeJ_inverse_cost));
			}
		}
//		sort savings
        Collections.sort(savingsPairList, new Comparator<Saving>() {
            public int compare(Saving o1, Saving o2) {
                return (o2.saving).compareTo(o1.saving);
            }
        });
        return savingsPairList;
	}
}
