package oliver.action;



public class CaseAction extends Action{

	public String caseKey;
	
	public CaseAction (int pc, int goToLabel, String catchType) {
		 super(pc);
		this.goToLabel = goToLabel;
		this.caseKey = catchType;
	}

	public String print() {
		this.toString();
		if(caseKey.length() == 0) {
			return String.format("default : ");
		}
		if(caseKey.contains("2051")) {
			int debugme =0;
		}
		return String.format("case  %s : ",this.caseKey);
	}
}
