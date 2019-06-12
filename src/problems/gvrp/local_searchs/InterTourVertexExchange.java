package problems.gvrp.local_searchs;

import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP;
import problems.gvrp.Route;
import problems.gvrp.Routes;

public class InterTourVertexExchange extends LocalSearch<GVRP, Routes>{

	@Override
	public Routes localOptimalSolution(GVRP eval, Routes solution) {
		Routes routes = (Routes) solution.clone();
		for (int i = 0; i < routes.size(); i++){
			for (int j = 0; j < routes.size(); j++){
				if (i != j){
					Route routeI = routes.get(i);
					Route routeJ = routes.get(j);
					for (int k = 1; k < routeI.size() - 1; k++){
						for (int l = 1; l < routeJ.size() - 1; l++){
							Route routeICopy = (Route) routeI.clone();
							Route routeJCopy = (Route) routeJ.clone();						
							routeJCopy.add(l, routeICopy.remove(k));
							routeICopy.add(k, routeJCopy.remove(l + 1));
							if (eval.getFuelConsumption(routeICopy) <= eval.vehicleAutonomy 
								&& eval.getFuelConsumption(routeJCopy) <= eval.vehicleAutonomy
								&& eval.getTimeConsumption(routeICopy) <= eval.vehicleOperationTime
								&& eval.getTimeConsumption(routeJCopy) <= eval.vehicleOperationTime
								&& eval.getDistance(routeJCopy) + eval.getDistance(routeICopy) 
								< eval.getDistance(routeJ) + eval.getDistance(routeI)){
								routes.set(i, routeICopy);
								routes.set(j, routeJCopy);
								routeI = routeICopy;
								routeJ = routeJCopy;
								k = 0;
								break;
							}
						}							
					}
				}
			}	
		}
		for (int i = 0; i < routes.size(); i++){
			if (routes.get(i).size() == 0){
				routes.remove(i);				
				i--;
			}
		}
		return routes;
	}

	@Override
	public Routes randomSolution(GVRP eval, Routes solution) {
		Routes routes = (Routes) solution.clone();
		// get routes
		int firstRouteIndex = eval.random.nextInt(solution.size());
		int secondRouteIndex = eval.random.nextInt(solution.size());
		while (secondRouteIndex == firstRouteIndex)
			secondRouteIndex = eval.random.nextInt(solution.size());
		Route routeI = routes.get(firstRouteIndex);
		Route routeJ = routes.get(secondRouteIndex);
		Route firstRoute = (Route) routeI.clone();
		Route secondRoute = (Route) routeJ.clone();
		// get nodes		
		int firstRouteNode = 1 + eval.random.nextInt(routeI.size() - 2);
		int secondRouteNode = 1 + eval.random.nextInt(routeJ.size() - 2);
		// exchange
		firstRoute.add(firstRouteNode, secondRoute.remove(secondRouteNode));
		secondRoute.add(secondRouteNode, firstRoute.remove(firstRouteNode + 1));
		if (eval.getFuelConsumption(firstRoute) <= eval.vehicleAutonomy 
			&& eval.getFuelConsumption(secondRoute) <= eval.vehicleAutonomy
			&& eval.getTimeConsumption(firstRoute) <= eval.vehicleOperationTime
			&& eval.getTimeConsumption(secondRoute) <= eval.vehicleOperationTime){
			routes.set(firstRouteIndex, firstRoute);
			routes.set(secondRouteIndex, secondRoute);
		}
		for (int i = 0; i < routes.size(); i++){
			if (routes.get(i).size() == 0){
				routes.remove(i);				
				i--;
			}
		}
		return routes;
	}

}
