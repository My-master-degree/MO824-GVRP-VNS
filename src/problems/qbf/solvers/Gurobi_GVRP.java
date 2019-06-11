package problems.qbf.solvers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import problems.gvrp.GVRP;
import problems.gvrp.constructive_heuristic.ShortestPaths;
import problems.gvrp.instances.Util;
import solutions.Solution;

public class Gurobi_GVRP {
	public GRBEnv env;
	public GRBModel model;
	public GRBVar[][] x;
	public GRBVar[] y;
	public GRBVar[] t;
	public GVRP problem;
	public Map<Integer, Double> nodesShortestPathFuelConsumption;
	public Map<Integer, Double> nodesShortestPathTimeConsumption;
	public double TIME_LIMIT_GUROBI;

	public Gurobi_GVRP(String filename) throws IOException {
		this.problem = new GVRP(filename);
		this.nodesShortestPathFuelConsumption = new HashMap<Integer, Double> ();
		this.nodesShortestPathTimeConsumption = new HashMap<Integer, Double> ();
		this.TIME_LIMIT_GUROBI = 120;
//		afss
		List<Stack<Integer>> afsPaths = Util.gvrpFromDepotToAFSDijkstra(problem);
		for (Stack<Integer> path : afsPaths) {
			for (Integer afs : problem.rechargeStationsRefuelingTime.keySet()) {
				if (path.get(0).equals(afs)) {
					this.nodesShortestPathFuelConsumption.put(afs, problem.distanceMatrix[afs][path.get(1)] * problem.vehicleConsumptionRate);					
//					this.nodesShortestPathFuelConsumption.put(afs, problem.getFuelConsumption(path));
					this.nodesShortestPathTimeConsumption.put(afs, problem.getTimeConsumption(path));
					break;
				}
			}
		}
//		customers
		List<Stack<Integer>> customersPaths = Util.gvrpCustomersDijkstra(problem);
		for (Stack<Integer> path : customersPaths) {
			for (Integer customer : problem.customersDemands.keySet()) {
				if (path.get(0).equals(customer)) {
					this.nodesShortestPathFuelConsumption.put(customer, problem.distanceMatrix[customer][path.get(1)] * problem.vehicleConsumptionRate);
//					this.nodesShortestPathFuelConsumption.put(customer, problem.getFuelConsumption(path));
					this.nodesShortestPathTimeConsumption.put(customer, problem.getTimeConsumption(path));
					
					break;
				}
			}
		}
	}	

	private int getIthVisitOfAFS(int i, int afs) {
//		return problem.size + (i - 1)* (problem.size - problem.customersSize + 1) + (afs%problem.customersSize) + 1;
		return problem.size + (i - 1)* (problem.size - problem.customersSize -1) + (afs%(problem.customersSize + 1));
	}
	
	private int getAFSByVisit(int i) {
		return ((i - problem.size)%(problem.size - problem.customersSize - 1)) + problem.customersSize + 1;
	}
	
	public void populateNewModel(GRBModel model) throws GRBException {
//		params
		int numberOfDummies = problem.customersSize;
//		extend graph
		List<Integer> v_line = new ArrayList<Integer>(problem.rechargeStationsSize * problem.customersSize);
		v_line.add(0);		
		v_line.addAll(problem.customersDemands.keySet());
		v_line.addAll(problem.rechargeStationsRefuelingTime.keySet());
//		add dummies
		for (Integer afs: problem.rechargeStationsRefuelingTime.keySet()) {
			for (int i = 1; i <= numberOfDummies; i++) {
				v_line.add(this.getIthVisitOfAFS(i, afs));
			}
		}
		Collections.sort(v_line);
//		for (Integer integer : v_line) {
//			if (integer >= problem.size) {
//				System.out.println(integer + ": "+ getAFSByVisit(integer));
//			}else
//				System.out.println(integer);
//		}
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
			t[i] = model.addVar(0, problem.vehicleOperationTime, 0.0f, GRB.CONTINUOUS, "t[" + i + "]");			
		}
		model.update();	
		// objective function
//		\sum_{i,j \in V' (i != j)} d_{ij} x_{ij} \forall i \in I
		GRBLinExpr obj = new GRBLinExpr();
		for (int i = 0; i < this.x.length; i++) {
			for (int j = 0; j < this.x.length; j++) {
				if (i != j) {
					int j_line = j >= problem.size ? this.getAFSByVisit(j) : j;
					int i_line = i >= problem.size ? this.getAFSByVisit(i) : i;
					obj.addTerm(problem.distanceMatrix[i_line][j_line], x[i][j]);
				}
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
//		constraint (5): \\sum_{j \\in V' (j != 0)} x_{j 0} \\leq |C|
		GRBLinExpr expr = new GRBLinExpr();
		for (Integer j : v_line) {
			if (!j.equals(0))
				expr.addTerm(1.0, x[j][0]);				
		}
		model.addConstr(expr, GRB.LESS_EQUAL, problem.customersSize, "\\sum_{j \\in V' (j != 0)} x_{j 0} \\leq |C|");
//		constraint (6): \\sum_{j \\in V' (j != 0)} x_{0 j} \\leq |C|
		expr = new GRBLinExpr();
		for (Integer j : v_line) {
			if (!j.equals(0))
				expr.addTerm(1.0, x[0][j]);				
		}
		model.addConstr(expr, GRB.LESS_EQUAL, problem.customersSize, "\\sum_{j \\in V' (j != 0)} x_{0 j} \\leq |C|");
//		constraint (7): \tau_j \geq \tau_i + (t_{ij} - p_j)x_{ij} - T_{max}(1-x_{ij}) i \in V', j \in V' \{0} and i != j  
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
					int j_line = j >= problem.size ? this.getAFSByVisit(j) : j;
					int i_line = i >= problem.size ? this.getAFSByVisit(i) : i;
					Double serviceTime = problem.rechargeStationsRefuelingTime.get(j_line) != null ? 
							problem.rechargeStationsRefuelingTime.get(j_line) : problem.customersServiceTime.get(j_line);
					rightExpr.addTerm(problem.timeMatrix[i_line][j_line] - serviceTime, x[i][j]);
					rightExpr.addConstant(-problem.vehicleOperationTime);
					rightExpr.addTerm(problem.vehicleOperationTime, x[i][j]);
					model.addConstr(leftExpr, GRB.GREATER_EQUAL, rightExpr, 
						"\\tau_"+j+" \\geq \\tau_"+i+" + ("+(problem.timeMatrix[i_line][j_line] - serviceTime)+") x_{"+i+" "+j+"} - "+problem.vehicleOperationTime+" (1-x_{"+i+" "+j+"})");					
				}
			}
		}
//		constraint (8): 0 \leq \tau_0 \leq T_{max} 
		GRBLinExpr tau_0 = new GRBLinExpr();
		tau_0.addTerm(1d, t[0]);				
		model.addConstr(tau_0, GRB.GREATER_EQUAL, 0, "\\tau_0 \\geq 0");
		model.addConstr(tau_0, GRB.LESS_EQUAL, problem.vehicleOperationTime, "\\tau_0 \\leq "+problem.vehicleOperationTime);
//		constraint (9): t_{p_{0j}} \leq \tau_{j} \leq T_{max} - t_{p_{j0}} \forall j \in V' \ {0}
		for (Integer j : v_line) {
			if (!j.equals(0)) {
				int j_line = j >= problem.size ? this.getAFSByVisit(j) : j;
				GRBLinExpr tau_j = new GRBLinExpr();
				tau_j.addTerm(1d, t[j]);				
				Double serviceTime = problem.rechargeStationsRefuelingTime.get(j_line) != null ? 
						problem.rechargeStationsRefuelingTime.get(j_line) : problem.customersServiceTime.get(j_line);
				Double time = this.nodesShortestPathTimeConsumption.get(j_line);
//				System.out.println((time - serviceTime) + "\\leq tau_"+j + " \\leq "+ (problem.vehicleOperationTime - time - serviceTime));
				model.addConstr(tau_j, GRB.GREATER_EQUAL, time, "\\tau_"+j+" \\geq "+time);
//				get correspondent afs node			
				model.addConstr(tau_j, GRB.LESS_EQUAL, problem.vehicleOperationTime - time, 
					"\\tau_"+j+" \\geq "+(problem.vehicleOperationTime - time));
			}
		}
//		constraint (10): y_j \leq y_i - d_{ij} x{ij} + Q(1 - x_{ij}) \forall j \in I and  i \in V', i != j
		for (Integer j : problem.customersDemands.keySet()) {			
			for (Integer i : v_line) {
				if (!j.equals(i)) {
					int i_line = i >= problem.size ? this.getAFSByVisit(i) : i;
					GRBLinExpr left = new GRBLinExpr();
					left.addTerm(1d, y[j]);
					GRBLinExpr right = new GRBLinExpr();
					right.addTerm(1d, y[i]);
//					right.addTerm(-problem.getFuelConsumption(i_line, j), x[i][j]);
					right.addTerm(-problem.distanceMatrix[i_line][j] * problem.vehicleConsumptionRate, x[i][j]);
					right.addConstant(problem.vehicleAutonomy);
					right.addTerm(-problem.vehicleAutonomy, x[i][j]);
					model.addConstr(left, GRB.LESS_EQUAL, right, 
						"y_"+j+" \\leq y_"+i+" - "+ (problem.distanceMatrix[i_line][j] * problem.vehicleConsumptionRate) + " x{"+i+" "+j+"} + "+problem.vehicleAutonomy+" (1 - x_{"+i+" "+j+"})");
					model.addConstr(left, GRB.GREATER_EQUAL, 0, 
							"y_"+j+" \\geq 0");
				}
			}
		}
//		constraint (11): y_j = Q \forall j \in F_0						
		for (Integer j : v_line) {
			if (j.equals(0) || j > problem.customersSize) {
				expr = new GRBLinExpr();
				expr.addTerm(1, y[j]);
				model.addConstr(expr, GRB.EQUAL, problem.vehicleAutonomy, "y_"+j+" = "+problem.vehicleAutonomy);
			}
		}
//		constraint (12): y_j \geq d_{p(j, 0)} \forall j \in I
		for (Integer j : problem.customersDemands.keySet()) {			
			expr = new GRBLinExpr();
			expr.addTerm(1, y[j]);
			model.addConstr(expr, GRB.GREATER_EQUAL, this.nodesShortestPathFuelConsumption.get(j),
				"y_"+j+" \\geq "+this.nodesShortestPathFuelConsumption.get(j));			
		}	
//		constraint (13): \sum_{i \in V'} x_{ij} \geq \sum_{i \in V'} x_{i n(j)} \forall j \in F \cup \phi		
//		for (Integer j : problem.rechargeStationsRefuelingTime.keySet()) {
//			GRBLinExpr left = new GRBLinExpr();
//			GRBLinExpr right = new GRBLinExpr();
//			int kThVisit = j;
//			int kThPlusOneThVisit = getIthVisitOfAFS(1, j);
//			for (Integer i : v_line) {				
//				if (!i.equals(kThVisit)) 
//					left.addTerm(1d, x[i][kThVisit]);				
//				if (!i.equals(kThPlusOneThVisit)) 
//					right.addTerm(1d, x[i][kThPlusOneThVisit]);				
//			}				
//			model.addConstr(left, GRB.GREATER_EQUAL, right, 
//				"\\sum_{i \\in V'} x_{i "+j+"} \\geq \\sum_{i \\in V'} x_{i "+kThPlusOneThVisit+"}");			
//			for (int k = 1; k < problem.customersSize - 1; k++) {
//				kThVisit = getIthVisitOfAFS(k, j);
//				kThPlusOneThVisit = getIthVisitOfAFS(k + 1, j);			
//				left = new GRBLinExpr();
//				right = new GRBLinExpr();
//				for (Integer i : v_line) {		
//					if (!i.equals(kThVisit)) 
//						left.addTerm(1d, x[i][kThVisit]);
//					if (!i.equals(kThPlusOneThVisit)) 
//						right.addTerm(1d, x[i][kThPlusOneThVisit]);
//				}
//				model.addConstr(left, GRB.GREATER_EQUAL, right, 
//					"\\sum_{i \\in V'} x_{i "+kThVisit+"} \\geq \\sum_{i \\in V'} x_{i "+kThPlusOneThVisit+"}");	
//			}		
//							
//		}	
		for (int k = problem.customersSize + 1; k < v_line.size(); k++) {		
			expr = new GRBLinExpr();
			expr.addTerm(1, x[k][k]);
			model.addConstr(expr, GRB.EQUAL, 0,
				"x_{"+k+" "+k+"} = 0 ");
		}	
		for (Integer afs : problem.rechargeStationsRefuelingTime.keySet()) {
			for (int j = 1; j <= numberOfDummies; j++) {
				int visit = getIthVisitOfAFS(j, afs);
				expr = new GRBLinExpr();
				expr.addTerm(1, x[afs][visit]);
				model.addConstr(expr, GRB.EQUAL, 0,
					"x_{"+afs+" "+visit+"} = 0 ");
				expr = new GRBLinExpr();
				expr.addTerm(1, x[visit][afs]);
				model.addConstr(expr, GRB.EQUAL, 0,
					"x_{"+visit+" "+afs+"} = 0 ");
				for (int i = j + 1; i <= numberOfDummies; i++) {
					int _visit = getIthVisitOfAFS(i, afs);
					expr = new GRBLinExpr();
					expr.addTerm(1, x[_visit][visit]);
					model.addConstr(expr, GRB.EQUAL, 0,
						"x_{"+_visit+" "+visit+"} = 0 ");
					expr = new GRBLinExpr();
					expr.addTerm(1, x[visit][_visit]);
					model.addConstr(expr, GRB.EQUAL, 0,
						"x_{"+visit+" "+_visit+"} = 0 ");
				}				
			}	
		}	
		for (Integer i : v_line) {
			for (Integer j : v_line) {
				int j_line = j >= problem.size ? this.getAFSByVisit(j) : j;
				int i_line = i >= problem.size ? this.getAFSByVisit(i) : i;
				if (problem.distanceMatrix[i_line][j_line] * problem.vehicleConsumptionRate > problem.vehicleAutonomy) {
					expr = new GRBLinExpr();
					expr.addTerm(1, x[i][j]);
					model.addConstr(expr, GRB.EQUAL, 0,
						"x_{"+i+" "+j+"} = 0 ");
				}
			}			
		}
//		setup
		model.setObjective(obj);
		model.update();
		// maximization objective function
		model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
	}

	public Solution<List<Integer>> run() throws GRBException, IOException{				
		this.env = new GRBEnv();
		this.model = new GRBModel(this.env);
		// execution time in seconds 
		this.model.getEnv().set(GRB.DoubleParam.TimeLimit, this.TIME_LIMIT_GUROBI);
		this.model.getEnv().set(GRB.IntParam.OutputFlag, 1);		  
		// generate the model
		this.populateNewModel(this.model);
		// write model to file
		this.model.write("model.lp");
		long time = System.currentTimeMillis();
		this.model.optimize();
		try { 			  		
			time = System.currentTimeMillis() - time;
	//		System.out.println("\n\nZ* = " + this.model.get(GRB.DoubleAttr.ObjVal));
			String str = "Z* = " + this.model.get(GRB.DoubleAttr.ObjVal)+"\n";
	//		System.out.println("\nTime = "+time);
			str += "Time = " + time +"\n";
	//		System.out.println("\nGAP = "+this.model.get(GRB.DoubleAttr.MIPGap));
			str += "GAP = " + this.model.get(GRB.DoubleAttr.MIPGap) +"\n";
	//		System.out.print("X = [");
			str += "X = [";
			for (int i = 0; i < this.problem.size; i++) {
				for (int j = 0; j < this.problem.size; j++) {
	//	          System.out.print(gurobi.x[j].get(GRB.DoubleAttr.X) + ", ");
		          str += this.x[i][j].get(GRB.DoubleAttr.X) + ", ";
				}
			}			
	//		System.out.println("]");
			str += "]\n";
	//		get routes
			System.out.print("   ");
			for (int i = 0; i < x.length; i++) {
				System.out.print(i + " ");
			}
			System.out.println();
			for (int i = 0; i < x.length; i++) {
				System.out.print(i + ": ");
				for (int j = 0; j < x[i].length; j++) {
					if (j > 9)
						System.out.print(" ");
					System.out.print((int) x[i][j].get(GRB.DoubleAttr.X) + " ");
				}
				System.out.println();
			}
					
			Solution<List<Integer>> routes = new Solution<List<Integer>>(); 
			List<Integer> route = new ArrayList<Integer> ();
			route.add(0);
			int i = 0, j = 0, I = 0;
			while (j < this.x.length) {
				if (this.x[i][j].get(GRB.DoubleAttr.X) == 1d) {
					route.add(j >= problem.size ? this.getAFSByVisit(j) : j);
					if (j == 0) {
						routes.add(route);
						route = new ArrayList<Integer> ();
						route.add(0);
						i = j;
						j = I;							
					}else {
						if (i == 0) {
							I = j + 1;						
						}
						i = j;
						j = 0;
					}
				}else {
					j++;
				}
			}		
	//		Write file			
		    BufferedWriter writer = new BufferedWriter(new FileWriter("results/"+problem.name));
		    writer.write(str);		     
		    writer.close();
		    this.model.dispose();
		    this.env.dispose();
		    return routes;
		} catch(Exception e) { 
			System.out.println("Infeasible model");
			return null;
		} 		
	}
	
}
