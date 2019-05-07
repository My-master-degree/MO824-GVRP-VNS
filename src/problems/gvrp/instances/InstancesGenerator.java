package problems.gvrp.instances;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstancesGenerator {
	public static class Node{
		int x;
		int y;
		Node (int x, int y){
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
	
	private static double getDistance(Node a, Node b) {		
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	
	public static Map<Node, Integer> generate(int customersNumber, int rechargeStationsNumber, int planeDimension, double vehicleAutonomy, double vehicleCapacity, double vehicleOperationTime) {
		Random random = new Random();
		int afsUsed = 0;
		Map<Node, Integer> nodes = new HashMap<Node, Integer>(customersNumber + rechargeStationsNumber + 1);
//		depot		
		Node depot = new Node (random.nextInt(planeDimension), random.nextInt(planeDimension));		
		nodes.put(depot, 0);
		System.out.println("Depot generated at "+depot.x+" "+depot.y);		
		for (int i = 1; i <= customersNumber; i++) {
			System.out.println("Generating customer "+ i);
			Node customer;
			int limit = afsUsed == rechargeStationsNumber ? (int) Math.floor(vehicleAutonomy/2) : (int) (vehicleAutonomy * 1.5); 
			do {				
//				X
				int r = random.nextInt(2);
				int x = depot.x;
				if (r == 1) 
					x -= random.nextInt(Math.min(limit, depot.x) + 1);
				else
					x += random.nextInt(Math.min(planeDimension - depot.x, limit) + 1);
//				Y
				r = random.nextInt(2);
				int y = depot.y;
				if (r == 1) 
					y -= random.nextInt(Math.min(limit, depot.y) + 1);
				else
					y += random.nextInt(Math.min(planeDimension - depot.y, limit) + 1);
				customer = new Node (x, y);				
				System.out.println("\tTrying generate customer "+ i);
			}while(nodes.get(customer) != null);
			System.out.println("\tCustomer "+ i+" generated at "+customer.x+" "+customer.y);			
			nodes.put(customer, i);
//			put some AFS in the middle
			if (getDistance(depot, customer) > (vehicleAutonomy / 2)) {				
				Node afs;
				
				do {	
					int x, y;
					if (customer.x < depot.x) {
						x = (int) (Math.max(depot.x - vehicleAutonomy, 0) + random.nextInt(customer.x - depot.x + (int) (3*vehicleAutonomy/2)) + 1); 
					}else {
						x = (int) (Math.max(depot.x - vehicleAutonomy, 0) + random.nextInt(customer.x - depot.x + (int) (3*vehicleAutonomy/2)) + 1);
					}
					afs = new Node(Math.min(customer.x, depot.x) + random.nextInt(Math.abs(customer.x - depot.x) + 1), 
							Math.min(customer.y, depot.y) + random.nextInt(Math.abs(customer.y - depot.y) + 1));
//					afs = new Node(random.nextInt(planeDimension), random.nextInt(planeDimension));
					System.out.println("\tTrying to put some AFS in "+afs.x+", "+afs.y+" between depot and customer "+ i);
					System.out.println("\tDistance between depot and afs is "+getDistance(depot, afs));
					System.out.println("\tDistance between cusotmer and afs is "+getDistance(afs, customer));
				}while(nodes.get(afs) != null || getDistance(depot, afs) > vehicleAutonomy || getDistance(afs, customer) > vehicleAutonomy/2);
				afsUsed++;
				nodes.put(afs, customersNumber + 1 + afsUsed);				
				System.out.println("\tAFS inserted between depot and customer "+i+" at "+afs.x+" "+afs.y);				
			} 
		}
//		put remaining AFS's
		for (int i = customersNumber + 1 + afsUsed; i < customersNumber + 1 + rechargeStationsNumber; i++) {
			System.out.println("Generating AFS "+ i);
//			 get max and min X and Y			
			int max_X = 0,
					min_X = planeDimension,
					max_Y = 0,
					min_Y = planeDimension;
			for (Node node:nodes.keySet()) {
				if (node.x > max_X)
					max_X = node.x;
				if (node.x < min_X)
					min_X = node.x;
				if (node.y > max_Y)
					max_Y = node.y;
				if (node.y < min_Y)
					min_Y = node.y;
			}
			Node afs;
			int x_size = max_X - min_X,
				y_size = max_Y - min_Y;
			do {
				afs = new Node(min_X + random.nextInt(x_size + 1), min_Y + random.nextInt(y_size + 1));
				System.out.println("\tTrying to generate AFS "+ i);
			}while(nodes.get(afs) != null);
			nodes.put(afs, i);
		}
		return nodes;
	}
}
