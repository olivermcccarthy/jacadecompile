package oliver.action;

public class ArrayIndexAction extends Action{

	Action array;
	Action index;
	
	public ArrayIndexAction(int pc, Action index, Action array) {
		super(pc);
		this.array = array;
		this.index = index;
		
		if(index instanceof NewArrayAction) {
			NewArrayAction newArrayAction = (NewArrayAction) index;
			int debugME =0;
		}
	}
	
	public String toString() {
		return String.format(" %s[%s]", array.print(), index.print());
		
	}
	
	public String print() {
		return toString();
	}
}
