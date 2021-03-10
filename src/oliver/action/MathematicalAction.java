package oliver.action;

public class MathematicalAction extends Action{

	
    Action leftHS; 

   public String getMathStr() {
		return mathStr;
	}


public Action getLeftHS() {
		return leftHS;
	}


	public Action getRightHS() {
		return rightHS;
	}


String mathStr; Action rightHS;
	
	public MathematicalAction(int pc, Action leftHS,  String mathStr, Action rightHS) {
		super(pc);
		
		this.rightHS = rightHS;
		this.leftHS = leftHS;
		this.mathStr = mathStr.trim();
		

		// TODO Auto-generated constructor stub
	}
	
	
	public String toString() {
		
		 return String.format(" (%s %s %s)", leftHS, mathStr,rightHS);
		
	}
	
	
	public String print() {
		
		if(mathStr.contains("=")) {
			 return String.format("%s %s %s", leftHS.print(), mathStr,rightHS.print());
		}
        return String.format("( %s %s %s)", leftHS.print(), mathStr,rightHS.print());
	}
}
