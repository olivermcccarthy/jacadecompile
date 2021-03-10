package oliver.action;

import oliver.Variable;

public class FieldAction extends Action{

	Variable field;
	Action valueAction ;
	public FieldAction(int pc, Action valueAction, Variable field) {
		super(pc);
		this.returnType = field.getType();
		this.valueAction = valueAction;
		this.field = field;
		
		
		
		// TODO Auto-generated constructor stub
	}
	public FieldAction(int pc) {
		super(pc);
		// TODO Auto-generated constructor stub
	}
	public String toString() {
		
		
		 return String.format(" %s.%s ", valueAction, field.getName());
		
	}
	
	public String print() {
		
		 if(valueAction.print().equals("null")) {
			 int debugme = 0;
		 }
		 return String.format(" %s.%s ", valueAction, field.getName());
		
	}
	
}
