/**
 * 
 */
package metaheuristics.vns;

import java.util.List;
import java.util.Random;

import problems.Evaluator;
import problems.gvrp.GVRP;
import problems.gvrp.Routes;
import problems.gvrp.analyzer.Analyzer;
import solutions.Solution;

/**
 * Abstract class for metaheuristic VNS (Variable Neighborhood Search). 
 * It consider a maximization problem.
 * 
 * @author matheus diógenes, cristina freitas
 * @param <E>
 *            Generic type of the element which composes the solution.
 */
public abstract class AbstractVNS<E extends Evaluator<T, S>, S extends Solution<T>, T> {
	
	public enum VNS_TYPE{
		INTENSIFICATION("INTENSIFICATION"),
		NONE("NONE");
		VNS_TYPE(String type){
			this.type = type;
		}
		public String type;
	}
	
	protected int a;
	
	protected VNS_TYPE vns_type;
	
	/**
	 * flag that indicates whether the code should print more information on
	 * screen
	 */
	public static boolean verbose = false;

	/**
	 * a random number generator
	 */
	static Random rng = new Random(0);
	
	/**
	 * the neighborhood structure list
	 */
	protected List<LocalSearch<E, S>> neighborhoodStructures;
	
	/**
	 * the objective function being optimized
	 */
	protected E ObjFunction;

	/**
	 * the best solution cost
	 */
	protected Double bestCost;

	/**
	 * the best solution
	 */
	protected S bestSol;

	/**
	 * the number of iterations the VNS main loop executes.
	 */
	protected Integer maxNumberOfIterations;
	
	/**
	 * the time in microseconds the VNS main loop executes.
	 */
	protected Integer maxDurationInMilliseconds;

	public AbstractVNS(E objFunction, Integer iterations, Integer maxDurationInMilliseconds, 
			List<LocalSearch<E, S>> localSearchs, VNS_TYPE vns_type) {
		this.ObjFunction = objFunction;
		this.maxNumberOfIterations = iterations;
		this.maxDurationInMilliseconds = maxDurationInMilliseconds;
		this.neighborhoodStructures = localSearchs;
		this.vns_type = vns_type;
		a = this.neighborhoodStructures.size();
	}	
	
	private int getKStep(int i, int c) {
		if(vns_type.equals(VNS_TYPE.INTENSIFICATION)) {
			return i + (c%a)/(a-1);
		}
		return i + 1;
	}
	
	public S solve() {
//		build initial solution
		bestSol = constructiveHeuristic();
		S localOptimalSolution = bestSol;
		this.ObjFunction.evaluate(localOptimalSolution);
//		System.out.println("Initial solution build");
		long endTime = System.currentTimeMillis() + this.maxDurationInMilliseconds;
//		set initial parameters							
		for (int i = 0, j = 1, c = 0; 
			i < this.neighborhoodStructures.size() &&
			j < this.maxNumberOfIterations &&
			System.currentTimeMillis() <= endTime
			; c++, j++) {
//			random solution			
			S randomSolution = this.neighborhoodStructures.get(i).randomSolution(this.ObjFunction, localOptimalSolution);
			this.ObjFunction.evaluate(randomSolution);
			String str = Analyzer.analyze((Routes) randomSolution, (GVRP) ObjFunction);
			if (!str.equals("")) {
				System.out.println("get random at "+i);
				System.out.println(str);
			}
//			local opt solution
			localOptimalSolution = this.neighborhoodStructures.get(i).localOptimalSolution(this.ObjFunction, randomSolution);
			this.ObjFunction.evaluate(localOptimalSolution);
			str = Analyzer.analyze((Routes) localOptimalSolution, (GVRP) ObjFunction);
			if (!str.equals("")) {
				System.out.println("get local at "+i);
				System.out.println(str);
			}
//			System.out.println("local opt achieved");
//			check cost
			if (localOptimalSolution.cost > bestSol.cost) {				
				i = 0;
				c = 0;
				bestSol = (S) localOptimalSolution.clone();
				this.ObjFunction.evaluate(bestSol);
				if (verbose) {
					System.out.println("\t(Iter. " + j + ") BestSol = " + bestSol);
				}
			}else {
				i = getKStep(i, c);
			}
		}
		

		return bestSol;
	}	
	
	public abstract S constructiveHeuristic();

	/**
	 * @return the objFunction
	 */
	public E getObjFunction() {
		return ObjFunction;
	}
	

}
