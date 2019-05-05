package problems.gvrp.constructive_heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problems.gvrp.GVRP_Inverse;
import solutions.Solution;

public class NearestNeighborhood {
	public Solution<List<Integer>> construct(GVRP_Inverse gvrp){
//		nearest neighborhood
//		calculating distances
		Map<Integer, Integer[]> closestCustomersAndStations = new HashMap<Integer, Integer[]>(gvrp.customersSize);
		for (int i = 0; i < gvrp.size; i++) {
//			find closest customer
			Integer closestCustomer = null;
			Double closestDistance = Double.MAX_VALUE;
			for (int j = 1; j <= gvrp.customersSize; j++) {
				Double distance = gvrp.getDistance(i, j);
				if (i != j &&  distance < closestDistance) { 
					closestCustomer = j;
					closestDistance = distance;
				}
			}
//			find closest recharge station
			Integer closestRechargeStation = null;
			closestDistance = Double.MAX_VALUE;
			for (int f = gvrp.customersSize + 1; f < gvrp.size; f++) {
				Double distance = gvrp.getDistance(i, f);  
				if (i != f && distance < closestDistance) { 
					closestRechargeStation = f;
					closestDistance = distance;
				}
			}
			closestCustomersAndStations.put(i, new Integer[] {closestCustomer, closestRechargeStation});
		}		
//		algorithm process
		Solution<List<Integer>> routes = new Solution<List<Integer>>();		
		Double availableCapacity = gvrp.vehicleCapacity;
		Double availableFuel= gvrp.vehicleAutonomy;
		Integer currentNode = 0;
		List<Integer> currentRoute = new ArrayList<Integer>();
		currentRoute.add(currentNode);
		Integer insertedCustomers = 0;
		while (insertedCustomers < gvrp.customersSize) {			
//			get closest
			Integer nextCustomer = closestCustomersAndStations.get(currentNode)[0];
			Integer nextRechargeStation = closestCustomersAndStations.get(currentNode)[1];			
			if (availableCapacity - gvrp.customersDemands.get(nextCustomer) >= 0) {				
				Double a = gvrp.getDistance(currentNode, nextCustomer, 0),
					b = gvrp.getDistance(currentNode, nextCustomer, nextRechargeStation, 0);
				Double best = Math.min(a, b);
				if (best <= availableFuel) {
					if (best == a && gvrp.getTimeConsumption(currentNode, nextCustomer, 0) <= gvrp.vehicleOperationTime) {
						currentRoute.add(nextCustomer);
						currentNode = nextCustomer;
						closestCustomersAndStations.remove(nextCustomer);
					}else if (gvrp.getTimeConsumption(currentNode, nextCustomer, nextRechargeStation, 0) <= gvrp.vehicleOperationTime){
						currentRoute.add(nextCustomer);
						currentRoute.add(nextRechargeStation);
						currentNode = nextRechargeStation;
					}
					continue;
				}								
			}
			currentRoute.add(0);
			routes.add(currentRoute);
			currentRoute = new ArrayList<Integer>();
			currentNode = 0;
			currentRoute.add(currentNode);			
		}
		
		return routes;
	}
}
