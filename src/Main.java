
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import problems.gvrp.instances.InstancesGenerator;
import problems.qbf.solvers.*;
import solutions.Solution;

public class Main {
	
	public static void main(String[] args) throws IOException {
		InstancesGenerator.generate(20, 100, 30, 20, 20);
	}

}
