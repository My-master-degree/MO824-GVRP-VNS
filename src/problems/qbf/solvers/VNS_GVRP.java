package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import metaheuristics.vns.AbstractVNS;
import problems.gvrp.GVRP_Inverse;
import solutions.Solution;



/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class VNS_GVRP extends AbstractVNS<List<Integer>> {
	
	/**
	 * Constructor for the GRASP_QBF class. An inverse QBF objective function is
	 * passed as argument for the superclass constructor.
	 * 
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public VNS_GVRP(String filename, Integer iterations, Integer maxDurationInMilliseconds) throws IOException {
		super(new GVRP_Inverse(filename), iterations, maxDurationInMilliseconds);
	}

	@Override
	public Solution<List<Integer>> localSearch(Solution<List<Integer>> solution) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Solution<List<Integer>> constructiveHeuristic() {
		Solution<List<Integer>> sol = new Solution<List<Integer>>();
		GVRP_Inverse GVRP_evaluator = (GVRP_Inverse) this.ObjFunction;
		Double vehicleAvailableCapacity = GVRP_evaluator.vehicleCapacity;
		List<Integer> currentRoute = new ArrayList<Integer>(); 		
		Integer insertedCustomers = 0;
		do {
			
			currentRoute.add();
		}while (insertedCustomers < GVRP_evaluator.customersSize);
		
		return null;
	}


	@Override
	public Solution<List<Integer>> createEmptySol() {
		// TODO Auto-generated method stub
		return null;
	}

}
