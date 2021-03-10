package oliver.action;

import oliver.Variable;

public class AssignFieldAction extends AssignAction {

	FieldAction leftHS;
	
	public AssignFieldAction(int pc, FieldAction leftHS, Action rightHS) {
		super(pc);

		this.rightHS = rightHS;
		this.leftHS = leftHS;

		if (rightHS instanceof ValueAction) {
			ValueAction valAct = (ValueAction) rightHS;
			if (valAct.value instanceof Integer) {
				Integer actualValue = (Integer)valAct.value; 
				FieldAction fieldAct = (FieldAction) leftHS;
				if (fieldAct.field.getType().equals("boolean")) {
					if (actualValue == 0) {
						valAct.value = false;
					} else {
						valAct.value = true;
					}
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

		return String.format(" %s = %s ", leftHS.print(), rightHS.print());
	}
}
