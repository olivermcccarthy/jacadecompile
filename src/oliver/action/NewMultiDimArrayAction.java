package oliver.action;

import java.util.ArrayList;

public class NewMultiDimArrayAction extends Action{

	String type;
	ArrayList<Action> sizes;
	public boolean fullArray;
	public ArrayList<Action> ArrayParts = new ArrayList<Action>();
	public NewMultiDimArrayAction(int pc, String type, ArrayList<Action> sizes) {
		super(pc);
		this.type = type;
		this.sizes = sizes;
		if(this.type.contains("[I")) {
			int debugME =90;
		}
	
		this.returnType = type +"[[]]";
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
	    String params ="";
	    for(int x =0; x < this.sizes.size(); x++) {
    		params += this.sizes.get(x).print();
    		if(x < this.sizes.size() -1) {
    			params += "][";
    		}
    		
    	}
		 return String.format("new  %s [%s]", type,params);
		
	}

}
