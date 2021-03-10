package oliver.neuron.pd;

import java.util.ArrayList;
import java.util.List;

public class Equation {

	List<EquationPart> parts = new ArrayList<EquationPart>();

	enum TYPE {
		ADD, SUB, MULT, DIV
	};

	static String MATHS = "+-/*";
	List<TYPE> types = new ArrayList<TYPE>();
	String eqStr;

	double combine(double A, double B, TYPE t) {
		double res = 0;
		switch (t) {
		case ADD:
			return A + B;
		case SUB:
			return A - B;
		case MULT:
			return A * B;
		case DIV:
			return A / B;
		}
		return res;
	}

	public Equation(Equation other) {
		for(EquationPart chP : other.parts) {
			this.parts.add(new EquationPart(chP));
		}
		
		this.types.addAll(other.types);
	}

	public Equation(String equation) {
		eqStr = equation;
		String[] parts = equation.split(" ");
		types.add(TYPE.ADD);
		for (String part : parts) {
			part = part.trim();
			if (MATHS.indexOf(part) >= 0) {
				types.add(TYPE.values()[MATHS.indexOf(part)]);
			} else {
				this.parts.add(new EquationPart(part));
			}

		}
	}

	public void rebuildEq() {
		String res = "";

		for (int index = 0; index < parts.size(); index++) {
			if (index > 0) {
				res += MATHS.charAt(this.types.get(index -1).ordinal());
			}
			res += " ";
			this.parts.get(index).rebuildEq();
			res += this.parts.get(index).toString();
		}
		eqStr = res;
	}

	public String toString() {

		return this.eqStr;
	}

	Equation partialDiff(int byIndex) {
		Equation partialDiff = new Equation(this);
		partialDiff.parts.clear();
		for (int x = 0; x < this.parts.size(); x++) {
			partialDiff.parts.add(this.parts.get(x).partialDiff(byIndex));
		}
		partialDiff.rebuildEq();
		return partialDiff;
	}

	List<Equation> partialDiffs() {
		List<Equation> pDifss = new ArrayList<Equation>();
		for (int index = 0; index < EquationPart.vars.length; index++) {
			pDifss.add(this.partialDiff(index));
		}

		return pDifss;
	}
	List<Equation> pDifss = null;
	double []  partialDiffs(double [] inVals) {
		if(this.pDifss == null) {
		pDifss = new ArrayList<Equation>();
		for (int index = 0; index < inVals.length; index++) {
			pDifss.add(this.partialDiff(index));
		}
		}
		double[] vector = new double[3];
		int index = 0;

		for (Equation pd : pDifss) {
			double res = pd.evaluate(inVals);

			vector[index] = res;
			index++;
		}
		
		
		return vector;
	}
	public static double speed(double []inV) {
		double squared = 0;
		
		for(int index =0; index < inV.length; index ++) {
			squared += Math.pow(inV[index], 2);
		}
		return Math.sqrt(squared);
	}
	double directionalDirivitive(double [] atPoint , double [] direction) {
		
		double[] partialDiffs =  partialDiffs(atPoint);
		double res = 0;
		for(int index = 0; index < atPoint.length; index ++) {
			res += (partialDiffs[index] * direction[index]);
		}
		res = Math.sqrt(res);
		
		res = res / Math.sqrt(speed(direction));
		
		return res;
	}
	
	
	public double evaluate(double... args) {
		int index = 0;
		double res = 0.0;
		for (EquationPart eqP : this.parts) {
			res = combine(res, eqP.evaluate(args), this.types.get(index));
			index++;
		}

		return res;
	}
	public double []evaluatePerPart(double... args) {
		int index = 0;
		double []res = new double[this.parts.size() +1];
		for (EquationPart eqP : this.parts) {
			res[index]=  eqP.evaluate(args);
			index ++;
		}

		return res;
	}
	public static void main(String[] args) {

		
		// f(g(x))  = df(gx)*dg(x)
		
		// f = y^2 +y  g = x^3 +2x^2
		//   (2(x^3 +2x^2) +1)(3x^2 +4x)
		Equation G = new Equation("x^3 + 2x^2");
		
		Equation F = new Equation("x^2 + x");
		Equation dG = G.partialDiff(0);
		Equation dF = F.partialDiff(0);
		double valueG = G.evaluate(3);
		double valuedG = dG.evaluate(3);
		double valuedF = dF.evaluate(valueG);
		
		double resDiv = valuedF*valuedG;
		
		
		System.out.println(dG);
		System.out.println(dF);
		Equation eq = new Equation("4x*y^2*2z + 4x*6y^2 + 3x*z");
		System.out.println(eq);
		double res = eq.evaluate(2, 3.4, 5.6);

		System.out.println(res);
		List<Equation> partialDiffs = eq.partialDiffs();

		System.out.println(partialDiffs);

		eq = new Equation("4x^4*y^2*2z^2 + 4x*6y^2 + 3x*z");
		System.out.println(eq);
		res = eq.evaluate(2, 3.4, 5.6);

		System.out.println(res);
		partialDiffs = eq.partialDiffs();

		double[] vector = new double[3];
		int index = 0;

		for (Equation pd : partialDiffs) {
			res = pd.evaluate(2, 3.4, 5.6);

			vector[index] = res;
			index++;
		}
		vector = normalize(vector);

		System.out.println(partialDiffs);
		System.out.println(toString(vector));

		index = 0;
		for (Equation pd : partialDiffs) {
			res = pd.evaluate(22, 32.4, 5.6);

			vector[index] = res;
			index++;
		}
		vector = normalize(vector);

		System.out.println(toString(vector));
	}

	private static double[] normalize(double[] in) {
		double first = in[0];
		double[] res = new double[in.length];
		for (int index = 0; index < in.length; index++) {
			res[index] = in[index] / first;
		}
		return res;
	}

	public static String toString(double[] in) {
		String res = "";
		for (int index = 0; index < in.length; index++) {
			if (index > 0) {
				res += ",";
			}
			res += in[index];
		}
		return res;
	}

	public void change(double[] diff) {
		
		for(EquationPart part : this.parts) {
			part.change(diff);
		}
		this.pDifss = null;
		
		
	}
}
