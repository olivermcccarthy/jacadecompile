package oliver.action;



public class IgnoreAction extends Action{

	
	public IgnoreAction (int pc, int goToLabel) {
		 super(pc);
		this.goToLabel = goToLabel;
	}

	public String print() {
		return "";
	}
}
