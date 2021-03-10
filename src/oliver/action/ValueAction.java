package oliver.action;

import oliver.Variable;

public class ValueAction extends Action{

	Object value;

	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}


	public ValueAction(int pc, Object value) {
		super(pc);
		if(value != null) {
		this.returnType = value.getClass().getSimpleName();
		}
		this.value = value;
	}
	
	
public String toString() {
		
		
		
		return  String.format(" %s" , value);
	}

public String print() {
	
	
	if(value instanceof Character) {
		return  String.format("'%s'" , value);
	}
	return  String.format("%s" , value);
}
}
