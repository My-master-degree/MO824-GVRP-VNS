
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import metaheuristics.vns.AbstractVNS;
import metaheuristics.vns.AbstractVNS.VNS_TYPE;
import metaheuristics.vns.LocalSearch;
import problems.gvrp.GVRP;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.Route;
import problems.gvrp.Routes;
import problems.gvrp.analyzer.Analyzer;
import problems.gvrp.constructive_heuristic.ShortestPaths;
import problems.gvrp.constructive_heuristic.ShortestPathsEMH;
import problems.gvrp.instances.EMHInstanceReader;
import problems.gvrp.instances.InstancesGenerator;
import problems.gvrp.instances.MyInstanceReader;
import problems.gvrp.local_searchs.FSDrop;
import problems.gvrp.local_searchs.InterTourVertexExchange;
import problems.gvrp.local_searchs.MergeRoutes;
import problems.gvrp.local_searchs.WithinTourTwoVertexInterchange;
import problems.qbf.solvers.Gurobi_GVRP;
import problems.qbf.solvers.Gurobi_GVRP_EMH;
import problems.qbf.solvers.VNS_GVRP;
import solutions.Solution;

public class Main {
	
	private static final Integer VNS_ITERATIONS_LIMIT = 1000000;
	private static final Integer VNS_TIME_MILLISECONDS_LIMIT = 600000;
	
	private static final Integer TIME_LIMIT_GUROBI = 1800;
//	private static final Integer TIME_LIMIT_GUROBI = 60;

	public static void main(String[] args) throws IOException {
//		instancesGenerator();
//		System.exit(0);
//		System.setProperty("java.library.path", "/home/matheusdiogenesandrade/gurobi811/linux64/lib/libGurobiJni81.so");
		String[] gvrpInstances = new String[] {
//			"A-n05-k2.vrp.gvrp",				
//			"A-n06-k2.vrp.gvrp",
//			"A-n07-k3.vrp.gvrp",
			"A-n32-k5.vrp.gvrp",
			"A-n33-k5.vrp.gvrp", 
			"A-n33-k6.vrp.gvrp",
			"A-n34-k5.vrp.gvrp",
			"A-n36-k5.vrp.gvrp",
			"A-n37-k5.vrp.gvrp",
			"A-n37-k6.vrp.gvrp",
			"A-n38-k5.vrp.gvrp",
			"A-n39-k5.vrp.gvrp",
			"A-n39-k6.vrp.gvrp",
			"A-n44-k7.vrp.gvrp",
			"A-n45-k6.vrp.gvrp",
			"A-n45-k7.vrp.gvrp",
			"A-n46-k7.vrp.gvrp",
			"A-n48-k7.vrp.gvrp",
			"A-n53-k7.vrp.gvrp",
			"A-n54-k7.vrp.gvrp",
			"A-n55-k9.vrp.gvrp",
			"A-n60-k9.vrp.gvrp",
			"A-n61-k9.vrp.gvrp",
			"A-n62-k8.vrp.gvrp",
			"A-n63-k9.vrp.gvrp",
			"A-n63-k10.vrp.gvrp",
			"A-n64-k9.vrp.gvrp",
			"A-n65-k9.vrp.gvrp",
			"A-n69-k9.vrp.gvrp",
			"A-n80-k10.vrp.gvrp",
		};
//		readErdoganInstances();
		runGurobi(gvrpInstances);		
//		runShortestPaths(gvrpInstances);
//		runLocalSearchs(gvrpInstances);
//		runVNS(gvrpInstances, AbstractVNS.VNS_TYPE.NONE);
//		runVNS(gvrpInstances, AbstractVNS.VNS_TYPE.INTENSIFICATION);
//		quickHotFix(gvrpInstances);
	}
	
	public static void readErdoganInstances() throws IOException {
		String[] s1instances =  new String[] {						
			"S1_20c3sU1.txt",
			"S1_20c3sU2.txt",
			"S1_20c3sU3.txt",
			"S1_20c3sU4.txt",
			"S1_20c3sU5.txt",
			"S1_20c3sU6.txt",
			"S1_20c3sU7.txt",
			"S1_20c3sU8.txt",
			"S1_20c3sU9.txt",
			"S1_20c3sU10.txt",
		},
		s2instances =  new String[] {						
			"S2_20c3sC1.txt",
			"S2_20c3sC2.txt",
			"S2_20c3sC3.txt",
			"S2_20c3sC4.txt",
			"S2_20c3sC5.txt",
			"S2_20c3sC6.txt",
			"S2_20c3sC7.txt",
			"S2_20c3sC8.txt",
			"S2_20c3sC9.txt",
			"S2_20c3sC10.txt",
		},
		s3instances =  new String[] {						
			"S3_S1_2i6s.txt",
			"S3_S1_4i6s.txt",
			"S3_S1_6i6s.txt",
			"S3_S1_8i6s.txt",
			"S3_S1_10i6s.txt",		
			"S3_S2_2i6s.txt",
			"S3_S2_4i6s.txt",
			"S3_S2_6i6s.txt",
			"S3_S2_8i6s.txt",
			"S3_S2_10i6s.txt",
		},
		s4instances =  new String[] {						
			"S4_S1_4i2s.txt",
			"S4_S1_4i4s.txt",
			"S4_S1_4i6s.txt",
			"S4_S1_4i8s.txt",
			"S4_S1_4i10s.txt",			
			"S4_S2_4i2s.txt",
			"S4_S2_4i4s.txt",
			"S4_S2_4i6s.txt",
			"S4_S2_4i8s.txt",
			"S4_S2_4i10s.txt",
		},
		largeInstances =  new String[] {			
			"Large_VA_Input_111c_21s.txt",
			"Large_VA_Input_111c_22s.txt",
			"Large_VA_Input_111c_24s.txt",
			"Large_VA_Input_111c_26s.txt",
			"Large_VA_Input_111c_28s.txt",
			"Large_VA_Input_200c_21s.txt",
			"Large_VA_Input_300c_21s.txt",
			"Large_VA_Input_350c_21s.txt",
			"Large_VA_Input_400c_21s.txt",
			"Large_VA_Input_450c_21s.txt",
			"Large_VA_Input_500c_21s.txt",	
		};
//		all instances
		Stream<String> stream = Stream.of();
//		for (String[] s: new String[][] {s1instances, s2instances, s3instances, s4instances, largeInstances, })
		for (String[] s: new String[][] {s1instances, s2instances, s3instances, s4instances, })
			stream = Stream.concat(stream, Arrays.stream(s));
		String[] instances = stream.toArray(String[]::new);		
		
		for (int i = 0; i < instances.length; i++) {
			System.out.print(instances[i]+": ");
			GVRP_Inverse gvrp = new GVRP_Inverse("instances/"+instances[i], new EMHInstanceReader());
//			List<LocalSearch<GVRP_Inverse, Routes>> localSearchs = new ArrayList<LocalSearch<GVRP_Inverse, Routes>>(); 
//			localSearchs.add(new InterTourVertexExchange());				
//			localSearchs.add(new MergeRoutes());
//			localSearchs.add(new FSDrop());
//			localSearchs.add(new WithinTourTwoVertexInterchange()); 				 			
//			Routes sol = new VNS_GVRP(
//					gvrp, 
//					Main.VNS_ITERATIONS_LIMIT, 
//					Main.VNS_TIME_MILLISECONDS_LIMIT, 
//					localSearchs, 
//					VNS_TYPE.INTENSIFICATION).solve();
			Routes sol = new ShortestPathsEMH().construct(gvrp);
			gvrp.evaluate(sol);
			Analyzer.analyze(sol, gvrp);
			System.out.println(Math.abs(sol.cost));	
			break;
		}		
	}
	
	public static void instancesGenerator() {
		String[] cvrpInstances = new String[]{
				"A-n05-k2.vrp",
				"A-n06-k2.vrp",
				"A-n07-k3.vrp",
				"A-n32-k5.vrp",
				"A-n33-k5.vrp",
				"A-n33-k6.vrp",
				"A-n34-k5.vrp",
				"A-n36-k5.vrp",
				"A-n37-k5.vrp",
				"A-n37-k6.vrp",
				"A-n38-k5.vrp",
				"A-n39-k5.vrp",
				"A-n39-k6.vrp",
				"A-n44-k7.vrp",
				"A-n45-k6.vrp",
				"A-n45-k7.vrp",
				"A-n46-k7.vrp",
				"A-n48-k7.vrp",
				"A-n53-k7.vrp",
				"A-n54-k7.vrp",
				"A-n55-k9.vrp",
				"A-n60-k9.vrp",
				"A-n61-k9.vrp",
				"A-n62-k8.vrp",
				"A-n63-k10.vrp",
				"A-n63-k9.vrp",
				"A-n64-k9.vrp",
				"A-n65-k9.vrp",
				"A-n69-k9.vrp",
				"A-n80-k10.vrp",
			};
			
			
			for (String cvrpInstance: cvrpInstances) {
				System.out.println(cvrpInstance);
				Pattern p = Pattern.compile("(\\d+)");
				Matcher m = p.matcher(cvrpInstance); 
				m.find();			
				m.find();			
				InstancesGenerator.verbose = false;
				InstancesGenerator.generate("CVRP Instances/"+cvrpInstance, Integer.valueOf(m.group()));
//				break;
			}		
	}
	
	public static void runGurobi(String[] instances) {		
//		Linear version
		for (int i = 0; i < instances.length; i++) {
			// instance name
			Gurobi_GVRP gurobi;
			try {
				gurobi = new Gurobi_GVRP("CVRP Instances/"+instances[i], new MyInstanceReader());
				gurobi.TIME_LIMIT_GUROBI = TIME_LIMIT_GUROBI; 
				Routes sol = gurobi.run();
				if (sol != null) {
	//				write routes
					BufferedWriter writer = new BufferedWriter(new FileWriter("results/"+instances[i]));
					String str = "";
					for (Route list : sol) {
						for (Integer integer : list) {
							System.out.print(integer + ",");
							str += integer + ",";							
						}
						str += "\n";
																																																																												
						System.out.println();
					}
					str += "\n"+Analyzer.analyze(sol, gurobi.problem);
					writer.write(str);		     
				    writer.close();
				}
			} catch (IOException | GRBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
//			break;
		}		
	}
	
	public static void runShortestPaths(String[] instances) {		
//		Linear version
		for (int i = 0; i < instances.length; i++) {
			System.out.println(instances[i]);
			// instance name
			ShortestPaths sp = new ShortestPaths();
			GVRP_Inverse gvrp;
			try {
				gvrp = new GVRP_Inverse("CVRP Instances/"+instances[i], new MyInstanceReader());				
				Routes sol = sp.construct(gvrp);
				for (Route list : sol) {	
					if (list.size() > 3) {
						System.out.println("FOund");
					}
					for (Integer integer : list) {
//						System.out.print(integer + ",");
					}
//					System.out.println(": "+gvrp.getFuelConsumption(list) + " " + gvrp.getTimeConsumption(list));
				}
				
				Analyzer.analyze(sol, gvrp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 						
//			break;
		}		
	}
	
	public static void runLocalSearchs(String[] instances) {		
//		Linear version
		ShortestPaths sp = new ShortestPaths();
		InterTourVertexExchange itve = new InterTourVertexExchange();
		WithinTourTwoVertexInterchange wttve = new WithinTourTwoVertexInterchange();
		MergeRoutes mr = new MergeRoutes();
		FSDrop fsd = new FSDrop(); 
		for (int i = 0; i < instances.length; i++) {
			System.out.println(instances[i]);
			// instance name			
			GVRP_Inverse gvrp;
			try {
				gvrp = new GVRP_Inverse("CVRP Instances/"+instances[i], new MyInstanceReader());
//				ShortestPaths
				Routes sol = sp.construct(gvrp);
				gvrp.evaluate(sol);
				Analyzer.analyze(sol, gvrp);
				System.out.println(sol.cost);				
//				for (Route list : sol) {
//					for (Integer integer : list) {
//						System.out.print(integer + ",");
//					}
//					System.out.println(": "+gvrp.getFuelConsumption(list) + " " + gvrp.getTimeConsumption(list));
//				}
//				InterTourVertexExchange
//				sol = itve.localOptimalSolution(gvrp, sol);
//				gvrp.evaluate(sol);
//				Analyzer.analyze(sol, gvrp);
//				System.out.println(sol.cost);		
//				WithinTourTwoVertexInterchange
//				sol = wttve.localOptimalSolution(gvrp, sol);				
//				gvrp.evaluate(sol);
//				Analyzer.analyze(sol, gvrp);
//				System.out.println(sol.cost);		
//				MergeRoutes
				sol = mr.localOptimalSolution(gvrp, sol);
				gvrp.evaluate(sol);
				Analyzer.analyze(sol, gvrp);
				System.out.println(sol.cost);
//				FSDrop
				sol = fsd.localOptimalSolution(gvrp, sol);
				gvrp.evaluate(sol);
				Analyzer.analyze(sol, gvrp);
				System.out.println(sol.cost);	
			} catch (IOException e1) {
				e1.printStackTrace();
			} 						
//			break;
		}		
	}
	
	public static void runVNS(String[] instances, AbstractVNS.VNS_TYPE vnsType) {		
//		Linear version
		for (int i = 0; i < instances.length; i++) {
//			System.out.println(instances[i]);
			// instance name						
			try {
				List<LocalSearch<GVRP_Inverse, Routes>> localSearchs = new ArrayList<LocalSearch<GVRP_Inverse, Routes>>(); 
				localSearchs.add(new InterTourVertexExchange());				
				localSearchs.add(new MergeRoutes());
				localSearchs.add(new FSDrop());
				localSearchs.add(new WithinTourTwoVertexInterchange()); 				 			
				GVRP_Inverse gvrp = new GVRP_Inverse("CVRP Instances/"+instances[i], new MyInstanceReader());
				Routes sol = new VNS_GVRP(
						gvrp, 
						Main.VNS_ITERATIONS_LIMIT, 
						Main.VNS_TIME_MILLISECONDS_LIMIT, 
						localSearchs, 
						vnsType).solve();						
				gvrp.evaluate(sol);
				Analyzer.analyze(sol, gvrp);
				System.out.println(Math.abs(sol.cost));	
				
//				write
				BufferedWriter writer = new BufferedWriter(new FileWriter("results/"+vnsType.type+"/"+instances[i]));
				String str = "Cost:" + Math.abs(sol.cost) +"\nRoutes:\n";
				for (Route list : sol) {
					for (Integer integer : list) {
						str += integer + ",";							
					}
					str += "\n";
				}
				writer.write(str);		     
			    writer.close();
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			} 						
//			break;
		}		
	}
	
	public static void quickHotFix(String[] instances) {		
//		Linear version
		String[] vns_costs = new String[] {
			"266", 
			"263", 
			"259", 
			"589", 
			"544", 
			"769", 
			"601", 
			"649", 
			"742", 
			"796", 
			"656", 
			"751", 
			"758", 
			"750", 
			"781", 
			"777", 
			"802", 
			"842", 
			"817", 
			"805", 
			"1070", 
			"758", 
			"987", 
			"1075", 
			"980", 
			"981", 
			"833", 
			"986", 
		}; 		
		String[] vns_intensification = new String[] {
			"266",
			"263", 
			"259", 
			"623", 
			"620", 
			"637", 
			"732", 
			"623", 
			"699", 
			"783", 
			"717", 
			"681", 
			"1009", 
			"782", 
			"840", 
			"768", 
			"738", 
			"792", 
			"773", 
			"863", 
			"829", 
			"741", 
			"972", 
			"1083", 
			"792", 
			"987", 
			"872", 
			"875", 	
		};
		String[] exact = new String[] {
			"266", 
			"263", 
			"259", 
			"538", 
			"483", 
			"501", 
			"518", 
			"521", 
			"581", 
			"475", 
			"590", 
			"551", 
			"681", 
			"635", 
			"562", 
			"613", 
			"673", 
			"670", 
			"654", 
			"662", 
			"706", 
			"621", 
			"852", 
			"953", 
			"784", 
			"723", 
			"740", 
			"786", 
		};
		
		Double[] gaps = new Double[] {
		0.0
		,0.0
		,0.0
		,0.2379182156133829
		,0.10559006211180125
		,0.14570858283433133
		,0.0888030888030888
		,0.0345489443378119
		,0.17728055077452667
		,0.07368421052631567
		,0.15254237288135594
		,0.07622504537205081
		,0.22320117474302498
		,0.1921259842519685
		,0.18505338078291814
		,0.1598694942903752
		,0.23922734026745915
		,0.21791044776119403
		,0.20948012232415902
		,0.2854984894259819
		,0.3002832861189802
		,0.21739130434782591
		,0.37910798122065725
		,0.40083945435466756
		,0.33418367346938777
		,0.2544951590594744
		,0.2864864864864788
		,0.20229007633587787

		};
		System.out.println(gaps.length + " " + vns_intensification.length + " " + vns_costs.length);
		for (int i = 0; i < instances.length; i++) {
//			System.out.println(instances[i]);
			// instance name									 			
			try {
				GVRP_Inverse gvrp = new GVRP_Inverse("CVRP Instances/"+instances[i], new MyInstanceReader());				
				String[] part = instances[i].split(".vrp.gvrp");
				System.out.print(gvrp.customersSize + " & " + gvrp.rechargeStationsSize + " & "+ gvrp.vehicleAutonomy +" &  & ");
				Double gap = gaps[i];
				gap = gap * 1000;
				long r =  Math.round(gap);
				System.out.print((double )r/1000 + " & "+exact[i] + " &  & "+vns_costs[i] + " &  & "+vns_intensification[i]);
				System.out.println("\\");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
//			break;
		}		
	}

}
