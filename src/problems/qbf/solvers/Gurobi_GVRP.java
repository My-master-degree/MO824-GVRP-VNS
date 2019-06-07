package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import problems.gvrp.GVRP;

public class Gurobi_GVRP {
	public GRBEnv env;
	public GRBModel model;
	public GRBVar[][] x;
	public GRBVar[] y;
	public GRBVar[] t;
	public GVRP problem;

	public Gurobi_GVRP(String filename) throws IOException {
		this.problem = new GVRP(filename);
	}	

	private int getIthVisitOfAFS(int i, int afs) {
//		return problem.size + (i - 1)* (problem.size - problem.customersSize + 1) + (afs%problem.customersSize) + 1;
		return problem.size + (i - 1)* (problem.size - problem.customersSize + 1) + (afs%problem.customersSize);
	}
	
	private int getIthVisitOfDepot(int i) {
		return problem.size + (i - 1)* (problem.size - problem.customersSize + 1) + 1;
	}
	
	private int getAFSByVisit(int i) {
		return ((i - problem.customersSize)%(problem.size - problem.customersSize + 1)) + problem.customersSize + 1;
	}
	
	public void populateNewModel(GRBModel model) throws GRBException {
//		extend graph
		int n = problem.rechargeStationsSize + problem.customersSize + 1;
		List<Integer> v_line = new ArrayList<Integer>((problem.rechargeStationsSize + 1) * problem.customersSize);
		v_line.add(0);		
		v_line.addAll(problem.customersDemands.keySet());
//		add depot visits
//		for (int i = 0; i < problem.customersSize; i++) {
//			v_line.add(this.getIthVisitOfDepot(i + 1));
//		}
		for (Integer afs: problem.rechargeStationsRefuelingTime.keySet()) {
			for (int i = 0; i < problem.customersSize; i++) {
				v_line.add(this.getIthVisitOfAFS(i + 1, afs));
			}
		}
		// variables
//		x
		x = new GRBVar[v_line.size()][v_line.size()];
		for (int i = 0; i < v_line.size(); i++) {
			for (int j = 0; j < v_line.size(); j++) {
				x[i][j] = model.addVar(0, 1, 0.0f, GRB.BINARY, "x[" + i + "]["+ j +"]");
			}
		}
//		y
		y = new GRBVar[v_line.size()];
		for (int i = 0; i < v_line.size(); i++) {			
			y[i] = model.addVar(0, problem.vehicleAutonomy, 0.0f, GRB.CONTINUOUS, "y[" + i + "]");			
		}
//		t
		t = new GRBVar[v_line.size()];
		for (int i = 0; i < v_line.size(); i++) {			
			t[i] = model.addVar(0, problem.vehicleOperationTime, 0.0f, GRB.CONTINUOUS, "y[" + i + "]");			
		}
		model.update();

		// objective functions
//		\sum_{i,j \in V' (i != j)} d_{ij} x_{ij} \forall i \in I
		GRBLinExpr obj = new GRBLinExpr();
		for (int i = 0; i < problem.size; i++) {
			for (int j = i; j < problem.size; j++) {
				obj.addTerm(problem.distanceMatrix[i][j], x[i][j]);
			}
		}

		// constraints
//		constraint (2): \sum_{j \in V' (i != j)} x_{ij} = 1 \forall i \in I
		for (Integer i : problem.customersDemands.keySet()) {
			GRBLinExpr expr = new GRBLinExpr();
			for (Integer j : v_line) {
				if (!i.equals(j))
					expr.addTerm(1.0, x[i][j]);				
			}
			model.addConstr(expr, GRB.EQUAL, 1.0, "\\sum_{j \\in V' ("+ i +" != j)} x_{"+i+" j} = 1");
		}
//		constraint (3): \sum_{j \in V' (f != j)} x_{fj} \leq 1 \forall f \in F_0
//		GRBLinExpr expr = new GRBLinExpr();
//		for (Integer j : v_line) {
//			if (!j.equals(0))
//				expr.addTerm(1.0, x[0][j]);				
//		}
//		model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "\\sum_{j \\in V' ("+ 0 +" != j)} x_{"+0+" j} \\leq 1");
		for (int f = problem.customersSize + 1; f < v_line.size(); f++) {
			GRBLinExpr expr = new GRBLinExpr();
			for (Integer j : v_line) {
				if (!j.equals(f))
					expr.addTerm(1.0, x[f][j]);				
			}
			model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "\\sum_{j \\in V' ("+ f +" != j)} x_{"+f+" j} \\leq 1");
		}
//		constraint (4): \sum_{i \in V' (i != j)} x_{ji} - \sum_{i \in V' (j != i)} x_{ij} = 0 \forall j \in V'		
		for (Integer j : v_line) {
			GRBLinExpr expr = new GRBLinExpr();
			for (Integer i : v_line) {
				if (!j.equals(i))
					expr.addTerm(1.0, x[j][i]);				
			}
			for (Integer i : v_line) {
				if (!j.equals(i))
					expr.addTerm(-1.0, x[i][j]);				
			}
			model.addConstr(expr, GRB.EQUAL, 0.0, "\\sum_{i \\in V' (i != "+ j +")} x_{"+ j +" i} - \\sum_{i \\in V' ("+ j +" != i)} x_{i "+ j +"} = 0");
		}
//		constraint (7) \tau_j \geq \tau_i + (t_{ij} - p_j)x_{ij} - T_{max}(1-x_{ij}) i \in V', j \in V' \{0} and i != j  
		for (Integer i : v_line) {
			for (Integer j : v_line) {
				if (!i.equals(j) && !j.equals(0)) {
//					left
					GRBLinExpr leftExpr = new GRBLinExpr();
					leftExpr.addTerm(1d, t[j]);
//					right
					GRBLinExpr rightExpr = new GRBLinExpr();
					rightExpr.addTerm(1d, t[i]);
//					get correspondent afs node
					int j_line = j > problem.size ? this.getAFSByVisit(j) : j;
					Double serviceTime = problem.rechargeStationsRefuelingTime.get(j_line) != null ? 
							problem.rechargeStationsRefuelingTime.get(j_line) : problem.customersServiceTime.get(j_line);
					rightExpr.addTerm(problem.timeMatrix[i][j] - serviceTime, x[i][j]);
					rightExpr.addConstant(-problem.vehicleOperationTime);
					rightExpr.addTerm(problem.vehicleOperationTime, x[i][j]);
					model.addConstr(leftExpr, GRB.GREATER_EQUAL, rightExpr, "\\tau_"+j+" \\geq \\tau_"+i+" + (t_{"+i+" "+j+"} - p_"+j+")x_{\"+i+\" \"+j+\"} - T_{max}(1-x_{"+i+" "+j+"})");					
				}
			}
		}
//		constraint (8) 0 \leq \tau_0 \leq T_{max} 
		GRBLinExpr tau_0 = new GRBLinExpr();
		tau_0.addTerm(1d, t[0]);				
		model.addConstr(tau_0, GRB.GREATER_EQUAL, 0, "\\tau_0 \\geq 0");
		model.addConstr(tau_0, GRB.LESS_EQUAL, problem.vehicleOperationTime, "\\tau_0 \\leq T_{max}");
//		constraint (9) t_{0j} \leq \tau_{j} \leq T_{max} - (t_{j0} + pj) \forall j \in V' \ {0}
		for (Integer j : v_line) {
			GRBLinExpr tau_j = new GRBLinExpr();
			tau_j.addTerm(1d, t[j]);				
			model.addConstr(tau_j, GRB.GREATER_EQUAL, 0, "\\tau_"+j+" \\geq "+problem.timeMatrix[0][j]);
//			get correspondent afs node
			int j_line = j > problem.size ? this.getAFSByVisit(j) : j;
			Double serviceTime = problem.rechargeStationsRefuelingTime.get(j_line) != null ? 
					problem.rechargeStationsRefuelingTime.get(j_line) : problem.customersServiceTime.get(j_line);
			model.addConstr(tau_j, GRB.LESS_EQUAL, problem.vehicleOperationTime - problem.timeMatrix[j][0] - serviceTime, "\\tau_"+j+" \\geq T_{max} - t_{"+j+" 0} - p_{"+j+"}");
		}
//		constraint (10) y_j \leq y_i - d_{ij} x{ij} + Q(1 - x_{ij}) \forall j \in I and  i \in V', i != j
		for (Integer j : problem.customersDemands.keySet()) {			
			for (Integer i : v_line) {
				if (!j.equals(i)) {
					GRBLinExpr left = new GRBLinExpr();
					left.addTerm(1d, y[j]);
					GRBLinExpr right = new GRBLinExpr();
					right.addTerm(1d, y[i]);
					right.addTerm(-problem.distanceMatrix[i][j], x[i][j]);
					right.addConstant(problem.vehicleAutonomy);
					right.addTerm(-problem.vehicleAutonomy, x[i][j]);
					model.addConstr(left, GRB.LESS_EQUAL, right, "y_"+j+" \\leq y_"+i+" - d_{"+i+" "+j+"} x{"+i+" "+j+"} + Q(1 - x_{"+i+" "+j+"})");
				}
			}
		}
//		constraint (11) y_j = Q \forall j \in F_0	
		for (Integer j : v_line) {
			if (j.equals(0) || j > problem.customersSize) {
				GRBLinExpr expr = new GRBLinExpr();
				expr.addTerm(1, y[j]);
				model.addConstr(expr, GRB.EQUAL, problem.vehicleAutonomy, "y_"+j+" = Q");
			}
		}
		
//		setup
		model.setObjective(obj);
		model.update();

		// maximization objective function
		model.set(GRB.IntAttr.ModelSense, -1);
	}

}
