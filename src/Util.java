import java.util.List;

import problems.gvrp.GVRP;
import problems.gvrp.Route;
import problems.gvrp.Routes;
import solutions.Solution;

public class Util {
	public static void printGVRPSolution(Routes sol, GVRP gvrp) {
		double cost = 0d;
		for (int i = 0; i < sol.size(); i++) {
			Route route = sol.get(i);
			cost += gvrp.getDistance(route);
			if (route.size() > 0) {
				System.out.print("Route " + (i + 1) + ": " + route.get(0) + ",");
				for (int j = 1; j < route.size(); j++) {
					if (gvrp.rechargeStationsRefuelingTime.keySet().contains(route.get(j)))
						System.out.print("+" + route.get(j) + ",");
					else
						System.out.print(route.get(j) + ",");
				}
				System.out.println("\n\t(Distance:"+gvrp.getDistance(route)+
						"|FuelConsumption:"+gvrp.getFuelConsumption(route)+
						"|TimeConsumption:"+gvrp.getTimeConsumption(route)+
						"|CapacityConsumption:"+gvrp.getCapacityConsumption(route)+")");
			}
				
		}
		System.out.println("Distance: "+cost);
		
	}
	
	public static void printGVRPSolutionDistance(Routes sol, GVRP gvrp) {
		double cost = 0d;
		for (int i = 0; i < sol.size(); i++) {
			Route route = sol.get(i);
			cost += gvrp.getDistance(route);
		}
//		System.out.println("Distance: "+cost);
		System.out.println(cost);
		
	}
}
