
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import problems.gvrp.instances.InstancesGenerator;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;


public class Main {
	
	public static void main(String[] args) throws IOException {
		Integer numOfCustomers = 50;
		Double vehicleAutonomy = 30d;
		Map<InstancesGenerator.Node, Integer> nodes = InstancesGenerator.generate(numOfCustomers, 500, vehicleAutonomy, 20, 20);
		
		nodes = nodes.entrySet().stream().sorted(comparingByValue()).collect(
		            toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
		                LinkedHashMap::new));
		
		
		
		String content = "NUM_CUSTOMERS="+numOfCustomers+"\nVEHICLE_AUTONOMY="+vehicleAutonomy+"\n";
		for (InstancesGenerator.Node node : nodes.keySet()) {
			Integer id = nodes.get(node);
			if (id == 0)
				content += "DEPOT=" + node.x + "," + node.y + "\n";
			else if (id > 0 && id < numOfCustomers + 1)
				content += "CUSTOMER_"+id+"=" + node.x + "," + node.y + "\n";
			else
				content += "AFS_"+id+"=" + node.x + "," + node.y + "\n";
		}		
	    BufferedWriter writer = new BufferedWriter(new FileWriter("c20a30.txt"));
	    writer.write(content);
	     
	    writer.close();
	}

}
