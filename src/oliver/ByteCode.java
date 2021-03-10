package oliver;
// https://docs.oracle.com/javase/specs/jvms/se7/html/index.html
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import decompile.ByteCodeParser;
import oliver.action.Action;
import oliver.action.ActionStack;
import oliver.action.ArrayIndexAction;
import oliver.action.AssignAction;
import oliver.action.AssignArrayIndexAction;
import oliver.action.AssignFieldAction;
import oliver.action.CaseAction;
import oliver.action.CastAction;
import oliver.action.DoubleEqualsAction;
import oliver.action.FieldAction;
import oliver.action.GotToAction;
import oliver.action.IfAction;
import oliver.action.InstanceOfAction;
import oliver.action.MathematicalAction;
import oliver.action.MethodAction;
import oliver.action.NewArrayAction;
import oliver.action.NewObjectAction;
import oliver.action.ReturnAction;
import oliver.action.StaticFieldAction;
import oliver.action.SwitchAction;
import oliver.action.SynchronizedAction;
import oliver.action.ThrowAction;
import oliver.action.ValueAction;
import oliver.action.VariableAction;
import oliver.action.NewMultiDimArrayAction;







public class ByteCode {

	String name;
	int code;
	String stack;
	String description;
    
	
    boolean pushToStack= false;
    public boolean isPushToStack() {
    	return this.pushToStack;
    }
	public ByteCode(String[] line) {
		if (line.length > 3) {
			this.name = line[0];
			try {
				this.code = Integer.parseInt(line[1], 16);
			} catch (Exception e) {
				this.code = -1;

			}
			if(this.code == 181) {
				int debugME = 0;
			}
			this.stack = line[4];
			if(this.stack.contains("?")) {
				int indexQ = this.stack.indexOf("?");
				if(this.stack.length() > indexQ +2) {
					pushToStack = true;
				}else {
					int debugME =0;
				}
			}
			this.description = line[5];
		}
	}

	public ByteCode() {
		// TODO Auto-generated constructor stub
	}

	public String toString() {

		String stackStr = "";
		String desc = "";
		try{
			desc = codes.get(this.code).description;
		}catch(Throwable t) {
			
		}
//		if (stackA.size() > 3) {
//			if (stackA.peek() != null) {
//				for (Object e : stackA.subList(stackA.size() - 3, stackA.size())) {
//					stackStr += "\n" + e;
//				}
//
//			}
//		}
		return Integer.toHexString(this.code & 0xff) + " " + this.stack + " " + desc
				+ "\n   " + stackStr;
	}

	private int getIndex(byte[] buf, int index) {
		int value = buf[index + 1] & 0xff;
		int v2 = buf[index + 2] & 0xff;
		value = value << 8;
		value = value | v2;
		
		return value;
	}
	private int getSignedIndex(byte[] buf, int index) {
		int value = buf[index + 1]  ;
		
		int v2 = buf[index + 2] & 0xff;
	
		value = value << 8;
		value = value | v2;
		
		
		
		return value;
	}
	//HashSet<Integer> sortedGoTo = new HashSet<Integer>();

	int nextGoTo = -1;
	
	ActionStack actionStack = new ActionStack(this);
	// List <PCLine> pcLines = null;
	String className;
	
	SwitchAction switchAction;
	static HashMap<Integer, CaseAction > cases = new HashMap<Integer, CaseAction >();
	Stack<Integer> synchStarted = new Stack<Integer>();
	int lastSyncStart = -1;
	
	HashMap<String , SynchronizedAction> synchs = new HashMap<String , SynchronizedAction>();
	static HashSet<Integer> codesTested = new HashSet<Integer>();
	int x;
	private int exitLockPC;
	public void decompileAll(byte[] buf, int last, String className, String methodName) {
		
			this.decompileAll(buf, last, className, methodName, 1);
			
		
	}
	static String allInfo ="";
	public void decompileAll(byte[] buf, int last, String className, String methodName, int debugMe) {

		allInfo ="";
		this.className = className;

		//sortedGoTo = new HashSet<Integer>();

		
		synchStarted = new Stack<Integer>();
		lastSyncStart = -1;
		//getGoTo(buf, last);
		
		
		
		
		
		
		List<Integer> sortedPC = ExceptionHandler.getSortedPC();
		this.actionStack.clear();
		//sortedGoTo.addAll(sortedPC);

		cases.clear();
	if(methodName.equals("printHtml")) {
		int debugME =0;
	}
		for (x = 0; x < last; x++) {
			

			this.code = buf[x] & 0xff;
			pushToStack =codes.get(this.code).pushToStack;
			stack=codes.get(this.code).stack;
			codesTested.remove(this.code);
			

			if (!codes.containsKey(this.code)) {
				throw new RuntimeException(" unknown code" + Integer.toHexString(this.code & 0xff) + " index " + x);
			}
			if (sortedPC.contains(x)) {
				ExceptionHandler.handlePC(actionStack, x, x);

			}
			
			
			

			if(cases.containsKey(x)) {
				Action lastAction = this.actionStack.popOther();
				if(lastAction instanceof GotToAction) {
					if(this.switchAction.getGoToLabel() == -1) {
						this.switchAction.setGoToLabel(lastAction.getGoToLabel());
					}
					GotToAction gotTo= (GotToAction)lastAction;
					gotTo.setBreak(true);
					gotTo.setGoToLabel(gotTo.getPc());
					 this.actionStack.pushOther(lastAction);
				}else {
					 this.actionStack.pushOther(lastAction);
				}
				CaseAction caseLine = cases.get(x);
				if (caseLine.caseKey.equals("") && this.switchAction.getGoToLabel() ==-1) {
					int debugME =90;
					
				}
				//caseLine.pc = x-1;
				this.actionStack.pushOther(caseLine);
				
				
				
				
			}
			int lastX =x;
			if(debugMe == 1 && x == 2303) {
				int debugME =0;
			}
			if(decompileCount == 23750) {
				int debugME =0;
			}
			x = decompile(buf, x);
			
			if(debugMe == 1) {
				
				String thisInfo =("*********************")+"\n"; 
				thisInfo+=("*********************\n");
				thisInfo+=(lastX +"\n");
				
				thisInfo+=this.actionStack.debug()+"\n";
				
				thisInfo+=(this+"\n" + decompileCount +"\n");
				thisInfo+=("*********************")+"\n\n";
				System.out.println(thisInfo);
				allInfo+= thisInfo;
			}
			if( x == 52) {
				int deff =0;
			}
		}
		
	
		if(methodName.equals("testRemainder")) {
			int debugME =0;
		}
		this.actionStack.ifsAndWhatNot(methodName);
		
		
	}

	public static int decompileCount =0;
	private int decompile(byte[] buf, int index) {
		int ret = index;
		ArrayList<ConstEntry2> constPool = ConstEntry2.consts;

		decompileCount ++;
		int goToIndex = -1;
		String varName = null;

		MethodVariable methodInstance = null;
		if (this.code == 0xb0 || this.code == 0xac || this.code == 0xad || this.code == 0xaf
				|| this.code == 0xae) {
			
		
		
		}
		else if (this.code == 0xc2) {
			
			synchStarted.push(ret);
			lastSyncStart =-1;

		}else if (this.code == 0xc3) {
			
			if(lastSyncStart == -1) {
				lastSyncStart = synchStarted.pop();
			}else {
		
			}

		}	
		
		if (this.code == 0x0) {

		} else if (this.code == 0x1) {

			ValueAction valueAction = new ValueAction(index,null);
			actionStack.push(valueAction);
		

		} else if (this.code == 0x9) {
			ValueAction valueAction = new ValueAction(index,0);
			actionStack.push(valueAction);
			
		
		}else if (this.code == 0xf) {
			ValueAction valueAction = new ValueAction(index,new Double(1.0));
			actionStack.push(valueAction);
		

		} 
		else if (this.code == 0xc) {
			ValueAction valueAction = new ValueAction(index,new Float(1.0));
			actionStack.push(valueAction);
		

		} 
		else if (this.code == 0xd) {
			ValueAction valueAction = new ValueAction(index,new Float(2.0));
			actionStack.push(valueAction);
			

		} 
		else if (this.code == 0xa) {
			ValueAction valueAction = new ValueAction(index,new Long(1));
			actionStack.push(valueAction);
			

		}
		else if (this.code == 0x3 || this.code == 0x4 || this.code == 0x5 || this.code == 0x6 || this.code == 0x7
				|| this.code == 0x8) {
			ValueAction valueAction = new ValueAction(index,this.code - 0x3);
			actionStack.push(valueAction);
		
		} else if (this.code == 0x11) {
			int value = getSignedIndex(buf, index);
			
			ValueAction valueAction = new ValueAction(index,value);
			actionStack.push(valueAction);
	
			ret += 2;
		} else if (this.code == 0xa7) {

			int goTo = getSignedIndex(buf, index);
			int branchEnd = index + goTo;
		

			
		
				
				
		
			ret += 2;
		
			goToIndex = branchEnd;
			GotToAction action = new 	GotToAction(index, goToIndex);
			
			actionStack.push(action);

		}else if (this.code == 0xab) {

			
			int offIndex = (index/4) *4;
			offIndex +=5;
			int defaultOff = index + this.getIndex(buf, offIndex);
			
			offIndex +=4;
			int numKeys = this.getIndex(buf, offIndex);
		
			Action key2 = this.actionStack.pop();
			this.switchAction = new SwitchAction(index, -1,key2);
			
			this.actionStack.pushOther(this.switchAction);
			CaseAction defaultAction = new CaseAction(defaultOff -1,-1, "");
			
			//this.stackA.extraLines.add(line);
			cases.put(defaultOff, defaultAction);
		
			for (int k = 0; k < numKeys; k++) {
				offIndex +=4;
				int keyI = this.getIndex(buf, offIndex);
				offIndex +=4;
				int offSet = index + this.getIndex(buf, offIndex);
				
			
				CaseAction caseAction = new CaseAction(offSet -1,-1,  "" +keyI);
				cases.put(offSet, caseAction);
				//this.stackA.extraLines.add(line);
			}
				
			ret = offIndex +2;
		

		}
else if (this.code == 0xaa) {

			
			int offIndex = (index/4) *4;
			offIndex +=5;
			int defaultOff = index + this.getIndex(buf, offIndex);
			
			offIndex +=4;
			int lowByte = this.getIndex(buf, offIndex);
			offIndex +=4;
			int highByte = this.getIndex(buf, offIndex);
			int numKeys = highByte - lowByte +1;
			Action key2 = this.actionStack.pop();
			this.switchAction = new SwitchAction(index, -1,key2);
			
			this.actionStack.pushOther(this.switchAction);
			CaseAction defaultAction = new CaseAction(defaultOff ,-1, "");
			
			//this.stackA.extraLines.add(line);
			cases.put(defaultOff, defaultAction);
		
			int startI = lowByte;
			for (int k = 0; k < numKeys ; k++) {
				offIndex +=4;
				
				int offSet = index + this.getIndex(buf, offIndex);
				
			     
				CaseAction caseAction = new CaseAction(offSet -1,-1,  "" +startI);
				startI++;
				if(offSet == defaultOff) {
					continue;
				}
				cases.put(offSet, caseAction);
				//this.stackA.extraLines.add(line);
			}
				
			ret = offIndex +2;
		

		}
		else if (this.code == 0x84) {

			int varIndex = buf[index + 1];
			int constInc = buf[index + 2];
			Variable var1 = Variable.getVariable(index, varIndex);
			
			VariableAction valueAction = new VariableAction(index, var1);
			ValueAction valueAction2 = new ValueAction(index, constInc);
			MathematicalAction action = new 	MathematicalAction(index,valueAction,"+=", valueAction2);
			
			actionStack.push(action);
		
			ret += 2;

		} else

		if (this.code == 0x19 || this.code == 0x15 || this.code == 0x16 || this.code == 0x17 || this.code == 0x18) {

			int varIndex = buf[index + 1];
			
Variable var1 = Variable.getVariable(index, varIndex);
			
			VariableAction valueAction = new VariableAction(index, var1);
			actionStack.push(valueAction);
			// // System.out.println("Load objectref from local Var " +
			// localVars.get(varIndex));
	
			ret += 1;
		} else if (this.code == 0x2a || this.code == 0x1a || this.code == 0x1e || this.code == 0x22
				|| this.code == 0x26 ) {
			// // System.out.println("Load objectref from local Var 0 " + localVars.get(0));
			
			
Variable var1 = Variable.getVariable(index, 0);
			
			VariableAction valueAction = new VariableAction(index, var1);
			actionStack.push(valueAction);
			// // System.out.println("Load objectref from local Var " +
			// localVars.get(varIndex));
		
		}

		else if (this.code == 0x2b || this.code == 0x1b || this.code == 0x1f || this.code == 0x23
				|| this.code == 0x27) {
			// // System.out.println("Load objectref from local Var 1 " + localVars.get(1));

			Variable var1 = Variable.getVariable(index, 1);

			VariableAction valueAction = new VariableAction(index, var1);
			actionStack.push(valueAction);
			// // System.out.println("Load objectref from local Var " +
			// localVars.get(varIndex));
		
		} else if (this.code == 0x2c || this.code == 0x1c || this.code == 0x20 || this.code == 0x24
				|| this.code == 0x28 ) {
			Variable var1 = Variable.getVariable(index, 2);

			VariableAction valueAction = new VariableAction(index, var1);
			actionStack.push(valueAction);
			// // System.out.println("Load objectref from local Var " +
			// localVars.get(varIndex));
		
		}

		else if (this.code == 0x2d || this.code == 0x1d || this.code == 0x21 || this.code == 0x25
				|| this.code == 0x29 ) {
			// // System.out.println("Load objectref from Var 3 " + localVars.get(3));
			Variable var1 = Variable.getVariable(index, 3);

			VariableAction valueAction = new VariableAction(index, var1);
			actionStack.push(valueAction);
			// // System.out.println("Load objectref from local Var " +
			// localVars.get(varIndex));
		
		} else if (this.code == 0xb5) {
			int constIndex = getIndex(buf, index);
			ret += 2;
			Variable field = (Variable) constPool.get(constIndex).getVariable();
			
			Action valueAction =actionStack.pop();
			
			
			Action objAction =actionStack.pop();
			
		    boolean isOdd = false;	
			if(objAction instanceof GotToAction ) {
				this.actionStack.push(objAction);
				isOdd = true;
				objAction = new ValueAction(index,"HELPME");
			}
			if(objAction instanceof IfAction ) {
				this.actionStack.push(objAction);
				isOdd = true;
				objAction = new ValueAction(index,"HELPME");
			}
			if(objAction.isValid() == false) {
				objAction =actionStack.pop();
			}
			FieldAction fieldAction = new FieldAction(index, objAction, field);
			
			AssignFieldAction assignAction = new AssignFieldAction(index,fieldAction, valueAction);
			
			
			   actionStack.push(assignAction);
			
			// System.out.println(lineStr);

		} else if (this.code == 0x13 || this.code == 0x14) {

			int constIndex = getIndex(buf, index);
			ret += 2;

			Variable field = (Variable) constPool.get(constIndex).getVariable();
			Action valueAction = new VariableAction(index,field);
			actionStack.push(valueAction);
			
		} else if (this.code == 0xb3) {

			int constIndex = getIndex(buf, index);
			ret += 2;
			Variable field = (Variable) constPool.get(constIndex).getVariable();
			
			StaticFieldAction fieldAction = new StaticFieldAction(index, field);
			Action valueAction =actionStack.pop();
			
			varName = field.getName();
		
			
			AssignAction assignAction = new AssignFieldAction(index,fieldAction, valueAction);
			actionStack.push(assignAction);
			
			// System.out.println(lineStr);

		} else if (this.code == 0xc1) {

			int constIndex = getIndex(buf, index);
			ret += 2;
			Variable field = (Variable) constPool.get(constIndex).getVariable();
			
 
			Action popAction = this.actionStack.pop(); 
		
			InstanceOfAction insatnceAction = new InstanceOfAction(index, popAction, new VariableAction(index, field));
			actionStack.push(insatnceAction);

		} else if (this.code == 0xb2) {
			int constIndex = getIndex(buf, index);
			ret += 2;
			Variable field = (Variable) constPool.get(constIndex).getVariable();

			
			StaticFieldAction fieldAction = new StaticFieldAction(index, field);
			actionStack.push(fieldAction);
			// System.out.println(lineStr);

		} else if (this.code == 0xc0) {
			int constIndex = getIndex(buf, index);
			ret += 2;
			
			Variable clazz = constPool.get(constIndex).getVariable();
			String name = clazz.getName();
             if(clazz.getName().contains("[")) {
            	
            	 name = Variable.getTheType(clazz.getName());
             }
            Action valueAction = actionStack.pop();
            CastAction castAction = new CastAction(index, clazz, valueAction);
            actionStack.push(castAction);
			

		} else if (this.code == 0xb4) {
			int constIndex = getIndex(buf, index);
			ret += 2;
		
			Variable var = constPool.get(constIndex).getVariable();
		
			varName = var.getName();
		
			FieldAction fieldAction = new FieldAction(index, actionStack.pop(), var);
			actionStack.push(fieldAction);

		} else if (this.code == 0xc2) {

		
            Action syncObject =actionStack.pop();
            
			SynchronizedAction syncAction = new SynchronizedAction(index,syncObject, true);
			this.synchs.put(syncObject.toString(), syncAction);
			actionStack.push(syncAction);

		} 
		else if (this.code == 0xc3) {

			 Action syncObject =actionStack.pop();
			 SynchronizedAction syncAction = synchs.get(syncObject.toString());
			 exitLockPC = ret;
			 if(syncAction != null) {
				 synchs.remove(syncObject.toString());
				
			 }
			
		

		}
		else if (this.code == 0xbe) {

		
			Action valueAction = actionStack.pop();
			FieldAction fieldAction = new FieldAction(goToIndex, valueAction, new Variable("int", "length"));
			 actionStack.push(fieldAction);
		

		} else if (this.code == 0x68 || this.code == 0x69 || this.code == 0x6a || this.code == 0x6b) {
		

		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
             MathematicalAction action = new 	MathematicalAction(index,valueAction," * ", valueAction2);
			
			actionStack.push(action);
			

		}else if (this.code == 0x73 || this.code == 0x72) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction," % ", valueAction2);
				
				actionStack.push(action);

		}
		else if (this.code == 0x6c || this.code == 0x6d || this.code == 0x6e || this.code == 0x6f || this.code == 0x71) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction2," / ", valueAction);
				actionStack.push(action);
		

		} else if (this.code == 0x60 || this.code == 0x61 || this.code == 0x62 || this.code == 0x63) {
			
            	
            	Action valueAction = actionStack.pop();
    			Action valueAction2 = actionStack.pop();
    			  MathematicalAction action = new 	MathematicalAction(index,valueAction2," + ", valueAction);
    				actionStack.push(action);
			

		} else if (this.code == 0x65 || this.code == 0x64 || this.code == 0x66 || this.code == 0x67) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction2," - ", valueAction);
				actionStack.push(action);
			

		} else if (this.code == 0x8e || this.code == 0x8b || this.code == 0x88) {
		
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "int", valueAction);
			actionStack.push(castAction);
		} else if (this.code == 0x8f || this.code == 0x8c || this.code == 0x85) {
		
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "long", valueAction);
			actionStack.push(castAction);
		} else if (this.code == 0x8d || this.code == 0x87 || this.code == 0x8a) {
			
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "double", valueAction);
			actionStack.push(castAction);
		}
		/*
		 * 86 90, 89 float 8e 8b 88, int 8f,8c 85 long 8d,87 8a double 91,byte
		 * 92,character
		 * 
		 * 93,short
		 */

		else if (this.code == 0x91) {
			
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "byte", valueAction);
			actionStack.push(castAction);
		} else if (this.code == 0x92) {
		
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "char", valueAction);
			actionStack.push(castAction);
		} else if (this.code == 0x93) {
			
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "short", valueAction);
			actionStack.push(castAction);
		} else if (this.code == 0x86 || this.code == 0x89 || this.code == 0x90) {
			
			Action valueAction = actionStack.pop();
			CastAction castAction = new CastAction(index, "float", valueAction);
			actionStack.push(castAction);
		} else if (this.code == 0x7e || this.code == 0x7f) {
			
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction," & ", valueAction2);
				actionStack.push(action);
			

		} else if (this.code == 0x79 || this.code == 0x78) {
			
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction2," << ", valueAction);
				actionStack.push(action);
			

		} else if (this.code == 0x7a || this.code == 0x7b || this.code == 0x7c) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction," >> ", valueAction2);
				actionStack.push(action);
			

		} else if (this.code == 0x83 || this.code == 0x82) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction," ^ ", valueAction2);
				actionStack.push(action);
			

		} else if (this.code == 0x95 || this.code == 0x96 || this.code == 0x98 || this.code == 0x97) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  DoubleEqualsAction action = new 	DoubleEqualsAction(index,valueAction," == ", valueAction2);
				actionStack.push(action);
		

		} else if (this.code == 0x80 || this.code == 0x81) {
			
			Action valueAction = actionStack.pop();
			
			Action valueAction2 = actionStack.pop();
if(valueAction2 instanceof GotToAction) {
				int debugME =0;
			}
			  MathematicalAction action = new 	MathematicalAction(index,valueAction2," | ", valueAction);
				actionStack.push(action);
		

		} else if (this.code == 0x70) {
		
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			  MathematicalAction action = new 	MathematicalAction(index,valueAction2," % ", valueAction);
				actionStack.push(action);
		

		} else if (this.code >= 0x4f && this.code <= 0x56) {
			
			Action valueAction = actionStack.pop();
			ArrayIndexAction arrIndexAction = new ArrayIndexAction(index, actionStack.pop(), actionStack.pop());
			AssignAction assignAction = new AssignArrayIndexAction (index,arrIndexAction, valueAction );
			actionStack.push(assignAction);
			boolean hj = false;
			
			

		} else if (this.code == 0x30 || this.code == 0x31 || this.code == 0x32 || this.code == 0x33 || this.code == 0x2e || this.code == 0x34
				|| this.code == 0x2f) {

			
			ArrayIndexAction arrIndexAction = new ArrayIndexAction(index, actionStack.pop(), actionStack.pop());
			actionStack.push(arrIndexAction);
		

		} else if (this.code == 0xbc) {

		
			int atype = buf[index + 1];
			String tyepStr = "int";
			if (atype == 8) {
				tyepStr = "byte";
			} else if (atype == 10) {
				tyepStr = "int";
			} else if (atype == 7) {
				tyepStr = "double";
			} else if (atype == 4) {
				tyepStr = "boolean";
			}else if (atype == 6) {
				tyepStr = "float";
			}else if (atype == 11) {
				tyepStr = "long";
			}else if (atype == 5) {
				tyepStr = "char";
			}else {
				throw new RuntimeException(" un handled type " + atype);
			}
			ret += 1;
			
			NewArrayAction newArr = new NewArrayAction(index,tyepStr ,actionStack.pop() );
			actionStack.push(newArr);
		

		} else if (this.code == 0xbd) {

		
			int constIndex = getIndex(buf, index);
			ret += 2;
			NewArrayAction newArr = new NewArrayAction(index,constPool.get(constIndex).getVariable().getType() ,actionStack.pop() );
			actionStack.push(newArr);
		

		}else if (this.code == 0xc5) {

		
			int constIndex = getIndex(buf, index);
			ret += 3;
			int numDim = buf[index+3];
			ArrayList<Action> sizes = new ArrayList<Action>();
			for(int d =0; d < numDim; d++) {
				sizes.add(actionStack.pop());
			}
			Variable h = constPool.get(constIndex).getVariable();
		String zz = Variable.getTheType(h.type.replace("[[", ""));
			
			
			NewMultiDimArrayAction newArr = new NewMultiDimArrayAction(index,zz ,sizes);
			actionStack.push(newArr);
		

		} 
		else if (this.code == 0xbb) {
			int constIndex = getIndex(buf, index);
			ret += 2;
			
			NewObjectAction newArr = new NewObjectAction(index,constPool.get(constIndex).getVariable().getType()  );
			actionStack.push(newArr);
		

		} else if (this.code == 0xb6 || this.code == 0xb7 || this.code == 0xb8 || this.code == 0xb9 || this.code == 0xba) {
			int constIndex = getIndex(buf, index);
			ret += 2;
			if (code == 0xb9) {
				ret += 2;
			}
			
			methodInstance = (MethodVariable) constPool.get(constIndex).getVariable();
		
			// varName = methodInstance.getName();

		

			ArrayList<Action> paramsAction = methodInstance.formatParams(actionStack);
			
			Object rt = "";
			Action objAction = null;
			if (this.code == 0xb8 || this.code == 0xba) {

				objAction = new VariableAction(index, new Variable(methodInstance.className));
				rt = new Variable(methodInstance.className);
			} else {
				
				objAction = actionStack.pop();
				
			}
//(int pc, Action objAction,MethodVariable methodVar ,ArrayList<Action> children)
			MethodAction methodAction = new MethodAction(index, this.className,objAction, methodInstance, paramsAction);
			actionStack.push(methodAction);
			if (rt instanceof Variable) {
			
				Variable var = (Variable) rt;
			
				

			} else if (rt instanceof String) {
				String var = (String) rt;
				

			} 
          
			// System.out.println(lineStr);

		} else if (this.code == 0x12) {
			int constIndex = buf[index + 1] & 0xff;
			ret += 1;
			
			VariableAction valueAction = new VariableAction(index,constPool.get(constIndex).getVariable() );
			actionStack.push(valueAction);
			// // System.out.println("Push const " + constPool.get(constIndex));
		

		} else if (this.code == 0x5f) {
			
			Action valueAction1 = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			actionStack.push(valueAction1);
			actionStack.push(valueAction2);
		
			// stackA.push(obj1);
			// // System.out.println("Push byte " + buf[index+1]);
			// ret += 1;
		} else if (this.code == 0x5a) {
			// Object obj1 = stackA.pop();
			// Object obj2 = stackA.pop();
			// stackA.push(obj1);
			// stackA.push(obj2);
			// stackA.push(obj1);
			// // System.out.println("Push byte " + buf[index+1]);
			// ret += 1;
		} else if (this.code == 0x10) {
		
			ValueAction valueAction = new ValueAction(index,buf[index + 1] );
			actionStack.push(valueAction);
			// // System.out.println("Push byte " + buf[index+1]);
			ret += 1;
		} else if (this.code == 0xb1) {
		
			ReturnAction retrurnAction = new ReturnAction(index);
			actionStack.push(retrurnAction);
		} else if (this.code == 0x38 ||this.code == 0x36 || this.code == 0x39 || this.code == 0x3c || this.code == 0x3b || this.code == 0x3d
				|| this.code == 0x3e || this.code == 0x4c || this.code == 0x4b || this.code == 0x4d || this.code == 0x4e
				|| this.code == 0x3a || this.code == 0x37 || this.code == 0x3f || this.code == 0x40 || this.code == 0x41
				|| this.code == 0x42 || this.code == 0x47 || this.code == 0x48 || this.code == 0x49
				|| this.code == 0x43 || this.code == 0x44 || this.code == 0x45 || this.code == 0x46
				|| this.code == 0x4a) {
			int varIndex = this.code - 0x3b;
			if (this.code == 0x44) {
				int debugMe =0;
			}
			if (this.code == 0x36) {
				varIndex = buf[index + 1];
				ret += 1;
			}
			if (this.code == 0x38) {
				varIndex = buf[index + 1];
				ret += 1;
			}
			if (this.code == 0x3a) {
				varIndex = buf[index + 1];
				ret += 1;
			}
			if (this.code == 0x39) {
				varIndex = buf[index + 1];
				ret += 1;
			}
			if (this.code == 0x37) {
				varIndex = buf[index + 1];
				ret += 1;
			}
			if (this.code >= 0x47 && this.code <= 0x4a) {
				varIndex = this.code - 0x47;
			}
			if (this.code >= 0x4b && this.code <= 0x4e) {
				varIndex = this.code - 0x4b;
			}
			if (this.code >= 0x3f && this.code <= 0x42) {
				varIndex = this.code - 0x3f;
			}
			if (this.code >= 0x43 && this.code <= 0x46) {
				varIndex = this.code - 0x43;
			}
			
			
			Action valueAction = actionStack.pop();
		    Variable varInstance = Variable.getVariable(index, varIndex,valueAction.getReturnType());
		    if(varInstance.getName().contains("unusedVariable")) {
		    	
		    }
			VariableAction varAction = new VariableAction(index, varInstance);
		
			
			AssignAction assignAction = new AssignAction(index, varAction,valueAction);
			actionStack.push(assignAction);
			varName = varInstance.getName();
			;
			

		
			// System.out.println(lineStr);
		} else if (isIF(this.code)) {
			
			
		
			int goTo = getSignedIndex(buf, index);
			int branchEnd = index + goTo;

			goToIndex = branchEnd;

			
			
			Action valueAction = actionStack.pop();
			Action valueAction2= null;
			boolean isBoolean = false;
			if(valueAction.getReturnType().equals("boolean")){
				isBoolean = true;
			}
			
			if (code == 0xc7 || code == 0xc6 || code == 0x99 || code == 0x9a || code == 0x9b || code == 0x9c
					|| code == 0x9d || code == 0x9e) {

				
			} else {
				
				valueAction2 = actionStack.pop();
			}

			
		
			// if (code == 0x99|| code == 0x9a || code == 0x9b || code == 0x9c || code ==
			// 0x9d || code == 0x9e || code == 0xa0 || code == 0x9f

			if(valueAction.print().contains("result3 =   me +   flArray[ 4]")) {
				int debugME =0;
			}
			
			String compareStr ="";
			if (this.code == 0x99) {
				if (isBoolean) {
					
					valueAction2 = new ValueAction(index, true);
					compareStr = "==";
				} else {
				
					compareStr = "!=";
					valueAction2 = new ValueAction(index, 0);
				}

			} else if (this.code == 0x9c) {
				
				if(valueAction2 == null) {
					  valueAction2 = new ValueAction(index, 0);
					  compareStr = "<";
				}

			} else if (this.code == 0x9d) {
				
				if(valueAction2 == null) {
					  valueAction2 = new ValueAction(index, 0);
					  compareStr = "<=";
				}

			} else if (this.code == 0xc6) {
				valueAction2 = new ValueAction(index, null);
				compareStr = "!=";
			

			} else if (this.code == 0xc7) {

				
				valueAction2 = new ValueAction(index, null);
				compareStr = "==";

			} else if (this.code == 0x9e) {

			
				if(valueAction2 == null) {
					  valueAction2 = new ValueAction(index, 0);
					  compareStr = ">";
				}

			} else if (this.code == 0x9b) {
				if(valueAction instanceof MathematicalAction) {
					MathematicalAction alreadyIF = (MathematicalAction)valueAction;
					if(alreadyIF.getMathStr().equals("==")) {
						compareStr = ">=";
						valueAction2 =  alreadyIF.getLeftHS();
						valueAction = alreadyIF.getRightHS();
				    }
				}
				if(valueAction2 == null) {
					  valueAction2 = new ValueAction(index, 0);
					  compareStr = ">=";
				}
			
			} else if (this.code == 0x9a) {
				if(index == 2305) {
					int debugme =0;
				}
				if (isBoolean) {
				
					 valueAction2 = new ValueAction(index, false);
						compareStr = "==";
				} else {
					
					 valueAction2 = new ValueAction(index, 0);
						compareStr = "==";
				}

			} else if (this.code == 0xa6) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
				
				compareStr = "equals";
			} else if (this.code == 0xa0) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
			
				compareStr = "==";
			} else if (this.code == 0x9f) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
	
				compareStr = "!=";
			} else if (this.code == 0xa4) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
				
				compareStr = ">";

			} else if (this.code == 0xa2) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
		
				compareStr = "<";

			} else if (this.code == 0xa1) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
			
				compareStr = ">=";

			} else if (this.code == 0xa3) {
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
		
				compareStr = "<=";

			} else if (this.code == 0xa5) {

			
				Action valueActionX= valueAction;
				valueAction = valueAction2;
				valueAction2 = valueActionX;
				compareStr = "notequals";

			} else {
				throw new RuntimeException(" Unandled if code " + this);
			}
           
			ret += 2;
			
			IfAction ifAction = new IfAction(index, goToIndex,valueAction, compareStr,valueAction2);
			actionStack.push(ifAction);
			
		}
		
		else if (this.code == 0x57) {
			
			
			//stackA.pop();
			 System.out.println("Drop last value");

		} else if (this.code == 0x2) {
			
			ValueAction valAction = new ValueAction(index, -1);
			actionStack.push(valAction);

		} else if (this.code == 0xbf) {
		
			
			if(exitLockPC == ret -1) {
				return ret;
			}
			ThrowAction throwAction = new ThrowAction(index, actionStack.pop());
			actionStack.push(throwAction);
			
			

		} else if (this.code == 0x59) {
			

		this.actionStack.markAsDuplicate();
			

		} else if (this.code == 0x5c) {
			

		this.actionStack.mark2AsDuplicate();
			

		} 
		else if (this.code == 0xb0 || this.code == 0xac || this.code == 0xad || this.code == 0xaf
				|| this.code == 0xae) {
		
			Action lastAction = actionStack.pop();
			if(lastAction instanceof AssignAction) {
				 actionStack.push(lastAction);
				 AssignAction assignAct = (AssignAction)lastAction;
				 ReturnAction returnAction = new ReturnAction(index,  assignAct.getLeftHS());
					actionStack.push(returnAction);
			}else {
				 ReturnAction returnAction = new ReturnAction(index,  lastAction);
					actionStack.push(returnAction);
			}
			
			

		} else if (this.code == 0x64) {
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			MathematicalAction action = new 	MathematicalAction(index,valueAction," += ", valueAction2);
			this.actionStack.push(action);

		} else if (this.code == 0x94) {
			Action valueAction = actionStack.pop();
			Action valueAction2 = actionStack.pop();
			MathematicalAction action = new 	DoubleEqualsAction(index,valueAction," == ", valueAction2);
			this.actionStack.push(action);
			// ((bits >> 63) == 0) ? 1 : -1;

		} else if(code ==0xe){
			this.actionStack.push(new ValueAction(index,0.0));
		}else if(code ==0x58){
		
		}
		else if(code ==0xb){
			this.actionStack.push(new ValueAction(index,0.0));
		}
		else if(code == 0xc4) {
			int code = buf[index+1] & 0xFF;
			
			if (code == 0x84) {
				int varIndex = getSignedIndex(buf,index+1);
				int constInc = getSignedIndex(buf,index+3);
				Variable var1 = Variable.getVariable(index, varIndex);
				
				VariableAction valueAction = new VariableAction(index, var1);
				ValueAction valueAction2 = new ValueAction(index, constInc);
				MathematicalAction action = new 	MathematicalAction(index,valueAction,"+=", valueAction2);
				
				actionStack.push(action);
			
			
				ret+=5;
			}else {

				int varIndex =  getSignedIndex(buf,index+1);
				
	Variable var1 = Variable.getVariable(index, varIndex);
				
				VariableAction valueAction = new VariableAction(index, var1);
				actionStack.push(valueAction);
				// // System.out.println("Load objectref from local Var " +
				// localVars.get(varIndex));
		
				ret += 2;
			}
		}
			else {
				throw new RuntimeException("UNknown code" + this);
			}
			
		

		/*
		 * if( index >= currentBranch.endIndex) { if(isIF(this.code)) { int rt=0; }
		 * currentBranch = currentBranch.parent;
		 * 
		 * lines.add(new Line(index, this.code,"}",-1)); }
		 */

		//if (sortedGoTo.contains(index)) {
		//	nextGoTo = index;
		//}
	

		return ret;
	}

	 

	private int decompile2(byte[] buf, int index,ArrayList<Action> gotos) {
		int ret = index;
		
		if (this.code == 0x0) {

		} else if (this.code == 0x1) {

		
		

		} else if (this.code == 0x9) {
		
			
		
		}else if (this.code == 0xf) {
			

		} 
		else if (this.code == 0xc) {
		
		

		} 
		else if (this.code == 0xd) {
			
			

		} 
		else if (this.code == 0xa) {
		
			

		}
		else if (this.code == 0x3 || this.code == 0x4 || this.code == 0x5 || this.code == 0x6 || this.code == 0x7
				|| this.code == 0x8) {
		
		
		} else if (this.code == 0x11) {
			
	
			ret += 2;
		} else if (this.code == 0xa7) {

			int goTo = getSignedIndex(buf, index);
			int branchEnd = index + goTo;
		

			
		
				
				
		
			ret += 2;
		
			int goToIndex = branchEnd;
			GotToAction action = new 	GotToAction(index, goToIndex);
			gotos.add(action);
		

		}else if (this.code == 0xab) {

			
			int offIndex = (index/4) *4;
			offIndex +=5;
			int defaultOff = index + this.getIndex(buf, offIndex);
			
			offIndex +=4;
			int numKeys = this.getIndex(buf, offIndex);
		
		
		
			for (int k = 0; k < numKeys; k++) {
				offIndex +=4;
				int keyI = this.getIndex(buf, offIndex);
				offIndex +=4;
				int offSet = index + this.getIndex(buf, offIndex);
				
			
				CaseAction caseAction = new CaseAction(offSet -1,-1,  "" +keyI);
				cases.put(offSet, caseAction);
				//this.stackA.extraLines.add(line);
			}
				
			ret = offIndex +2;
		

		}
		else if (this.code == 0x84) {

		
		
			ret += 2;

		} else

		if (this.code == 0x19 || this.code == 0x15 || this.code == 0x16 || this.code == 0x17 || this.code == 0x18) {

	
			ret += 1;
		} else if (this.code == 0x2a || this.code == 0x1a || this.code == 0x1e || this.code == 0x22
				|| this.code == 0x26 ) {
		
		
		}

		else if (this.code == 0x2b || this.code == 0x1b || this.code == 0x1f || this.code == 0x23
				|| this.code == 0x27) {
		
		
		} else if (this.code == 0x2c || this.code == 0x1c || this.code == 0x20 || this.code == 0x24
				|| this.code == 0x28 ) {
		
		
		}

		else if (this.code == 0x2d || this.code == 0x1d || this.code == 0x21 || this.code == 0x25
				|| this.code == 0x29 ) {
			
		
		} else if (this.code == 0xb5) {
			
			ret += 2;
			

		} else if (this.code == 0x13 || this.code == 0x14) {

			int constIndex = getIndex(buf, index);
			ret += 2;

			
			
		} else if (this.code == 0xb3) {

			
			ret += 2;
			

		} else if (this.code == 0xc1) {

			int constIndex = getIndex(buf, index);
			

		} else if (this.code == 0xb2) {
			
			ret += 2;
			

		} else if (this.code == 0xc0) {
			
			ret += 2;
			
			

		} else if (this.code == 0xb4) {
			
			ret += 2;
		
		

		} else if (this.code == 0xc2) {

		
          
		

		} 
		else if (this.code == 0xc3) {

			
		
		

		}
		else if (this.code == 0xbe) {

		
		

		} else if (this.code == 0x68 || this.code == 0x69 || this.code == 0x6a || this.code == 0x6b) {
		

		

		}else if (this.code == 0x73 || this.code == 0x72) {
		
	

		}
		else if (this.code == 0x6c || this.code == 0x6d || this.code == 0x6e || this.code == 0x6f || this.code == 0x71) {
		
		
		

		} else if (this.code == 0x60 || this.code == 0x61 || this.code == 0x62 || this.code == 0x63) {
			
            	
           
			

		} else if (this.code == 0x65 || this.code == 0x64 || this.code == 0x66 || this.code == 0x67) {
		
		
			

		} else if (this.code == 0x8e || this.code == 0x8b || this.code == 0x88) {
		
		
		} else if (this.code == 0x8f || this.code == 0x8c || this.code == 0x85) {
		
			
		} else if (this.code == 0x8d || this.code == 0x87 || this.code == 0x8a) {
			
			
		}
		/*
		 * 86 90, 89 float 8e 8b 88, int 8f,8c 85 long 8d,87 8a double 91,byte
		 * 92,character
		 * 
		 * 93,short
		 */

		else if (this.code == 0x91) {
			
			
		} else if (this.code == 0x92) {
		
			
		} else if (this.code == 0x93) {
			
			
		} else if (this.code == 0x86 || this.code == 0x89 || this.code == 0x90) {
			
		
		} else if (this.code == 0x7e || this.code == 0x7f) {
			
		
			

		} else if (this.code == 0x79 || this.code == 0x78) {
			
		
			

		} else if (this.code == 0x7a || this.code == 0x7b || this.code == 0x7c) {
		
		
			

		} else if (this.code == 0x83 || this.code == 0x82) {
		
		
			

		} else if (this.code == 0x95 || this.code == 0x96 || this.code == 0x98 || this.code == 0x97) {
		
		
		

		} else if (this.code == 0x80 || this.code == 0x81) {
			
		
		

		} else if (this.code == 0x70) {
		
		
		

		} else if (this.code >= 0x4f && this.code <= 0x56) {
			
			
			
			

		} else if (this.code == 0x30 || this.code == 0x31 || this.code == 0x32 || this.code == 0x33 || this.code == 0x2e || this.code == 0x34
				|| this.code == 0x2f) {

			
			

		} else if (this.code == 0xbc) {

		
		
			ret += 1;
			
		
		

		} else if (this.code == 0xbd) {

		
			
			ret += 2;
		
		

		} else if (this.code == 0xbb) {
			
			ret += 2;
			
		

		} else if (this.code == 0xb6 || this.code == 0xb7 || this.code == 0xb8 || this.code == 0xb9 || this.code == 0xba) {
			
			ret += 2;
			if (code == 0xb9) {
				ret += 2;
			}
			
		

		} else if (this.code == 0x12) {
		
			ret += 1;
		

		} else if (this.code == 0x5f) {
			
		
		} else if (this.code == 0x5a) {
			
		} else if (this.code == 0x10) {
		
		
			ret += 1;
		} else if (this.code == 0xb1) {
		
			
		} else if (this.code == 0x38 ||this.code == 0x36 || this.code == 0x39 || this.code == 0x3c || this.code == 0x3b || this.code == 0x3d
				|| this.code == 0x3e || this.code == 0x4c || this.code == 0x4b || this.code == 0x4d || this.code == 0x4e
				|| this.code == 0x3a || this.code == 0x37 || this.code == 0x3f || this.code == 0x40 || this.code == 0x41
				|| this.code == 0x42 || this.code == 0x47 || this.code == 0x48 || this.code == 0x49
				|| this.code == 0x43 || this.code == 0x44 || this.code == 0x45 || this.code == 0x46
				|| this.code == 0x4a) {
			
			if (this.code == 0x36) {
				
				ret += 1;
			}
			if (this.code == 0x38) {
				
				ret += 1;
			}
			if (this.code == 0x3a) {
				
				ret += 1;
			}
			if (this.code == 0x39) {
				
				ret += 1;
			}
			if (this.code == 0x37) {
			
				ret += 1;
			}
		

		
			// System.out.println(lineStr);
		} else if (isIF(this.code)) {
			
			
		
			int goTo = getSignedIndex(buf, index);
			int branchEnd = index + goTo;

			int goToIndex = branchEnd;

			
           
			ret += 2;
			
			GotToAction ifAction = new GotToAction(index, goToIndex);
			ifAction.isIf = true;
			if(goToIndex < index) {
				int debugME =0;
			}
			gotos.add(ifAction);
			
		}
		
		else if (this.code == 0x57) {
			
			
		

		} else if (this.code == 0x58) {
			
			
		

		}else if (this.code == 0x2) {
			
		

		} else if (this.code == 0xbf) {
		
		
			

		} else if (this.code == 0x59) {
			

		
			

		} else if (this.code == 0xb0 || this.code == 0xac || this.code == 0xad || this.code == 0xaf
				|| this.code == 0xae) {
		
		
			
			

		} else if (this.code == 0x64) {
		

		} else if (this.code == 0x94) {
		

		} else if(code ==0xe){
			
		}else if(code ==0xb){
			
		}else if(code == 0xc4) {
			int code = buf[index];
			
			if (code == 84) {
				ret+=5;
			}else {
				ret+=3;
			}
		}
			else {
				throw new RuntimeException("UNknown code" + this);
			}
			
		

		

		return ret;
	}
	
	public static HashMap<Integer, ByteCode> codes = new HashMap<Integer, ByteCode>();

	public static void loadByteCodes() {
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();

			InputStream is2 = classloader.getResourceAsStream("oliver/bytecode.csv");

		    if(is2 == null) {
           	 is2 = new FileInputStream("src/oliver/bytecode.csv");
            }
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(is2));
			String line = bufReader.readLine();
			while (line != null) {

				// line = line.replaceAll("\"[^\"]+", "$1");

				Pattern pat = Pattern.compile("\"[^\"]+\"");
				Matcher matcher = pat.matcher(line);
				while (matcher.find()) {
					String part = matcher.group();
					String newStr = part.replaceAll(",", " ");
					line = line.replace(part, newStr);
					
				}
				String[] parts = line.split(",");
				ByteCode code = new ByteCode(parts);
				codesTested.add( code.code );
				codes.put(code.code, code);
				line = bufReader.readLine();
			}
		} catch (Exception t) {
			t.printStackTrace();
		}

	}

	static boolean isIF(int code) {
		if (code == 0xc7 || code == 0xc6) {
			return true;
		}
		if (code <= 0xa6 && code >= 0x99) {
			return true;
		}
		return false;
	}
	
	static void  printNotTested() {
		
		
		ByteCode code = new ByteCode();
		for(int c : codesTested) {
		  code.code = c;
		  System.out.println(code);
		}
		
		 System.out.println(codesTested.size() + " Codes not tested");
	}
	
	public ArrayList<Action> getGottos(byte[] buf, int last) {

	
		ArrayList<Action> gotos = new ArrayList<Action>();
		for (x = 0; x < last; x++) {
			

			this.code = buf[x] & 0xff;
			
			x = decompile2(buf, x,gotos);
			
		}
		
	  return gotos;
		
		
	}
	
	private static void addToEach( ArrayList<Action> old, int toAdd) {
		
		HashMap<Integer, Integer> oldToNew = new HashMap<Integer, Integer>();
	 //15, 46, 54 add 12  
	 // y = y + n*diff
	 // goto = goto + n *diff
     // how many between  pc = 12 goto = 100
	 //  	
	 	
		
		
	}
}
