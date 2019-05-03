package problems.gvrp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import problems.Evaluator;
import solutions.Solution;

/**
 * A quadractic binary function (QBF) is a function that can be expressed as the
 * sum of quadractic terms: f(x) = \sum{i,j}{a_{ij}*x_i*x_j}. In matricial form
 * a QBF can be expressed as f(x) = x'.A.x 
 * The problem of minimizing a QBF is NP-hard [1], even when no constraints
 * are considered.
 * 
 * [1] Kochenberger, et al. The unconstrained binary quadratic programming
 * problem: a survey. J Comb Optim (2014) 28:58–81. DOI
 * 10.1007/s10878-014-9734-0.
 * 
 * @author ccavellucci, fusberti
 *
 */
public class GVRP implements Evaluator<List<Integer>> {

	/**
	 * Dimension of the domain.
	 */
	public Integer size;

	/**
	 * Size of customers list.
	 */
	public Integer customersSize;
	
	/**
	 * Size of recharge stations list.
	 */
	public Integer rechargeStationsSize;
	
	/**
	 * 
	 */
	public Double vehicleCapacity;
	
	/**
	 * 
	 */
	public Double vehicleAutonomy;
	
	/**
	 * 
	 */
	public Double vehicleOperationTime;
	
	/**
	 * 
	 */
	public Double vehicleConsumptionRate;
	
	/**
	 * The graph vertices, containing customers and fuel stations.
	 */
	public Integer[] vertices;

	/**
	 * The customers demands.
	 */
	public Map<Integer, Double> customersDemands;
	
	/**
	 * The customers demands.
	 */
	public Map<Integer, Double> customersServiceTime;
	
	/**
	 * The customers demands.
	 */
	public Map<Integer, Double> rechargeStationsRefuelingTime;
	
	/**
	 * The matrix of distance among the graph nodes
	 */
	public Double[][] distanceMatrix;
	
	/**
	 * The matrix of time among the graph nodes
	 */
	public Double[][] timeMatrix;

	/**
	 * The constructor for Green-VRP class. The filename of the
	 * input for setting matrix of distance, customers and fuel stations data. 	
	 * 
	 * @param filename
	 *            Name of the file containing the input for setting the GVRP.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	public GVRP(String filename) throws IOException {
		readInput(filename);
	}	
	
//	public Double getDistance(int... indexes) {
//		Double distance = 0d;
//		for (int k = 0; k < indexes.length - 1; k++) {
//			distance += this.distanceMatrix[indexes[k]][indexes[k + 1]];
//		}
//		return distance;
//	}
	
	public Double getDistance(Integer... indexes) {
		Double distance = 0d;
		for (int k = 0; k < indexes.length - 1; k++) {
			distance += this.distanceMatrix[indexes[k]][indexes[k + 1]];
		}
		return distance;
	}
	
	public Double getDistance(List<Integer> indexes) {
		Double distance = 0d;
		for (int k = 0; k < indexes.size()- 1; k++) {
			distance += this.distanceMatrix[indexes.get(k)][indexes.get(k + 1)];
		}
		return distance;
	}
	
	public Double getFuelConsumption(int... indexes) {
		Double consumption = 0d;		
		for (int k = 0; k < indexes.length - 1; k++) {						
			consumption += this.distanceMatrix[indexes[k]][indexes[k + 1]] * this.vehicleConsumptionRate;
			if (this.rechargeStationsRefuelingTime.get(indexes[k + 1]) != null)
				consumption = consumption - vehicleAutonomy >= 0 ? consumption - vehicleAutonomy : 0;
		}
		return consumption;
	}
	
	public Double getTimeConsumption(int... indexes) {
		Double consumption = 0d;
		if (indexes.length > 0) {
			Double customerServiceTime = this.customersServiceTime.get(indexes[0]),
				rechargeStationRefuelingTime = this.rechargeStationsRefuelingTime.get(indexes[0]);
			if (customerServiceTime != null)
				consumption += customerServiceTime;
			else if (rechargeStationRefuelingTime != null)
				consumption += rechargeStationRefuelingTime;
			for (int k = 0; k < indexes.length - 1; k++) {
				consumption += this.timeMatrix[indexes[k]][indexes[k + 1]];
				customerServiceTime = this.customersServiceTime.get(indexes[k + 1]);
				rechargeStationRefuelingTime = this.rechargeStationsRefuelingTime.get(indexes[k + 1]);
				if (customerServiceTime != null)
					consumption += customerServiceTime;
				else if (rechargeStationRefuelingTime != null)
					consumption += rechargeStationRefuelingTime;
			}
		}
		return consumption;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see problems.Evaluator#getDomainSize()
	 */
	@Override
	public Integer getDomainSize() {
		return size;
	}

	/**
	 * {@inheritDoc} In the case of a GVRP, the evaluation correspond to
	 * computing the sum of arcs used in the solution.
	 * 
	 * @return The evaluation of the GVRP.
	 */
	@Override
	public Double evaluate(Solution<List<Integer>> sol) {

		return sol.cost = 0d;

	}

	/**
	 * Responsible for setting the QBF function parameters by reading the
	 * necessary input from an external file. this method reads the domain's
	 * dimension and matrix {@link #distanceMatrix}.
	 * 
	 * @param filename
	 *            Name of the file containing the input for setting the black
	 *            box function.
	 * @return The dimension of the domain.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	protected Integer readInput(String filename) throws IOException {
		Reader fileInst = new BufferedReader(new FileReader(filename));
		StreamTokenizer stok = new StreamTokenizer(fileInst);

		stok.nextToken();
		Integer _size = (int) stok.nval;
		distanceMatrix = new Double[_size][_size];

		for (int i = 0; i < _size; i++) {
			for (int j = i; j < _size; j++) {
				stok.nextToken();
				distanceMatrix[i][j] = stok.nval;
				//A[j][i] = A[i][j];
				if (j>i)
					distanceMatrix[j][i] = 0.0;
			}
		}

		return _size;

	}

}
