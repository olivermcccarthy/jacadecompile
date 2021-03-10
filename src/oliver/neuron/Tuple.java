package oliver.neuron;

public class Tuple {

	
	

		double[] values;
		Tuple (double [] values){
			this.values = new double[values.length];
			for(int x=0; x < values.length; x++) {
				this.values[x] = values[x];
			}
			this.values = values;
		}
	    
		public void add(Tuple t) {
			for(int x =0; x < values.length; x++) {
				this.values[x] += t.values[x];
			}
		}
		public String toString() {
			return TruthTable.printArr(this.values);
		}
		public void divide(double div) {
			for(int x =0; x < values.length; x++) {
				this.values[x] /= div;
			}
		}
		public double getMax() {
			double sum =0;
			for(int x =0; x < values.length; x++) {
				 if(this.values[x]> sum) {
					 sum = this.values[x];
				 }
			}
			return sum;
		}
		public double getAverage() {
			double sum =0;
			for(int x =0; x < values.length; x++) {
				 sum += this.values[x];
			}
			return sum/this.values.length;
		}
		public int getNumber() {
			int sum =0;
			for(int x =0; x < values.length; x++) {
				int fl = (int)Math.floor(this.values[x]);
				 
				 sum = sum << 2;
				 sum = sum | fl;
			}
			return sum;
		}
}
