package oliver.action;



public class FinallyAction extends Action{

	
	public FinallyAction (int pc, int goToLabel) {
		 super(pc);
		this.goToLabel = goToLabel -1;
	}
	public String print() {
		return "finally ";
	}
}
