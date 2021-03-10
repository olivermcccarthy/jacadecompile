package oliver.neuron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import oliver.neuron.TruthTable.TruthRow;
import oliver.neuron.pd.Equation;

public class Neuron {

	List<Neuron> inputs = new ArrayList();

	List<Double> weights = new ArrayList();


    // Bias of this neuron
	double bias = 0.0;
	

	// The output neurons from this neuron will pass back a weighted error ( During
	// their calculation of error)
	// Once the next Layer has completed the calculation of error . This value will
	// be the sum of all weighted errors from neurons in the next layer
	// This value is then used in the handleError to adjust weights and biases
	// As best as I can understand. A small adjustment in this neuron will have its
	// affect multiplied by the weighted error in all neurons its connected to
	double weigthedError = 0;

	
    public static double learningRate = 5;
	
	// Calculated errorVariable
	double errorVar = 0;
	
	
	// Ensure names are unique
	static HashMap<String, Neuron> uniqueNames = new HashMap<String, Neuron>();
	
	// Is this an inut Neuron or not
	boolean input = false;
	
	String name;


	// Current value of the neuron 1 (1 -e^-z)
	// Where z is the sum across all input nodes of weight*input value 
	double sigMoid = 0.5;

	/**
	 * Create a neuron with a given bias
	 * @param name
	 * @param bias
	 */
	public Neuron(String name, double bias) {
		this.bias = bias;
		this.name = name;
		while (uniqueNames.containsKey(this.name)) {
			this.name += "_1";

		}
		uniqueNames.put(this.name, this);
		

	}


 /**
  * Create an input Neuron. This just stores the input value
  * @param name
  * @param value
  * @param input
  */
	public Neuron(String name, double value, boolean input) {
		this.sigMoid = value;
		this.input = true;
		this.name = name;
		while (uniqueNames.containsKey(this.name)) {
			this.name += "_1";

		}
		uniqueNames.put(this.name, this);
	}

	public String toString() {
		return String.format("%s bias(%s) value (%s)", this.name, this.bias, this.getValue());
	}


	
	
	
    /**
     * Return the calculated sigmoid value for this neuron
     * @return
     */
	public double getValue() {

		return sigMoid;

	}

	
	

	/**
	 * Get the sum across all input neurons of weight[x] * inputValue[x] 
	 * @param inValues
	 * @return
	 */
    
 
	public double getSigmoidWeightedValue() {
		
		double result =0;

		
		for (int n = 0; n < inputs.size(); n++) {
			double weight = inputs.get(n).getValue() * weights.get(n);
			
			result += weight;
			if(weight < 0) {
				weight = weight *-1;
			}
			
		}
		
		return result;
	}
	
	public void addInput(Neuron input, double weight) {
		this.inputs.add(input);
		this.weights.add(weight);
	
		this.input = false;
	}

	
	/**
	 * Called by each Neuron in the next layer
	 * Once the next layer has completed this value
	 *  will be the sum of all the weightedErrors from the neurons in the next Layer
	 * @param weightedError
	 */
	public void addWeightedError(double weightedError) {
		this.weigthedError += weightedError;
	}

	/**
	 * Set weightedError to 0
	 */
	public void clearWeightedError() {
		this.weigthedError = 0;
	}

	
    /**
     * Adjust each weight by an errorVariable
     * Maths is hard but a small change in each weight causes a small improvement in the cost
     * So we calculate the partial derivative  of each weight with respect  to Cost
     * Then adjust this weight by a multiple of this  partial derivative
     * <br> 
     *  Derivative rules here https://www.mathsisfun.com/calculus/derivatives-rules.html
     *  Some variable definitions
     *  T               Expected value
     *  Sigmoid         Function to calculate output of neuron  1/(1 + e^-Z)
     *  Z               Sum of all weights * inputs  w(1)*input(1) + w(2)*input(2) + .. + w(x)*input(x) + ... + w(n)*input(n)
     *  Cost            Squared difference between expected and actual(Sigmoid)   0.5 *( T - Sigmoid)^2 
     *  pdW(x)-Cost     Partial derivative  of weight with respect  to Cost  <br>
     *  pdW(x)-Z        Partial derivative  of weight with respect  to Z
     *  pdZ-Sigmoid     Partial derivative  of Z with respect  to Sigmoid
     *  pdSigmoid-Cost  Partial derivative  of Sigmoid  respect  to Cost
     *  Error           Use variable used over and over pdZ-Sigmoid *  pdSigmoid-Cost       
     *  Using chain rule pdW-Cost =  
     *   pdW(x)-Sigmoid * pSigmoid-Cost  = 
     *   pdW(x)-Z  *  pdZ-Sigmoid * pdSigmoid-Cost
     *   Mad but true.   As Cost is computed from Sigmoid which in turn is computed from Z which in turn is computed using W(x)

     *   pdW(x)-Z is just   input(x) as we can treat the other coeffs in Z as constants ( we are only making a small change in W(x) and therefore they have a derivative of 0
     *   pdZ-Sigmoid is more fun  Sigmoid = 1/(1 + e^-Z)
     *   Reciprocal Rule	derivative of 1/f = 	-df/(f^2)    
     *   f =  1 + e^-Z
     *   Derivative of 1 +e-Z =  -e-Z  so -df = e-Z 
     *   f sqaured = 
     *   df  = e^-z   We drop the 1 and the derivative e^x = e^x 
     *   f^2 = (1 + e^-Z) * (1 + e^-Z))  
     *   
     *   so thats
     *    1/(1 + e^-Z)  -  e^-z/((1 + e^-Z) * (1 + e^-Z))
     *              
     * @param t
     * @param inValues
     */
	public void handleError(double t) {
		// (y-t)*y*(1-y)* (in1) Dont ask

		//
		double sigM = sigMoid;
		errorVar = (sigM - t) * sigM * (1 - sigM);
		for (int wI = 0; wI < this.weights.size(); wI++) {
			Neuron inputNeuron = this.inputs.get(wI);
			double input = inputNeuron.getValue();
			double weight = this.weights.get(wI);
			inputNeuron.addWeightedError( weight * errorVar);
			weight = weight - learningRate * (errorVar * input);
			this.weights.set(wI, weight);
		}
		bias = bias - learningRate * (errorVar);

	}

	/**
	 * Adjust each weight by   -1 * learningRate * errorVariable * input
	 * A small change in weight causes a small improvement in cost
	 * Small change equates to Partial derivitive of the weight on the overallCost
	 * As this is not a Neuron in the final Layer. The change will affect each Neuron in the next Layer
	 *
	 */
	public void handleError() {
		// (y-t)*y*(1-y)* (in1) Dont ask

		//
		if (this.input) {
			return;
		}
		double sigM = sigMoid;
		
		// A small change cuases a small chnage in sigmoid
		// This change is the dirivitive of sigmoid multiplied by the weighted errors of
		// the next layer
		errorVar = (this.weigthedError) * sigM * (1 - sigM);
		for (int wI = 0; wI < this.weights.size(); wI++) {
			
			Neuron inputNeuron = this.inputs.get(wI);
			double input =  inputNeuron.getValue();

			double weight = this.weights.get(wI);
		
			if(this.inputs.size() >0) {
				inputNeuron.addWeightedError(weight * errorVar);
			}
			weight = weight - learningRate * (input * errorVar);
			
			this.weights.set(wI, weight);
		}
		
		bias = bias - learningRate * (errorVar);
	}

	/**
	  Sigmoid =  1/(1 + e^-z)
	   Where z is the sum across all input nodes of weight*input value 
	   as z gets large e^-z gets small and Sigmoid approaches 1
	   as z gets small e^-z gets large and Sigmoid approaches 0 
	 * @return
	 */
	public double sigmoid() {

		if (this.input) {
			return sigMoid;
		}
		double res = getSigmoidWeightedValue();

		res += bias;
		res *= -1;
		res = Math.exp(res);
		res = 1 / (1 + res);
		sigMoid = res;
		return res;
	}
	
	public static void main(String[] args) {

		Layer inputLayer = new Layer("input", 3);
		Layer hiddenLayer = new Layer("hidden2", inputLayer, 3);
		Layer hiddenLayer2 = new Layer("hidden3", hiddenLayer, 3);
		Layer outputLayer = new Layer("output", hiddenLayer2, 2);
		TruthTable tt = new TruthTable("adder.csv");
		DrawPanel.showNeurons();
		learningRate = 1;
		for (int trial = 0; trial < 2000; trial++) {
			Cost myCost = new Cost(2);

			boolean matches = true;
			for (TruthRow tr : tt.rows) {
				for (int run = 0; run < 2; run++) {
					double[] ins = tr.inpputs;
					inputLayer.setvalues(ins);
					hiddenLayer.sigmoid();
					hiddenLayer2.sigmoid();
					// hiddenLayer2.sigmoid();
					outputLayer.sigmoid();
					double[] outs = outputLayer.getvalues();
					// System.out.println("Sigmoid" + testSigMoid.sigmoid());
					// System.out.println("partialdiff" + testSigMoid.partialDiff());

					// System.out.println(adder);

					double[] expected = tt.getOuts(ins);

					// if (!tt.matches(ins, outs)) {
					// matches = false;
					// }

					// DrawPanel.stopAMinute(" Expected" + Equation.toString(expected));
					// DrawPanel.frame.repaint();
					outputLayer.handleTopError(expected);
					// DrawPanel.stopAMinute(" Handlederror" + Equation.toString(expected));
					myCost.addResult(expected, outs);

				}
			}
			System.out.println("Trial" + trial + " Cost " + myCost.getCost() + " matches " + matches);

		}

		for (TruthRow tr : tt.rows) {
			double[] ins = tr.inpputs;
			inputLayer.setvalues(ins);
			hiddenLayer.sigmoid();
			hiddenLayer2.sigmoid();
			outputLayer.sigmoid();
			double[] outs = outputLayer.getvalues();
			DrawPanel.stopAMinute(" Expected" + Equation.toString(outs));
			System.out.println(Equation.toString(ins) + " : " + Equation.toString(outs));
		}
	}

	public static List<Double> getWeightsAndBiases() {
		List<Double> result = new ArrayList<Double>();
		for (Neuron nu : uniqueNames.values()) {
			result.add(nu.bias);
			result.addAll(nu.weights);

		}
		return result;
	}
}
