package problems.gvrp;

import java.io.IOException;

public class GVRP_Inverse extends GVRP {
	
	public GVRP_Inverse(String filename) throws IOException {
		super(filename);
	}

	@Override
	public Double evaluate(Routes sol) {
		return sol.cost = -super.evaluate(sol);

	}

}
