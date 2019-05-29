package problems.gvrp.instances;
import java.util.HashSet;
import java.util.Set;

import problems.gvrp.instances.NodeData;

public class Util {
	public static boolean isAConnectedGraph(Set<NodeData> nodes) {
		Set<NodeData> visitedNodes = new HashSet<NodeData> (nodes.size());
		dfs(nodes.iterator().next(), visitedNodes);
		for (NodeData node : nodes) {
			if (!visitedNodes.contains(node)) {
				return false;
			}
		}
		return true;
//		return visitedNodes.size() == nodes.size();
	}
	
	public static void dfs(NodeData currentNode, Set<NodeData> visitedNodes) {
		if (!visitedNodes.contains(currentNode)) {
			visitedNodes.add(currentNode);
			for (NodeData node : currentNode.neighborhoods) {
				dfs(node, visitedNodes);
			}
		}
	}

	static double get2DEuclidianDistance(NodeData a, NodeData b) {		
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
}
