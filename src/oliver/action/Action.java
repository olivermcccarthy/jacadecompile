package oliver.action;

import java.util.ArrayList;


public abstract class Action implements Comparable<Action>{

	int goToLabel = -1;

	private int pc = -1;
	int numHits =0;
	public static ArrayList<Action>  allActions = new ArrayList<Action>();
	
	
	static int uniqueNumber =0;
	
	int id; 
	public Action (int pc) {
		this.setPc(pc);
		id = uniqueNumber ++;
		if(id == 10362) {
			int debugME =0;
			
		}
		allActions.add(this);
	}
	
	public boolean isWhile;

	public boolean isElse;
	 boolean isBreak;
	 public boolean isIf;
	public boolean isContinue;
	public boolean dubious;
	ArrayList<Integer> gotTos = new ArrayList<Integer>();
	public abstract String print();
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	
	String returnType = "";
	
	
	public String getReturnType() {
		return returnType;
	}

	public int getRealGoto() {
		if(this.realGoto != 0) {
		return realGoto;
		}

		return this.goToLabel;
	}
	public void setRealGoto(int realGoto) {
		
		this.realGoto = realGoto;
		
	}

	private boolean isValid = true;
	private int realGoto = 0;;
	public boolean dropME;
	public boolean dropMEBreak;
	public int elseTo;
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public int getGoToLabel() {
		return goToLabel;
	}
	public void setGoToLabel(int goToLabel) {
		this.goToLabel = goToLabel;
	}

	static boolean sortBySorto = false;
	@Override
	
	public int compareTo(Action o) {
		
		if(sortBySorto) {
			return  this.goToLabel - o.goToLabel;
		}
		return this.getPc() - o.getPc();
	}
	
}
