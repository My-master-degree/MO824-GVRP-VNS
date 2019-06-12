package problems.gvrp.local_searchs;

import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.Route;
import problems.gvrp.Routes;

public class MergeRoutes extends LocalSearch<GVRP_Inverse, Routes>{

	@Override
	public Routes localOptimalSolution(GVRP_Inverse eval, Routes solution) {
		Routes routes = (Routes) solution.clone();
		for (int i = 0; i < routes.size(); i++){
			for (int j = 0; j < routes.size(); j++){
				if (i != j){
					Route routeI = routes.get(i);
					Route routeJ = routes.get(j);
					Integer bestIndex = null;
					Double bestReduction = eval.getDistance(routeI) + eval.getDistance(routeJ);
					for (int k = 1; k < routeI.size() - 1; k++){
						Route routeICopy = (Route) routeI.clone();
						routeICopy.addAll(k, routeJ.subList(1, routeJ.size() - 1));
						Double cost = eval.getDistance(routeICopy);
						if (eval.getFuelConsumption(routeICopy) <= eval.vehicleAutonomy 
							&& eval.getTimeConsumption(routeICopy) <= eval.vehicleOperationTime
							&& cost < bestReduction){
							bestIndex = k;
							bestReduction = cost; 
						}						
					}
					if (bestIndex != null){
						routes.remove(j);
						routeI.addAll(bestIndex, routeJ.subList(1, routeJ.size() - 1));
						i = -1;
						break;
					}
				}
			}	
		}
//		for (int i = 0; i < routes.size(); i++){
//			if (routes.get(i).size() == 0){
//				routes.remove(i);				
//				i--;
//			}
//		}
		return routes;
	}

	@Override
	public Routes randomSolution(GVRP_Inverse eval, Routes solution) {
		Routes routes = (Routes) solution.clone();
		if (routes.size() > 1) {
			// get routes
			int firstRouteIndex = eval.random.nextInt(solution.size());
			int secondRouteIndex = eval.random.nextInt(solution.size());
			while (secondRouteIndex == firstRouteIndex)
				secondRouteIndex = eval.random.nextInt(solution.size());
			Route routeI = routes.get(firstRouteIndex);
			Route routeJ = routes.get(secondRouteIndex);
			Integer index = null;
			for (int k = 1; k < routeI.size() - 1; k++){
				Route routeICopy = (Route) routeI.clone();
				routeICopy.addAll(k, routeJ.subList(1, routeJ.size() - 1));
				if (eval.getFuelConsumption(routeICopy) <= eval.vehicleAutonomy 
					&& eval.getTimeConsumption(routeICopy) <= eval.vehicleOperationTime){
					index = k;
				}						
			}
			if (index != null){
				routes.remove(secondRouteIndex);
				routeI.addAll(index, routeJ.subList(1, routeJ.size() - 1));
			}
		}
		return routes;
	}

}
