package problems.gvrp.instances;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Tree<E> {
	private class TreeNode{
		List<TreeNode> sons;
		E value;
		TreeNode (E value){
			sons = new ArrayList<TreeNode> ();
			this.value = value;
		}
	}
	
	Tree(E rootValue){
		this.root = new TreeNode(rootValue);
	}
	
	private TreeNode root;
	
	public void addSonOnValue(E valueToAdd, E valueToTarget) {
		TreeNode treeNode = bfsToFindValue(valueToTarget);
		treeNode.sons.add(new TreeNode(valueToAdd));
	}
	
	private TreeNode bfsToFindValue (E value) {
		Queue<TreeNode> q = new LinkedList<TreeNode> ();
		q.add(root);
		while (q.size() > 0) {
			TreeNode currentNode = q.poll();
//			System.out.println("BF in "+currentNode.value);
			if (currentNode.value.equals(value)) {
				return currentNode; 
			}
			for (TreeNode son : currentNode.sons) {
				q.add(son);				
			}
		}
		return null;
	}
	
//	public List<E> getPathToValue(E value){
//		
//	}
	
}
