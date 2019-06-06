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
	
	public static Map<Integer, Integer> calculateClosestsCustomer(GVRP gvrp, Set<Integer> usedCustomers) {				
		Map<Integer, Integer> closestCustomers = new HashMap<Integer, Integer>(gvrp.size);
		for (int i = 0; i < gvrp.size; i++) {
			if (usedCustomers.contains(i))
				continue;
//			find closest customer
			Integer closestCustomer = null;
			Double closestDistance = Double.MAX_VALUE;
			for (Integer customer : gvrp.customersDemands.keySet()) {
				if (usedCustomers.contains(customer))
					continue;
				Double distance = gvrp.getDistance(i, customer);
				if (!customer.equals(i) &&  distance < closestDistance) { 
					closestCustomer = customer;
					closestDistance = distance;
				}
			}
			closestCustomers.put(i, closestCustomer);
		}	
		return closestCustomers;
	}
	
	public static Map<Integer, Integer> calculateClosestsAFS(GVRP gvrp, Set<Integer> usedAFSs) {				
		Map<Integer, Integer> closestAFSs = new HashMap<Integer, Integer>(gvrp.size);
		for (int i = 0; i < gvrp.size; i++) {
//			find closest customer
			Integer closestCustomer = null;
			Double closestDistance = Double.MAX_VALUE;
			for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {
				if (usedAFSs.contains(afs))
					continue;
				Double distance = gvrp.getDistance(i, afs);
				if (!afs.equals(i) &&  distance < closestDistance) { 
					closestCustomer = afs;
					closestDistance = distance;
				}
			}
			closestAFSs.put(i, closestCustomer);
		}	
		return closestAFSs;
	}
	
	public static Solution<List<Integer>> construct(GVRP_Inverse gvrp){
//		nearest neighborhood
		int visitedCustomers = 0;		
//		calculating distances
		Set<Integer> usedCustomers = new HashSet<Integer> ();
		Map<Integer, Integer> closestCustomers = calculateClosestsCustomer(gvrp, usedCustomers);		
//		algorithm process
		Solution<List<Integer>> routes = new Solution<List<Integer>>();		
		Double availableCapacity = gvrp.vehicleCapacity;
		Double availableFuel= gvrp.vehicleAutonomy;
		Double availableTime= gvrp.vehicleOperationTime;
		Integer currentNode = 0;
		List<Integer> currentRoute = new ArrayList<Integer>();
		Set<Integer> usedRouteAFS = new HashSet<Integer> ();
		currentRoute.add(currentNode);
		while (visitedCustomers < gvrp.customersSize) {
			Double customerDemand = gvrp.customersDemands.get(currentNode) == null ? 0d : gvrp.customersDemands.get(currentNode); 
//			get closest			
			if (availableCapacity - customerDemand >= 0) {
				if (gvrp.getFuelConsumption(0, nextCustomer, 0) <= availableFuel &&
						gvrp.getTimeConsumption(0, nextCustomer, 0) <= availableTime) {
					availableFuel -= gvrp.getFuelConsumption(currentNode, nextCustomer);
					availableTime -= gvrp.getTimeConsumption(currentNode, nextCustomer);
					currentNode = nextCustomer;
					currentRoute.add(currentNode);
				}else {
//					put the closest afs here					
//					usedRouteAFS
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
