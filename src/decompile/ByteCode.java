package decompile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import oliver.ClassDescription2;
import oliver.ConstEntry2;
import oliver.MethodVariable;
import oliver.Variable;
import oliver.action.Action;
import oliver.action.CaseAction;
import oliver.action.SwitchAction;

public class ByteCode {

	String name;
	public int code;

	String fromCode;

	String stackChange;

	String instruction;
	String[] byteNames = new String[0];
	String[] preStack = new String[0];
	String[] postStack = new String[0];
	String loadVal = "";
	public boolean isConstantPool = false;
   boolean wide;
	public enum TYPE {
		INTOVAR, FROMVAR, METHOD, LOADVAL, LOADARRAY, STOREARRAY, NEWARRAY, COMPARE, RETURN, SUBTRACT, DIVIDE, MULTIPLY,
		ADD, REMAINDER, BTWISEAND, BTWISEOR, BTWISESHIFTLEFT, BTWISESHIFTRIGHT, IF, ARRAYLENGTH, PUTFIELD, GETFIELD,
		CONVERT, NEGATE, PUSH, NEWOBJECT, THROW, GOTO, INCVAR, POP, DUPLICATE, BYTEPUSH, BTWISEXOR, INSTANCEOF, CASE,
		SWITCH, COMPAREZERO, SWAP, SYNCHRONIZED, UNSYNCHRONIZED, CAST, TRY, IGNORE, FINALLY, CATCH, WIDE, BREAK, CONTINUE, DO, ELSE
	};

	boolean multiArray = false;
	public int var;
	public boolean isStatic = false;
	public TYPE type;
	private boolean returnVoid = false;
	private boolean mathematical;
	private String ifCmp;
	private boolean ifTwoParts;
	private String convert;
	private boolean pop2 = false;
	static int lastGoTo;

	public ByteCode(String name, int code, String fromCode, String stackChange, String instruction) {
		super();
		this.name = name;
		this.code = code;
		this.fromCode = fromCode;

		if (fromCode.length() > 0) {

			if (name.equals("lookupswitch") || name.equals("tableswitch") || name.equals("wide")) {

			} else {
				fromCode = fromCode.replaceAll("\"", "");
				String[] parts = fromCode.split(":");
				int numBytes = Integer.valueOf(parts[0]);
				byteNames = parts[1].split("comma ");
				int y = 0;

			}
		}

		if (stackChange.length() > 0) {

			stackChange = stackChange.replaceAll("\"", "");

			if (stackChange.startsWith("?")) {
				stackChange = stackChange.replace("?", "");
				postStack = stackChange.split("comma ");

			} else if (stackChange.endsWith("?")) {
				stackChange = stackChange.replace("?", "");
				preStack = stackChange.split("comma ");

			} else if (stackChange.toLowerCase().equals("[no change]")) {

			} else if (stackChange.toLowerCase().equals("[same as for corresponding instructions]")) {

			} else {
				String[] parts = stackChange.split("\\?");

				preStack = parts[0].split("comma ");
				postStack = parts[1].split("comma ");
			}

			int y = 0;

		}
		this.stackChange = stackChange;
		this.instruction = instruction;
		this.instruction = this.instruction.replace("into a local variable", "into variable")
				.replace("in a local variable", "into variable").replace("into local variable", "into variable");
		if (this.instruction.contains("into variable")) {
			type = TYPE.INTOVAR;
			if (byteNames.length == 0) {
				String variable = "" + this.instruction.replaceAll(".*into variable ", "").charAt(0);
				this.var = Integer.valueOf(variable);
			}

		}

		if (this.instruction.contains("constant pool")) {
			isConstantPool = true;
		}
		if (this.instruction.contains("invoke")) {
			if (this.instruction.contains("method")) {
				this.type = TYPE.METHOD;

			}
			if (this.instruction.contains("static")) {
				this.isStatic = true;

			}
		}
		this.instruction = this.instruction.replace("from a local", "from local");
		if (this.instruction.contains("from local variable")) {
			type = TYPE.FROMVAR;
			if (byteNames.length == 0) {
				String variable = "" + this.instruction.replaceAll(".*from local variable ", "").charAt(0);
				this.var = Integer.valueOf(variable);
			} else {
				int debugME = 0;
			}
		}
		if (type == null) {

			if (this.name.matches(".*const_.*")) {

				String val = this.name.replaceAll(".*_", "");
				if (val.length() != 1) {
					if (val.equals("m1")) {
						val = "-1";
					}
					if (val.equals("null")) {
						val = "null ";
					}
				}
				char firstChar = this.name.charAt(0);
				switch (firstChar) {
				case 'f':
					val = "float " + val + "f";
					break;
				case 'l':
					val = "long " + val + "L";
					break;
				case 'i':
					val = "int " + val;
					break;
				case 'd':
					val = "double " + val;
					break;
				}

				this.type = TYPE.LOADVAL;
				this.loadVal = val;

			}

			if (this.name.matches(".aload")) {

				this.type = TYPE.LOADARRAY;

			}
			if (this.name.matches("monitorenter")) {

				this.type = TYPE.SYNCHRONIZED;

			}
			if (this.name.matches("monitorexit")) {

				this.type = TYPE.UNSYNCHRONIZED;

			}
			if (this.name.matches("instanceof")) {

				this.type = TYPE.INSTANCEOF;

			}

			if (this.name.matches(".astore")) {

				this.type = TYPE.STOREARRAY;
				if (this.name.startsWith("l")) {
					this.convert = "long";
				}
				if (this.name.startsWith("i")) {
					this.convert = "int";
				}
				if (this.name.startsWith("f")) {
					this.convert = "float";
				}
				if (this.name.startsWith("d")) {
					this.convert = "double";
				}
				if (this.name.startsWith("s")) {
					this.convert = "int";
				}
				if (this.name.startsWith("b")) {
					this.convert = "byte";
				}
				if (this.name.startsWith("c")) {
					this.convert = "char";
				}
			}

			if (this.name.matches(".*newarray")) {

				this.type = TYPE.NEWARRAY;

				if (this.name.equals("multianewarray")) {
					multiArray = true;
				}
			}
			if (this.name.matches(".cmp[g,l]")) {

				this.type = TYPE.COMPARE;

			}
			if (this.name.matches("lcmp")) {

				this.type = TYPE.SUBTRACT;
				this.mathematical = true;
			}
			if (this.name.matches("swap")) {

				this.type = TYPE.SWAP;

			}

			if (this.name.matches(".*return")) {

				if (this.name.equals("return")) {
					this.returnVoid = true;
				}
				this.type = TYPE.RETURN;

			}
			if (this.name.matches(".sub")) {

				this.mathematical = true;
				this.type = TYPE.SUBTRACT;

			}
			if (this.name.matches("pop.*")) {

				if (this.name.endsWith("2")) {
					this.pop2 = true;
				}
				this.type = TYPE.POP;

			}
			if (this.name.matches("dup.*")) {

				this.type = TYPE.DUPLICATE;

			}
			if (this.name.matches("..push")) {

				this.type = TYPE.BYTEPUSH;

			}
			if (this.name.matches("lookupswitch")) {

				this.type = TYPE.SWITCH;

			}
			if (this.name.matches("checkcast")) {

				this.type = TYPE.CAST;

			}
			if (this.name.matches("tableswitch")) {

				this.type = TYPE.SWITCH;

			}
			if (this.name.matches(".div")) {

				this.mathematical = true;
				this.type = TYPE.DIVIDE;

			}
			if (this.name.matches(".mul")) {
				this.mathematical = true;
				this.type = TYPE.MULTIPLY;

			}
			if (this.name.matches(".add")) {
				this.mathematical = true;
				this.type = TYPE.ADD;

			}
			if (this.name.matches(".neg")) {
				this.mathematical = true;
				this.type = TYPE.NEGATE;

			}
			if (this.name.matches(".rem")) {
				this.mathematical = true;
				this.type = TYPE.REMAINDER;

			}
			if (this.name.matches(".and")) {
				this.mathematical = true;
				this.type = TYPE.BTWISEAND;

			}
			if (this.name.matches(".or")) {
				this.mathematical = true;
				this.type = TYPE.BTWISEOR;

			}
			if (this.name.matches("ldc.*")) {

				this.type = TYPE.PUSH;

			}
			if (this.name.matches(".shl")) {
				this.mathematical = true;
				this.type = TYPE.BTWISESHIFTLEFT;

			}
			if (this.name.matches(".xor")) {
				this.mathematical = true;
				this.type = TYPE.BTWISEXOR;

			}
			if (this.name.matches(".*shr")) {
				this.mathematical = true;
				this.type = TYPE.BTWISESHIFTRIGHT;

			}
			if (this.name.matches("arraylength")) {
				this.type = TYPE.ARRAYLENGTH;
			}
			if (this.name.matches("put.*")) {
				this.type = TYPE.PUTFIELD;
				if (this.name.endsWith("static")) {
					this.isStatic = true;
				}
			}
			if (this.name.equals("new")) {
				this.type = TYPE.NEWOBJECT;
			}
			if (this.name.equals("athrow")) {
				this.type = TYPE.THROW;
			}
			if (this.name.equals("goto")) {
				this.type = TYPE.GOTO;
			}
			if (this.name.equals("iinc")) {
				this.type = TYPE.INCVAR;
			}

			if (this.name.matches(".2.")) {
				this.type = TYPE.CONVERT;
				if (this.name.endsWith("l")) {
					this.convert = "long";
				}
				if (this.name.endsWith("i")) {
					this.convert = "int";
				}
				if (this.name.endsWith("f")) {
					this.convert = "float";
				}
				if (this.name.endsWith("d")) {
					this.convert = "double";
				}
				if (this.name.endsWith("s")) {
					this.convert = "short";
				}
				if (this.name.endsWith("b")) {
					this.convert = "byte";
				}
				if (this.name.endsWith("c")) {
					this.convert = "char";
				}
			}
			if (this.name.matches("get.*")) {
				this.type = TYPE.GETFIELD;
				if (this.name.endsWith("static")) {
					this.isStatic = true;
				}
			}
			if (this.name.matches("wide")) {
				this.type = TYPE.WIDE;
			}
			if (this.name.matches("if.*")) {

				if (this.name.endsWith("lt")) {
					this.ifCmp = "<";
				}
				if (this.name.endsWith("eq")) {
					this.ifCmp = "==";
				}

				if (this.name.endsWith("le")) {
					this.ifCmp = "<=";
				}
				if (this.name.endsWith("gt")) {
					this.ifCmp = ">";
				}
				if (this.name.endsWith("ge")) {
					this.ifCmp = ">=";
				}
				if (this.name.endsWith("ne")) {
					this.ifCmp = "!=";
				}

				this.type = TYPE.IF;
				if (this.name.startsWith("if_a")) {
					this.ifCmp = "==";
					this.ifTwoParts = true;
					if (this.name.startsWith("if_acmpne")) {
						this.ifCmp = "!=";
					}
				} else {
					if (this.name.endsWith("null")) {
						this.ifCmp = "== null";
						if (this.name.endsWith("nonnull")) {
							this.ifCmp = "!= null";
						}
					} else if (this.name.startsWith("if_")) {
						this.ifTwoParts = true;
					} else {
						this.ifCmp += " 0 ";
					}
				}
			}
		}
	}

	private int getIndex(byte[] buf, int pos, int numBytes) {
		int ret = 0;
		for (int x = 0; x < numBytes; x++) {
			ret = ret << 8;
			ret += buf[pos + x] & 0xff;
		}
		return ret;
	}
	private int getUnsignedIndex(byte[] buf, int pos, int numBytes) {
		int ret = 0;
		short s = (short) (buf[pos]<<8 | buf[pos+1] & 0xFF);
		return (int)s;
	}
	static HashMap<String, JavaAction> newArrStr = new HashMap<String, JavaAction>();
	public int decompile(byte[] buf, int index, Stack<JavaAction> stack, ArrayList<JavaAction> store, boolean wide2, boolean returnsBoolean) {

		if ( index == 291) {
			int debugME =0;
		}
		if (this.type == null) {
			if(this.name.equals("nop")) {
				return index +1;
			}
			throw new RuntimeException("Unhandled" + this.name);
		}
		int x = 0;
		int constantPoolIndex = -1;
		Variable var = null;
		if (this.type == TYPE.PUSH) {
			int debugME = 0;
		}
		if (isConstantPool) {

			if (byteNames.length == 1) {
				constantPoolIndex = getIndex(buf, index + 1, 1);
			} else {
				constantPoolIndex = getIndex(buf, index + 1, 2);
			}
			if(constantPoolIndex == 277) {
				int debugME =0;
			}
			var = ConstEntry2.consts.get(constantPoolIndex).getVariable();
		}
		if (this.mathematical) {
			return this.decompileMatchemactical(buf, index, stack, store);
		}
		if (type == TYPE.SWITCH) {
			if (this.name.equals("tableswitch")) {
				return this.decompileTableSwitch(buf, index, stack, store);
			}
			return this.decompileSwitch(buf, index, stack, store);
		}
		switch (this.type) {
		case PUSH:

			 if(var == null) {
				 int debugME =0;
			 }
			String val = var.getName();
			if (var.getType().equals("") && !val.contains("\"")) {
				val = '"' + val + '"';
			}
			stack.push(new JavaAction(TYPE.PUSH, index, var.getType(), val));

			break;
		case BYTEPUSH:

			int xo = getIndex(buf, index + 1, this.byteNames.length);

			stack.push(new JavaAction(TYPE.BYTEPUSH, index, "int", "" + xo));

			break;
		case INTOVAR:
			int varIndex = this.var;
			if (byteNames.length > 0) {

				varIndex = getIndex(buf, index + 1, this.byteNames.length);

			}
			JavaAction popped = stack.pop();
			Variable vr = Variable.getVariable(index, varIndex);

			val = popped.value;
            if(vr.getType().equals("boolean")) {
            	val=val.replace("1", "true");
            	val=val.replace("0", "false");
            	
            }
            if(vr.getName().startsWith("unusedVariable")) {
            	 if(vr.getType().equals("boolean") && popped.returnType.equals("int"))  {
            		 popped.returnType="boolean";
            	 }
            	 if(!val.equals("finally")) {
            		 JavaAction newAcc =new JavaAction(TYPE.INTOVAR, index, popped.returnType, vr.getName() + "=" + val);
                	 if(popped.type == ByteCode.TYPE.CATCH) {
                		 newAcc.funnyCatch = true;
                	 }
                	  store.add(newAcc); 
            	 }
            	
            	 
            	

            }else {
            	if(popped.type == ByteCode.TYPE.CATCH) {
            		popped.value = popped.value.replace(")", " " + vr.getName() +")");
            		store.add(popped);
            	}else {
            		if(index == lastGoTo) {
            			
            			//long m = (e == 0) ? (bits & 0xfffffffffffffL) << 1 : (bits & 0xfffffffffffffL) | 0x10000000000000L;
            			JavaAction ff = store.get(store.size() -2);
            			JavaAction ff2 = stack.pop();
            			 if(vr.getType().equals("boolean")) {
            				 ff2.value=ff2.value.replace("1", "true");
            				 ff2.value=ff2.value.replace("0", "false");
            	            	
            	            }
            			store.add(new JavaAction(TYPE.INTOVAR, ff2.index, vr.getType(), vr.getName() + "=" + ff2.value));
            		    
            			store.add(new JavaAction(TYPE.INTOVAR, ff.index, vr.getType(), vr.getName() + "=" + val));
            			ff.index ++;
            			int debugME =0;
            		}else {
            			if(vr.getType().equals("REPLACEME")) {
            				if(vr.newType != null && !vr.newType.equals(popped.returnType)) {
            					vr.name +="x";
            					
            				}
            				vr.newType=popped.returnType;
            				store.add(new JavaAction(TYPE.INTOVAR, index, popped.returnType, vr.getName() + "=" + val));
            			}else {
            				store.add(new JavaAction(TYPE.INTOVAR, index, vr.getType(), vr.getName() + "=" + val));
            			}
            			
            		}
            		
            	}
            	

            }
		
			System.out.println("Store into var" + varIndex);
			break;
		case FROMVAR:
			varIndex = this.var;
			if (byteNames.length > 0) {

				varIndex = getIndex(buf, index + 1, this.byteNames.length);

			}
			vr = Variable.getVariable(index, varIndex);
			JavaAction action = new JavaAction(TYPE.INTOVAR, index, vr.getType(), vr.getName());
			stack.push(action);
			System.out.println("Load from var" + varIndex);
			break;

		case METHOD:
			MethodVariable methodVar = (MethodVariable) var;
			String sig = methodVar.formatParams(stack);
			if(sig.contains("println")) {
				int debugme =0;
			}
			if (this.isStatic) {
				String result = methodVar.className + "." + sig;
				action = new JavaAction(TYPE.METHOD, index, methodVar.getType(), result);

				stack.push(action);
			} else {
				action = stack.pop();
				String obj = action.value;
				String result = obj + "." + sig;
				result= result.replace(".<init>", "");
				result= result.replace("this(", "super(");
				if(result.startsWith("super(")) {
					action = new JavaAction(TYPE.METHOD, 0, methodVar.getType(), result);
				}else {
					if( methodVar.name.equals("<init>")) {
						  action = new JavaAction(TYPE.METHOD, index, methodVar.getClassName(), result);
					}else {
						  action = new JavaAction(TYPE.METHOD, index, methodVar.getType(), result);
					}
				 
				}
			
				stack.push(action);
			}
			break;
		case LOADVAL:

			if(this.code == 1) {
				action = new JavaAction(TYPE.LOADVAL, index, null, "null");
				stack.push(action);
			}else {
			String typeS = loadVal.split(" ")[0];
			val = loadVal.split(" ")[1];
			action = new JavaAction(TYPE.LOADVAL, index, typeS, val);
			stack.push(action);
			}
			break;
		case NEWOBJECT:

			String typeS = var.getType();
			val = "new " + typeS;
			action = new JavaAction(TYPE.NEWOBJECT, index, typeS, val);
			stack.push(action);
			break;
		case CAST:

			typeS = var.getType();
			
			action = stack.pop();
			typeS=Variable.getTheType(typeS);
			
			val = "((" + typeS +")" + action.value +")";
			action = new JavaAction(TYPE.CAST, index, typeS, val);
			stack.push(action);
			break;	
		case GOTO:

			int goTo = index +getUnsignedIndex(buf, index + 1, this.byteNames.length);
			if(goTo < index) {
				int debugME =0;
			}
			val = "GOTO" + goTo;
			action = new JavaAction(TYPE.GOTO, index, "null", val);
			action.goTo = goTo;
			store.add(action);
			lastGoTo = goTo;
			break;
		case LOADARRAY:

			action = stack.pop();
			JavaAction action2 = stack.pop();

			String arrIndex = action.value;
			String arr = action2.value;
			val = arr + "[" + arrIndex + "]";
			action = new JavaAction(TYPE.LOADARRAY, index, action2.returnType, val);
			stack.push(action);

			break;
		case POP:

			if (stack.size() == 0) {

			} else {
				action = stack.pop();
				store.add(action);
				if (this.pop2) {
					action = stack.pop();
					store.add(action);
				}
			}
			break;
		case SWAP:

			action = stack.pop();
			if (action.returnType.equals("double") || action.returnType.equals("long")) {
				stack.push(action);
			} else {
				action2 = stack.pop();
				stack.push(action);
				stack.push(action2);
			}

			break;
		case DUPLICATE:

			
			 
			if (this.name.equals("dup")) {
			
				 if(stack.peek().type == ByteCode.TYPE.NEWOBJECT ) {
					 return index + 1;
				 }
				JavaAction other = new JavaAction(stack.peek(), index);
				stack.push(other);
			} else if (this.name.equals("dup_x1")) {
				action = stack.pop();
				if (action.returnType.equals("double") || action.returnType.equals("long")) {
					stack.push(action);
				} else {
					JavaAction act2 = stack.pop();
					JavaAction other = new JavaAction(action, index);
					stack.push(action);
					stack.push(act2);
					stack.push(other);
				}

			} else if (this.name.equals("dup_x2")) {
				action = stack.pop();
				if (action.returnType.equals("double") || action.returnType.equals("long")) {
					stack.push(action);
				} else {
					JavaAction act2 = stack.pop();
					JavaAction act3 = stack.pop();
					stack.push(action);
					stack.push(act3);
					stack.push(act2);
					JavaAction other = new JavaAction(action, index);
					stack.push(other);
				}
			} else if (this.name.equals("dup2")) {
				action = stack.pop();
				if (action.returnType.equals("double") || action.returnType.equals("long")) {
					stack.push(action);
					JavaAction other = new JavaAction(action, index);
					stack.push(other);
				} else {
					JavaAction act2 = stack.pop();
					stack.push(act2);
					stack.push(action);
					JavaAction other = new JavaAction(act2, index);
					stack.push(other);
					other = new JavaAction(action, index);
					stack.push(other);
				}
			} else if (this.name.equals("dup2_x1")) {
				action = stack.peek();
				if (action.returnType.equals("double") || action.returnType.equals("long")) {
					JavaAction act2 = stack.pop();
					stack.push(action);
					stack.push(act2);
					stack.push(action);
				} else {
					JavaAction act2 = stack.pop();
					JavaAction act3 = stack.pop();
					stack.push(act2);
					stack.push(action);
					stack.push(act3);
					stack.push(act2);
					stack.push(action);
				}
			} else if (this.name.equals("dup2_x2")) {
				throw new RuntimeException("I give up");
			}

			break;

		case CONVERT:

			action = stack.pop();

			val = "(" + this.convert + ")(" + action.value+ ")";
			action = new JavaAction(TYPE.CONVERT, index, this.convert, val);
			stack.push(action);

			break;
		case THROW:

			action = stack.pop();

			if(action.value.startsWith("unusedVariable")) {
				action = new JavaAction(TYPE.IGNORE, index, action.returnType, "ignore");
				stack.push(action);
				break;
			}
			if(action.value.startsWith("lambdaVar")) {
				break;
			}
			val = " throw " + action.value;
			action = new JavaAction(TYPE.THROW, index, action.returnType, val);
			stack.push(action);

			break;
		case SYNCHRONIZED:

			action = stack.peek();

			val = " synchronized (" + action.value + ")";
			action = new JavaAction(TYPE.SYNCHRONIZED, index, action.returnType, val);
			store.add(action);

			break;
		case UNSYNCHRONIZED:
			action = stack.peek();
			action = new JavaAction(TYPE.UNSYNCHRONIZED, index, action.returnType, "");
			store.add(action);

			break;
		case COMPARE:

			action = stack.pop();
			action2 = stack.pop();

			val = action2.value + " == " + action.value;
			action = new JavaAction(TYPE.COMPARE, index, action2.returnType, val);
			stack.push(action);

			break;

		case INSTANCEOF:

			action = stack.pop();

			val = action.value + " instanceof " + var.getType();
			action = new JavaAction(TYPE.INSTANCEOF, index, "boolean", val);
			stack.push(action);

			break;
		case ARRAYLENGTH:

			action = stack.pop();

			val = action.value + ".length";
			action = new JavaAction(TYPE.ARRAYLENGTH, index, "int", val);
			stack.push(action);

			break;
		case IF:

			goTo = index + getUnsignedIndex(buf, index + 1, 2);
			if(goTo < index) {
				int debugME =0;
			}
			action = stack.pop();
			val = action.value;

			
            
			if (this.ifTwoParts) {
				
				action = new JavaAction(TYPE.IF, index, action.returnType, stack.pop().value, this.ifCmp , val);
			} else {
				String  ifCmp = this.ifCmp.split(" ")[0];
				String val2 = this.ifCmp.split(" ")[1];
				if (action.returnType.contentEquals("boolean")) {
					val2  = val2.replace("0", "false");
					
				}
				 if(action.type == TYPE.COMPARE) {
					 val = val.replace(" == ", " - ");
				 }
				action = new JavaAction(TYPE.IF, index, action.returnType, val, ifCmp , val2);
			}

		
			action.goTo = goTo;
			store.add(action);

			break;
		case INCVAR:

			
			varIndex = getIndex(buf, index + 1, 1);
			if(wide2) {
				varIndex = getIndex(buf, index + 1, 2);
			}
			var = Variable.getVariable(index, varIndex);
			int increment = buf[index + 2];
			if(wide2) {
				increment =  0xff & buf[index + 3];
				increment = increment << 8;
				increment += 0xff & buf[index + 4];
			}
			val = var.getName() + " += " + increment;
			action = new JavaAction(TYPE.ADD, index, var.getType(), val);
			store.add(action);

			break;
		case PUTFIELD:

			action = stack.pop();
			val = action.value;

			if(var.className.contains("PrintStream")) {
				int debugME =0;
			}
			if (this.isStatic) {
				val = var.className + "." + var.getName() + "=" + val;
			} else {
				action = stack.pop();
				if(action.type == TYPE.NEWOBJECT) {
					System.out.println("Investigate");
					action = stack.pop();
				}
				val = action.value + "." + var.getName() + "=" + val;
			}
			 if(var.getType().equals("boolean")) {
	            	val=val.replace("1", "true");
	            	val=val.replace("0", "false");
	            }
			action = new JavaAction(TYPE.PUTFIELD, index, var.getType(), val);
			store.add(action);

			break;
		case GETFIELD:

			if (this.isStatic) {
				val = var.className + "." + var.getName();
			} else {
				action = stack.pop();

				val = action.value + "." + var.getName();
			}

			action = new JavaAction(TYPE.PUTFIELD, index, var.getType(), val);
			stack.push(action);

			break;
		case STOREARRAY:

			action = stack.pop();
			action2 = stack.pop();
			JavaAction action3 = stack.pop();
			val = action.value;
			if(action.returnType.equals("double")) {
				if(val.equals("Infinity")) {
					action.value ="Double.NaN";
				}
			}
			arrIndex = action2.value;
			if(action3.type == TYPE.NEWARRAY) {
			  int count = action3.count;
			  if(count > 1) {
				  int debugME =0;
			  }
			 
			  int valOne = Integer.valueOf(arrIndex);
			  if(valOne == 0) {
				  
				  String repStr = "[]{REPME";
				  for(int y =0; y< count-1; y++) {
					  repStr += ",REPME";
				  }
				  repStr += "}";
				  String newArrStr2 = action3.value.replace("[" + count + "]", repStr);
				  newArrStr2 = newArrStr2.replaceFirst("REPME", action.value );
				  JavaAction action4 = new JavaAction(TYPE.STOREARRAY, index, action.returnType, newArrStr2);
				  
				  if(action3.other != null) {
					  action3.other.value = action4.value; 
				  }
				
			  }else {
				  
				  action3.value = action3.value.replaceFirst("REPME", action.value );
				  if(action3.other != null) {
					  action3.other.value = action3.value; 
				  }
				  int debugME =0;
			  }
			  
			  
			}else {
			arr = action3.value;
			String conv ="";
			if(this.convert != null && !this.convert.equals(action.returnType)) {
				conv = "(" + this.convert +")(" + action.value +")";
			}else {
				conv = action.value;
			}
			val = arr + "[" + arrIndex + "] = " + conv ;
			action = new JavaAction(TYPE.STOREARRAY, index, action.returnType, val);
			store.add(action);
			}

			break;

		case RETURN:

			typeS = "void";
			if (this.returnVoid == true) {
				val = "return ";
			} else {
				action = stack.pop();
				val = "return " + action.value;
				typeS = action.returnType;
			}

			if(returnsBoolean) {
				if(val.endsWith(" 1")) {
					val = "return true";
				}
				if(val.endsWith(" 0")) {
					val = "return false";
				}
			}
			action = new JavaAction(TYPE.RETURN, index, typeS, val);
			store.add(action);

			break;
		case NEWARRAY:

			typeS = "";
			if (var == null) {
				int atype = buf[index + 1];
				typeS = this.getAType(atype);
			} else {
				typeS = Variable.getTheType(var.getType());
			}
            val ="";
			action = stack.pop();
			String count = action.value;
			if(typeS.contains("[")) {
				int debugME =0;
			}
			boolean set = false;
			if (this.multiArray) {
				int dimensions = buf[index + 3];
				typeS =typeS.replace("]", "").replace("[", "");
				for (int y = 1; y < dimensions; y++) {
					count = "" + stack.pop().value + "][" + count;

				}
			}else {
				if(typeS.contains("[")) {
					int debugME =0;
					set = true;
					val = "new " + typeS.replace("[", "[" + count) + "[]";
				}
			}

			if(!set) {
			val = "new " + typeS + "[" + count + "]";
			}
			
			action = new JavaAction(TYPE.NEWARRAY, index, typeS + "[]", val);
			try {
			action.count = Integer.valueOf(count);
			} catch(Exception t) {
				t.printStackTrace();
			}
			catch(Throwable t) {
				t.printStackTrace();
			}finally {
				action.count = action.count;
			}
			stack.push(action);

			break;
		default:
			System.out.println("Not being processed");
		}
		index += byteNames.length;
		return index + 1;

	}

	public int decompileSwitch(byte[] buf, int index, Stack<JavaAction> stack, ArrayList<JavaAction> store) {
		int offIndex = ((index) / 4) * 4;
		offIndex += 6;
		int defaultOff = index + this.getIndex(buf, offIndex, 2);

		offIndex += 4;
		int numKeys = this.getIndex(buf, offIndex, 2);

		JavaAction key2 = stack.pop();
		//ActionBranch.$SWITCH_TABLE$decompile$ByteCode$TYPE()[lastAction.type.ordinal()]
		
		int bracketIndex = key2.value.indexOf("[");
		int lastBracketIndex =  key2.value.lastIndexOf("]");
		String simplified = key2.value;
		if(bracketIndex != -1) {
			simplified = key2.value.substring(bracketIndex +1, lastBracketIndex).replace(".ordinal()", "");
			
		}
		JavaAction switchAction = new JavaAction(this.type, index, "null", "switch (" + simplified + ")");
		List<String> enums = new ArrayList<String>();
		for (String key : ClassDescription2.enumes.keySet()) {
			if(key2.value.contains(key)) {
				enums =  ClassDescription2.enumes.get(key);
			}
		}
		
		store.add(switchAction);

		JavaAction defaultAction = new JavaAction(TYPE.CASE, defaultOff, "null", "default:");
		store.add(defaultAction);
		  ArrayList<JavaAction> caseActions = new ArrayList<JavaAction> ();
		for (int k = 0; k < numKeys; k++) {
			offIndex += 4;
			int keyI = this.getIndex(buf, offIndex, 2);
			offIndex += 4;
			int offSet = index + this.getIndex(buf, offIndex, 2);

			String keyStr = "" + keyI;
			if(enums.size() >= keyI -1) {
				keyStr = enums.get(keyI -1);
			}
			JavaAction caseAction = new JavaAction(TYPE.CASE, offSet - 1, "null", "case " + keyStr +" :");
			store.add(caseAction);
			caseActions.add(caseAction);
			// this.stackA.extraLines.add(line);
		}
		
		switchAction.goTo= defaultOff +2;
		JavaAction lastCase = null;
		Collections.sort(caseActions);
		for(JavaAction caseAction : caseActions) {
			if(lastCase != null) {
				lastCase.goTo = caseAction.index -1;
			}
			lastCase = caseAction;
		}
		if(lastCase != null) {
			lastCase.goTo = defaultOff -1;
		}
		return offIndex + 2;

	}

	public int decompileTableSwitch(byte[] buf, int index, Stack<JavaAction> stack, ArrayList<JavaAction> store) {
		int offIndex = (index / 4) * 4;
		offIndex += 6;
		int defaultOff = index + this.getIndex(buf, offIndex, 2);

		offIndex += 4;
		int lowByte = this.getIndex(buf, offIndex, 2);
		offIndex += 4;
		int highByte = this.getIndex(buf, offIndex, 2);
		int numKeys = highByte - lowByte + 1;

		JavaAction key2 = stack.pop();

		int bracketIndex = key2.value.indexOf("[");
		int lastBracketIndex =  key2.value.lastIndexOf("]");
		String simplified = key2.value;
		if(bracketIndex != -1) {
		simplified = key2.value.substring(bracketIndex +1, lastBracketIndex).replace(".ordinal()", "");
		}
		JavaAction switchAction = new JavaAction(this.type, index, "null", "switch (" + simplified + ")");
		List<String> enums = new ArrayList<String>();
		for (String key : ClassDescription2.enumes.keySet()) {
			if(key2.value.contains(key)) {
				enums =  ClassDescription2.enumes.get(key);
			}
		}
		
		store.add(switchAction);

		JavaAction defaultAction = new JavaAction(TYPE.CASE, defaultOff, "null", "default:");
		store.add(defaultAction);
        
        ArrayList<JavaAction> caseActions = new ArrayList<JavaAction> ();
		for (int k = 0; k < numKeys; k++) {
			offIndex += 4;
			int keyI = this.getIndex(buf, offIndex, 2) + index;

			if (keyI == defaultOff) {
				continue;
			}
			int caseK = lowByte + k;
			String keyStr = "" + caseK;
			if(enums.size() >= caseK -1) {
				keyStr = enums.get(caseK -1);
			}
			JavaAction caseAction = new JavaAction(TYPE.CASE, keyI, "null", "case " +  keyStr +" :");
			
			 caseActions.add(caseAction);
			store.add(caseAction);

			// this.stackA.extraLines.add(line);
		}
		switchAction.goTo= defaultOff;
		JavaAction lastCase = null;
		Collections.sort(caseActions);
		for(JavaAction caseAction : caseActions) {
			if(lastCase != null) {
				lastCase.goTo = caseAction.index -1;
			}
			lastCase = caseAction;
		}
		if(lastCase != null) {
			lastCase.goTo = defaultOff -1;
		}
		return offIndex + 2;

	}

	public int decompileMatchemactical(byte[] buf, int index, Stack<JavaAction> stack, ArrayList<JavaAction> store) {

		JavaAction action = stack.pop();
		if (this.type == TYPE.NEGATE) {
			action = new JavaAction(this.type, index, action.returnType, action.value + " * -1 ");
			stack.push(action);
			index += byteNames.length;
			return index + 1;
		}
		JavaAction action2 = stack.pop();
		// REMAINDER, BTWISEAND, BTWISEOR, BTWISESHIFTLEFT, BTWISESHIFTRIGHT
		String operand = "";
		switch (this.type) {
		case ADD:
			operand = "+";
			break;
		case SUBTRACT:
			operand = "-";
			break;
		case MULTIPLY:
			operand = "*";
			break;
		case DIVIDE:
			operand = "/";
			break;
		case REMAINDER:
			operand = "%";
			break;
		case BTWISEAND:
			operand = "&";
			break;
		case BTWISEOR:
			operand = "|";
			break;
		case BTWISEXOR:
			operand = "^";
			break;
		case BTWISESHIFTLEFT:
			operand = "<<";
			break;
		case BTWISESHIFTRIGHT:
			operand = ">>";
			break;
		}
		action = new JavaAction(this.type, index, action.returnType,"("+
				action2.value + " " + operand + " " + action.value+ ")");
		stack.push(action);
		index += byteNames.length;
		return index + 1;
	}

	private String getAType(int atype) {

		String tyepStr = "int";
		if (atype == 8) {
			tyepStr = "byte";
		} else if (atype == 10) {
			tyepStr = "int";
		} else if (atype == 7) {
			tyepStr = "double";
		} else if (atype == 4) {
			tyepStr = "boolean";
		} else if (atype == 6) {
			tyepStr = "float";
		} else if (atype == 11) {
			tyepStr = "long";
		} else if (atype == 5) {
			tyepStr = "char";
		} else {
			throw new RuntimeException(" un handled type " + atype);
		}
		return tyepStr;
	}
}
