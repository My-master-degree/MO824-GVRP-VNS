package problems.gvrp.local_searchs;

import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.Route;
import problems.gvrp.Routes;

public class FSDrop extends LocalSearch<GVRP_Inverse, Routes>{

	@Override
	public Routes localOptimalSolution(GVRP_Inverse eval, Routes solution) {
		Routes routes = (Routes) solution.clone();
		for (int i = 0; i < routes.size(); i++){
			Route route = routes.get(i);
			for (int k = 1; k < route.size() - 1; k++){
				if (eval.rechargeStationsRefuelingTime.get(route.get(k)) != null) {
					Route routeCopy = (Route) route.clone();
					routeCopy.remove(k);
					if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy
						&& eval.getTimeConsumption(routeCopy) <= eval.vehicleOperationTime
						&& eval.getDistance(routeCopy) < eval.getDistance(route)) {
						routes.set(i, routeCopy);						
						i = -1;
						break;
					}
				}
			}
		}		
		return routes;
	}

	@Override
	public Routes randomSolution(GVRP_Inverse eval, Routes solution) {	
		Routes routes = (Routes) solution.clone();
		// get routes
		int routeIndex = eval.random.nextInt(solution.size());
		Route route = solution.get(routeIndex);		
//		try to insert an afs
		for (int k = 1; k < route.size() - 1; k++){			
			if (eval.rechargeStationsRefuelingTime.get(route.get(k)) != null) {
				Route routeCopy = (Route) route.clone();
				routeCopy.remove(k);
				if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy
					&& eval.getTimeConsumption(routeCopy) <= eval.vehicleOperationTime) {
					routes.set(routeIndex, routeCopy);		
					break;
				}
			}
		}
		return routes;
	}

}
