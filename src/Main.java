
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import metaheuristics.vns.AbstractVNS.NeighborhoodStructure;
import problems.gvrp.GVRP_Inverse;
import problems.gvrp.analyzer.Analyzer;
import problems.gvrp.constructive_heuristic.MCWS;
import problems.gvrp.constructive_heuristic.NearestNeighborhood;
import problems.gvrp.constructive_heuristic.NearestNeighborhood.CustomerPair;
import problems.gvrp.instances.InstancesGenerator;
import problems.qbf.solvers.VNS_GVRP;
import solutions.Solution;

import static java.util.stream.Collectors.*;

import static java.util.Map.Entry.*;


public class Main {
	
//	public static void generateInstance()  throws IOException {
//		Integer[] numbersOfCustomers = new Integer[] {80};
//		for (int i = 0; i < numbersOfCustomers.length; i++) {
////			params
//			Integer numOfCustomers = numbersOfCustomers[i];
//			Integer numOfAFSs = Math.floorDiv(numOfCustomers, 2);
//			Integer planeDimension = numOfCustomers * 30;
//			Double vehicleAutonomy = numOfCustomers * 1.5;
//			Double vehicleCapacity = numOfCustomers * 2.25;
////			rels
//			Map<InstancesGenerator.Node, Integer> nodes = 
//					InstancesGenerator.generate(numOfCustomers, numOfAFSs, planeDimension, vehicleAutonomy, vehicleCapacity)
//					.entrySet().stream().sorted(comparingByValue())
//					.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
//			String content = "NUM_CUSTOMERS="+numOfCustomers+"\nNUM_AFSs="+numOfAFSs+"\nVEHICLE_AUTONOMY="+vehicleAutonomy+"\n"+
//					"VEHICLE_CAPACITY="+vehicleCapacity+"\n";
//			Integer afssUsed = 0;
//			for (InstancesGenerator.Node node : nodes.keySet()) {
//				Integer id = nodes.get(node);
//				if (id == 0)
//					content += "DEPOT=" + node.x + "," + node.y + "\n";
//				else if (id > 0 && id < numOfCustomers + 1)
//					content += "CUSTOMER_"+id+"=" + node.x + "," + node.y + " " + node.demand + "\n";
//				else {
//					content += "AFS_"+id+"=" + node.x + "," + node.y + "\n";
//					afssUsed++;
//				}
//			}		
//		    BufferedWriter writer = new BufferedWriter(new FileWriter("customers"+numOfCustomers+"afss"+
//			Math.max(afssUsed, numOfAFSs)+"autonomy"+vehicleAutonomy+"capacity"+vehicleCapacity+".txt"));
//		    writer.write(content);
//		     
//		    writer.close();
//		}		
//	}
	
	public static void main(String[] args) throws IOException {
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
		
//		String[] instances = s3instances;
		
//		for (int i = 0; i < instances.length; i++) {
//			System.out.print(instances[i]+": ");
//			GVRP_Inverse gvrp = new GVRP_Inverse("instances/"+instances[i]);
			
//			Solution<List<Integer>> sol = MCWS.construct(gvrp);
//			System.out.println(gvrp.toString());
//			Util.printGVRPSolutionDistance(sol, gvrp);
//			Analyzer.analyze(sol, gvrp);		
//			break;		
//		}		
		String[] cvrpInstances = new String[]{				
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
			InstancesGenerator.generate("CVRP Instances/"+cvrpInstance, Integer.valueOf(m.group()));
//			break;
		}		
		
		String[] gvrpInstances = new String[] {
			"A-n54-k7.vrp.gvrp",
			"A-n60-k9.vrp.gvrp",
			"A-n65-k9.vrp.gvrp",
			"A-n33-k5.vrp.gvrp",
			"A-n62-k8.vrp.gvrp",
			"A-n39-k6.vrp.gvrp",
			"A-n38-k5.vrp.gvrp",
			"A-n36-k5.vrp.gvrp",
			"A-n39-k5.vrp.gvrp",
			"A-n32-k5.vrp.gvrp",
			"A-n37-k5.vrp.gvrp",
			"A-n69-k9.vrp.gvrp",
			"A-n61-k9.vrp.gvrp",
			"A-n48-k7.vrp.gvrp",
			"A-n45-k7.vrp.gvrp",
			"A-n45-k6.vrp.gvrp",
			"A-n55-k9.vrp.gvrp",
			"A-n53-k7.vrp.gvrp",
			"A-n34-k5.vrp.gvrp",
			"A-n64-k9.vrp.gvrp",
			"A-n80-k10.vrp.gvrp",
			"A-n63-k9.vrp.gvrp",
			"A-n46-k7.vrp.gvrp",
			"A-n63-k10.vrp.gvrp",
			"A-n37-k6.vrp.gvrp",
			"A-n44-k7.vrp.gvrp",
			"A-n33-k6.vrp.gvrp",
		};
		for (int i = 0; i < gvrpInstances.length; i++) {	
			GVRP_Inverse gvrp = new GVRP_Inverse("CVRP Instances/"+gvrpInstances[i]);
			System.out.println(gvrp.toString());
			break;
		}
	}
	
}
