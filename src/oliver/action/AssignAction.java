package oliver.action;

import java.util.HashMap;

import oliver.Variable;

public class AssignAction extends Action {

	VariableAction leftHS;
	Action rightHS;

	public VariableAction getLeftHS() {
		return leftHS;
	}

	public Action getRightHS() {
		return rightHS;
	}

	public AssignAction(int pc) {
		super(pc);
	}

	static HashMap<String,String> rebranded = new HashMap<String,String>();
	public AssignAction(int pc, VariableAction leftHS, Action rightHS) {
		super(pc);

		if(pc == 662) {
			int debugME =0;
		}
		this.rightHS = rightHS;
		this.leftHS = leftHS;
		if ( (rightHS.print().contains("99.99"))) {
			int debugME =0;
		}
		if (rightHS instanceof ValueAction) {
			ValueAction valAct = (ValueAction) rightHS;
			if (valAct.value instanceof Integer) {
				
				Integer actualValue = (Integer)valAct.value;
				
				if (leftHS.getType().equals("boolean")) {
					if (actualValue == 0) {
						valAct.value = false;
					} else {
						valAct.value = true;
					}
				}

			}
			if (valAct.value instanceof Float) {
				if(leftHS.type.equals("float")) {
					valAct.value =valAct.value +"f";
					int debugme =0;
				}
			}
		}

		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return String.format(" %s = %s ", leftHS.print(), rightHS.print());

	}

	public String print() {

		/*
		 * if(this.leftHS instanceof ValueAction) { ValueAction valueA =
		 * (ValueAction)this.leftHS;
		 * 
		 * return String.format(" %s = %s ", valueA.toFullString(), rightHS.print()); }
		 */
		if(id == 28169) {
			int debugME =0;
			
		}
		return String.format(" %s = %s ", leftHS.print(), rightHS.print());
	}
}
