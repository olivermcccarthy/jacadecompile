package oliver.neuron;

import java.util.ArrayList;
import java.util.List;
/**
 * Get the root mean square of the difference between actual and expected
 * To use this class call addResult for each result you get passing actual and expected
 * To get the cost then call getCost;
 * Expected and actual are arrays  
 * @param tupleSize
 */
public class Cost implements Comparable<Cost>{

	ArrayList<Tuple> rootMeanSquare = new ArrayList<Tuple>();
	int tupleSize =0;
	

	/**
	 * Get the root mean square of the difference between actual and expected
	 * @param tupleSize
	 */
	public Cost(int tupleSize) {
		this.tupleSize = tupleSize;
	}
	public void addResult(double [] expected, double [] actual) {
		
		
		double [] meanSq = new double[expected.length];
		for(int x =0; x < expected.length; x++) {
			meanSq[x]= (expected[x] - actual[x])* (expected[x] - actual[x]);
			
		}
		Tuple tuple = new Tuple(meanSq);
		this.rootMeanSquare.add(tuple);
	}
	Tuple cost;
	public Tuple getCost() {
		double []doubleRess = new double[tupleSize];
		cost = new Tuple(doubleRess);
		for(Tuple tuple : rootMeanSquare) {
			cost.add(tuple);
		}
		cost.divide(2*this.rootMeanSquare.size());
		
		return cost;
	}
		
	public String toString() {
		return "Cost " + this.cost ;
	}
	@Override
	public int compareTo(Cost o) {
		
		double db2 = o.cost.getAverage();
		if(this.cost.getAverage() < db2) {
			
			return -1;
		}
        if(this.cost.getAverage() > db2) {
			
			return 1;
		}
		// TODO Auto-generated method stub
		return 0;
	}
}
