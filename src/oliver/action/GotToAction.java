package oliver.action;

import java.util.ArrayList;

public class GotToAction extends Action{

	
	Action nextAction = null;

	int leastContinue =0;
	
	ArrayList<Action> posibleContinues = new ArrayList<Action>();
	public boolean isBreak() {
		return isBreak;
	}
	public void setBreak(boolean isBreak) {
		this.isBreak = isBreak;
	}
	IfAction otherIf = null;
	public boolean isFor;

	public int lastBack;
	public GotToAction(int pc, int goToLabel) {
		super(pc);
		this.goToLabel = goToLabel;
		this.setRealGoto(this.goToLabel);
		// TODO Auto-generated constructor stub
	}
	public String toString() {
		if(this.isContinue) {
			return String.format("continue;// -> " + this.goToLabel);
		}
		if(this.isBreak) {
			return String.format("break;// -> " + this.getRealGoto());
		}
		if(this.isWhile) {
			
			if(this.getPc() == 223) {
				int debugME =0;
			}
			otherIf.setValid(true);
			String ifStr = otherIf.print();
			otherIf.setValid(false);
			for(IfAction dd : otherIf.otherIfs) {
				dd.setValid(false);
			}
			
			ifStr = ifStr.replaceFirst("if", "");
			ArrayList<Action> dubiousContinuesForThisWhile = ActionStack.dubiousContinues
					.get(getPc());
			if (!ifStr.contains("nusedVariable") && dubiousContinuesForThisWhile != null && dubiousContinuesForThisWhile.size() == 1) {
				int debugME =0;
				//if(dubiousContinuesForThisWhile.get(0) instanceof MathematicalAction) {
			    	dubiousContinuesForThisWhile.get(0).setValid(false);
				    ActionStack.dubiousContinues.remove(getPc());
				    return String.format("for (;%s;%s)", ifStr,dubiousContinuesForThisWhile.get(0).print());
				//}else {
				//	 ActionStack.dubiousContinues.remove(getPc());
				//}
				
			}
			
			return String.format("while (%s)", ifStr);
			
		}
		if(this.isElse) {
			return String.format(" else ");
		}
		 return String.format(" GOTO %d ", goToLabel);
		
	}
	public String print() {
		
		 return toString();
		
	}
}
