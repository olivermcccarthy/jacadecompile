package oliver.action;

import oliver.Variable;

public class StaticFieldAction extends FieldAction{



	public StaticFieldAction(int pc, Variable field) {
		super(pc);
		this.returnType = field.getType();

		this.field = field;
		
		// TODO Auto-generated constructor stub
	}

public String toString() {
		
		
	
		return  String.format(" %s.%s" , field.getClassName(), field.getName());
	}

public String print() {
	
	
	
	return  String.format(" %s.%s " , field.getClassName(), field.getName());
}
}
