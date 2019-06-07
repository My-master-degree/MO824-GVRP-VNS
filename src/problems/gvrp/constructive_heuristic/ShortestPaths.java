package problems.gvrp.constructive_heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import problems.gvrp.GVRP_Inverse;
import problems.gvrp.instances.NodeData;
import problems.gvrp.instances.Util;
import solutions.Solution;

public class ShortestPaths {
	public static Solution<List<Integer>> construct(GVRP_Inverse gvrp){
		Solution<List<Integer>> routes = new Solution<List<Integer>>(); 
//		customers
		for (Integer customer : gvrp.customersServiceTime.keySet()) {
			if (!Util.checkIfHasAPath(gvrp,  customer))
				System.out.println("NOT path");
//			build base graph
			Map<Integer, NodeData> nodes = Util.buildGraphWithAFSDepotAndCustomer(gvrp, customer);						
//			check if is connected
//			if (!Util.isAConnectedGraph(nodes.values())) {
//				System.out.println("Not connected graph");
//			}
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
			Stack<Integer> s = new Stack<Integer> ();
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
				if (u.equals(customer)) {					
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
			if (s.isEmpty()) {
//				System.out.println(customer + " no path");
			}else if (s.size() > 3){
//				for (Integer integer : s) {
//					System.out.print(integer+",");
//				}
//				System.out.println();
			}
			List<Integer> route = new ArrayList<Integer> ();			
			for (int i = s.size() - 1; i >= 0; i--) {
				route.add(s.get(i));
			}		
			for (int i = 1; i < s.size(); i++) {
				route.add(s.get(i));
			}
			routes.add(route);
		}
//		
			
//		Map<Integer, NodeData> nodes = Util.buildGVRPGraph(gvrp);						
////	check if is connected
//		if (!Util.isAConnectedGraph(nodes.values())) {
//			System.out.println("Not connected graph");
//		}
////		dijkstra			
//		Set<Integer> vertexesSet = new HashSet<Integer> ();
//		Map<Integer, Double> dist = new HashMap<Integer, Double> (nodes.keySet().size());
//		Map<Integer, Integer> prev = new HashMap<Integer, Integer> (nodes.keySet().size()); 
//		for (Integer id : nodes.keySet()) {
//			dist.put(id, 100000000d);
//			prev.put(id, null);
//			vertexesSet.add(id);
//		}
//		dist.put(0, 0d);
////		S ← empty sequence
//		Queue<Integer> s = new LinkedList<Integer> ();
//		while (vertexesSet.size() > 0) {
////			System.out.println(vertexesSet.size());
////			u ← vertex in Q with min dist[u]
//			Integer u = null;
//			Double minDist = Double.MAX_VALUE;
//			for (Integer id : vertexesSet) {
//				if (dist.get(id) < minDist) {
//					minDist = dist.get(id);
//					u = id;
//				}					
//			}
////			remove u from Q
//			vertexesSet.remove(u);				
////			for each neighbor v of u: // only v that are still in Q
//			NodeData uNode = nodes.get(u);
//			for (NodeData v : uNode.neighborhoods) {
//				if (vertexesSet.contains(v.id)) {
////					alt ← dist[u] + length(u, v)
//					Double alt = dist.get(u) + gvrp.distanceMatrix[u][v.id];
////					if alt < dist[v]:   
//					if (alt < dist.get(v.id)) {
////						dist[v] ← alt
//						dist.put(v.id, alt);
////						prev[v] ← u
//						prev.put(v.id, u);
//					}	
//				}			       
//			}	
//		}
		
//		if (u.equals(customer)) {					
////	get path				
////	u ← target			
////	if prev[u] is defined or u = source:          // Do something only if the vertex is reachable
//	if (prev.get(u) != null || u.equals(0)) {
////		System.out.println("OK");
////		while u is defined:                       // Construct the shortest path with a stack S
//		while (u != null) {
////			insert u at the beginning of S        // Push the vertex onto the stack
//			s.add(u);
////			u ← prev[u]                           // Traverse from target to source
//			u = prev.get(u);
//		}
//		break;
//	}
////	break;
		
		
		
		return routes;		
	}
}
