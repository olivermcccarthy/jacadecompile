package oliver.action;import oliver.Variable;

public class AssignArrayIndexAction extends AssignAction{

	
	ArrayIndexAction leftHS; Action rightHS;
	
	public AssignArrayIndexAction(int pc, ArrayIndexAction leftHS,  Action rightHS) {
		super(pc);
		
		this.rightHS = rightHS;
		this.leftHS = leftHS;
		if(rightHS.print().trim().equals("0") || (rightHS.print().trim().equals("1"))){
			int debugME=0;
		}
		
		
		// TODO Auto-generated constructor stub
	}

	
	public String toString() {
		return String.format(" %s = %s ", leftHS.print(), rightHS.print());
	
		
	}
	public String print() {
		
		/*if(this.leftHS instanceof ValueAction) {
			ValueAction valueA = (ValueAction)this.leftHS;
			
			return String.format(" %s = %s ", valueA.toFullString(), rightHS.print());
		}
		*/
		
		
		return  String.format(" %s = %s ", leftHS.print(), rightHS.print());
	}
}
