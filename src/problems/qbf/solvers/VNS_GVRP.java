package problems.qbf.solvers;

import java.util.List;

import metaheuristics.vns.AbstractVNS;
import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.Route;
import problems.gvrp.Routes;
import problems.gvrp.constructive_heuristic.ShortestPaths;

public class VNS_GVRP extends AbstractVNS<GVRP_Inverse, Routes, Route> {	

	public VNS_GVRP(GVRP_Inverse objFunction, Integer iterations, Integer maxDurationInMilliseconds,
			List<LocalSearch<GVRP_Inverse, Routes>> localSearchs, VNS_TYPE vns_type) {
		super(objFunction, iterations, maxDurationInMilliseconds, localSearchs, vns_type);
	}

	@Override
	public Routes constructiveHeuristic() {		
		return new ShortestPaths().construct(super.ObjFunction);
	}


}
