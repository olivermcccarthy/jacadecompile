package oliver.neuron.pd;

import java.util.ArrayList;

public class ProductEquation extends Equation {

	Equation firstEquation;
	public ProductEquation(String equation, String firstEquation) {
		super(equation);
		this.firstEquation = new Equation(firstEquation);
		// TODO Auto-generated constructor stub
	}
	
	public double evaluate(double ... args) {
		
		double firstResult = this.firstEquation.evaluate(args);
		return super.evaluate(firstResult);
	}

	
	double[] partialDiffs(double[] inVals) {
		if (this.pDifss == null) {
			pDifss = new ArrayList<Equation>();
			for (int index = 0; index < 1; index++) {
				pDifss.add(this.partialDiff(index));
			}
		}
		Equation pd = this.pDifss.get(0);
		double[] vector = new double[inVals.length];
	
		
		double [] partialDiffsG = this.firstEquation.partialDiffs(inVals);
		double resultG = this.firstEquation.evaluate(inVals);
		
		for (int index = 0; index < inVals.length; index++) {
	        // Chain rule h(x,y) = f(g(x,y))
			// f only takes one result thats is g (x,y)
			// And partial diffs 
			//  dh(x) = df(g(x,y))*dgx(x,y))
			// dh(y)  = df(g(x,y))*dgy(x,y))
			// 
			
			double valuedG = partialDiffsG[index];
			double valuedF = pd.evaluate(resultG);
			double resDiv = valuedF*valuedG;
			vector[index] = resDiv;
			
		}

		return vector;
	}
	
	
	
	public static void testValues(Equation G, double [] in) {
double res = G.evaluate(in);
		
	
		
		double [] pdiff = G.partialDiffs(in);
		
		double v1 = in[0];
		in[0]=in[0] + .001;
		double res2 = G.evaluate(in);
		
		double diffRes = res2 - res;
		
		double mult = pdiff[0]  *0.001;
		
		
		double []err = new double[2];
	    err[0]=mult/diffRes;
		
		if(in.length > 1) {
			
			in[1]=in[1] + .001;
			in[0] = v1;
			res2 = G.evaluate(in);
			diffRes = res2 - res;
			mult = pdiff[1]  *0.001;
			 err[1] = mult/diffRes;
		}
		System.out.println("pdiffs of " + Equation.toString(in) + "= " +  Equation.toString(pdiff)  +" error " +  Equation.toString(err));

	}
	public static void main(String[] args) {

		
		// f(g(x))  = df(gx)*dg(x)
		
		// f = y^2 +y  g = x^3 +2x^2
		//   (2(x^3 +2x^2) +1)(3x^2 +4x)
		ProductEquation G = new ProductEquation("x^2 + x","x^3 + 2x^2 + y^2");
		
		
		
		double[] in = new double[] {3, 3};
	    testValues(G,in);		
	}
}
