
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import problems.gvrp.GVRP_Inverse;
import problems.gvrp.analyzer.Analyzer;
import problems.gvrp.constructive_heuristic.MCWS;
import problems.gvrp.constructive_heuristic.ShortestPaths;
import problems.gvrp.instances.InstancesGenerator;
import solutions.Solution;

public class Main {
	
	public static void main(String[] args) throws IOException {
//		instancesGenerator();
//		System.exit(0);
		String[] gvrpInstances = new String[] {
//			"A-n32-k5.vrp.gvrp",
//			"A-n33-k5.vrp.gvrp",
//			"A-n33-k6.vrp.gvrp",
//			"A-n34-k5.vrp.gvrp",
//			"A-n36-k5.vrp.gvrp",
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
		for (int i = 0; i < gvrpInstances.length; i++) {	
			GVRP_Inverse gvrp = new GVRP_Inverse("CVRP Instances/"+gvrpInstances[i]);
			
//			System.out.println(0+ " " + gvrp.nodesCoordinates.get(0)[0] + " " + gvrp.nodesCoordinates.get(0)[1]);
//			
//			List<Integer> customers = new ArrayList<Integer> (gvrp.customersDemands.keySet());
//			Collections.sort(customers);
//			for (Integer customer: customers) {
//				System.out.println(customer + " " + gvrp.nodesCoordinates.get(customer)[0] + " " + gvrp.nodesCoordinates.get(customer)[1]);
//			}
//			
//			
//			List<Integer> afss = new ArrayList<Integer> (gvrp.rechargeStationsRefuelingTime.keySet());
//			Collections.sort(afss);
//			for (Integer afs: afss) {
//				System.out.println(afs+ " " + gvrp.nodesCoordinates.get(afs)[0] + " " + gvrp.nodesCoordinates.get(afs)[1]);
//			}
			
//			System.out.println(gvrp.toString());
			ShortestPaths sp = new ShortestPaths(); 
			sp.construct(gvrp);
//			break;
		}
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
//				"A-n32-k5.vrp",
//				"A-n33-k5.vrp",
//				"A-n33-k6.vrp",
//				"A-n34-k5.vrp",
//				"A-n36-k5.vrp",
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
	
}
