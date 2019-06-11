package problems.gvrp.instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import problems.gvrp.GVRP;

public class MyInstanceReader {
	public static void read(String path, GVRP gvrp) {
		List<NodeData> customersCoordinates,
				afssCoordinates;
		NodeData depotCoordinates; 
		File file = new File(path); 		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));			
//			get number of customers and afs
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(br.readLine());			
			m.find();			
			gvrp.customersSize = Integer.valueOf(m.group()) - 1;	
			customersCoordinates = new ArrayList<NodeData>(gvrp.customersSize);
			m.find();
			gvrp.rechargeStationsSize = Integer.valueOf(m.group());
			afssCoordinates = new ArrayList<NodeData>(gvrp.rechargeStationsSize);			
//			discard four second lines
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
//			get vehicle capacity
			m = p.matcher(br.readLine());
			m.find();
			gvrp.vehicleCapacity = Double.valueOf(m.group());
//			get vehicle autonomy
			m = p.matcher(br.readLine());
			m.find();
			gvrp.vehicleAutonomy = Double.valueOf(m.group());
//		 	vehicle operation time limit
			m = p.matcher(br.readLine());
			m.find();
			gvrp.vehicleOperationTime = Double.valueOf(m.group());
//			vehicle speed
			m = Pattern.compile("(\\d+)").matcher(br.readLine());
			m.find();
			Integer speed = Integer.valueOf(m.group());
//			vehicle consumption rate
			m = Pattern.compile("(\\d+).(\\d+)").matcher(br.readLine());
			m.find();
			gvrp.vehicleConsumptionRate = Double.valueOf(m.group());
//			discard a line
			br.readLine();
//			get depot
			m = p.matcher(br.readLine());
			m.find();			
			m.group();
			m.find();
			Integer x = Integer.valueOf(m.group());
			m.find();
			Integer y = Integer.valueOf(m.group());
			depotCoordinates = new NodeData(0, x, y);
//			discard a line
			br.readLine();
//			get customers			
			gvrp.customersDemands = new HashMap<Integer, Double> (gvrp.customersSize);
			gvrp.customersServiceTime = new HashMap<Integer, Double> (gvrp.customersSize);
			gvrp.nodesCoordinates = new HashMap<Integer, Integer[]> (gvrp.customersSize);
			gvrp.nodesCoordinates.put(0, new Integer[] {x, y});
			int i = 1;
			while(true) {				
				String st = br.readLine();
				m = p.matcher(st); 
				if (!m.find()) 
					break;	
				m.find();
				x = Integer.valueOf(m.group());
				m.find();
				y = Integer.valueOf(m.group());								
				customersCoordinates.add(new NodeData (i, x, y));
				gvrp.nodesCoordinates.put(i, new Integer[] {x, y});
				m.find();
				gvrp.customersDemands.put(i, Double.valueOf(m.group()));
				m.find();
				gvrp.customersServiceTime.put(i, Double.valueOf(m.group()));
				i++;
			}
//			get afss
			gvrp.rechargeStationsRefuelingTime = new HashMap<Integer, Double> ();
			while(true) {				
				String st = br.readLine();
				m = p.matcher(st); 
				if (!m.find()) 
					break;	
				m.find();
				x = Integer.valueOf(m.group());
				m.find();
				y = Integer.valueOf(m.group());								
				afssCoordinates.add(new NodeData (i, x, y));
				gvrp.nodesCoordinates.put(i, new Integer[] {x, y});
				m.find();
				gvrp.rechargeStationsRefuelingTime.put(i, Double.valueOf(m.group()));
				i++;
			}
//			get remaining datas
			gvrp.rechargeStationsSize = afssCoordinates.size();		
//			calculate distances and times			
			List<NodeData> allNodes = new ArrayList<NodeData> (gvrp.customersSize + gvrp.rechargeStationsSize + 1);
			allNodes.add(depotCoordinates);
			allNodes.addAll(customersCoordinates);
			allNodes.addAll(afssCoordinates);
			gvrp.distanceMatrix = new Double[allNodes.size()][allNodes.size()];
			gvrp.timeMatrix = new Double[allNodes.size()][allNodes.size()];			
			for (i = 0; i < allNodes.size(); i++) {
				gvrp.distanceMatrix[i][i] = 0d;
				gvrp.timeMatrix[i][i] = 0d;
				NodeData nodeI = allNodes.get(i); 
				for (int j = i + 1; j < allNodes.size(); j++) {		
					NodeData nodeJ = allNodes.get(j);		
					gvrp.distanceMatrix[i][j] = Util.get2DEuclidianDistance(nodeI, nodeJ);
					gvrp.distanceMatrix[j][i] = gvrp.distanceMatrix[i][j];
					gvrp.timeMatrix[i][j] = gvrp.distanceMatrix[i][j] / speed;
					gvrp.timeMatrix[j][i] = gvrp.timeMatrix[i][j];
				}
			}
//			set values
			gvrp.size = allNodes.size();
			gvrp.name = "n"+(gvrp.customersSize + 1)+"k"+gvrp.rechargeStationsSize+".gvrp";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 				
	}
}
