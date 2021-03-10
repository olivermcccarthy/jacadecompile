package oliver.action;

import java.util.ArrayList;

import oliver.MethodVariable;
import oliver.Variable;

public class MethodAction extends Action{

	MethodVariable methodVar;
	Action objAction;
	ArrayList<Action> children = new ArrayList<Action>();
	String callingClassName;
	
	public MethodAction(int pc, String className, Action objAction,MethodVariable methodVar ,ArrayList<Action> children) {
		super(pc);
		if(children != null) {
		this.children.addAll(children);
		}
		if(pc == 676) {
			int debugME =0;
		}
		this.methodVar = methodVar;
		if(this.methodVar.getName().equals("format")) {
			int debugME =0;
		}
		this.callingClassName = className;
		this.objAction = objAction;
		this.returnType = methodVar.getType();
	    if(this.objAction instanceof NewObjectAction) {
	    	NewObjectAction neObj = (NewObjectAction)this.objAction;
	    	this.returnType = neObj.getReturnType();
	    }
		// TODO Auto-generated constructor stub
	}
	public String toString() {
		
		if(methodVar.getName().equals("format")) {
			int debugME =0;
		}
		String params = "";
		int index =0;
		for(Action action : children) {
			params+= action.print() ;
			if(index < children.size() -1) {
				params+= ",";
			}
			index++;
		}
		String ret = String.format(" %s(%s)", objAction.print(),params);
		
		if(methodVar.getName().length() >0) {
			
			
			ret = String.format(" %s.%s(%s)", objAction.print(), methodVar.getName(),params);
		}
		
        return ret;
		
	}
	
	public String print() {
		if(methodVar.getName().equals("reverseIf")) {
			int debugME =0;
		}
		String params = "";
		int index =0;
		if(methodVar.isHasBoolean()) {
			int debugME =0;
		}
			
		for(Action action : children) {
			if(methodVar.getParamTypes().get(index).equals("boolean")) {
				String actStr = action.print().trim();
				if(actStr.equals("0")) {
					actStr = "false";
				}else if(actStr.equals("1")) {
					actStr = "true";
				}
					else {
				
					 
				}
				params+= actStr;
			}else {
			   params+= action.print() ;
			}
			if(index < children.size() -1) {
				params+= ",";
			}
			index++;
		}
		String ret = String.format(" %s(%s)", objAction.print(),params);
		if(objAction instanceof VariableAction && ! methodVar.getClassName().equals(this.callingClassName)) {
			
			if(objAction.print().contains("this")) {
				VariableAction valAct =(VariableAction)objAction;
			
					if(methodVar.getName().length() ==0) {
						return String.format(" %s(%s)", objAction.print().replaceFirst("this", "super"),params);
					}else {
						return  String.format(" %s.%s(%s)", objAction.print().replaceFirst("this", "super"), methodVar.getName(),params);
					}
				
			}
		}
		
		if(methodVar.getName().length() >0) {
			ret = String.format(" %s.%s(%s)", objAction.print(), methodVar.getName(),params);
		}
		
        return ret;
	}
}


