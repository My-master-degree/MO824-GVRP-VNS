package problems.gvrp;

import solutions.Solution;

public class Routes extends Solution<Route>{
	
	@Override
	public Object clone() {
		Routes copy = new Routes();
		for(Route route : this) {
			copy.add((Route) route.clone());
		}		
		return copy;
		
	}
}
