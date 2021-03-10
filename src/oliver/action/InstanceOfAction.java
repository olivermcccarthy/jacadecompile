package oliver.action;

public class InstanceOfAction extends Action{
Action leftHS; Action rightHS;
	
	public InstanceOfAction(int pc, Action leftHS,  Action rightHS) {
		super(pc);
		
		this.rightHS = rightHS;
		this.leftHS = leftHS;
		this.returnType = "boolean";
	
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		
		 return String.format("( %s instanceof %s)", leftHS, rightHS);
		
	}


public String print() {
		
        return String.format("( %s instanceof %s)", leftHS.print(), rightHS.print());
	}
}
