package oliver.neuron;


import java.util.ArrayList;
import java.util.List;

import oliver.neuron.pd.Equation;

public class MnistTrainer {

	
	public MnistTrainer() {

	}

	
	
	public List<double[]> normalize(List<int[][]> images, int numRows, int numCols){
		
		List<double[]> normalized = new ArrayList<double[]>();
		for(int [][] image : images) {
			double [] newImage = new double[numRows*numCols];
			int index = 0;
			for(int [] row : image) {
				for(int c =0; c < row.length; c++) {
					newImage[index] = row[c];
					newImage[index] /= 256;
					index ++;
				}
			}
			normalized.add(newImage);
		}
		return normalized;
		
	}
	/**
	 * Run each image through and compare actual with expected Expected will
	 * converted into 10 bit array. Where only one bit is set. For example if
	 * expected is 9 only bit 9 is set For example if expected is 0 only bit 0 is
	 * set
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		MnistTrainer bmpFile = new MnistTrainer();
		MnistReader2 r = new MnistReader2();
		List<int[][]> images = r.getImages("C:\\workarea\\train-images.idx3-ubyte");
		List<double[]> inpuData = bmpFile.normalize(images, 28, 28);
		MNINSTLabels labels = new MNINSTLabels("C:\\workarea\\train-labels.idx1-ubyte");
		Layer inputLayer = new Layer("input", 28*28);
		Layer hiddenLayer = new Layer("hidden2", inputLayer, 40);
		
		double[][] tenBitArray = labels.asTenBitArray();
		double[] doubleArray = labels.asDoubleArray();
		Layer outputLayer = new Layer("output", hiddenLayer, 10);
		Neuron.learningRate =.2;
	//	DrawPanel.showNeurons();
		
		for(int trial =0; trial < 10; trial ++) {
			Cost theCost = new Cost(10);
	  int numWrong =0;
	 long startTime = System.currentTimeMillis();
		for (int image =0; image < 60000; image++) {
			
			double[] input = inpuData.get(image);
			//DrawPanel.input = images.get(image);
			inputLayer.setvalues(input);
			for(int innerTrial =0; innerTrial < 1; innerTrial ++) {
		
			
			outputLayer.sigmoid();
			double [] expected = tenBitArray[image];
			double [] output =  outputLayer.getvalues();
			double expected2 = labels.labels[image];
			
			double max = 0;
			int maxI = 0;
			for (int x =0; x < 10; x++) {
				if(output[x]  > max) {
					max = output[x];
					maxI= x;
				}
				
			}
			if(maxI != expected2) {
				numWrong ++;
			}
		
		
			theCost.addResult(expected, output);
			outputLayer.handleTopError(expected);
			outputLayer.sigmoid();
			
			}
           int df =0;
		}
		
		long diff = System.currentTimeMillis() - startTime;
		  System.out.println("Finished trial " + trial +" Taking ms " + diff);
		  System.out.println(" Num wrong " + numWrong);
		  System.out.println(" The cost is " + Equation.toString(theCost.getCost().values));
		}
		
	}
}
