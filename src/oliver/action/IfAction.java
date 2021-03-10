package oliver.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class IfAction extends Action{

	
	Action leftHS; String compare; Action rightHS;
	
	public Action getLeftHS() {
		return leftHS;
	}
	public Action getRightHS() {
		return rightHS;
	}

	ArrayList<IfAction> otherIfs = new ArrayList<IfAction>();

	public IfAction(int pc, int goToLabel, Action leftHS, String compare, Action rightHS) {
		super(pc);
		//290->297
		if (  290 ==  pc && goToLabel == 297){ //9->15
		     int   unusedVariable24 =  0 ;//13 oliver.action.AssignAction
		  }
		this.isIf = true;
		this.goToLabel = goToLabel;
		
		this.setRealGoto(goToLabel);
		this.rightHS = rightHS;
		this.leftHS = leftHS;
		this.compare = compare;
		if (this.compare.contains(".equals")) {
			
			this.compare = "equals";
		}
		
      if(leftHS instanceof DoubleEqualsAction && rightHS instanceof ValueAction) {
    	  ValueAction val = (ValueAction)rightHS;
    	  if(this.compare.equals("!=" ) || this.compare.equals("!=" )) {
    	    if(val.value.toString().equals("0")) {
    		  val.value = false;
    	    }
    	    if(val.value.toString().equals("1")) {
    		  val.value = true;
    	    }
    	  }else {
    		  DoubleEqualsAction aaa = (DoubleEqualsAction)leftHS;
    		  if(val.value.toString().equals("0")) {
        		 
        		  this.leftHS = aaa.rightHS;
        		  this.rightHS = aaa.leftHS;
        	    }
        	    if(val.value.toString().equals("1")) {
        	    	 this.leftHS = aaa.rightHS;
           		     this.rightHS = aaa.leftHS;
        	    }  
    		  
    	  }
      }
	
		// TODO Auto-generated constructor stub
	}
	public String toString() {
		
		 return String.format("( %s %s %s)// GOTO %d ", leftHS, compare, rightHS, goToLabel);
		
		 
	}
	public String reverseIf(String prevIf) {
		prevIf=prevIf.trim();
		if (prevIf.equals("==")) {
			prevIf = prevIf.replace("==", "!=");
		} else if (prevIf.equals("!=")) {
			prevIf = prevIf.replace("!=", "==");
		} else if (prevIf.equals("<=")) {
			prevIf = prevIf.replace("<=", ">");
		} else if (prevIf.equals("<")) {
			prevIf = prevIf.replace("<", ">=");
		} else if (prevIf.equals(">")) {
			prevIf = prevIf.replace(">", "<=");
		} else if (prevIf.equals(">=")) {
			prevIf = prevIf.replace(">=", "<");
		} else if (prevIf.equals("equals")) {
			prevIf="notequals";
		}
		 else if (prevIf.equals("notequals")) {
				prevIf="equals";
			}
	
		return prevIf;
	}

	public String print() {
		String ret = "if (";
		if (this.getPc() == 373) {
			int debugME = 0;
		}

		if (isValid() == false) {
			return "";
		}
		if(otherIfs.size()  > 4) {
			int debugME =0;
		}
		if(this.getRealGoto() > 0 && this.getRealGoto() < this.goToLabel && this.getRealGoto() == this.andGoTo) {
			int debugME =0;
		}
		String compare = this.compare;
		ArrayList<IfAction> otherIfs = new ArrayList<IfAction>();
		otherIfs.addAll(this.otherIfs);
		otherIfs.add(this);
		Collections.sort(otherIfs);
		boolean lastWasOr = false;
		if(otherIfs.size() == 1 && this.andGoTo != 0) {
			this.andGoTo = 0;
		}
		HashSet<Integer> pcsHandled = new HashSet<Integer>();
		for (IfAction IFF : otherIfs) {
            if(pcsHandled.contains(IFF.getPc())) {
            	continue;
            }
            if(IFF.isContinue) {
            	int debugME =0;
            }
            pcsHandled.add(IFF.getPc());
			if (ret.length() > 5) {
				if(lastWasOr ) {
	    			ret += " || ";
	    		}else {
	    			ret += " && ";
	    		}
	    		
			}

			compare = IFF.compare;
			if(IFF.getRealGoto() == this.andGoTo || IFF.goToLabel < IFF.getPc()) {
    			lastWasOr = true;
    			compare = reverseIf(IFF.compare);
    			
    		}
			if(this.andGoTo != 0 && IFF.originalGoTo == this.andGoTo) {
				lastWasOr = true;
    			compare = reverseIf(IFF.compare);
			}
			if (compare.equals("equals")) {
				ret += String.format("( %s.equals(%s))", IFF.leftHS.print(), IFF.rightHS.print());
			} else if (compare.equals("notequals")) {
				ret += String.format("(! %s.equals(%s))", IFF.leftHS.print(), IFF.rightHS.print());
			} else {
				ret += String.format("( (%s) %s (%s))", IFF.leftHS.print(), compare, IFF.rightHS.print());
			}

		}

		
		if(this.isContinue) {
			ret += ")" +"/*CONTINUEX" + this.isContinue+"*/";
			return ret;
		}
		ret += ")";

		return ret;

	}
	int andGoTo;

	public int originalGoTo =0;


	
	
	
	public boolean isValid() {
		if(this.otherIfs.size() > 0) {
			
			return true;
		}
		return super.isValid();
	}
	
}
