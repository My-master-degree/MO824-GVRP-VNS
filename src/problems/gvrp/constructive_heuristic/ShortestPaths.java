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

import problems.gvrp.GVRP;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.instances.NodeData;
import problems.gvrp.instances.Util;
import solutions.Solution;

public class ShortestPaths {
	public static Solution<List<Integer>> construct(GVRP gvrp){
		Solution<List<Integer>> routes = new Solution<List<Integer>>(); 
		List<Stack<Integer>> paths = Util.gvrpCustomersDijkstra(gvrp);
//		get customers paths
		for (Stack<Integer> path : paths) {					
//			create route
			List<Integer> route = new ArrayList<Integer> ();			
			for (int i = path.size() - 1; i >= 0; i--) {
				route.add(path.get(i));
			}		
			for (int i = 1; i < path.size(); i++) {
				route.add(path.get(i));
			}
//			for (Integer integer : route) {
//				System.out.print(integer+",");
//			}
//			System.out.println();
			routes.add(route);
		}	
		return routes;		
	}
}
