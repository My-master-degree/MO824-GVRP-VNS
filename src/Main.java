
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.analyzer.Analyzer;
import problems.gvrp.constructive_heuristic.MCWS;
import problems.gvrp.constructive_heuristic.ShortestPaths;
import problems.gvrp.instances.InstancesGenerator;
import problems.qbf.solvers.Gurobi_GVRP;
import solutions.Solution;

public class Main {
	
	private static final double TIME_LIMIT_GUROBI = 600;

	public static void main(String[] args) throws IOException {
//		instancesGenerator();
//		System.exit(0);
		String[] gvrpInstances = new String[] {
			"A-n05-k2.vrp.gvrp",				
			"A-n06-k2.vrp.gvrp",
			"A-n07-k3.vrp.gvrp",
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
		runGurobi(gvrpInstances);		
//		runShortestPaths(gvrpInstances);
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
		for (String[] s: new String[][] {s1instances, s2instances, s3instances, s4instances, largeInstances, })
			stream = Stream.concat(stream, Arrays.stream(s));
		String[] instances = stream.toArray(String[]::new);		
		
		for (int i = 0; i < instances.length; i++) {
			System.out.print(instances[i]+": ");
			GVRP_Inverse gvrp = new GVRP_Inverse("instances/"+instances[i]);
			
			Solution<List<Integer>> sol = MCWS.construct(gvrp);
			System.out.println(gvrp.toString());
			Util.printGVRPSolutionDistance(sol, gvrp);
			Analyzer.analyze(sol, gvrp);		
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
				gurobi = new Gurobi_GVRP("CVRP Instances/"+instances[i]);
				gurobi.TIME_LIMIT_GUROBI = TIME_LIMIT_GUROBI; 
				Solution<List<Integer>> sol = gurobi.run();
				if (sol != null) {
	//				write routes
					BufferedWriter writer = new BufferedWriter(new FileWriter("results/"+instances[i]));
					String str = "";
					for (List<Integer> list : sol) {
						for (Integer integer : list) {
							System.out.print(integer + ",");
							str += integer + ",";							
						}
						str += "\n";
						System.out.println();
					}
					Analyzer.analyze(sol, gurobi.problem);
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
				gvrp = new GVRP_Inverse("CVRP Instances/"+instances[i]);
				Solution<List<Integer>> sol = sp.construct(gvrp);
				for (List<Integer> list : sol) {
					for (Integer integer : list) {
						System.out.print(integer + ",");
					}
					System.out.println(": "+gvrp.getFuelConsumption(list) + " " + gvrp.getTimeConsumption(list));
				}				
				Analyzer.analyze(sol, gvrp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 						
//			break;
		}		
	}
	
}
