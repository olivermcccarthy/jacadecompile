package oliver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import decompile.JavaAction;
import oliver.ClassDescription2.VariableInfo;
import oliver.action.Action;
import oliver.action.ActionStack;
import oliver.action.NewArrayAction;
import oliver.action.ValueAction;

public class MethodVariable extends Variable {

	//String className = null;


	int numParams = 0;
	ArrayList<String> paramTypes = new ArrayList<String>();


	public ArrayList<String> getParamTypes() {
		return paramTypes;
	}




	public String getClassName() {
		// TODO Auto-generated method stub
		return className;
	}

	public void setParamTypes(ArrayList<String> paramTypes) {
		this.paramTypes = paramTypes;
	}






	public boolean isHasBoolean() {
		return hasBoolean;
	}






	public void setHasBoolean(boolean hasBoolean) {
		this.hasBoolean = hasBoolean;
	}
	boolean hasBoolean = false;
	public MethodVariable(String className, String methodName, String methodDesc) {
		super(methodDesc, methodName);
		
		
    
		if(methodName.equals("<init>")) {
			this.type = className;
		}
		this.className = getTheType(className);
		
		if(this.className.indexOf(".") > 0) {
			this.className = this.className.substring(this.className.lastIndexOf(".") +1);
		}
		  
		 this.formatSignature(methodDesc);

	}

	

	
	

	public String formatSignature(String methodDesc) {

		String methodName = this.getName();
		
		this.paramTypes.clear();
		Variable.decodeSig(methodDesc, this.paramTypes);
		if(this.paramTypes.size() == 0) {
			Variable.decodeSig(methodDesc, this.paramTypes);
		}
		String retType = this.paramTypes.remove(this.paramTypes.size() -1);
		this.type = retType;
		
		String params = retType + " " + methodName + " (";
		params += formatParams();
		params += ")";
		return params;
	}
	public String formatParams() {
		String params = "";
		this.numParams =  this.paramTypes.size();
		for (int x = 0; x < this.paramTypes.size(); x++) {
			String p = this.paramTypes.get(x);
			params += " " +p + " param" +x;
			if (x < numParams - 1 && numParams > 1) {
				params = params +","  ;
			}
			
		}
		return params;
	}
	
	public String formatParamsFromVarTable2(ArrayList<VariableInfo> otherVariables) {

		String params = "";
		
		int startIndex = 0;
		int diff = 0;
		for (int x = 0; x < numParams; x++) {

			Variable paramVar = null;
			
			if(multiVars[startIndex + x] != null) {
				paramVar = multiVars[startIndex + x].vars.get(0);
				
				if(paramVar.name.equals("this")) {
					if(x == 0 && this.name.contains("$")) {
					      if(this.name.startsWith(this.paramTypes.get(0))) {
					    	  otherVariables.add(new VariableInfo(this.paramTypes.get(0) ,"lambdaVar1",0));
								params = params +  this.paramTypes.get(0) + " lambdaVar1";
								if (x < numParams - 1 && numParams > 1) {
									params = params +","  ;
								}
								continue;
					      }
					}
					 startIndex ++;
					  x --;
					  continue;
				}
				if(!paramVar.getSimpleType().equals(this.paramTypes.get(x - diff))) {
					if(this.name.contains("lambda$") ) {
						
						if(x == 0) {
							otherVariables.add(new VariableInfo(paramVar.getType() , paramVar.getName(),0));
							params = params +  paramVar.getType() + " " +paramVar.getName();
							break;
						}
					}
						else if(x == 0 && this.name.contains("$")) {
						      if(this.name.startsWith(this.paramTypes.get(0))) {
						    	  otherVariables.add(new VariableInfo(paramVar.getType() ,"myParent",0));
									params = params +  paramVar.getType() + " myParent";
									if (x < numParams - 1 && numParams > 1) {
										params = params +","  ;
									}
									continue;
						      }
							
					}else {
					  startIndex ++;
					  x --;
					  continue;
					}
					
				}
			}else {
				startIndex ++;
				x --;
			
			}
			
		
			otherVariables.add(new VariableInfo(paramVar.getType() , paramVar.getName(),0));
			params = params +  paramVar.getType() + " " +paramVar.getName();
			
			if (x < numParams - 1 && numParams > 1) {
				params = params +","  ;
			}
		}
		
		if (name.equals("<init>")) {
			return this.className + " (" + params + ")";
		}
		if (name.equals("<clinit>")) {
             return "";
		}
		String medthodSig = this.type + "  " + this.getName() + " (" + params + ")";
		return medthodSig;
	}
	
	public String formatParamsFromVarTable(ArrayList<VariableInfo> otherVariables) {

		String params = "";
		
		int startIndex = 0;
		int diff = 0;
		if(this.className.contains("$") && this.paramTypes.size() > Variable.maxIndex +1) {
			for(int x = 0; x < this.paramTypes.size(); x++) {
				Variable.getVariable(0, x+1, paramTypes.get(x));
			}
		}
		for (int x = 0; x < numParams; x++) {

			Variable paramVar = null;
			
			if(startIndex + x == 80) {
				int debugME =0;
			}
			if(multiVars[startIndex + x] != null) {
				paramVar = multiVars[startIndex + x].vars.get(0);
				
				if(paramVar.name.equals("this")) {
					if(x == 0 && this.name.contains("$")) {
					      if(this.name.startsWith(this.paramTypes.get(0))) {
					    	  otherVariables.add(new VariableInfo(this.paramTypes.get(0) ,"lambdaVar1",0));
								params = params +  this.paramTypes.get(0) + " lambdaVar1";
								if (x < numParams - 1 && numParams > 1) {
									params = params +","  ;
								}
								continue;
					      }
					}
					 startIndex ++;
					  x --;
					  continue;
				}
				if(!paramVar.getSimpleType().equals(this.paramTypes.get(x - diff))) {
					if(this.name.contains("lambda$") ) {
						
						if(x == 0) {
							otherVariables.add(new VariableInfo(paramVar.getType() , paramVar.getName(),0));
							params = params +  paramVar.getType() + " " +paramVar.getName();
							break;
						}
					}
						else if(x == 0 && this.name.contains("$")) {
						      if(this.name.startsWith(this.paramTypes.get(0))) {
						    	  otherVariables.add(new VariableInfo(paramVar.getType() ,"myParent",0));
									params = params +  paramVar.getType() + " myParent";
									if (x < numParams - 1 && numParams > 1) {
										params = params +","  ;
									}
									continue;
						      }
							
					}else {
					  startIndex ++;
					  x --;
					  continue;
					}
					
				}
			}else {
				startIndex ++;
				x --;
				if(x + startIndex < 70) {
				continue;
				}
				else {
					break;
				}
			}
			
		
			otherVariables.add(new VariableInfo(paramVar.getType() , paramVar.getName(),0));
			params = params +  paramVar.getType() + " " +paramVar.getName();
			
			if (x < numParams - 1 && numParams > 1) {
				params = params +","  ;
			}
		}
		
		if (name.equals("<init>")) {
			return this.className + " (" + params + ")";
		}
		if (name.equals("<clinit>")) {
             return "";
		}
		String medthodSig = this.type + "  " + this.getName() + " (" + params + ")";
		return medthodSig;
	}
	
	public String getName() {
		if (name.equals("<init>")) {
			return "";
		}
		if (name.equals("<clinit>")) {
             return "";
		}
		return name;

	}
	
	public ArrayList<Action> formatParams(ActionStack stackA) {
		ArrayList<Action> params = new ArrayList<Action>();
	
		for(int x =0; x < this.numParams; x++) {
			Action popped = stackA.pop();
			if(popped.isValid() == false) {
				 popped = stackA.pop();
			}
			
			if(this.paramTypes.get(x).equals("char") && popped instanceof ValueAction) {
				ValueAction valAct = (ValueAction)popped;
				Object testObj= valAct.getValue();
				if( testObj instanceof Byte) {
					Byte bVal = (Byte)testObj;
					char charS = (char)bVal.byteValue();
					valAct.setValue(charS);
					int debugME =0;
				}
			}
			params.add(popped);
		}
		Collections.reverse(params);
		return params;
	}
	
	public String formatParams(Stack<JavaAction> stack) {
		
	    String ret = "";
		for(int x =0; x < this.numParams; x++) {
			JavaAction popped = stack.pop();
			if(paramTypes.get(this.numParams-x-1).equals("boolean")) {
				if(popped.value.equals("0")) {
					popped.value="false";
				}else if(popped.value.equals("1")) {
					popped.value="true";
				}
			}
		    if(x > 0) {
		    	ret = "," + ret;
		    }
		    if(popped.value.equals("\"")) {
		    	int debugME =0;
		    	ret = "\"\\" +popped.value + "\""+ret;
		    }else {
		       ret = popped.value + ret;
		    }
		}
	
		return this.name + "(" +ret +")";
	}
	

}
