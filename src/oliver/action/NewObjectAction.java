package oliver.action;



public class NewObjectAction extends Action{

	String type;


	public NewObjectAction(int pc, String type) {
		super(pc);
		this.type = type;
		
		this.returnType = type;
		
		// TODO Auto-generated constructor stub
	}
	
	
public String toString() {
		
	
	    
		
		 return String.format("new  %s ", type);
		
	}

public String print() {
	
	
    
	
	 return String.format("new  %s ", type);
	
}

}
