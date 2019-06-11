package metaheuristics.vns;

public abstract class LocalSearch<E, S> {

	public abstract S localOptimalSolution(E eval, S solution);
	
	public abstract S randomSolution(E eval, S solution);
	
}
