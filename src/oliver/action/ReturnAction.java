package oliver.action;



public class ReturnAction extends Action {
	Action value;

	boolean returnNothing = false;

	public static boolean returnBoolean = false;

	public static boolean staticInit;

	public static boolean returnFloat;

	public Action getValue() {
		return value;
	}

	public ReturnAction(int pc, Action value) {
		super(pc);
		this.value = value;
		if (returnBoolean) {
			if (value instanceof ValueAction) {
				ValueAction valAct = (ValueAction) value;
				if (valAct.value instanceof Integer) {
					Integer actualValue = (Integer) valAct.value;

					if (actualValue == 0) {
						valAct.value = false;
					} else {
						valAct.value = true;
					}

				}
			}
		}
		if (returnFloat) {
			if (value instanceof ValueAction) {
				ValueAction valAct = (ValueAction) value;
				valAct.value = valAct.value +"f";
				
			}
		}
	}

	public ReturnAction(int pc) {
		super(pc);

		returnNothing = true;
	}

	public String toString() {

		if (returnNothing) {
			return String.format(" return");
		}

		return String.format(" return %s", value.print());
	}

	public String print() {

		if(staticInit) {
			return "";
		}
		return (this.toString());

	}

}
