package oliver.action;



public class SwitchAction extends Action{

	Action switchOn;
	
	public SwitchAction (int pc, int goToLabel, Action catchType) {
		 super(pc);
		this.goToLabel = goToLabel;
		this.switchOn = catchType;
	}

	public String print() {
		return String.format("switch (%s)  ",this.switchOn.print());
	}
}
