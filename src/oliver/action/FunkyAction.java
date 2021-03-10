package oliver.action;

public class FunkyAction extends AssignAction {
	VariableAction var;
	String ifString; 
	Action leftHS; 
	Action rightHS;
	
	
	public FunkyAction(int pc, VariableAction var, IfAction ifString, Action leftHS, Action rightHS) {
		super(pc);
		this.var = var;
		this.ifString = ifString.print().replace("if", "");
		this.leftHS = leftHS;
		this.rightHS = rightHS;
		// TODO Auto-generated constructor stub
	}
	public String toString() {
		return (print());
	}
	public String print() {
		//int m = (e == 0) ? (bits & 0x7fffff) << 1 : (bits & 0x7fffff) | 0x800000;
		return String.format("%s %s = %s ? (%s) : %s", var.getReturnType() , var.getName(), this.ifString, this.leftHS,this.rightHS);
	}
}
