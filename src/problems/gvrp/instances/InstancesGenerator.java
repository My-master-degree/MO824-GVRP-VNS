package problems.gvrp.instances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstancesGenerator {
	
	public static boolean verbose = true; 
	
	public static void generate(String fileName, int afssNumber) {
		Map<Integer, NodeData> customers = new HashMap<Integer, NodeData>();
		NodeData depot = null;
		File file = new File(fileName); 		
		BufferedReader br;
		Integer vehicleCapacity = null;
		Integer vehicleAutonomy = null;
		Integer opt = null;
		Double speed = 40d;
		Integer lastCustomerId = null;
		Integer optPartition = 15;
		try {
			br = new BufferedReader(new FileReader(file));			
//			discard first line
			br.readLine();
//			get optimal cost
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(br.readLine());			
			while(m.find())
				opt = Integer.valueOf(m.group());
//			discar third line
			br.readLine();
//			get number of nodes			
			m = p.matcher(br.readLine()); 
			m.find();
//			discard next line
			br.readLine();
//			get capacity
			m = p.matcher(br.readLine()); 
			m.find();
			vehicleCapacity = Integer.valueOf(m.group(0));
//			discard next line
			br.readLine();
//			get nodes
			String st = br.readLine();
			m = p.matcher(st); 
			m.find();
			m.group();
			m.find();
			Integer x = Integer.valueOf(m.group());
			m.find();
			Integer y = Integer.valueOf(m.group());											
			depot = new NodeData(0, x, y);
			while(true) {				
				st = br.readLine();
				m = p.matcher(st); 
				if (!m.find()) 
					break;	
				lastCustomerId = Integer.valueOf(m.group());
				m.find();
				x = Integer.valueOf(m.group());
				m.find();
				y = Integer.valueOf(m.group());								
				customers.put(lastCustomerId - 1, new NodeData(lastCustomerId - 1, x, y));
			}
//			get demands			
			while(true) {
				st = br.readLine();
				m = p.matcher(st);
				if (!m.find())
					break;
				lastCustomerId = Integer.valueOf(m.group());
				m.find();
				NodeData node = customers.get(lastCustomerId - 1);
				if (node != null)
					node.demand = Double.valueOf(m.group());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
//		get graph dimension
		Integer maxX = Integer.MIN_VALUE,
				maxY = Integer.MIN_VALUE,
				minX = Integer.MAX_VALUE,
				minY = Integer.MAX_VALUE;
		for (NodeData node : customers.values()) {
			if (node.x > maxX)
				maxX = node.x;
			if (node.y > maxY)
				maxY = node.y;
			if (node.x < minX)
				minX = node.x;
			if (node.y < minY)
				minY = node.y;
		}
		if (depot.x > maxX)
			maxX = depot.x;
		if (depot.y > maxY)
			maxY = depot.y;
		if (depot.x < minX)
			minX = depot.x;
		if (depot.y < minY)
			minY = depot.y;
		Integer Xlength = maxX - minX;
		Integer Ylength = maxY - minY;				
//		generate random afss
		Random random = new Random();	
		random.setSeed(System.currentTimeMillis());
		Set<NodeData> afss;
//		timer attrs
		long timer = 15000;
		long targetTime = System.currentTimeMillis() + timer;
//		calculate vehicle autonomy
		vehicleAutonomy = Math.floorDiv(opt, optPartition);
		while(true) {		
			if (System.currentTimeMillis() >= targetTime) {				
				optPartition--;
				System.out.println("Partition updated to " + optPartition);
				vehicleAutonomy = Math.floorDiv(opt, optPartition);
				targetTime = System.currentTimeMillis() + timer;
			}
//			process
			afss = new HashSet<NodeData> (afssNumber);
			afss.add(depot);
//			generate afss
			if (verbose)
				System.out.println("\tGenerating AFSs");
			Integer firstAFSId = lastCustomerId;
			for (int i = 0; i < afssNumber; i++) {
				NodeData afs = new NodeData (firstAFSId, minX + random.nextInt(Xlength), minY + random.nextInt(Ylength));
//				NodeData afs = new NodeData (firstAFSId, random.nextInt(Xlength), random.nextInt(Ylength));
				if (!afss.contains(afs) && !customers.containsValue(afs)) { 
					afss.add(afs);
					firstAFSId++;
				}else
					i--;
			}
			if (verbose)
				System.out.println("\tAFSs generated");
//			calculate distance matrix	
//			int n = afss.size() + customers.size();
//			Double costs[][] = new Double[n][n];
//			for (NodeData b : customers.values()) {
//				for (NodeData e : customers.values()) {
//					if (!b.equals(e)) {
//						costs[b.id][e.id] = Util.get2DEuclidianDistance(b, e);
//					}else {
//						costs[b.id][e.id] = 0d;
//					}
//				}
//				for (NodeData f : afss) {
//					costs[b.id][f.id] = Util.get2DEuclidianDistance(b, f);
//					costs[f.id][b.id] = Util.get2DEuclidianDistance(f, b);					
//				}
//			}
//			for (NodeData b : afss) {
//				for (NodeData e : afss) {
//					if (!b.equals(e)) {
//						costs[b.id][e.id] = Util.get2DEuclidianDistance(b, e);
//					}else {
//						costs[b.id][e.id] = 0d;
//					}
//				}
//			}
//			generate tree
			if (verbose)
				System.out.println("\tChecking if each afs have at least one afs at a maximum of distance of \\beta");
//			Map<Integer, NodeData> afssMap = new HashMap<Integer, NodeData> ();
//			for (NodeData afs : afss) {
//				if (afs.id != 0) {
//					afssMap.put(afs.id, afs);
//				}
//			}
//			infeasibleInstance = false;
//			for (NodeData customer : customers.values()) {				
//				if (!Util.checkIfHasAPath(afssMap, depot, customer, (double) vehicleAutonomy, costs)) {
//					if (verbose)
//						System.out.println("\tTheres no path between "+customer.id+" and depot");
//					infeasibleInstance = true;
//					break;
//				}
//					
//			}
//			if (infeasibleInstance) {
//				if (verbose)
//					System.out.println("\tInvalid tree");
//				continue;
//			}
//			if (verbose)
//				System.out.println("\tValid tree");
//			break;	
			boolean infeasibleInstance = false;
			for (NodeData b : afss) {
				boolean afsCloserEnough = false;
				for (NodeData e : afss) {					
					if (!b.equals(e) && Util.get2DEuclidianDistance(b, e) <= vehicleAutonomy) {
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
			if (infeasibleInstance) {
				if (verbose)
					System.out.println("\tAFSs not at good distance");
				continue;
			}
			if (verbose)
				System.out.println("\tAFSs at good distance");
//			check if each customers have at least one afs at a maximum of distance of \beta/2
			if (verbose)
				System.out.println("\tChecking if each customers have at least one afs at a maximum of distance of \\beta/2");
			infeasibleInstance = false;
			for (NodeData b : customers.values()) {
				boolean afsCloserEnough = false;
				for (NodeData e : afss) {
					if (Util.get2DEuclidianDistance(b, e) <= vehicleAutonomy/2) {
						b.neighborhoods.add(e);
						e.neighborhoods.add(b);
						afsCloserEnough = true;
						break;
					}
				}
				if (!afsCloserEnough) {
					infeasibleInstance = true;
					break;
				}
			}			
			if (infeasibleInstance) {
				if (verbose)
					System.out.println("\tCustomers not at good distance");
				continue;
			}
			if (verbose)
				System.out.println("\tCustomers at good distance");
//			check if is connected
			Set<NodeData> allNodes = new HashSet<NodeData> ();
			allNodes.addAll(afss);
			allNodes.addAll(customers.values());
			if (!Util.isAConnectedGraph(allNodes)) {
				if (verbose)
					System.out.println("\tNot a valid tree");
				continue;
			}
			if (verbose)
				System.out.println("\tValid tree");
			break;
		}
		if (verbose)
			System.out.println("AFSS generated");
//		check if instance is valid		
		for (NodeData b : afss) {
			boolean afsCloserEnough = false;
			for (NodeData e : afss) {					
				if (!b.equals(e) && Util.get2DEuclidianDistance(b, e) <= vehicleAutonomy) {					
					afsCloserEnough = true;
					break;
				}
			}
			if (!afsCloserEnough) {		
				System.out.println("Invalid Instance: Not enough closer afs");
				break;
			}
		}	
		for (NodeData b : customers.values()) {
			boolean afsCloserEnough = false;
			for (NodeData e : afss) {
				if (Util.get2DEuclidianDistance(b, e) <= vehicleAutonomy/2) {
					afsCloserEnough = true;
					break;
				}
			}
			if (!afsCloserEnough) {
				System.out.println("Invalid instance");
				break;
			}
		}	
//		define time operation limit
		
//		write file		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".gvrp"));
			String str = "NAME: A-n" + (customers.size() + 1) + "-f"+(afss.size() - 1)+"\n"
					+ "COMMENT : -\n"
					+ "TYPE : GVRP\n"
					+ "DIMENSION : "+ (customers.size() + afss.size() - 1) + "\n"
					+ "EDGE_WEIGHT_TYPE : EUC_2D\n"
					+ "CAPACITY : "+vehicleCapacity+"\n"
					+ "AUTONOMY : "+vehicleAutonomy+"\n"
					+ "TIME LIMIT : 11\n"
					+ "SPEED : "+speed+"\n"
					+ "FUEL CONSUMPTION : 1.0\n"
					+ "DEPOT_COORD_SECTION\n"
					+ "0 " + depot.x + " " + depot.y + "\n"
					+ "CUSTOMER_COORD_SECTION\n";
			List<Integer> ids = new ArrayList<Integer> (customers.keySet());
			Collections.sort(ids);			
			for (Integer id: ids) {
				NodeData node = customers.get(id);
				str += id + " " + node.x + " " + node.y + " " + (int) node.demand + " " + random.nextDouble() + "\n";
			}
			str += "AFS_COORD_SECTION\n";
			Integer afsId = ids.get(ids.size() - 1) + 1;
			afss.remove(depot);
			for (NodeData afs : afss) {
//				str += afsId + " " + afs.x + " " + afs.y  + " " + random.nextDouble() + "\n";
				str += afsId + " " + afs.x + " " + afs.y  + " " + 0.25 + "\n";
				afsId++;
			}
			str += "EOF";
			
			writer.write(str);		     
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
		
		
	}
}
