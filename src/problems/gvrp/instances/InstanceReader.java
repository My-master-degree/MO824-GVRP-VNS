package problems.gvrp.instances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import problems.gvrp.GVRP;

public class InstanceReader {
	public static void read(String path, GVRP gvrp) {
		List<Double[]> coordinates = new Double[][];
		File file = new File(path); 		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String st; 
			while ((st = br.readLine()) != null) {
				String[] parts = st.split("\t");
				if (parts[1] == "d") {					
					for (int i = 0; i < parts.length; i++) {
						parts[i]
					}
				}
				System.out.println(st); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 				
	}
}
