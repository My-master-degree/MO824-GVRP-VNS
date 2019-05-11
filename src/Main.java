
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import problems.gvrp.GVRP_Inverse;
import problems.gvrp.analyzer.Analyzer;
import problems.gvrp.constructive_heuristic.NearestNeighborhood;
import problems.gvrp.instances.InstancesGenerator;
import problems.qbf.solvers.VNS_GVRP;
import solutions.Solution;

import static java.util.stream.Collectors.*;

import static java.util.Map.Entry.*;


public class Main {
	
	public static void generateInstance()  throws IOException {
		Integer[] numbersOfCustomers = new Integer[] {80};
		for (int i = 0; i < numbersOfCustomers.length; i++) {
//			params
			Integer numOfCustomers = numbersOfCustomers[i];
			Integer numOfAFSs = Math.floorDiv(numOfCustomers, 2);
			Integer planeDimension = numOfCustomers * 30;
			Double vehicleAutonomy = numOfCustomers * 1.5;
			Double vehicleCapacity = numOfCustomers * 2.25;
//			rels
			Map<InstancesGenerator.Node, Integer> nodes = 
					InstancesGenerator.generate(numOfCustomers, numOfAFSs, planeDimension, vehicleAutonomy, vehicleCapacity)
					.entrySet().stream().sorted(comparingByValue())
					.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
			String content = "NUM_CUSTOMERS="+numOfCustomers+"\nNUM_AFSs="+numOfAFSs+"\nVEHICLE_AUTONOMY="+vehicleAutonomy+"\n"+
					"VEHICLE_CAPACITY="+vehicleCapacity+"\n";
			Integer afssUsed = 0;
			for (InstancesGenerator.Node node : nodes.keySet()) {
				Integer id = nodes.get(node);
				if (id == 0)
					content += "DEPOT=" + node.x + "," + node.y + "\n";
				else if (id > 0 && id < numOfCustomers + 1)
					content += "CUSTOMER_"+id+"=" + node.x + "," + node.y + " " + node.demand + "\n";
				else {
					content += "AFS_"+id+"=" + node.x + "," + node.y + "\n";
					afssUsed++;
				}
			}		
		    BufferedWriter writer = new BufferedWriter(new FileWriter("customers"+numOfCustomers+"afss"+
			Math.max(afssUsed, numOfAFSs)+"autonomy"+vehicleAutonomy+"capacity"+vehicleCapacity+".txt"));
		    writer.write(content);
		     
		    writer.close();
		}		
	}
	
	public static void main(String[] args) throws IOException {
		GVRP_Inverse gvrp = new GVRP_Inverse("instances/S1_20c3sU1.txt");
		Solution<List<Integer>> sol = NearestNeighborhood.construct(gvrp);
		
		Analyzer.analyze(sol, gvrp);
	}
	
}
