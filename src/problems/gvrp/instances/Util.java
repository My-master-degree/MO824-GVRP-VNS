package problems.gvrp.instances;
import java.util.HashSet;
import java.util.Set;

public class Util {
	public static boolean isAConnectedGraph(Set<InstancesGenerator.Node> nodes) {
		Set<InstancesGenerator.Node> visitedNodes = new HashSet<InstancesGenerator.Node> (nodes.size());
		dfs(nodes.iterator().next(), visitedNodes);
		return visitedNodes.size() == nodes.size();
	}
	
	public static void dfs(InstancesGenerator.Node currentNode, Set<InstancesGenerator.Node> visitedNodes) {
		if (!visitedNodes.contains(currentNode)) {
			visitedNodes.add(currentNode);
			for (InstancesGenerator.Node node : currentNode.neighborhoods) {
				dfs(node, visitedNodes);
			}
		}
	}
}
