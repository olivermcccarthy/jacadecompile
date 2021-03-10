package oliver.neuron.pd;

public class RunEquation {

	
	
	public static double [] randomVals(int num, int max, int min) {
		
		double [] res = new double[num];
		double range = max -min;
		for(int index =0; index < num; index ++) {
			double rand = Math.random() *range;
			
			rand += min;
			rand = Math.floor(rand);
			res [index] = rand;
		}
		
		return res;
		
	}
	public static void main(String [] args) {
		
		Equation testEq = new Equation("4.3x*z + 67x^2 + 4x*3y + 45y*z - 3x + 2y");
		Result result = new Result();
		for(int x = 0; x < 300; x++) {
			double []inVals = randomVals(3,100,-100);
			double res=  testEq.evaluate(inVals);
		    result.addResult(inVals, res);
		}
		/// 3,2,4
		//  1,1,1  = 3 + 2 + 4/Sqrt3 
		//result.print();
		long startTime= System.currentTimeMillis();
		Equation trialEq = new Equation("x*z + x^2 + x*y  + x +  y*z + y");
			
		
		
		double [] solved = result.solveEquation(trialEq);
		long diff = System.currentTimeMillis() - startTime;
	System.out.println(trialEq  + " solved " +Equation.toString(solved) +" in " + diff);
	
	
	}
}
