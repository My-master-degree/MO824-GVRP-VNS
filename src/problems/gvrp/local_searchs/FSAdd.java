package problems.gvrp.local_searchs;


import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.Route;
import problems.gvrp.Routes;

public class FSAdd extends LocalSearch<GVRP_Inverse, Routes>{

	@Override
	public Routes localOptimalSolution(GVRP_Inverse eval, Routes solution) {
		return solution;
	}

	@Override
	public Routes randomSolution(GVRP_Inverse eval, Routes solution) {	
		Routes routes = (Routes) solution.clone();
		// get routes
		int routeIndex = eval.random.nextInt(solution.size());
		Route route = solution.get(routeIndex);		
		// get nodes
		int firstNode = 1 + eval.random.nextInt(route.size() - 1);
//		try to insert an afs
		for (Integer afs: eval.rechargeStationsRefuelingTime.keySet()) {
			Route routeCopy = (Route) route.clone();	
			routeCopy.add(firstNode, afs);
			if (eval.getFuelConsumption(routeCopy) <= eval.vehicleAutonomy 
				&& eval.getTimeConsumption(routeCopy) <= eval.vehicleOperationTime){
				routes.set(routeIndex, routeCopy);
				break;
			}
		}
		return routes;
	}

}
