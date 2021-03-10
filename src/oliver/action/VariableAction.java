package oliver.action;

import oliver.Variable;

public class VariableAction extends Action{

	String name;
	String type;

	boolean isString = false;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	public String getReturnType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public VariableAction(int pc, String type , String name) {
		super(pc);
		this.name = name;
		if(name.contains("start_pc") && type.length() ==0) {
			int debugMe =0;
		}
		this.type = type;
	}
	public VariableAction(int pc, Variable value) {
		super(pc);
		this.name = value.getName();
		if(value.containsQuotes) {
			int debugMe =0;
			this.isString = true;
		}
		if(name.contains("start_pc")) {
			int debugMe =0;
		}
		this.type = value.getType();
	}
	
	public String toFullString() {
		
			return  String.format("%s %s" , getType(), getName());
		
	}
public String toString() {
		
		
		
		return  String.format(" %s" , getName());
	}

public String print() {
	
	
	if(this.isString) {
		if(this.name.contains("\n")) {
			int debugME =0;
		} 
		String test = name.replace("\\", "\\\\");
	    test = test.replace("\n", "\\n");
		
		test = test.replace("\"", "\\\"");
		test= "\"" + test + "\"";
		return test;
	}
	
	return  String.format(" %s" , getName());
}
}
