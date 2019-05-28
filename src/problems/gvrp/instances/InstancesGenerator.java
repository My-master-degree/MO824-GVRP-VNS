package problems.gvrp.instances;

import gurobi.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstancesGenerator {
	public static class Node{
		public int x;
		public int y;
		public double demand;
		public List<Node> neighborhoods;
		
		Node (int x, int y){
			this.x = x;
			this.y = y;
			this.neighborhoods = new ArrayList<Node> ();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
	
	private static double getDistance(Node a, Node b) {		
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
	}
	
	public static void generate(String fileName, int afssNumber, double vehicleAutonomy) {
		Map<Integer, Node> customerAndDepotDemands = new HashMap<Integer, InstancesGenerator.Node>();
		File file = new File(fileName); 		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));			
//			discard three first lines
			br.readLine();
			br.readLine();
			br.readLine();
//			get number of nodes
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(br.readLine()); 
			m.find();
			Integer numOfNodes = Integer.valueOf(m.group(0)); 
//			discard next line
			br.readLine();
//			get capacity
			m = p.matcher(br.readLine()); 
			m.find();
			Integer vehicleCapacity = Integer.valueOf(m.group(0));
//			discard next line
			br.readLine();
//			get nodes
			String st;
			while(true) {				
				st = br.readLine();
				m = p.matcher(st); 
				if (!m.find()) 
					break;	
				Integer id = Integer.valueOf(m.group());
				m.find();
				Integer x = Integer.valueOf(m.group());
				m.find();
				Integer y = Integer.valueOf(m.group());								
				customerAndDepotDemands.put(id, new Node(x, y));
			}
//			get demands
			while(true) {
				st = br.readLine();
				m = p.matcher(st);
				if (!m.find())
					break;
				Integer id = Integer.valueOf(m.group());
				m.find();									
				customerAndDepotDemands.get(id).demand = Double.valueOf(m.group());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 				
//		get graph dimension
		Integer maxX = Integer.MIN_VALUE,
				maxY = Integer.MIN_VALUE;
		for (Node node : customerAndDepotDemands.values()) {
			if (node.x > maxX)
				maxX = node.x;
			if (node.y > maxY)
				maxY = node.y;
		}
//		generate random afss
		Random random = new Random();	
		random.setSeed(System.currentTimeMillis());
		Set<Node> afss;
		while(true) {
			afss = new HashSet<Node> (afssNumber);
			afss.add(customerAndDepotDemands.get(1));
//			generate afss
			System.out.println("\tGenerating AFSs");
			for (int i = 0; i < afssNumber; i++) {
				Node afs = new Node (random.nextInt(maxX + 1), random.nextInt(maxY + 1));
				if (!afss.contains(afs)) 
					afss.add(afs);
				else
					i--;
			}
			System.out.println("\tAFSs generated");
//			check if each customers have at least one afs at a maximum of distance of \beta/2
			System.out.println("\tChecking if each customers have at least one afs at a maximum of distance of \\beta/2");
			boolean infeasibleInstance = false;
			for (Node b : customerAndDepotDemands.values()) {
				boolean afsCloserEnough = false;
				for (Node e : afss) {
					if (getDistance(b, e) <= vehicleAutonomy/2) {
						afsCloserEnough = true;
						break;
					}
				}
				if (!afsCloserEnough) {
					infeasibleInstance = true;
					break;
				}
			}			
			if (!infeasibleInstance) {
				System.out.println("\tCustomers not at good distance");
				continue;
			}
			System.out.println("\tCustomers at good distance");
//			generate tree
			System.out.println("\tChecking if each afs have at least one afs at a maximum of distance of \\beta");
			for (Node b : afss) {
				boolean afsCloserEnough = false;
				for (Node e : afss) {					
					if (!b.equals(e) && getDistance(b, e) <= vehicleAutonomy) {
						b.neighborhoods.add(e);
						e.neighborhoods.add(b);
						afsCloserEnough = true;
					}
				}
				if (!afsCloserEnough) {
					infeasibleInstance = true;
					break;
				}
			}	
			if (!infeasibleInstance) {
				System.out.println("\tAFSs not at good distance");
				continue;
			}
			System.out.println("\tAFSs at good distance");
//			check if is connected
			if (!Util.isAConnectedGraph(afss)) {
				System.out.println("\tNot a valid tree");
				continue;
			}
			System.out.println("\tValid tree");
			break;
		}
		System.out.println("AFSS generated");
//		write file		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".gvrp"));
			String str = "NAME: A-n" + customerAndDepotDemands.size() + "-f"+afss.size()+"\n"
					+ "COMMENT : -\n"
					+ "TYPE : GVRP\n"
					+ "DIMENSION : "+ customerAndDepotDemands.size();
			
			
			writer.write(str);		     
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
		
		
	}
}
