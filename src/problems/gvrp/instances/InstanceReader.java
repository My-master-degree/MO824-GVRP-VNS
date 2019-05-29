package problems.gvrp.instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import problems.gvrp.GVRP;

public class InstanceReader {
	public static void read(String path, GVRP gvrp) {
		List<Double[]> customersCoordinates = new ArrayList<Double[]>(),
				afssCoordinates = new ArrayList<Double[]>();
		Double[] depotCoordinates = new Double[2]; 
		File file = new File(path); 		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));			
//			discard first line
			br.readLine();
//			get depot
			String st = br.readLine();			
			String[] parts = st.split("\t");
			depotCoordinates[0] = Double.valueOf(parts[2]);
			depotCoordinates[1] = Double.valueOf(parts[3]);					
//			get afss
			for (st = br.readLine(), parts = st.split("\t"); parts[1].equals("f"); 
				afssCoordinates.add(new Double[] {Double.valueOf(parts[2]), Double.valueOf(parts[3])}), 
				st = br.readLine(), parts = st.split("\t"));
//			get customers
			for (; parts.length == 4 && parts[1].equals("c");
				customersCoordinates.add(new Double[] {Double.valueOf(parts[2]), Double.valueOf(parts[3])}), st = br.readLine(), 
				parts = st.split("\t"));
//			get remaining datas
			gvrp.customersSize = customersCoordinates.size();
			gvrp.rechargeStationsSize = afssCoordinates.size();
//				vehicle autonomy
			gvrp.vehicleAutonomy = Double.valueOf(br.readLine().split("/")[1]);			
//				vehicle consumption rate
			gvrp.vehicleConsumptionRate = Double.valueOf(br.readLine().split("/")[1]);
//			 	vehicle operation time limit
			gvrp.vehicleOperationTime = Double.valueOf(br.readLine().split("/")[1]);
			Double averageVelocity = Double.valueOf(br.readLine().split("/")[1]);
//			calculate distances and times			
			List<Double[]> allNodes = new ArrayList<Double[]> (gvrp.customersSize + gvrp.rechargeStationsSize + 1);
			allNodes.add(depotCoordinates);
			allNodes.addAll(customersCoordinates);
			allNodes.addAll(afssCoordinates);
			gvrp.distanceMatrix = new Double[allNodes.size()][allNodes.size()];
			gvrp.timeMatrix = new Double[allNodes.size()][allNodes.size()];
			double radiusOfEarth = 4182.44949;
			for (int i = 0; i < allNodes.size(); i++) {
				gvrp.distanceMatrix[i][i] = 0d;
				gvrp.timeMatrix[i][i] = 0d;
				Double[] nodeI = allNodes.get(i);
				Double lon1 = nodeI[0], 
					lat1 = nodeI[1]; 
				for (int j = i + 1; j < allNodes.size(); j++) {		
					Double[] nodeJ = allNodes.get(j);
					Double lon2 = nodeJ[0], 
							lat2 = nodeJ[1];						 
					// miles, 6371km; 
					double dLat = Math.toRadians(lat2-lat1); 
					double dLon = Math.toRadians(lon2-lon1); 
					double a = Math.sin(dLat/2) * Math.sin(dLat/2) + 
							Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
							Math.sin(dLon/2) * Math.sin(dLon/2); 
					double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 						
					gvrp.distanceMatrix[i][j] = radiusOfEarth * c;
					gvrp.distanceMatrix[j][i] = gvrp.distanceMatrix[i][j];
					gvrp.timeMatrix[i][j] = gvrp.distanceMatrix[i][j] / averageVelocity;
					gvrp.timeMatrix[j][i] = gvrp.timeMatrix[i][j];
				}
			}
//			set values
			gvrp.size = allNodes.size();
			gvrp.vehicleCapacity = 0d;
			gvrp.customersDemands = new HashMap<Integer, Double> (gvrp.customersSize);
			gvrp.customersServiceTime = new HashMap<Integer, Double> (gvrp.customersSize);
			for (int i = 1; i <= gvrp.customersSize; gvrp.customersDemands.put(i, 0d), gvrp.customersServiceTime.put(i, 0.25d), i++);			
			gvrp.rechargeStationsRefuelingTime = new HashMap<Integer, Double> ();
			for (int i = 1; i <= gvrp.rechargeStationsSize; gvrp.rechargeStationsRefuelingTime.put(gvrp.customersSize + i, 0.5d), i++);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 				
	}
}
