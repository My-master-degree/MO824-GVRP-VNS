package problems.gvrp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import problems.Evaluator;
import problems.gvrp.instances.GVRPInstanceReader;
import problems.gvrp.instances.MyInstanceReader;

public class GVRP implements Evaluator<Route, Routes> {

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
	 * Nodes coordinates.
	 */
	public Map<Integer, Integer[]> nodesCoordinates;

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

	public String name;

	public Random random;

	public GVRPInstanceReader instanceReader;
	
	/**
	 * The constructor for Green-VRP class. The filename of the
	 * input for setting matrix of distance, customers and fuel stations data. 	
	 * 
	 * @param filename
	 *            Name of the file containing the input for setting the GVRP.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	public GVRP(String filename, GVRPInstanceReader instanceReader) throws IOException {
		this.instanceReader = instanceReader; 
		readInput(filename);
		this.random = new Random(System.currentTimeMillis());		
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
	
	public Double getDistance(Route indexes) {
		Double distance = 0d;
		for (int k = 0; k < indexes.size()- 1; k++) {
			distance += this.distanceMatrix[indexes.get(k)][indexes.get(k + 1)];
		}
		return distance;
	}
	
	public Double getDistance(Routes routes) {
		Double distance = 0d;
		for (Route route:routes) {
			distance += this.getDistance(route);
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
	
	public Double getFuelConsumption(Route indexes) {
		Double consumption = 0d;		
		for (int k = 0; k < indexes.size() - 1; k++) {						
			consumption += this.distanceMatrix[indexes.get(k)][indexes.get(k + 1)] * this.vehicleConsumptionRate;
			if (this.rechargeStationsRefuelingTime.get(indexes.get(k + 1)) != null)
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
	
	public Double getTimeConsumption(Route indexes) {
		Double consumption = 0d;
		if (indexes.size() > 0) {
			Double customerServiceTime = this.customersServiceTime.get(indexes.get(0)),
				rechargeStationRefuelingTime = this.rechargeStationsRefuelingTime.get(indexes.get(0));
			if (customerServiceTime != null)
				consumption += customerServiceTime;
			else if (rechargeStationRefuelingTime != null)
				consumption += rechargeStationRefuelingTime;
			for (int k = 0; k < indexes.size() - 1; k++) {
				consumption += this.timeMatrix[indexes.get(k)][indexes.get(k + 1)];
				customerServiceTime = this.customersServiceTime.get(indexes.get(k + 1));
				rechargeStationRefuelingTime = this.rechargeStationsRefuelingTime.get(indexes.get(k + 1));
				if (customerServiceTime != null)
					consumption += customerServiceTime;
				else if (rechargeStationRefuelingTime != null)
					consumption += rechargeStationRefuelingTime;
			}
		}
		return consumption;
	}
	
	public Double getCapacityConsumption(int... indexes) {
		Double capacity = 0d;
		for (int i = 0; i < indexes.length; i++) {
			Double demand = this.customersDemands.get(indexes[i]);
			if (demand != null)
				capacity += demand;
		}
		return capacity;
	}
	
	public Double getCapacityConsumption(Route indexes) {
		Double capacity = 0d;
		for (int i = 0; i < indexes.size(); i++) {
			Double demand = this.customersDemands.get(indexes.get(i));
			if (demand != null)
				capacity += demand;
		}
		return capacity;
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
	public Double evaluate(Routes sol) {
		return sol.cost = this.getDistance(sol);

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
	protected void readInput(String filename) throws IOException {
		this.instanceReader.read(filename, this);

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GVRP ["
				+ "\n\tcustomersSize=" + customersSize + 
				"\n\trechargeStationsSize=" + rechargeStationsSize + 
				"\n\tvehicleCapacity=" + vehicleCapacity + 
				"\n\tvehicleAutonomy=" + vehicleAutonomy + 
				"\n\tvehicleOperationTime=" + vehicleOperationTime + 
				"\n\tvehicleConsumptionRate=" + vehicleConsumptionRate +
				"\n]";
	}
	
	

}
