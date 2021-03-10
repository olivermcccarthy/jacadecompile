package oliver.action;

public class SynchronizedAction extends Action{

	Action valueAction;
	boolean sync;
	public SynchronizedAction(int pc , Action valueAction, boolean sync) {
		super(pc);
		this.valueAction = valueAction;
		this.sync = sync;
	
		// TODO Auto-generated constructor stub
	}

	
	public String toString() {
		
		
		
		return  String.format(" synchronized (%s)" , valueAction);
	}
	
public String print() {
		
		if(this.goToLabel == -1) {
			return "";
		}
		
		return  String.format(" synchronized (%s)" , valueAction.print());
	}
}
