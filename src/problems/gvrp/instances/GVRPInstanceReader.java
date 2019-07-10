package problems.gvrp.instances;

import problems.gvrp.GVRP;

public interface GVRPInstanceReader {
	public void read(String path, GVRP gvrp);
}
