package problems.gvrp.instances;

import gurobi.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstancesGenerator {
	public static class Node{
		public int x;
		public int y;
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
	
	public static Map<Node, Integer> generate(int customersNumber, int planeDimension, double vehicleAutonomy, double vehicleCapacity, double vehicleOperationTime) {
		Random random = new Random();
		int afsUsed = 0;
		Map<Node, Integer> nodes = new HashMap<Node, Integer>(customersNumber + 1);
//		depot		
		Node depot = new Node (random.nextInt(planeDimension), random.nextInt(planeDimension));		
		nodes.put(depot, 0);
		System.out.println("Depot generated at "+depot.x+" "+depot.y);		
		for (int i = 1; i <= customersNumber; i++) {
			System.out.println("Generating customer "+ i);
			Node customer;			
			int limit = (int) (vehicleAutonomy * 1.5);
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
		}
//		generate random points to be possibly AFS's
		int maxAFSsNumber = customersNumber * 50,
			limit = (int) Math.floor(vehicleAutonomy);
		Map<Node, Map<Node, Boolean>> possibleAFSs = new HashMap<Node, Map<Node, Boolean>> (maxAFSsNumber);
		for (int i = 0; i < maxAFSsNumber; i++) {
			Node possibleAFS;
			do {
				possibleAFS = new Node (
					depot.x + (random.nextInt(2) == 1 ? - random.nextInt(Math.min(limit, depot.x) + 1) : 
						random.nextInt(Math.min(planeDimension - depot.x, limit) + 1)), 
					depot.y + (random.nextInt(2) == 1 ? - random.nextInt(Math.min(limit, depot.y) + 1) :
						random.nextInt(Math.min(planeDimension - depot.y, limit) + 1)));		
				System.out.println("Trying");
			}while(possibleAFSs.get(possibleAFS) != null || nodes.get(possibleAFS) != null);
			System.out.println("Possible AFS at "+possibleAFS.x+" "+possibleAFS.y);
//			get customers in possible AFS cover
			Map<Node, Boolean> customersInAFSCover = new HashMap<Node, Boolean>();
			System.out.println("Its covering");
			for (Node node:nodes.keySet()) {				
				if (nodes.get(node) != 0 && getDistance(node, possibleAFS) <= (limit / 2)) { 
					customersInAFSCover.put(node, true);
					System.out.print(nodes.get(node) + " ");
				}				
			}
			System.out.println();
			possibleAFSs.put(possibleAFS, customersInAFSCover);
		}
//		check
		System.out.println("Check");
		for (Node customer:nodes.keySet()) {
			boolean find = false;
			if (nodes.get(customer) != 0) {
				for (Node possibleAFS:possibleAFSs.keySet()) {
					if (possibleAFSs.get(possibleAFS).get(customer) != null) {
						find = true;
						break;
					}
				}
				if (!find) {
					System.out.println("Customer "+ nodes.get(customer)+" not served");
				}
			}			
		}
//		run cover set
		System.out.println("SET COVER MIP");
		try {
	      GRBEnv    env   = new GRBEnv("setCover.log");
		  GRBModel  model = new GRBModel(env);		
		  // Create variables
		  GRBVar[] y = new GRBVar[possibleAFSs.size()];
		  int i = 0;
		  for (Node possibleAFS:possibleAFSs.keySet()) {
			  y[i] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x_"+i);
			  i++;
		  }
		  // Set objective: min \sum_{i \in AFSs candidates set} y_{i}
		  GRBLinExpr expr = new GRBLinExpr();
		  for (i = 0; i < y.length; i++) {
			  expr.addTerm(1.0, y[i]);
		  }		  
		  model.setObjective(expr, GRB.MINIMIZE);		
		  // Add constraint: \sum_{i \in covering of possible AFS j} >= 1 \forall_{j \in set of customers}
		  for (Node customer:nodes.keySet()) {
			  if (nodes.get(customer) != 0) {
				  expr = new GRBLinExpr();
				  i = 0;
				  for (Node possibleAFS:possibleAFSs.keySet()) {
					  if (possibleAFSs.get(possibleAFS).get(customer) != null) {
						  expr.addTerm(1.0, y[i]);
						  i++;
					  }
				  }				  
				  model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c_customer_"+nodes.get(customer));
			  }			  
		  }		  		
		  // Optimize model
		  model.optimize();
		  i = 0;
		  for (Node possibleAFS:possibleAFSs.keySet()) {
			  if (y[i].get(GRB.DoubleAttr.X) == 1.0) 
				  nodes.put(possibleAFS, nodes.size() + 1);			  
			  i++;
		  }			  		
		  System.out.println("Number of AFSs choosed: " + model.get(GRB.DoubleAttr.ObjVal));		
		  // Dispose of model and environment		
		  model.dispose();
		  env.dispose();		
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
		}		
//		return
		return nodes;
	}
}
