package problems.gvrp.instances;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import problems.gvrp.GVRP;
import solutions.Solution;

public class Util {
	
	public static class Edge {
		Integer node1;
		Integer node2;
		Double weight;
		
		Edge (Integer node1, Integer node2, Double weight){
			this.node1 = node1;
			this.node2 = node2;
			this.weight = weight;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((node1 == null) ? 0 : node1.hashCode());
			result = prime * result + ((node2 == null) ? 0 : node2.hashCode());
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
			Edge other = (Edge) obj;
			if (node1 == null) {
				if (other.node1 != null)
					return false;
			} else if (!node1.equals(other.node1))
				return false;
			if (node2 == null) {
				if (other.node2 != null)
					return false;
			} else if (!node2.equals(other.node2))
				return false;
			return true;
		}
				
	}
	
	public static boolean isAConnectedGraph(Set<NodeData> nodes) {
		Set<Integer> visitedNodes = new HashSet<Integer> (nodes.size());
		dfs(nodes.iterator().next(), visitedNodes);
		for (NodeData node : nodes) {
			if (!visitedNodes.contains(node.id)) {
				return false;
			}
		}
		return true;
//		return visitedNodes.size() == nodes.size();
	}
	
	public static boolean isAConnectedGraph(Collection<NodeData> nodes) {
		Set<Integer> visitedNodes = new HashSet<Integer> (nodes.size());
		dfs(nodes.iterator().next(), visitedNodes);
//		for (NodeData node : nodes) {
//			if (!visitedNodes.contains(node.id)) {
//				return false;
//			}
//		}
//		return true;
		return visitedNodes.size() == nodes.size();
	}
	
	public static void dfs(NodeData currentNode, Set<Integer> visitedNodes) {
		if (!visitedNodes.contains(currentNode.id)) {
			visitedNodes.add(currentNode.id);
			for (NodeData node : currentNode.neighborhoods) {
				dfs(node, visitedNodes);
			}
		}
	}	

	static double get2DEuclidianDistance(NodeData a, NodeData b) {		
		return Math.floor(Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2)));
	}
	
	public static Map<Integer, NodeData> buildGraphWithAFSDepotAndCustomer(GVRP gvrp, Integer customer){
//		build base graph
		Map<Integer, NodeData> nodes = new HashMap<Integer, NodeData> (gvrp.rechargeStationsRefuelingTime.size() + 2);			
		NodeData customerNode = new NodeData(customer, gvrp.nodesCoordinates.get(customer)[0], gvrp.nodesCoordinates.get(customer)[1]);
		NodeData depotNode = new NodeData(0, gvrp.nodesCoordinates.get(0)[0], gvrp.nodesCoordinates.get(0)[1]);
		nodes.put(0, depotNode);	
		nodes.put(customer, customerNode);
		if (gvrp.distanceMatrix[0][customer] <= gvrp.vehicleAutonomy / 2) {
			customerNode.neighborhoods.add(depotNode);
			depotNode.neighborhoods.add(customerNode);
		}
//		afss
		for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {	
			NodeData afsNode = new NodeData(afs, gvrp.nodesCoordinates.get(afs)[0], gvrp.nodesCoordinates.get(afs)[1]);
			nodes.put(afs, afsNode);
			if (gvrp.distanceMatrix[customer][afs] <= gvrp.vehicleAutonomy / 2) {
				customerNode.neighborhoods.add(afsNode);
				afsNode.neighborhoods.add(customerNode);
			}
			if (gvrp.distanceMatrix[0][afs] <= gvrp.vehicleAutonomy) {
				afsNode.neighborhoods.add(depotNode);
				depotNode.neighborhoods.add(afsNode);
			}
		}
		for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {
			NodeData afsNode = nodes.get(afs);				
			for (Integer _afs : gvrp.rechargeStationsRefuelingTime.keySet()) {					
				if (!afs.equals(_afs) && gvrp.distanceMatrix[_afs][afs] <= gvrp.vehicleAutonomy) {
					NodeData _afsNode = nodes.get(_afs);	
					_afsNode.neighborhoods.add(afsNode);
					afsNode.neighborhoods.add(_afsNode);
				}					
			}
		}	
		return nodes;
	}
	
	public static Map<Integer, NodeData> buildGVRPGraphOnlyWithDepotAndAFSs(GVRP gvrp){
//		build base graph
		Map<Integer, NodeData> nodes = new HashMap<Integer, NodeData> (gvrp.size);					
		NodeData depotNode = new NodeData(0, gvrp.nodesCoordinates.get(0)[0], gvrp.nodesCoordinates.get(0)[1]);
		nodes.put(0, depotNode);				
//		afss
		for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {	
			NodeData afsNode = new NodeData(afs, gvrp.nodesCoordinates.get(afs)[0], gvrp.nodesCoordinates.get(afs)[1]);
			nodes.put(afs, afsNode);
			if (gvrp.distanceMatrix[0][afs] <= gvrp.vehicleAutonomy) {
				afsNode.neighborhoods.add(depotNode);
				depotNode.neighborhoods.add(afsNode);
			}
		}
		for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {
			NodeData afsNode = nodes.get(afs);				
			for (Integer _afs : gvrp.rechargeStationsRefuelingTime.keySet()) {					
				if (!afs.equals(_afs) && gvrp.distanceMatrix[_afs][afs] <= gvrp.vehicleAutonomy) {
					NodeData _afsNode = nodes.get(_afs);	
					_afsNode.neighborhoods.add(afsNode);
					afsNode.neighborhoods.add(_afsNode);
				}					
			}
		}			
		return nodes;
	}
	
	public static boolean checkIfHasAPath(Map<Integer, NodeData> afss, NodeData depotNode, 
		NodeData customerNode, Double vehicleAutonomy, Double[][] costs){
//		build base graph
		Map<Integer, NodeData> nodes = new HashMap<Integer, NodeData> (afss.size() + 1);
		nodes.put(0, depotNode);	
		nodes.put(customerNode.id, customerNode);
		if (costs[0][customerNode.id] <= vehicleAutonomy / 2) {
			customerNode.neighborhoods.add(depotNode);
			depotNode.neighborhoods.add(customerNode);
		}
//		afss
		for (Integer afs : afss.keySet()) {	
			NodeData afsNode = afss.get(afs);
			nodes.put(afs, afsNode);
			if (costs[customerNode.id][afs] <= vehicleAutonomy / 2) {
				customerNode.neighborhoods.add(afsNode);
				afsNode.neighborhoods.add(customerNode);
			}
			if (costs[0][afs] <= vehicleAutonomy) {
				afsNode.neighborhoods.add(depotNode);
				depotNode.neighborhoods.add(afsNode);
			}
		}
		for (Integer afs : afss.keySet()) {
			NodeData afsNode = nodes.get(afs);				
			for (Integer _afs : afss.keySet()) {					
				if (!afs.equals(_afs) && costs[_afs][afs] <= vehicleAutonomy) {
					NodeData _afsNode = nodes.get(_afs);	
					_afsNode.neighborhoods.add(afsNode);
					afsNode.neighborhoods.add(_afsNode);
				}					
			}
		}	
//		dijkstra			
		Set<Integer> vertexesSet = new HashSet<Integer> ();
		Map<Integer, Double> dist = new HashMap<Integer, Double> (nodes.keySet().size());
		Map<Integer, Integer> prev = new HashMap<Integer, Integer> (nodes.keySet().size()); 
		for (Integer id : nodes.keySet()) {
			dist.put(id, 100000000d);
			prev.put(id, null);
			vertexesSet.add(id);
		}
		dist.put(0, 0d);
//		S ← empty sequence
		Queue<Integer> s = new LinkedList<Integer> ();
		while (vertexesSet.size() > 0) {
//			System.out.println(vertexesSet.size());
//			u ← vertex in Q with min dist[u]
			Integer u = null;
			Double minDist = Double.MAX_VALUE;
			for (Integer id : vertexesSet) {
				if (dist.get(id) < minDist) {
					minDist = dist.get(id);
					u = id;
				}					
			}
//			remove u from Q
			vertexesSet.remove(u);
			if (u.equals(customerNode.id)) {					
//				get path				
//				u ← target			
//				if prev[u] is defined or u = source:          // Do something only if the vertex is reachable
				if (prev.get(u) != null || u.equals(0)) {
//					System.out.println("OK");
//					while u is defined:                       // Construct the shortest path with a stack S
					while (u != null) {
//						insert u at the beginning of S        // Push the vertex onto the stack
						s.add(u);
//						u ← prev[u]                           // Traverse from target to source
						u = prev.get(u);
					}
					break;
				}
//				break;
			}					
//			for each neighbor v of u: // only v that are still in Q
			NodeData uNode = nodes.get(u);
			for (NodeData v : uNode.neighborhoods) {
				if (vertexesSet.contains(v.id)) {
//					alt ← dist[u] + length(u, v)
					Double alt = dist.get(u) + costs[u][v.id];
//					if alt < dist[v]:   
					if (alt < dist.get(v.id)) {
//						dist[v] ← alt
						dist.put(v.id, alt);
//						prev[v] ← u
						prev.put(v.id, u);
					}	
				}
			}                      				
		}		
		if (s.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public static boolean checkIfHasAPath(GVRP gvrp, Integer customer){
//			build base graph
		Map<Integer, NodeData> nodes = new HashMap<Integer, NodeData> (gvrp.rechargeStationsRefuelingTime.size() + 1);
		NodeData depotNode = new NodeData(0, gvrp.nodesCoordinates.get(0)[0], gvrp.nodesCoordinates.get(0)[1]);
		NodeData customerNode = new NodeData(customer, gvrp.nodesCoordinates.get(customer)[0], gvrp.nodesCoordinates.get(customer)[1]);
		nodes.put(0, depotNode);	
		nodes.put(customer, customerNode);
		if (gvrp.distanceMatrix[0][customer] <= gvrp.vehicleAutonomy / 2) {
			customerNode.neighborhoods.add(depotNode);
			depotNode.neighborhoods.add(customerNode);
		}
//			afss
		for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {	
			NodeData afsNode = new NodeData(afs, gvrp.nodesCoordinates.get(afs)[0], gvrp.nodesCoordinates.get(afs)[1]);
			nodes.put(afs, afsNode);
			if (gvrp.distanceMatrix[customerNode.id][afs] <= gvrp.vehicleAutonomy / 2) {
				customerNode.neighborhoods.add(afsNode);
				afsNode.neighborhoods.add(customerNode);
			}
			if (gvrp.distanceMatrix[0][afs] <= gvrp.vehicleAutonomy) {
				afsNode.neighborhoods.add(depotNode);
				depotNode.neighborhoods.add(afsNode);
			}
		}
		for (Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {
			NodeData afsNode = nodes.get(afs);				
			for (Integer _afs : gvrp.rechargeStationsRefuelingTime.keySet()) {					
				if (!afs.equals(_afs) && gvrp.distanceMatrix[_afs][afs] <= gvrp.vehicleAutonomy) {
					NodeData _afsNode = nodes.get(_afs);	
					_afsNode.neighborhoods.add(afsNode);
					afsNode.neighborhoods.add(_afsNode);
				}					
			}
		}	
//			dijkstra			
		Set<Integer> vertexesSet = new HashSet<Integer> ();
		Map<Integer, Double> dist = new HashMap<Integer, Double> (nodes.keySet().size());
		Map<Integer, Integer> prev = new HashMap<Integer, Integer> (nodes.keySet().size()); 
		for (Integer id : nodes.keySet()) {
			dist.put(id, 100000000d);
			prev.put(id, null);
			vertexesSet.add(id);
		}
		dist.put(0, 0d);
//			S ← empty sequence
		Queue<Integer> s = new LinkedList<Integer> ();
		while (vertexesSet.size() > 0) {
//				System.out.println(vertexesSet.size());
//				u ← vertex in Q with min dist[u]
			Integer u = null;
			Double minDist = Double.MAX_VALUE;
			for (Integer id : vertexesSet) {
				if (dist.get(id) < minDist) {
					minDist = dist.get(id);
					u = id;
				}					
			}
//				remove u from Q
			vertexesSet.remove(u);
			if (u.equals(customerNode.id)) {					
//					get path				
//					u ← target			
//					if prev[u] is defined or u = source:          // Do something only if the vertex is reachable
				if (prev.get(u) != null || u.equals(0)) {
//						System.out.println("OK");
//						while u is defined:                       // Construct the shortest path with a stack S
					while (u != null) {
//							insert u at the beginning of S        // Push the vertex onto the stack
						s.add(u);
//							u ← prev[u]                           // Traverse from target to source
						u = prev.get(u);
					}
					break;
				}
//					break;
			}					
//				for each neighbor v of u: // only v that are still in Q
			NodeData uNode = nodes.get(u);
			for (NodeData v : uNode.neighborhoods) {
				if (vertexesSet.contains(v.id)) {
//						alt ← dist[u] + length(u, v)
					Double alt = dist.get(u) + gvrp.distanceMatrix[u][v.id];
//						if alt < dist[v]:   
					if (alt < dist.get(v.id)) {
//							dist[v] ← alt
						dist.put(v.id, alt);
//							prev[v] ← u
						prev.put(v.id, u);
					}	
				}
			}                      				
		}		
		if (s.isEmpty()) {
			return false;
		}
		return true;
	}

	public static List<Stack<Integer>> gvrpCustomersDijkstra(GVRP gvrp){
		Solution<Stack<Integer>> paths = new Solution<Stack<Integer>>(); 
		for (Integer customer : gvrp.customersDemands.keySet()) {					
	//		build base graph
			Map<Integer, NodeData> nodes = Util.buildGraphWithAFSDepotAndCustomer(gvrp, customer);						
	//		check if is connected
			if (!Util.isAConnectedGraph(nodes.values())) {
				System.out.println("Not connected graph");
			}
	//		dijkstra			
			Set<Integer> vertexesSet = new HashSet<Integer> ();
			Map<Integer, Double> dist = new HashMap<Integer, Double> (nodes.keySet().size());
			Map<Integer, Integer> prev = new HashMap<Integer, Integer> (nodes.keySet().size()); 
			for (Integer id : nodes.keySet()) {
				dist.put(id, 100000000d);
				prev.put(id, null);
				vertexesSet.add(id);
			}
			dist.put(0, 0d);
			while (vertexesSet.size() > 0) {
	//			System.out.println(vertexesSet.size());
	//			u ← vertex in Q with min dist[u]
				Integer u = null;
				Double minDist = Double.MAX_VALUE;
				for (Integer id : vertexesSet) {
					if (dist.get(id) < minDist) {
						minDist = dist.get(id);
						u = id;
					}		
				}
	//			remove u from Q
				vertexesSet.remove(u);							
	//			for each neighbor v of u: // only v that are still in Q
				NodeData uNode = nodes.get(u);
				for (NodeData v : uNode.neighborhoods) {
					if (vertexesSet.contains(v.id)) {
	//					alt ← dist[u] + length(u, v)
						Double alt = dist.get(u) + gvrp.distanceMatrix[u][v.id];
	//					if alt < dist[v]:   
						if (alt < dist.get(v.id)) {
	//						dist[v] ← alt
							dist.put(v.id, alt);
	//						prev[v] ← u
							prev.put(v.id, u);
						}	
					}
				}  
			}
//			S ← empty sequence
			Stack<Integer> s = new Stack<Integer> ();
//			get path				
//			u ← target
			Integer customer_line = customer;
//			if prev[u] is defined or u = source:          // Do something only if the vertex is reachable
			if (prev.get(customer_line) != null || customer_line.equals(0)) {
//				System.out.println("OK");
//				while u is defined:                       // Construct the shortest path with a stack S
				while (customer_line != null) {
//					insert u at the beginning of S        // Push the vertex onto the stack
					s.add(customer_line);
//					u ← prev[u]                           // Traverse from target to source
					customer_line = prev.get(customer_line);
				}
			}	
			if (s.isEmpty()) {
				System.out.println(customer + " no path");
			}else{
//				for (Integer integer : s) {
//					System.out.print(integer+",");
//				}
//				System.out.println();
			}
//			create route
			paths.add(s);
		}
		return paths;
	}

	public static List<Stack<Integer>> gvrpFromDepotToAFSDijkstra(GVRP gvrp){
		Solution<Stack<Integer>> paths = new Solution<Stack<Integer>>(); 	
		Map<Integer, NodeData> graph = Util.buildGVRPGraphOnlyWithDepotAndAFSs(gvrp);
//		check if is connected
		if (!Util.isAConnectedGraph(graph.values())) {
			System.out.println("Not connected graph");
		}
//		dijkstra			
		Set<Integer> vertexesSet = new HashSet<Integer> ();
		Map<Integer, Double> dist = new HashMap<Integer, Double> (graph.keySet().size());
		Map<Integer, Integer> prev = new HashMap<Integer, Integer> (graph.keySet().size()); 
		for (Integer id : graph.keySet()) {
			dist.put(id, 100000000d);
			prev.put(id, null);
			vertexesSet.add(id);
		}
		dist.put(0, 0d);
		while (vertexesSet.size() > 0) {
//			System.out.println(vertexesSet.size());
//			u ← vertex in Q with min dist[u]
			Integer u = null;
			Double minDist = Double.MAX_VALUE;
			for (Integer id : vertexesSet) {
				if (dist.get(id) < minDist) {
					minDist = dist.get(id);
					u = id;
				}		
			}
//			remove u from Q
			vertexesSet.remove(u);							
//			for each neighbor v of u: // only v that are still in Q
			NodeData uNode = graph.get(u);
			for (NodeData v : uNode.neighborhoods) {
				if (vertexesSet.contains(v.id)) {
//					alt ← dist[u] + length(u, v)
					Double alt = dist.get(u) + gvrp.distanceMatrix[u][v.id];
//					if alt < dist[v]:   
					if (alt < dist.get(v.id)) {
//						dist[v] ← alt
						dist.put(v.id, alt);
//						prev[v] ← u
						prev.put(v.id, u);
					}	
				}
			}  
		}
		for(Integer afs : gvrp.rechargeStationsRefuelingTime.keySet()) {
//			S ← empty sequence
			Stack<Integer> s = new Stack<Integer> ();
//			get path				
//			u ← target
			Integer afs_line = afs;
//			if prev[u] is defined or u = source:          // Do something only if the vertex is reachable
			if (prev.get(afs_line) != null || afs_line.equals(0)) {
//				System.out.println("OK");
//				while u is defined:                       // Construct the shortest path with a stack S
				while (afs_line != null) {
//					insert u at the beginning of S        // Push the vertex onto the stack
					s.add(afs_line);
//					u ← prev[u]                           // Traverse from target to source
					afs_line = prev.get(afs_line);
				}
			}	
			if (s.isEmpty()) {
				System.out.println(afs + " no path");
			}else{
	//				for (Integer integer : s) {
	//					System.out.print(integer+",");
	//				}
	//				System.out.println();
			}
	//			create route
			paths.add(s);		
		}
		return paths;
	}
}
