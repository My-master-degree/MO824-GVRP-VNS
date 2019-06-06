package problems.gvrp.instances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeData {
	public Integer id;
	public Integer x;
	public Integer y;
	public double demand;
	public Set<NodeData> neighborhoods;

	public NodeData (Integer id, Integer x, Integer y){
		this.id = id;
		this.x = x;
		this.y = y;
		this.neighborhoods = new HashSet<NodeData> ();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + id;
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
		NodeData other = (NodeData) obj;
		if (!x.equals(other.x))
			return false;
		if (!y.equals(other.y))
			return false;
		if (!id.equals(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeData [id=" + id + ", x=" + x + ", y=" + y + ", demand=" + demand + "]";
	}
	
	
}