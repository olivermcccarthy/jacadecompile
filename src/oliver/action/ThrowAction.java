package oliver.action;

public class ThrowAction extends Action{

	Action value;

	public ThrowAction(int pc, Action value) {
		super(pc);
		this.value = value;
		
	}
	
public String toString() {
		
		
		
		return  String.format(" throw (%s)//%s" , value,getPc());
	}



public String print() {
	
	
	
	return  String.format(" throw %s" , value.print());
}
}
