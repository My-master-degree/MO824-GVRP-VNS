package problems.qbf.solvers;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import problems.qbf.solvers.*;
import solutions.Solution;

public class Experimentos {
	
	
	
	public static void main(String[] args) throws IOException {
		
		String arquivo = "resultado-reactive-QBF.txt" ;
		new File(arquivo).createNewFile();
		File resultados  = new File(arquivo);
		FileOutputStream fos;
		fos = new FileOutputStream(resultados);	
		String[] intancesWords = new String[]{"020", "040", "060", "080", "100", "200", "400"};
		String r = "";
		int minNumberSolutionsPerAlpha = 5;
		double[] alphas = new double[10];
		for (int j = 0; j < alphas.length; j++) {
			alphas[j] = ((double) 1/alphas.length)*(j + 1);
		}
		//GRASP-QBF
		for (int i = 0; i < intancesWords.length; i++) {
			
			long startTime = System.currentTimeMillis();		
			Reactive_GRASP_QBFPT grasp = new Reactive_GRASP_QBFPT(alphas, 50000, minNumberSolutionsPerAlpha, "instances/qbf"+intancesWords[i]);
			Solution<Integer> bestSol = grasp.solve();
			
			
			r += " qbf"+intancesWords[i] ;
			r += " maxVal = "+bestSol.cost.toString();
			
			System.out.println("R: "+r);
			System.out.println("maxVal = " + bestSol);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			r+= " Time = "+(double)totalTime/(double)1000+" seg";
			System.out.println("Time = "+(double)totalTime/(double)1000+" seg");				
			
			r+= "\n";
	
		}
			
		fos.write(r.getBytes());
		fos.close();
		
	}

}
