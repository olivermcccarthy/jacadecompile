package oliver.action;

import oliver.Variable;

public class CastAction extends Action{

	
	Variable castTo; Action rightHS;
	String castToStr;
	public CastAction(int pc, Variable castTo,  Action rightHS) {
		super(pc);
		
		this.rightHS = rightHS;
		this.castTo = castTo;
		
		if(castTo.getType().contains("[")) {
			int debugME =0;
		}
		this.returnType = castTo.getType();
	
		// TODO Auto-generated constructor stub
	}
	
	public CastAction(int pc, String castTo,  Action rightHS) {
		super(pc);
		
		this.rightHS = rightHS;
		this.castTo = null;
		castToStr = castTo;
		this.returnType = castTo;
		// TODO Auto-generated constructor stub
	}
	
	
	public String toString() {
		if(castTo != null) {
		  return String.format("(( %s)(%s))", Variable.getTheType(castTo.getType()), rightHS);
		}
		 return String.format("(( %s)(%s)) ", castToStr, rightHS);
		
	}

	public String getCastTo() {
		if(castTo != null) {
			return castTo.getType();
		}
		return castToStr;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return toString();
	}
}
