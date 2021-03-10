package oliver.action;



public class TryAction extends Action{

	
	public boolean isFunky;

	public TryAction (int pc, int goToLabel) {
		 super(pc);
		this.goToLabel = goToLabel;
	}

	public String print() {
		if(isFunky ) {
			int debugME =0;
		}
		return "try ";
	}
}
