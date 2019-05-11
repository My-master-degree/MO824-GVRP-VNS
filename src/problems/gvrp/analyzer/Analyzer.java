package problems.gvrp.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problems.gvrp.GVRP;
import solutions.Solution;

public class Analyzer {
	public static void analyze (Solution<List<Integer>> sol, GVRP gvrp) {
		Map<Integer, Boolean> visitedCustomers = new HashMap<Integer, Boolean> ();
		for (int i = 0; i < sol.size(); i++) {
			List<Integer> route = sol.get(i);
			if (route.get(0) != 0) 
				System.out.println("Route "+i+" do not starts at depot");
			if (route.get(route.size() - 1) != 0) 
				System.out.println("Route "+i+" do not ends at depot");
			if (gvrp.getTimeConsumption(route) > gvrp.vehicleOperationTime)
				System.out.println("Route "+i+" has time out of max operation time");
			if (gvrp.getFuelConsumption(route) > gvrp.vehicleAutonomy)
				System.out.println("Route "+i+" has fuel consumption out of the vehicle autonomy");
			if (gvrp.getCapacityConsumption(route) > gvrp.vehicleCapacity)
				System.out.println("Route "+i+" has comsumed capacity out of the vehicle capacity");	
			for (int j = 0; j < route.size() - 1; j++) {
				Integer b = route.get(j), e = route.get(j);
				visitedCustomers.put(b, true);
				visitedCustomers.put(e, true);
				System.out.print(b + ",");
				if (gvrp.getFuelConsumption(b, e) > gvrp.vehicleAutonomy) 
					System.out.println("Edge ("+ b + ", "+ e +") of route "+i+" is bigger than vehicle autonomy");				
			}
			System.out.println();
		}
		for (Integer customer: gvrp.customersDemands.keySet()) {
			if (visitedCustomers.get(customer) == null)
				System.out.println("Cusotmer "+customer+" no visited");			
		}
	}
}
