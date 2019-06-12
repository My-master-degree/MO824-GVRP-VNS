package problems.gvrp.analyzer;

import java.util.HashMap;
import java.util.Map;

import problems.gvrp.GVRP;
import problems.gvrp.Route;
import problems.gvrp.Routes;

public class Analyzer {
	public static String analyze (Routes sol, GVRP gvrp) {
		String str = "";
		Map<Integer, Integer> visitedCustomers = new HashMap<Integer, Integer> ();
		for (int i = 0; i < sol.size(); i++) {
			Route route = sol.get(i);
			if (route.get(0) != 0) { 
				System.out.println("Route "+i+" do not starts at depot");
				str += "Route "+i+" do not starts at depot\n";
			}
			if (route.get(route.size() - 1) != 0) {
				System.out.println("Route "+i+" do not ends at depot");
				str += "Route "+i+" do not ends at depot\n";
			}
			if (gvrp.getTimeConsumption(route) > gvrp.vehicleOperationTime) {
				System.out.println("Route "+i+" has time out of max operation time");
				str += "Route "+i+" has time out of max operation time\n";
			}
			if (gvrp.getFuelConsumption(route) > gvrp.vehicleAutonomy) {
				System.out.println("Route "+i+" has fuel consumption out of the vehicle autonomy");
				str += "Route "+i+" has fuel consumption out of the vehicle autonomy\n";
			}
			if (gvrp.getCapacityConsumption(route) > gvrp.vehicleCapacity) {
//				System.out.println("Route "+i+" has comsumed capacity out of the vehicle capacity");
//				str += "Route "+i+" has comsumed capacity out of the vehicle capacity\n";
			}
			for (int j = 0; j < route.size() - 1; j++) {
				Integer b = route.get(j), e = route.get(j + 1);
				visitedCustomers.put(b, visitedCustomers.get(b) == null ? 0 : visitedCustomers.get(b) + 1);
				visitedCustomers.put(e, visitedCustomers.get(e) == null ? 0 : visitedCustomers.get(b) + 1);
//				System.out.print(b + ",");
//				System.out.print(b + "-"+ e+ ":("+gvrp.distanceMatrix[b][e]/gvrp.vehicleConsumptionRate+")\t");
				if (gvrp.getFuelConsumption(b, e) > gvrp.vehicleAutonomy) { 
					System.out.println("Edge ("+ b + ", "+ e +") of route "+i+" is bigger than vehicle autonomy");
					str += "Edge ("+ b + ", "+ e +") of route "+i+" is bigger than vehicle autonomy\n";
				}
			}
//			System.out.println();
//			System.out.println(gvrp.vehicleAutonomy);
//			System.out.println(route.get(route.size() - 1));
		}
		for (Integer customer: gvrp.customersDemands.keySet()) {
			if (visitedCustomers.get(customer) == null) {
				System.out.println("Cusotmer "+customer+" no visited");
				str += "Cusotmer "+customer+" no visited\n";
			}else if (visitedCustomers.get(customer) > 1) {
				System.out.println("Cusotmer "+customer+" repeated");
				str += "Cusotmer "+customer+" repeated\n";
			}
		}
		return str;
	}
}
