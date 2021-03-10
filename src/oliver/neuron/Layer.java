package oliver.neuron;

import java.util.ArrayList;
import java.util.List;

/**
 * A layer of neurons. Sigmoid is called on this layer and it calculates sigmoid
 * for each neuron in the Layer. Sigmoid is a function that returns a value
 * between 0 and 1  means the neuron is fully on
 * 
 * @author oliver
 *
 */
public class Layer {

	static double getRandNum(double upperLevel) {
		double res = (Math.random() * 2 * upperLevel) - upperLevel;
		return res;
	}

	List<Neuron> neurons = new ArrayList<Neuron>();

	String layerName;

	Layer childLayer;

	static List<Layer> layers = new ArrayList();

	/*
	 * sqrt(d) where d is the number of inputs to the neuron. The resulting weights
	 * are normally distributed between [−1/d√,1/d√] s
	 */
	public Layer(String layerName, Layer childLayer, int numNeurons) {
		layers.add(this);
		this.layerName = layerName;
		this.childLayer = childLayer;
		//
		double upperLevel = 1 / (Math.sqrt(numNeurons));

		for (int x = 0; x < numNeurons; x++) {
			Neuron newNu = new Neuron(layerName + "-" + x, 0);

			for (Neuron child : childLayer.neurons) {

				newNu.addInput(child, getRandNum(upperLevel));

			}

			this.neurons.add(newNu);
		}
	}

	/**
	 * InputLayer
	 * 
	 * @param layerName
	 * @param numNeurons
	 */
	public Layer(String layerName, int numNeurons) {
		layers.add(this);
		for (int x = 0; x < numNeurons; x++) {
			Neuron newNu = new Neuron(layerName + "-" + x, 0, true);
			this.neurons.add(newNu);
		}

		this.layerName = layerName;
	}

	public void setvalues(double[] inVals) {
		int index = 0;
		for (Neuron nu : this.neurons) {
			nu.sigMoid = inVals[index];
			index ++;
		}
		
	}

	public double[] getvalues() {
		double [] values = new double[this.neurons.size()];
		int index = 0;
		for (Neuron nu : this.neurons) {
			values[index]=nu.getValue();
		    index ++;
		}
		
		return values;
	}

	public void sigmoid() {
		

		if (this.childLayer != null) {
			this.childLayer.sigmoid();
		} 

		
		for (Neuron nu : this.neurons) {
			nu.sigmoid();
		
		}
		
	}

	public void clearWeightedError() {
		for (Neuron nu : this.neurons) {
			nu.clearWeightedError();
		}
	}

	/**
	 * Handle the error from outside. This method is called only on the outLayer
	 * handlerError is called for each Neuron in the outputlayer
	 * 
	 * @param args
	 *            Expected values. arg[x] is the expected value for Neuron(x)
	 */
	public void handleTopError(double[] args) {

		this.childLayer.clearWeightedError();
		int index =0;
		for (Neuron nu : this.neurons) {
			nu.handleError(args[index]);
			index ++;
		
		}

		if (this.childLayer != null) {
			this.childLayer.handleError();
		}
	}

	public void handleError() {

		if (this.childLayer != null) {
			// DrawPanel.stopAMinute("Handled layer" + this.layerName);
			this.childLayer.clearWeightedError();
		}

		// Each neuorn adds a little bit of weighted error to each of the
		// childWeigthedErrors
		// we then use this array to pass onto teh child layer.
		for (Neuron nu : this.neurons) {
			nu.handleError();
		}

		if (this.childLayer != null) {
			// DrawPanel.stopAMinute("Handled layer" + this.layerName);
			this.childLayer.handleError();
		}
	}
}
