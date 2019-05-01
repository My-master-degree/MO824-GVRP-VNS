/**
 * 
 */
package metaheuristics.vns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import problems.Evaluator;
import solutions.Solution;

/**
 * Abstract class for metaheuristic GRASP (Greedy Randomized Adaptive Search
 * Procedure). It consider a maximization problem.
 * 
 * @author ccavellucci, fusberti
 * @param <E>
 *            Generic type of the element which composes the solution.
 */
public abstract class AbstractVNS<E> {

	/**
	 * Class used to represent a neighborhood structure 
	 *
	 */
	public abstract class NeighborhoodStructure {
		
		/**
		 * Returns an random solution from a the neighborhood structure space
		 * 
		 * @return An random solution.
		 */
		public abstract Solution<E> randomSolution(Solution<E> solution);
		
		/**
		 * Returns the local optimal solution from a the neighborhood structure space
		 * 
		 * @return The local optimal solution.
		 */
		public abstract Solution<E> localOptimalSolution(Solution<E> solution);
	}
	
	/**
	 * flag that indicates whether the code should print more information on
	 * screen
	 */
	public static boolean verbose = true;

	/**
	 * a random number generator
	 */
	static Random rng = new Random(0);
	
	/**
	 * the neighborhood structure list
	 */
	protected List<NeighborhoodStructure> neighborhoodStructures;
	
	/**
	 * the objective function being optimized
	 */
	protected Evaluator<E> ObjFunction;

	/**
	 * the best solution cost
	 */
	protected Double bestCost;

	/**
	 * the best solution
	 */
	protected Solution<E> bestSol;

	/**
	 * the number of iterations the VNS main loop executes.
	 */
	protected Integer maxNumberOfIterations;
	
	/**
	 * the time in microseconds the VNS main loop executes.
	 */
	protected Integer maxDurationInMilliseconds;	

	/**
	 * Constructor for the AbstractGRASP class.
	 * 
	 * @param objFunction
	 *            The objective function being maximized.
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 */
	public AbstractVNS(Evaluator<E> objFunction, Integer iterations, Integer maxDurationInMilliseconds) {
		this.ObjFunction = objFunction;
		this.maxNumberOfIterations = iterations;
		this.maxDurationInMilliseconds = maxDurationInMilliseconds;
	}	
	
	/**
	 * The GRASP mainframe. It consists of a loop, in which each iteration goes
	 * through the constructive heuristic and local search. The best solution is
	 * returned as result.
	 * 
	 * @return The best feasible solution obtained throughout all iterations.
	 */
	public Solution<E> solve() {
		bestSol = constructiveHeuristic();
		long startTime = System.currentTimeMillis();
		for (int i = 0, j = 0; i < this.neighborhoodStructures.size() && j < maxNumberOfIterations && System.currentTimeMillis() > startTime + maxDurationInMilliseconds; i++, j++) {
//			get random solution
			Solution<E> randomSolution = this.neighborhoodStructures.get(i).randomSolution(this.bestSol);
			this.ObjFunction.evaluate(randomSolution);
//			get local optimal solution
			Solution<E> localOptimalSolution = localSearch(randomSolution);			
			this.ObjFunction.evaluate(localOptimalSolution);
//			check cost
			if (localOptimalSolution.cost > bestSol.cost) {
				bestSol = new Solution<E>(localOptimalSolution);
				if (verbose) {
					System.out.println("(Iter. " + j + ") BestSol = " + bestSol);
				}
			}		
		}

		return bestSol;
	}

	/**
	 * The GRASP local search phase is responsible for repeatedly applying a
	 * neighborhood operation while the solution is getting improved, i.e.,
	 * until a local optimum is attained.
	 * 
	 * @return An local optimum solution.
	 */
	public abstract Solution<E> localSearch(Solution<E> solution);	
	
	/**
	 * The GRASP constructive heuristic, which is responsible for building a
	 * feasible solution by selecting in a greedy-random fashion, candidate
	 * elements to enter the solution.
	 * 
	 * @return A feasible solution to the problem being maximized.
	 */
	public abstract Solution<E> constructiveHeuristic();
	
	/**
	 * Creates a new solution which is empty, i.e., does not contain any
	 * element.
	 * 
	 * @return An empty solution.
	 */
	public abstract Solution<E> createEmptySol();

}
