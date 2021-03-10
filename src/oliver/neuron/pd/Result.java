package oliver.neuron.pd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Result {
	List<double[]> inVals = new ArrayList<double[]>();
	List<Double> results = new ArrayList<Double>();

	public void addResult(double[] invals, double res) {
		this.inVals.add(invals);
		this.results.add(res);
	}

	public void print() {
		for (int x = 0; x < inVals.size(); x++) {
			String res = Equation.toString(inVals.get(x)) + " = " + results.get(x);
			System.out.println(res);
		}
	}

	public static double speed(double[] inV) {
		double squared = 0;

		for (int index = 0; index < inV.length; index++) {
			squared += Math.pow(inV[index], 2);
		}
		return Math.sqrt(squared);
	}
	List<double[]> newResultPerPart;
	Iterator<double[]> iter;
	public double [] solveEquation(Equation newEq) {
		List<Double> newResults = new ArrayList<Double>();
		newResultPerPart = new ArrayList<double[]>();
		int index = 0;
		for (double[] inV : this.inVals) {
			double res = newEq.evaluate(inV);
			newResults.add(res);
			double[] arr1 = newEq.evaluatePerPart(inV);
			arr1[arr1.length - 1] = this.results.get(index);
			newResultPerPart.add(arr1);
			index++;
		}
		double diff = diff(newResults);

		/**
		 * 16A + 4B +2C = 788 
		 * 12A + 3B +C = 988 
		 * 4A + 3B + 8C = 566
		 * 
		 * 
		 */

		iter= newResultPerPart.iterator();
		
		int largestCoeff = this.findlargestCoef(newResultPerPart, 7);
		double []known = new double[newEq.parts.size()];
		for(int c =0 ; c < newEq.parts.size(); c++) {
			known[c] = Double.NaN;
		}
		double [] ref = noCoef( largestCoeff,0,newEq.parts.size());
		double [] coeffs = new double[newEq.parts.size()];
		coeffs[largestCoeff] = ((ref[newEq.parts.size() ]*100/ref[largestCoeff]));
		coeffs[largestCoeff]= Math.round(coeffs[largestCoeff]);
		coeffs[largestCoeff]= coeffs[largestCoeff]/100;
		known[largestCoeff] = coeffs[largestCoeff];
		for(int c =0 ; c < newEq.parts.size() ; c++) {
			if(c != largestCoeff) {
				ref = noCoef( c,0,newEq.parts.size());
				coeffs[c] = ((ref[newEq.parts.size() ]*100/ref[c]));
				coeffs[c]= Math.round(coeffs[c]);
				coeffs[c]= coeffs[c]/100;
				known[c] = coeffs[c];
			}
		}
		return coeffs;
	}

	
	boolean solveable(double [] inputs, int coeff) {
		
		for(int c =0; c < coeff; c++) {
			while(inputs[c ] == 0) {
				return false;
			}
		}
		return true;
	}
	double  []  noCoef(int notCoeff ,int coeff, int numvars) {
		
	
		if (coeff == numvars -1 || (coeff == numvars -2 && notCoeff == coeff +1)) {
		    double[] arr1 = getNonZero();
		  
		    double[] arr2 = getNonZero();
	    	double[] noCoef = normalizeAndSubract(arr1, arr2, coeff);
	    	int trial =0;
	    	while(!solveable(noCoef,  coeff)) {
	    		arr2 = getNonZero();
	    		noCoef = normalizeAndSubract(arr1, arr2, coeff);
	    		trial ++;
	    		if(trial > 20) {
	    			throw new RuntimeException("Cannot solve " + coeff);
	    		}
	    	}
	    	return  noCoef;
		}else {
			
			 double[] arr1 = noCoef( notCoeff ,coeff +1, numvars);
		
			 if(coeff == notCoeff) {
				 return arr1;
			 }
			
			 double[] arr2 = noCoef(  notCoeff ,coeff +1, numvars);
			 double[] noCoef = normalizeAndSubract(arr1, arr2, coeff );
			 
			 int trial =0;
		    	while(!solveable(noCoef,  coeff)) {
		    		arr2 = noCoef( notCoeff ,coeff +1, numvars);
		    		noCoef = normalizeAndSubract(arr1, arr2, coeff);
		    		trial ++;
		    		if(trial > 20) {
		    			throw new RuntimeException("Cannot solve " + coeff);
		    		}
		    	}
			 
		    	return  noCoef;
		}
		
	}
	
   int findlargestCoef(List<double[]> list, int numseraches) {
	   int ret =0;
	   double []totals = null;
	   for(int index =0; index < numseraches; index++) {
		   double[] ty = list.get(index);
		   if(totals == null) {
			   totals = new double[ty.length];
			   for(int y=0; y < ty.length -1; y ++) {
				   totals[y] += Math.abs(ty[y]);
			   }
		   }
	   }
	   double maxValue =0;
	   
	   for(int y=0; y < totals.length -1; y ++) {
		   if(totals[y]  > maxValue) {
			   ret =y;
			   maxValue = totals[y];
		   }
	   }
	   return ret;
	   
   }
	double[] getNonZero() {
		double[] res = null;
		if(iter.hasNext() == false) {
			iter = newResultPerPart.iterator();
		}
		while(iter.hasNext()) {
			res =iter.next();
			boolean ok= true;
			for(int df = 0; df < res.length; df++) {
				if(res[df] == 0) {
					ok = false;
					break;
				}
			}
			
			
		}
		
		return res;
	}

	double[] normalizeAndSubract(double[] arr1, double[] arr2, int index) {

		double[] res = new double[arr1.length];
		double[] res1 = new double[arr1.length];
		double[] res2 = new double[arr1.length];
		double divideBy = arr1[index];
	
		double divideBy2 = arr2[index];
	
		
		for (int df = 0; df < arr1.length; df++) {
			
			double d1 = arr1[df]/divideBy;
			res1[df] = d1;
			
			double d2 = arr2[df]/divideBy2;
			res2[df] = d2;
			double d3= d1 -d2;
			res[df] = d3;
			
		}
		
		
		return res;
	}

	double[] normalize(List<double[]> newResults, int numVars, double diff2) {

		double[] diff = new double[numVars];

		for (int x = 0; x < newResults.size(); x++) {

			double[] B = newResults.get(x);
			for (int y = 0; y < numVars; y++) {
				if (diff2 > 0) {
					diff[y] -= B[y];
				} else {
					diff[y] += B[y];
				}

			}
		}
		for (int y = 0; y < numVars; y++) {
			diff[y] = diff[y] / newResults.size();
		}
		return diff;

	}

	double diff(List<Double> newResults) {

		double diff = 0;
		for (int x = 0; x < newResults.size(); x++) {
			double A = this.results.get(x);
			double B = newResults.get(x);

			diff += Math.sqrt(Math.pow(A - B, 2));

			/*
			 * if(A > B) { diff += (A -B); }else { diff += (B -A); }
			 */
		}
		return diff / newResults.size();

	}

}
