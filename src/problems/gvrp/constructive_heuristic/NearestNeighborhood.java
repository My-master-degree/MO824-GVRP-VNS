package problems.gvrp.constructive_heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.synth.SynthSeparatorUI;

import problems.gvrp.GVRP;
import problems.gvrp.GVRP_Inverse;
import solutions.Solution;

public class NearestNeighborhood {
	
	public static class CustomerPair{
		Integer customerI;
		Integer customerII;
		
		public CustomerPair(Integer customerI, Integer customerII){
			this.customerI = customerI;
			this.customerII = customerII;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((customerI == null) ? 0 : customerI.hashCode());
			result = prime * result + ((customerII == null) ? 0 : customerII.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CustomerPair other = (CustomerPair) obj;
			if (customerI == null) {
				if (other.customerI != null)
					return false;
			} else if (!customerI.equals(other.customerI))
				return false;
			if (customerII == null) {
				if (other.customerII != null)
					return false;
			} else if (!customerII.equals(other.customerII))
				return false;
			return true;
		}
		
	} 
	
	public static Map<Integer, Integer> calculateClosestDistances(GVRP gvrp, Set<Integer> usedCustomers) {		
		
		Map<Integer, Integer> closestCustomers = new HashMap<Integer, Integer>(gvrp.customersSize);
		for (int i = 0; i < gvrp.size; i++) {
			if (usedCustomers.contains(i))
				continue;
//			find closest customer
			Integer closestCustomer = null;
			Double closestDistance = Double.MAX_VALUE;
			for (int j = 1; j <= gvrp.customersSize; j++) {
				if (usedCustomers.contains(j))
					continue;
				Double distance = gvrp.getDistance(i, j);
				if (i != j &&  distance < closestDistance) { 
					closestCustomer = j;
					closestDistance = distance;
				}
			}
			closestCustomers.put(i, closestCustomer);
		}	
		return closestCustomers;
	}
	
	public static Solution<List<Integer>> construct(GVRP_Inverse gvrp){
//		System.out.println("Building solution");
//		nearest neighborhood
//		calculating distances
		Set<Integer> usedCustomers = new HashSet<Integer> ();
		Map<Integer, Integer> closestCustomers = calculateClosestDistances(gvrp, usedCustomers);
		Map<CustomerPair, Integer> closestAFSbetweenCustomers = new HashMap<CustomerPair, Integer> (gvrp.customersSize * gvrp.customersSize);
		for (int i = 0; i <= gvrp.customersSize; i++) {
			for (int j = 0; j <= gvrp.customersSize; j++) {
				if (i != j) {
					Integer closestAFS = null;
					Double closestAFSdistance = Double.MAX_VALUE;
					for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {
						Double distance = gvrp.getDistance(i, afs, j);
						if (distance < closestAFSdistance) {
							distance = closestAFSdistance;
							closestAFS = afs;
						}
					}
					closestAFSbetweenCustomers.put(new CustomerPair(i, j),  closestAFS);					
				}
			}
		}
//		System.out.println("Distances calculated");
//		algorithm process
		Solution<List<Integer>> routes = new Solution<List<Integer>>();		
		Double availableCapacity = gvrp.vehicleCapacity;
		Double availableFuel= gvrp.vehicleAutonomy;
		Double availableTime= gvrp.vehicleOperationTime;
		Integer currentNode = 0;
		List<Integer> currentRoute = new ArrayList<Integer>();
		currentRoute.add(currentNode);
		Integer insertedCustomers = 0;
		while (insertedCustomers <= gvrp.customersSize) {			
//			get closest
//			System.out.println("Current node "+currentNode);
			
			Integer nextCustomer = closestCustomers.get(currentNode);
			Integer nextRechargeStation = closestAFSbetweenCustomers.get(new CustomerPair(currentNode, nextCustomer));
			Double nextCustomerDemand = gvrp.customersDemands.get(nextCustomer);
			if (availableCapacity - nextCustomerDemand >= 0) {
//				System.out.println("Remaining capacity enough");
				Integer closestAFSBetweenNextCustomerAndDepot = closestAFSbetweenCustomers.get(new CustomerPair(currentNode, nextCustomer));
//				System.out.println(currentNode + " " + nextCustomer);
//				System.out.println(closestAFSBetweenNextCustomerAndDepot);
				Double a = gvrp.getDistance(currentNode, nextCustomer, 0),
					b = gvrp.getDistance(currentNode, nextCustomer, closestAFSBetweenNextCustomerAndDepot, 0),
					c = gvrp.getDistance(currentNode, nextRechargeStation, nextCustomer, 0),
					d = gvrp.getDistance(currentNode, nextRechargeStation, nextCustomer, closestAFSBetweenNextCustomerAndDepot, 0);
				Double best = Math.min(Math.min(a, b), Math.min(c, d));	
				Double customerDemand = gvrp.customersDemands.get(nextCustomer);
//				option A
				if (best.equals(a) && gvrp.getFuelConsumption(currentNode, nextCustomer, 0) <= availableFuel && 
					gvrp.getTimeConsumption(currentNode, nextCustomer, 0) <= availableTime) {										
//					System.out.println("Option A feasible");
					currentRoute.add(nextCustomer);
					if (1 <= currentNode && currentNode <= gvrp.customersSize)
						usedCustomers.add(currentNode);
					availableFuel -= gvrp.getFuelConsumption(currentNode, nextCustomer);
					availableCapacity -= customerDemand;
					availableTime -= gvrp.getTimeConsumption(currentNode, nextCustomer);
					currentNode = nextCustomer;									
					closestCustomers = calculateClosestDistances(gvrp, usedCustomers);
					insertedCustomers++;
					continue;
				}
//				option B
				best = Math.min(b, Math.min(c, d));
				if (best.equals(b) && gvrp.getFuelConsumption(currentNode, nextCustomer, closestAFSBetweenNextCustomerAndDepot, 0) <= availableFuel && 
					gvrp.getTimeConsumption(currentNode, nextCustomer, closestAFSBetweenNextCustomerAndDepot, 0) <= availableTime){
//					System.out.println("Option B feasible");
					currentRoute.add(nextCustomer);						
					if (1 <= currentNode && currentNode <= gvrp.customersSize)
						usedCustomers.add(currentNode);
					availableFuel -= gvrp.getFuelConsumption(currentNode, nextCustomer);
					availableCapacity -= customerDemand;
					availableTime -= gvrp.getTimeConsumption(currentNode, nextCustomer);
					currentNode = nextCustomer;								
					closestCustomers = calculateClosestDistances(gvrp, usedCustomers);
					insertedCustomers++;
					continue;
				}	
//				option C
				best = Math.min(c, d);
				if (best.equals(c) && gvrp.getFuelConsumption(currentNode, nextRechargeStation, nextCustomer, 0) <= availableFuel && 
					gvrp.getTimeConsumption(currentNode, nextRechargeStation, nextCustomer, 0) <= availableTime){
//					System.out.println("Option C feasible");
					currentRoute.add(nextRechargeStation);						
					currentRoute.add(nextCustomer);			
					if (1 <= currentNode && currentNode <= gvrp.customersSize)
						usedCustomers.add(currentNode);
					availableFuel -= gvrp.getFuelConsumption(currentNode, nextRechargeStation, nextCustomer);
					availableCapacity -= customerDemand;
					availableTime -= gvrp.getTimeConsumption(currentNode, nextRechargeStation, nextCustomer);
					currentNode = nextCustomer;		
					closestCustomers = calculateClosestDistances(gvrp, usedCustomers);
					insertedCustomers++;
					continue;
				}
//				option D
				if (gvrp.getFuelConsumption(currentNode, nextRechargeStation, nextCustomer, closestAFSBetweenNextCustomerAndDepot, 0) <= availableFuel && 
					gvrp.getTimeConsumption(currentNode, nextRechargeStation, nextCustomer, closestAFSBetweenNextCustomerAndDepot, 0) <= availableTime){
//					System.out.println("Option D feasible");
					currentRoute.add(nextRechargeStation);						
					currentRoute.add(nextCustomer);
					if (1 <= currentNode && currentNode <= gvrp.customersSize)
						usedCustomers.add(currentNode);
					availableFuel -= gvrp.getFuelConsumption(currentNode, nextRechargeStation, nextCustomer);
					availableCapacity -= customerDemand;
					availableTime -= gvrp.getTimeConsumption(currentNode, nextRechargeStation, nextCustomer);
					currentNode = nextCustomer;				
					closestCustomers = calculateClosestDistances(gvrp, usedCustomers);
					insertedCustomers++;
					continue;
				}								
			}
			availableFuel= gvrp.vehicleAutonomy;
			availableCapacity = gvrp.vehicleCapacity;
			availableTime = gvrp.vehicleOperationTime;
			currentRoute.add(0);
			routes.add(currentRoute);
			currentRoute = new ArrayList<Integer>();
			currentNode = 0;
			currentRoute.add(currentNode);					
		}
		currentRoute.add(0);
		routes.add(currentRoute);
		return routes;
	}
}
