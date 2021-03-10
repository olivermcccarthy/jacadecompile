package oliver.action;



public class CatchAction extends Action{

	String catchType;
	public String expName;
	public CatchAction (int pc, int goToLabel, String catchType) {
		 super(pc);
		this.goToLabel = goToLabel;
		this.catchType = catchType;
	}

	public String print() {
		return String.format("catch ( %s %s)",this.catchType, this.expName);
	}
}
