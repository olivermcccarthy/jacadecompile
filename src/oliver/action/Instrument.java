package oliver.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oliver.ByteCode;
import oliver.BytePart;
import oliver.ConstEntry2;
import oliver.FieldBytePart;
import oliver.StackMapMad;
import oliver.TypeInfo;
import oliver.Variable;
import oliver.VariableBytePart;

public class Instrument {

	public static boolean isStaticMethod;

	public static ArrayList<Action> getGotos(byte[] code) {

		ByteCode b2 = new ByteCode();

		return b2.getGottos(code, code.length);
	}
    /**
     * if(tyeOFFrame == 0) {
				newType = "Top_variable_info";
			}else if(tyeOFFrame == 1) {
				newType = "Integer_variable_info";
			}else if(tyeOFFrame == 2) {
				newType = "Float_variable_info";
			}
			else if(tyeOFFrame == 3) {
				newType = "Double_variable_info";
			}
			else if(tyeOFFrame == 4) {
				newType = "Long_variable_info";
			}
			else if(tyeOFFrame == 5) {
				newType = "Null_variable_info";
			}
			else if(tyeOFFrame == 6) {
				newType = "UninitializedThis_variable_info";
			}else if(tyeOFFrame == 7) {
				newType = "Object_variable_info";
			}
			else if(tyeOFFrame == 8) {
				newType = "Uninitialized_variable_info";
			}
     * @param type
     * @return
     */
	public static TypeInfo getVerificationBytePart(String type) {
		return TypeInfo.getType(getVerificationType(type));
		
	}
	 public static int getVerificationTag(String type) {
		 if(type.equals("top")) {
	    		return 0;
	    	}	
		 if(type.equals("int")) {
	    		return 1;
	    	}
	    	if(type.equals("long")) {
	    		return 4;
	    	}
	    	if(type.equals("double")) {
	    		return 3;
	    	}
	    	if(type.equals("float")) {
	    		return 2;
	    	}
	    	if(type.equals("char")) {
	    		return 1;
	    	}
	    	
	    	if(type.equals("byte")) {
	    		return 1;
	    	}
	    	if(type.equals("short")) {
	    		return 1;
	    	}
	    	if(type.equals("boolean")) {
	    		return 1;
	    	}
	          
	    	return 7;
	    }
	 public static String getVerificationType(String type) {
		 if(type.equals("top")) {
	    		return "Top_variable_info";
	    	}	
		 if(type.equals("int")) {
	    		return "Integer_variable_info";
	    	}
	    	if(type.equals("long")) {
	    		return "Long_variable_info";
	    	}
	    	if(type.equals("double")) {
	    		return "Double_variable_info";
	    	}
	    	if(type.equals("float")) {
	    		return "Float_variable_info";
	    	}
	    	if(type.equals("char")) {
	    		return "Integer_variable_info";
	    	}
	    	
	    	if(type.equals("byte")) {
	    		return "Integer_variable_info";
	    	}
	    	if(type.equals("short")) {
	    		return "Integer_variable_info";
	    	}
	    	if(type.equals("boolean")) {
	    		return "Integer_variable_info";
	    	}
	          
	    	return "Object_variable_info";
	    }
	 
	 static BytePart newVerificationType(BytePart parent,String className,String type) {
		 TypeInfo valPart = getVerificationBytePart(type);
    	 BytePart valBytePrt = new BytePart(parent,valPart,0);
    	 int tag = getVerificationTag(type);
    	 
    	 valBytePrt.createChildren();
    	 valBytePrt.getChild("tag").intToBuf(tag);
    	 if(tag== 7) {
    		 int poolIndex = 0;
    		 if(type.equals(className)) {
    			 poolIndex = 1;
    		 }else {
    			 poolIndex =ConstEntry2.addNewClass(type); 
    		 }
    	 
    		 valBytePrt.getChild("cpool_index").intToBuf( poolIndex);
    	 }
    	 return valBytePrt;
    	 
	 }
	static BytePart getMethodStackMap(String className,BytePart codeBytePart) {
		BytePart method = codeBytePart.parent.parent.parent.parent;
		int descriptorIndex = method.getChild("descriptor_index").bufAsInt();
	
		Variable descriptionVariable = ConstEntry2.consts.get(descriptorIndex).getVariable();
		 ArrayList<String> params = new  ArrayList<String>();
		if(!isStaticMethod) {
		 params.add(className);
		 }
		Variable.decodeSig(descriptionVariable.getName(), params,false);
		params.remove(params.size() -1);
	    TypeInfo fullFrame = TypeInfo.getType("full_frame");
	    BytePart fullFrameBytePart = new BytePart(codeBytePart,fullFrame,0);
	    fullFrameBytePart.createChildren();
	    BytePart locals = fullFrameBytePart.getChild("locals");
	    fullFrameBytePart.getChild("frame_type").intToBuf(255);
	    fullFrameBytePart.getChild("number_of_locals").intToBuf(params.size());
	    fullFrameBytePart.getChild("number_of_stack_items").intToBuf(0);
	    fullFrameBytePart.getChild("offset_delta").intToBuf(0);
	    for(String param : params) {
	    	if(param.endsWith( "[]")) {
	    		param = param.replace("[]", "").trim();
	    		param = "[L"+param +";";
	    	}
	    	 BytePart valBytePrt = newVerificationType(locals,className,param);
	    	 
	    	 locals.children.add(valBytePrt);
	    }
	    return fullFrameBytePart;
		
	}
	public static void  goMadStatic(BytePart bPart, BytePart bPartLength, String className,String methodName) {
		
		if (!methodName.equals("printHtml")) {
			
		}
	}

	
	
public static String goMad(BytePart codeBytePart, BytePart bPartLength, String className,String methodName) {
		
		if (!methodName.equals("<clinsssit>")) {
			return null;
		}
		if (methodName.equals("print")) {
			int debugME =0;
		}
		byte [] initialBlock = new byte[40];
		int maxLocals = codeBytePart.parent.getChild("max_locals").bufAsInt();
		
		long max = Long.MAX_VALUE;
		
		int codeSize =invokeMethod(initialBlock,0, "java.lang.System", "nanoTime", "()J",true);
		 codeSize =storeValue(initialBlock,codeSize,"long",maxLocals);
		
		
		
		
		 ArrayList<Action> goTos =	getGotTos(codeBytePart);
		 if(goTos.size() == 0) {
			return null;
		 }
		 GotToAction dummyEnd = new GotToAction(codeBytePart.buffer.length -1,codeSize);
		 
		int initialSze =  codeSize;
		 
	
	
			
			
		 for(Action goi: goTos ) {
			 System.out.println(goi.getPc() +" -> " + goi.getGoToLabel() + " " + goi.isIf);
		 }
		 
		 
		
	
		 
		
		
		 

		byte[] codeBlock = new byte[40];
		final int lastPush = 0;
		  codeSize=loadStaticField(codeBlock,0, 0);
		 
		 
		 codeSize =invokeMethod(codeBlock,codeSize, "java.lang.System", "nanoTime", "()J",true);
		
		 codeSize =loadValue(codeBlock,codeSize,"long",maxLocals);
		 codeSize=subtract(codeBlock, codeSize,"long");
		 codeSize=add(codeBlock, codeSize,"long");
		 final int lastPush2 = codeSize;
		 codeSize=setStaticField(codeBlock,codeSize, 0);
		 final int lastPush3 = codeSize;
		 codeSize=loadStaticField(codeBlock,codeSize, 0);
		 codeSize=pushShort(codeBlock,codeSize,1);
		 codeSize=add(codeBlock, codeSize,"int");
		 final int lastPush4 = codeSize;
		 codeSize=setStaticField(codeBlock,codeSize, 0);
		 codeSize =invokeMethod(codeBlock,codeSize, "java.lang.System", "nanoTime", "()J",true);
		 codeSize =storeValue(codeBlock,codeSize,"long",maxLocals);

		 CodeInsertionTool insertCodeAtMethodStart = new CodeInsertionTool(className, methodName,codeBytePart,goTos,codeBlock,codeSize, initialBlock, initialSze);
	
		
		 insertCodeAtMethodStart.populateMe = new PopulateMe() {

			@Override
			public void populateMe(CodeInsertionTool newCode, int point) {
				
				int counterID =  newCode.timeCounters.get(point);
				loadStaticField(newCode.buffer,lastPush, counterID);
				setStaticField(newCode.buffer,lastPush2, counterID);
				counterID =  newCode.hitCounters.get(point);
				loadStaticField(newCode.buffer,lastPush3, counterID);
				setStaticField(newCode.buffer,lastPush4, counterID);
				
			}};
			
			insertCodeAtMethodStart.insertNewCode(className,codeBytePart, maxLocals);	
		addNewLocalVar(codeBytePart, bPartLength, "xxx", "long",0, codeBytePart.buffer.length,maxLocals, false);
		
		return null;
	}

	
	
	private static int newConstEntryField(BytePart bPart, String className,String name, String descriptor, boolean isArray) {
		int consEntry = ConstEntry2.addNewFieldRef(className, name, descriptor, isArray);
		
		return consEntry;
	}
	static int newField(BytePart bPart, String className,String name, String descriptor, String accessFlags, boolean isArray) {
		int cpoolSize = ConstEntry2.consts.size();
		int consEntry = ConstEntry2.addNewFieldRef(className, name, descriptor, isArray);
		if(consEntry < cpoolSize) {
			return  consEntry;
		}
		BytePart classFile = bPart.parent.parent.parent.parent.parent.parent;
		BytePart fileds = classFile.getChild("fields");
		FieldBytePart newFiled = new FieldBytePart(fileds, name, descriptor, accessFlags,isArray);
		return consEntry;
	}
	 
	
    private static void addNewLocalVar(BytePart bPart,BytePart bPartLength, String name, String descriptor,int startPc, int length,int index, boolean isArray) {
    	BytePart localVarTable = bPart.parent.getChild("attributes").getChild("LocalVariableTable");
    	if (localVarTable != null) {
    		BytePart  localVarTable2 = localVarTable.getChild("info").getChild("table");
    		VariableBytePart varPart = new VariableBytePart(localVarTable2, name,  descriptor, isArray,startPc, length-1, index);
    				
			//start_pc, length, name_index, descriptor_index, index

		}
    	byte[] buffer = new byte[1000];
    	int df =localVarTable.write(buffer, 0);
    	int currentLength = bPartLength.bufAsInt();			
		currentLength+=10;
		bPartLength.intToBuf(currentLength);
	//	localVarTable.getChild("info").bufferSize += 10;
		bPartLength= localVarTable.getChild("attribute_length");
		currentLength = bPartLength.bufAsInt();			
		currentLength+=10;
		//bPartLength.intToBuf(currentLength);
		int maxLocals= bPart.parent.getChild("max_locals").bufAsInt();
		maxLocals++;
		if(descriptor.equals("long")) {
			maxLocals++;
			maxLocals++;
		}
		bPart.parent.getChild("max_locals").intToBuf(20);
		bPart.parent.getChild("max_stack").intToBuf(20);
    }
    
    
    
   static ArrayList<Action> getGotTos(BytePart bPart){
		byte[] code = bPart.buffer;
		ByteCode b2 = new ByteCode();

		ArrayList<Action> all = b2.getGottos(code, code.length);
		return all;
   }
   // Bump one  by 5
   // No jump increases
   //15 +30=45   27+5 = 32  45+10 = 55    76+20 = 96
   //  20 +30=50   32+5 = 37  50+10 = 60  81+20 = 101  // No chnage needed
   //  
// Bump two  by 5 the first jump increases by 5
   //15 +30=45     27+5 = 32  45+10 = 55    76+20 = 96
   //  20 +35=55   37+5 = 42  55+10 = 65   86+20 = 106  // No chnage needed
   // How many jumps you go over that have been pushed forward
   // For jump one we go over jump 2 and this has been pushed forward by 5 . So we need to expand jump 1 by 5. 
   
	
   
   
   private static int getNumJumps(ArrayList<Action> all, int startPC , int endPC ) {
	   boolean backwards = false;
	   if(startPC > endPC) {
		   int hold = endPC;
		   endPC = startPC;
		   startPC=hold;
		   backwards = true;
	   }
	   Action firstAct = all.get(0);
	   int x =0;
	   int addMe =0;
	  if(firstAct.getPc() == startPC) {
		  addMe=1;
	  }
	   for(; x < all.size(); x++) {
		   firstAct = all.get(x);
		   if(firstAct.getPc() > startPC) {
			   break;
		   }
	   }
	  
	   int y =x;
	   for(; y < all.size(); y++) {
		   firstAct = all.get(y);
		   if(firstAct.getPc() > endPC) {
			   break;
		   }
	   }
	   if(backwards) {
		   return x -y + addMe;
	   }
	   return y -x + addMe;
   }

	static int getSignedIndex(byte[] buf, int index) {
		int value = buf[index + 1];

		int v2 = buf[index + 2] & 0xff;

		value = value << 8;
		value = value | v2;

		return value;
	}

	static int writeSignedIndex(byte[] buf, int index, int value) {
		buf[index + 2] = (byte) (value & 0xFF);
		value = value >> 8;
		buf[index + 1] = (byte) (value & 0xFF);

		return value;
	}

	private static byte[] insertArray(byte[] src, int index, byte[] newEntry, int newSize) {
		byte[] newArray = new byte[src.length + newSize];
		int x = 0;
		for (; x < index; x++) {
			newArray[x] = src[x];
		}
		for (int y = 0; y < newSize; y++) {
			newArray[x + y] = newEntry[y];
		}
		for (; x < src.length; x++) {
			newArray[x + newSize] = src[x];
		}
		return newArray;

	}

	
	//ladd,61,0110 0001,,"value1, value2 ? result",add two longs
	public static int subtract(byte[] codex, int startIndex, String type) {

		int command = 0x999;// default for object

		if (type.equals("int")) {
			command = 0x64;
		}
		if (type.equals("long")) {
			command = 0x65;
		}
		if (type.equals("float")) {
			command = 0x66;
		}
		
		
		codex[startIndex] = (byte) command;

		return startIndex + 1;

	}
	
	public static int add(byte[] codex, int startIndex, String type) {

		int command = 0x999;// default for object

		if (type.equals("int")) {
			command = 0x60;
		}
		if (type.equals("long")) {
			command = 0x61;
		}
		if (type.equals("float")) {
			command = 0x62;
		}
		if (type.equals("double")) {
			command = 0x63;
		}
		
		codex[startIndex] = (byte) command;

		return startIndex + 1;

	}
	public static int loadArrayIndex(byte[] codex, int startIndex, String type, int indexInArray) {

		startIndex= pushShort(codex, startIndex, indexInArray);
		
		int command = 0x32;// default for object

		if (type.equals("int")) {
			command = 0x2e;
		}
		if (type.equals("long")) {
			command = 0x2f;
		}
		if (type.equals("float")) {
			command = 0x30;
		}
		if (type.equals("double")) {
			command = 0x31;
		}
		if (type.equals("byte")) {
			command = 0x33;
		}
		if (type.equals("boolean")) {
			command = 0x33;
		}
		if (type.equals("char")) {
			command = 0x34;
		}
		codex[startIndex] = (byte) command;

		return startIndex + 1;

	}
	public static int loadArrayIndex(byte[] codex, int startIndex, String type) {

		int command = 0x32;// default for object

		if (type.equals("int")) {
			command = 0x2e;
		}
		if (type.equals("long")) {
			command = 0x2f;
		}
		if (type.equals("float")) {
			command = 0x30;
		}
		if (type.equals("double")) {
			command = 0x31;
		}
		if (type.equals("byte")) {
			command = 0x33;
		}
		if (type.equals("boolean")) {
			command = 0x33;
		}
		if (type.equals("char")) {
			command = 0x34;
		}
		codex[startIndex] = (byte) command;

		return startIndex + 1;

	}
	public static int storeArrayIndex(byte[] codex, int startIndex, String type) {

		int command = 0x53;// default for object

		if (type.equals("int")) {
			command = 0x4f;
		}
		if (type.equals("long")) {
			command = 0x50;
		}
		if (type.equals("float")) {
			command = 0x51;
		}
		if (type.equals("double")) {
			command = 0x52;
		}
		if (type.equals("byte")) {
			command = 0x54;
		}
		if (type.equals("boolean")) {
			command = 0x54;
		}
		if (type.equals("char")) {
			command = 0x55;
		}
		codex[startIndex] = (byte) command;

		return startIndex + 1;

	}
//lstore,37,0011 0111,1: index,value ?,store a long value in a local variable #index
	
	public static int pushShort(byte[] codex, int startIndex, int value) {

		codex[startIndex] = (byte) 0x11;
		codex[startIndex + 2] = (byte) (value & 0xff);
		value = value >> 8;
		codex[startIndex + 1] = (byte) (value & 0xff);
		return startIndex + 3;

	}
	//0x59
	
	public static int duplicate(byte[] codex, int startIndex) {

		codex[startIndex] = (byte) 0x59;
		
		return startIndex + 1;

	}
	public static int duplicate2(byte[] codex, int startIndex) {

		codex[startIndex] = (byte) 0x5c;
		
		return startIndex + 1;

	}
	public static int loadValue(byte[] codex, int startIndex, String type, int varIndex) {
        int command = 0x19;
        if(type.equals("int")) {
        	command =0x15;
        }
        if(type.equals("long")) {
        	command =0x16;
        }
        if(type.equals("float")) {
        	command =0x17;
        }
        if(type.equals("double")) {
        	command =0x18;
        }
       
        
		codex[startIndex] = (byte) command;
	
		codex[startIndex + 1] = (byte) (varIndex & 0xff);
		return startIndex + 2;

	}
	public static int storeValue(byte[] codex, int startIndex, String type, int varIndex) {
        int command = 0x3a;
        if(type.equals("int")) {
        	command =0x36;
        }
        if(type.equals("long")) {
        	command =0x37;
        }
        if(type.equals("float")) {
        	command =0x38;
        }
        if(type.equals("double")) {
        	command =0x39;
        }
        
        
		codex[startIndex] = (byte) command;
	
		codex[startIndex + 1] = (byte) (varIndex & 0xff);
		return startIndex + 2;

	}
	// b5 13,
	public static int setField(byte[] codex, int startIndex, int fieldID) {

		codex[startIndex] = (byte) 0xb5;
		codex[startIndex + 2] = (byte) (fieldID & 0xff);
		fieldID = fieldID >> 8;
		codex[startIndex + 1] = (byte) (fieldID & 0xff);
		return startIndex + 3;

	}
	
	public static int ifnonnull(byte[] codex, int startIndex, int jumpTo) {

		codex[startIndex] = (byte) 0xc7;
		codex[startIndex + 2] = (byte) (jumpTo & 0xff);
		jumpTo = jumpTo >> 8;
		codex[startIndex + 1] = (byte) (jumpTo & 0xff);
		return startIndex + 3;

	}
	public static int setStaticField(byte[] codex, int startIndex, int fieldID) {

		codex[startIndex] = (byte) 0xb3;
		codex[startIndex + 2] = (byte) (fieldID & 0xff);
		fieldID = fieldID >> 8;
		codex[startIndex + 1] = (byte) (fieldID & 0xff);
	
		return startIndex + 3;

	}
	public static int loadField(byte[] codex, int startIndex, int fieldID) {

		codex[startIndex] = (byte) 0xb4;
		codex[startIndex + 2] = (byte) (fieldID & 0xff);
		fieldID = fieldID >> 8;
		codex[startIndex + 1] = (byte) (fieldID & 0xff);
		return startIndex + 3;

	}
	
	public static int invokeMethod(byte[] codex, int startIndex, String className, String methodName, String signature, boolean isStatic) {
	int classID =  ConstEntry2.addNewClass(className);
	int methodID= ConstEntry2.addMethodRef(classID, methodName, signature);
	methodID= ConstEntry2.addMethodRef(classID, methodName, signature);
	 if(isStatic) {
		 return invokeStaticMethod(codex, startIndex, methodID);
	 }
	 return invokeMethod(codex, startIndex, methodID);
	}
	public static int invokeStaticMethod(byte[] codex, int startIndex, int methodID) {

		codex[startIndex] = (byte) 0xb8;
		codex[startIndex + 2] = (byte) (methodID & 0xff);
		methodID = methodID >> 8;
		codex[startIndex + 1] = (byte) (methodID & 0xff);
		return startIndex + 3;

	}
	public static int invokeMethod(byte[] codex, int startIndex, int methodID) {

		codex[startIndex] = (byte) 0xb6;
		codex[startIndex + 2] = (byte) (methodID & 0xff);
		methodID = methodID >> 8;
		codex[startIndex + 1] = (byte) (methodID & 0xff);
		return startIndex + 3;

	}
	public static int loadStaticField(byte[] codex, int startIndex, int fieldID) {

		codex[startIndex] = (byte) 0xb2;
		codex[startIndex + 2] = (byte) (fieldID & 0xff);
		fieldID = fieldID >> 8;
		codex[startIndex + 1] = (byte) (fieldID & 0xff);
		return startIndex + 3;

	}
	public static int newArray(byte[] codex, int startIndex, int count, String type) {
		
		int atype=getAType(type);
		codex[startIndex] = (byte) 0x11;
		codex[startIndex + 2] = (byte) (count & 0xff);
		count = count >> 8;
		codex[startIndex + 1] = (byte) (count & 0xff);
		codex[startIndex + 3] = (byte) 0xbc;
		codex[startIndex + 4] = (byte) atype;
		
		return startIndex + 5;

	}

	public static int getAType(String type) {
		int atype=10;
		if(type.equals("long")) {
			atype=11;
		}
		if(type.equals("byte")) {
			atype=8;
		}
		if(type.equals("double")) {
			atype=7;
		}
		if(type.equals("boolean")) {
			atype=4;
		}
		if(type.equals("float")) {
			atype=4;
		}
		if(type.equals("char")) {
			atype=5;
		}
		return atype;
	}
	public static int loadConstField(byte[] codex, int startIndex,int constID) {

		
		codex[startIndex] = (byte) 0x13;
		codex[startIndex+2] = (byte) (constID & 0xff);
		constID = constID >> 8;
		codex[startIndex+1] = (byte) (constID & 0xff);
		
		return startIndex+3;

	}

	
	// aload,19,0001 1001,1: index,? objectref,load a reference onto the stack from
	// a local variable #index
	public static int loadVar(byte[] codex, int startIndex, int varID) {

		codex[startIndex] = (byte) 0x19;
		codex[startIndex + 1] = (byte) varID;

		
		return startIndex + 2;

	}
	
	
	
	static int getStackSize(BytePart stackEntry) {
		int count = 0;
		for(BytePart child : stackEntry.getChild("locals").children) {
			int tag = child.getChild("tag").bufAsInt();
			count ++;
			if(tag == 4) {
				count ++;
			}
		  
		}
		return count;
	}
	
	
public static void insertStackMap(String className,BytePart codeBytePart,BytePart parent,  int pc) {
		
		
		int stackIndex =0;
		// ArrayList<StackMapMad> ff = StackMapMad.processStackMap("ddd", parent);
		int newIndex2 = 0;
		
		 int count=0;
		BytePart  fullFrameStart =  getMethodStackMap(className,codeBytePart);
		int diff = 0;
		BytePart otherLocals =null;
		
		ArrayList<BytePart> newChildren = new ArrayList<BytePart>();
		BytePart zeroEntry = parent.children.get(0);
		if(zeroEntry.getChild("locals") == null) {
			BytePart dummyLocals = new BytePart(zeroEntry,fullFrameStart.getChild("locals").typeInfo,0);
			 dummyLocals.name = "locals";
			zeroEntry.children.add(dummyLocals);
		}
		boolean manipluated = false;
		for ( BytePart stackEntry : parent.children) {
		   
		    if(count == 3) {
		    	int gg =0;
		    }
		    count++;
			int frType= stackEntry.getChild("frame_type").bufAsInt();
			BytePart offSetDelata = stackEntry.getChild("offset_delta");
			if(offSetDelata != null) {
				if(stackIndex > 0) {
					stackIndex ++;
				}
				stackIndex += offSetDelata.bufAsInt();
			}else {
				if(frType > 63) {
					frType -= 64;
				}
				if(stackIndex > 0) {
					stackIndex ++;
				}
				
				stackIndex += frType;
				
			}
			
			 if (!manipluated  && stackIndex > pc) {
				 manipluated = true;
		           fullFrameStart.getChild("number_of_locals").intToBuf(fullFrameStart.getChild("locals").children.size());
					
					int newOffSet = pc - newIndex2;
					fullFrameStart.getChild("offset_delta").intToBuf(newOffSet);
					fullFrameStart.name = "XXXX";
					if(offSetDelata != null) {
						int offSet = offSetDelata.bufAsInt();
					
							  offSet -= newOffSet ;
					
						
						offSetDelata.intToBuf(offSet -1);
					}else {
						// WTF
					}
					 newChildren.add(fullFrameStart);
				}
			  newChildren.add(stackEntry);
				newIndex2=stackIndex;
			}
		  
		     parent.children.clear();
		     parent.children.addAll(newChildren);
		
		
	}
}
