package problems.gvrp;

import java.io.IOException;

import problems.gvrp.instances.GVRPInstanceReader;

public class GVRP_Inverse extends GVRP {
	
	public GVRP_Inverse(String filename, GVRPInstanceReader gvrpInstanceReader) throws IOException {
		super(filename, gvrpInstanceReader);
	}

	@Override
	public Double evaluate(Routes sol) {
		return sol.cost = -super.evaluate(sol);

	}

}
