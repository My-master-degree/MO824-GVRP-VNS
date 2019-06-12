package problems.gvrp.local_searchs;

import java.util.ArrayList;
import java.util.List;

import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP;
import problems.gvrp.Route;
import problems.gvrp.Routes;

public class WithinTourTwoVertexInterchange extends LocalSearch<GVRP, Routes>{

	@Override
	public Routes localOptimalSolution(GVRP eval, Routes solution) {
		Routes routes = (Routes) solution.clone();
		for (int i = 0; i < routes.size(); i++){
			Route route = routes.get(i);
			for (int k = 1; k < route.size() - 1; k++){
				for (int l = 1; l < route.size() - 1; l++){
					if (k != l){
						Route routeCopy = (Route) route.clone();
//						first
						Integer possibleAFS = routeCopy.get(k);
						if (eval.rechargeStationsRefuelingTime.get(possibleAFS) != null){
							routeCopy.remove(k);
							if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy
								&& eval.getDistance(routeCopy) < eval.getDistance(route)) {
								routes.set(i, routeCopy);
								route = routeCopy;
								k = 0;
								break;
							} else {
								routeCopy.add(k, possibleAFS);
							}
						}
//						second
						possibleAFS = routeCopy.get(l);
						if (eval.rechargeStationsRefuelingTime.get(possibleAFS) != null){
							routeCopy.remove(l);
							if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy
								&& eval.getDistance(routeCopy) < eval.getDistance(route)) {
								routes.set(i, routeCopy);
								route = routeCopy;
								k = 0;
								break;
							} else {
								routeCopy.add(l, possibleAFS);
							}
						}
//						third
						routeCopy.add(k, routeCopy.remove(l));						
						if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy 
							&& eval.getTimeConsumption(routeCopy) <= eval.vehicleOperationTime
							&& eval.getDistance(routeCopy) < eval.getDistance(route)){
							routes.set(i, routeCopy);
							route = routeCopy;
							k = 0;
							break;
						}						
					}
				}							
			}
		}		
		return routes;
	}

	@Override
	public Routes randomSolution(GVRP eval, Routes solution) {
		List<Integer> routesBiggerThanThree = new ArrayList<Integer>(); 
		for (int i = 0; i < solution.size(); i++){
			if (solution.get(i).size() > 3){
				routesBiggerThanThree.add(i);
			}
		}
		if (routesBiggerThanThree.size() == 0) {
			return solution;
		}
		Routes routes = (Routes) solution.clone();
		// get routes
		int routeIndex = eval.random.nextInt(routesBiggerThanThree.size());
		Route route = routes.get(routesBiggerThanThree.get(routeIndex));
		Route routeCopy = (Route) route.clone();		
		// get nodes
		int firstNode = 1 + eval.random.nextInt(route.size() - 1);
		int secondNode = 1 + eval.random.nextInt(route.size() - 1);
		while (firstNode == secondNode) {
			secondNode = 1 + eval.random.nextInt(route.size() - 1);
		}
		// exchange
		routeCopy.set(firstNode, routeCopy.set(secondNode, routeCopy.get(firstNode)));
		if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy 
			&& eval.getTimeConsumption(routeCopy) <= eval.vehicleOperationTime){
			routes.set(routeIndex, routeCopy);
		}
		return routes;
	}

}
