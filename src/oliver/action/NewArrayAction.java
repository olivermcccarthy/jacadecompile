package oliver.action;

import java.util.ArrayList;

import oliver.Variable;

public class NewArrayAction extends Action{

	String type;
	Action size;
	public boolean fullArray;
	public ArrayList<Action> ArrayParts = new ArrayList<Action>();
	boolean appendBracket = false;
	public NewArrayAction(int pc, String type, Action size) {
		super(pc);
		this.type = type;
		if (type.contains("[")) {
		
			this.type = Variable.getTheType(type);
			this.type = this.type.replace("[]", "");
			appendBracket = true;
		}
		this.size = size;
		
	
		this.returnType = type +"[]";
		// TODO Auto-generated constructor stub
	}
	
	
public String print() {
		
	
	    if(this.fullArray) {
	    	String params = "";
	    	for(int x =0; x < this.ArrayParts.size(); x++) {
	    		params += this.ArrayParts.get(x).print();
	    		if(x < this.ArrayParts.size() -1) {
	    			params += ",";
	    		}
	    		
	    	}
	    	
	    	
	    	 return String.format("new  %s [] {%s}", type,params);
	    }
		if(appendBracket) {
			 return String.format("new  %s [%s][]", type,size);
		}
		 return String.format("new  %s [%s]", type,size);
		
	}

}
