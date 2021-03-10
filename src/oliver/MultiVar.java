package oliver;

import java.util.ArrayList;

public class MultiVar {

	ArrayList<Variable> vars = new ArrayList<Variable>();
	public  MultiVar( Variable var) {
		vars.add(var);
	}
	
	public static boolean isCatch = false;
  public  void addVariable ( Variable var) {
	  vars.add(var);
	}
  
   public Variable getVariable(int codeIndex) {
	   if(isCatch) {
		   codeIndex ++;
		   
	   }
	   isCatch=false;
	   for(Variable var : vars) {
		
		   if ( (codeIndex +2 >= var.startPC) && (codeIndex <= var.endPC)) {
			  
				return var;
			}
	   }
	   return null;
	   
   }
   
   
   public static void init() {
	   
   }
	
}
