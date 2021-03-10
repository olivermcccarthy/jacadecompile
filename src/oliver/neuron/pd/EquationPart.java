package oliver.neuron.pd;

public class EquationPart {
	
	
	//2xypow(z,2)
	static String [] vars = new String[] {"x","y","z"};
	double []mult = new double[vars.length];
	double[] pow = new double[vars.length];
	String eqStr ;
	public EquationPart (EquationPart other) {
		for(int index =0; index < other.mult.length; index ++) {
			mult[index] = other.mult[index];
			this.pow[index] = other.pow[index];
			this.eqStr = other.eqStr;
		}
	}
	
	public void rebuildEq() {
		String res ="";
		for(int index =0; index < vars.length; index++) {
			
			
			if(mult[index] != 0) {
				if(res.length() > 0) {
					res += "*";
				}
				res += mult[index] + vars[index];
				if(pow[index] != 1) {
					res += "^" + pow[index];
				}
			}
			
		}
		eqStr = this.overAllMult + "*"+res;
	}
	
	public String toString () {
		
		return this.eqStr;
	}
	public EquationPart (String part) {
		
		eqStr = part;
		int index =0;
		String [] parts = part.split("\\*");
		
		for(String var : vars) {
			mult[index]=0;
			pow[index]=1;
			index ++;
		}
		
		for(String p : parts) {
			index =0;
			for(String var : vars) {
				if(p.contains(var)) {
					int varIndex = p.indexOf(var);
					String beforeVar = p.substring(0, varIndex);
					mult[index]=1;
					if(varIndex > 0) {
					   mult[index]=Double.valueOf(beforeVar);
					}
					if(varIndex < p.length() -1) {
						String afterVar = p.substring(p.indexOf("^") +1, p.length());
						   pow[index]=Double.valueOf(afterVar);
					}
					break;
				}
				index ++;
			}
			
		}
		
		
	}
	
	public double evaluate(double ... entries) {
		
		if(overAllMult == 0) {
			return 0;
		}
		int index =0;
		double res = 1.0;
		for(double value : entries) {
			if(this.mult[index] > 0) {
			   res *= this.mult[index] * (Math.pow(value, this.pow[index]));
			}
			index ++;
		}
		 res *= overAllMult;
		
		return res;
	}
	double overAllMult = 1;
	
	EquationPart partialDiff (int byIndex) {
		EquationPart partialDiff = new EquationPart(this);
		double oldpow = this.pow[byIndex];
		if(this.mult[byIndex] == 0) {
			partialDiff.overAllMult = 0;
			return partialDiff;
		}
		if(oldpow == 1) {
			partialDiff.overAllMult = oldpow * partialDiff.mult[byIndex ];
			partialDiff.mult[byIndex ]=0;
		}else {
			
			partialDiff.mult[byIndex ] *= (oldpow);
			partialDiff.pow[byIndex ] = oldpow -1;
		}
		return partialDiff;
	}
	public static void main (String [] args) {
		
		EquationPart eq = new EquationPart("4x*y^2*2z^3");
		
		double res =  eq.evaluate(2,3.4,5.6);
		
		System.out.println(res);
		
		
		
		
		
		
	}

	public void change(double[] diff) {
		// TODO Auto-generated method stub
		for(int index =0; index < diff.length; index ++) {
			if(this.mult[index] != 0) {
			this.mult[index] += diff[index];
			}
		}
	}
	
}
