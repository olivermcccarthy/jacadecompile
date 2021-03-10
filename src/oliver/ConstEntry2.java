package oliver;

import java.util.ArrayList;
import java.util.HashMap;

public class ConstEntry2 {

	int index1;
	int index2;
	String value;
	int typeAsInt;
	static final int CONSTANT_Class = 7;
	static final int CONSTANT_Fieldref = 9;
	static final int CONSTANT_Methodref = 10;
	static final int CONSTANT_InterfaceMethodref = 11;
	static final int CONSTANT_String = 8;
	static final int CONSTANT_Integer = 3;
	static final int CONSTANT_Float = 4;
	static final int CONSTANT_Long = 5;
	static final int CONSTANT_Double = 6;
	static final int CONSTANT_NameAndType = 12;
	static final int CONSTANT_Utf8 = 1;
	static final int CONSTANT_MethodHandle = 15;
	static final int CONSTANT_MethodType = 16;
	static final int CONSTANT_InvokeDynamic = 18;
	static final int CONSTANT_Module = 19;
	static final int CONSTANT_Package = 20;
	public static ArrayList<ConstEntry2> consts = new ArrayList<ConstEntry2>();

	static String replaceQuote = "REP" + Math.random();
	static String replaceDOLLAR = "DOLLAR" + Math.random();
	
	static HashMap<String, String> dynamicCalls = new HashMap<String, String>();
	static HashMap<String, Integer> stringToIndex = new HashMap<String, Integer>();
	
	static int localVariableIndex = -1;
	static int localVariableTypeIndex = -1;
	static int codeIndex = -1;
	static int stackMapIndex = -1;
	static int exceptionsIndex = -1;
	static int signatureIndex = -1;
	static int lineNumberIndex = -1;
	static void writeConsts() {
		int x =0;
		for (ConstEntry2 constEntry : ConstEntry2.consts) {
			if (x > 0) {
			constEntry.write();
			}
			x++;
		}
	}
	static BytePart myParts;
	static void initConsts(BytePart myPart) {
		myParts = myPart;
		consts = new ArrayList<ConstEntry2>();
		consts.add(new ConstEntry2(null,0));
		localVariableIndex = -1;
		localVariableTypeIndex = -1;
		exceptionsIndex = -1;
		codeIndex = -1;
		signatureIndex = -1;
		lineNumberIndex = -1;
		 stackMapIndex = -1;
		dynamicCalls = new HashMap<String, String>();
		int index = 1;
		for (BytePart bPart : myPart.children) {
			ConstEntry2 constEntry = new ConstEntry2(bPart,index);
			index ++;
			ConstEntry2.consts.add(constEntry);
			if (constEntry.typeAsInt == ConstEntry2.CONSTANT_Double || constEntry.typeAsInt == ConstEntry2.CONSTANT_Long) {
				ConstEntry2.consts.add(constEntry);
				myPart.funnyCount++;
			}
		}
		index = 0;
		for (ConstEntry2 constEntry : ConstEntry2.consts) {
			constEntry.init();
			if (constEntry.typeAsInt == ConstEntry2.CONSTANT_Utf8) {
				stringToIndex.put(constEntry.value, index);
				if(constEntry.value.equals("Code") && codeIndex == -1) {
					codeIndex = index;
				}
				if(constEntry.value.startsWith("LocalVariableTable") && localVariableIndex == -1) {
					localVariableIndex = index;
				}
				else if(constEntry.value.startsWith("LocalVariableTypeTable") && localVariableTypeIndex == -1) {
					localVariableTypeIndex = index;
				}
				else if(constEntry.value.equals("Exceptions") && exceptionsIndex == -1) {
					exceptionsIndex = index;
				}
				else if(constEntry.value.equals("Signature") && signatureIndex == -1) {
					signatureIndex = index;
				}
				else if(constEntry.value.equals("StackMapTable") && stackMapIndex == -1) {
					stackMapIndex = index;
				}
				else if(constEntry.value.equals("LineNumberTable") && lineNumberIndex == -1) {
					lineNumberIndex  = index;
				}
			}
					
			index ++;
		}
		for (ConstEntry2 constEntry : ConstEntry2.consts) {
			constEntry.getVariable();
		}
	}

	static String getTypeString(int type) {
		String ret = "";
		if (type == CONSTANT_Class) {

			ret = "CONSTANT_Class";

		} else if (type == CONSTANT_MethodType) {

			ret = "CONSTANT_MethodType";

		} else if (type == CONSTANT_String) {

			ret = "CONSTANT_String";

		} else if (type == CONSTANT_Utf8) {

			ret = "CONSTANT_Utf8";
		} else if (type == CONSTANT_Integer) {

			ret = "CONSTANT_Integer";

		} else if (type == CONSTANT_Float) {

			ret = "CONSTANT_Float";

		} else if (type == CONSTANT_Double) {

			ret = "CONSTANT_Double";

		} else if (type == CONSTANT_Long) {

			ret = "CONSTANT_Long";

		} else if (type == CONSTANT_Methodref) {

			ret = "CONSTANT_Methodref";

		} else if (type == CONSTANT_InvokeDynamic) {

			ret = "CONSTANT_InvokeDynamic";

		} else if (type == CONSTANT_Fieldref) {

			ret = "CONSTANT_Fieldref";

		} else if (type == CONSTANT_NameAndType) {

			ret = "CONSTANT_NameAndType";

		} else if (type == CONSTANT_MethodHandle) {

			ret = "CONSTANT_MethodHandle";

		} else if (type == CONSTANT_InterfaceMethodref) {

			ret = "CONSTANT_InterfaceMethodref";

		}
		else if (type == CONSTANT_Module) {

			ret = "CONSTANT_Module";

		}
		else if (type == CONSTANT_Package) {

			ret = "CONSTANT_Package";

		}
		return ret;

	}

	BytePart bytePart;

	int index = 0;
	public ConstEntry2(BytePart bytePart, int index) {

		this.bytePart = bytePart;
		this.index = index;
		if(bytePart!= null) {
		this.typeAsInt = getChild("tag").bufAsInt();
		value = "";
		}
	}

	public static long REPLACE_QUOTE =  System.currentTimeMillis();
	public static long REPLACE_BACKSLASH =  REPLACE_QUOTE+1;
	public static long REPLACE_NEWLINE =  REPLACE_QUOTE+2;
	BytePart getChild(String typeLookFor) {
		return this.bytePart.getChild(typeLookFor);
	}

	public void init() {
		if (bytePart == null) {
			return;
		}
		int type = getChild("tag").bufAsInt();
		if (type == CONSTANT_Class) {

			index1 = getChild("name_index").bufAsInt();
			index2 = -1;
			value = "";
			classes.put(index, this);

		} else if (type == CONSTANT_Integer) {

			int result = getChild("bytes").bufAsInt();
			index2 = -1;
			index1 = -1;
			value = "" + result;

		} else if (type == CONSTANT_Float) {
			int bits = getChild("bytes").bufAsInt();
			int s = ((bits >> 31) == 0) ? 1 : -1;
			int e = ((bits >> 23) & 0xff);
			int m = (e == 0) ? (bits & 0x7fffff) << 1 : (bits & 0x7fffff) | 0x800000;
			double res = s * m * Math.pow(2, e - 150);
			float resf = (float) res;
			float gh = Float.intBitsToFloat(bits);
			if (resf != gh) {
				int debugME = 0;
			}
			value = "" + resf +"f";
		} else if (type == CONSTANT_Double) {

			double bigD = 88888888888.9999944444;
			long high_bytes = getChild("high_bytes").bufAsInt();
			long low_bytes = getChild("low_bytes").bufAsInt();
			long bits = ((long) high_bytes << 32) + low_bytes;

			if (low_bytes != 0) {
				int debugMe = 0;
			}

			int s = ((bits >> 63) == 0) ? 1 : -1;
			int e = (int) ((bits >> 52) & 0x7ffL);
			long m = (e == 0) ? (bits & 0xfffffffffffffL) << 1 : (bits & 0xfffffffffffffL) | 0x10000000000000L;

			double result = s * m * Math.pow(2, e - 1075);
			index2 = -1;
			index1 = -1;

			double gh = Double.longBitsToDouble(bits);
			if (result != gh) {
				int debugME = 0;
			}

			value = "" + result;

		} else if (type == CONSTANT_Long) {

			long high_bytes = getChild("high_bytes").bufAsInt();
			long low_bytes = getChild("low_bytes").bufAsInt();
			long bits = ((long) high_bytes << 32) + low_bytes;

			index2 = -1;
			index1 = -1;
			value = "" + bits + "L";

		} else if (type == CONSTANT_String) {
			index1 = getChild("string_index").bufAsInt();
			value = "";
			index2 = -1;
			strings.put(index, this);

		} else if (type == CONSTANT_Utf8) {

			index2 = -1;
			index1 = -1;
			String result = getChild("bytes").bufAsString();
			if(result.contains("\"")) {
				result = result.replace("\"", "" +REPLACE_QUOTE);
				if(result.contains("\\")) {
					int debugME =0;
				}
				if(result.contains("\\n")) {
					int debugME=0;
				}
				
				result = result.replace("\\", "" +REPLACE_BACKSLASH);
			}
			result = result.replace("\n", "" +REPLACE_NEWLINE);
			value = "" + result;
			utf8s.put(value, this);
		} else if (type == CONSTANT_Methodref) {
			index1 = getChild("class_index").bufAsInt();
			index2 = getChild("name_and_type_index").bufAsInt();
			value = "";
			methodRefs.put(index, this);

		} else if (type == CONSTANT_MethodHandle) {
			index1 = getChild("reference_index").bufAsInt();

			value = "";

		} else if (type == CONSTANT_MethodType) {
			index1 = getChild("descriptor_index").bufAsInt();

			value = "";

		} else if (type == CONSTANT_InvokeDynamic) {
			index1 = getChild("bootstrap_method_attr_index").bufAsInt();
			index2 = getChild("name_and_type_index").bufAsInt();
			value = "";

		} else if (type == CONSTANT_InterfaceMethodref) {
			index1 = getChild("class_index").bufAsInt();
			index2 = getChild("name_and_type_index").bufAsInt();
			value = "";
			methodRefs.put(index, this);

		}

		else if (type == CONSTANT_Fieldref) {
			index1 = getChild("class_index").bufAsInt();
			index2 = getChild("name_and_type_index").bufAsInt();
			;

			value = "";
			fields.put(index, this);

		} else if (type == CONSTANT_NameAndType) {

			index1 = getChild("name_index").bufAsInt();
			index2 = getChild("descriptor_index").bufAsInt();
			;
			nameAndTypes.put(index, this);
			value = "";

		}else if (type == CONSTANT_Module) {
			index1 = getChild("name_index").bufAsInt();
			value = "";
			index2 = -1;
			strings.put(index, this);

		}
		else if (type == CONSTANT_Package) {
			index1 = getChild("name_index").bufAsInt();
			value = "";
			index2 = -1;
			strings.put(index, this);

		}
		else {
			throw new RuntimeException("Unhanled type" + type);
		}
        
		// value = value.replace("Loliver/CodeBranch$LEVEL;", "LLEVEL;");
		this.typeAsInt = type;
	}
	public void write() {
		if (bytePart == null) {
			return;
		}
		int type = getChild("tag").bufAsInt();
		if (type == CONSTANT_Class) {

			
			getChild("name_index").intToBuf(index1);
		   


		} else if (type == CONSTANT_Integer) {

			getChild("bytes").intToBuf(Integer.valueOf(value));
			

		} else if (type == CONSTANT_Float) {
			
			
			float val = Float.valueOf(value);
			int intBits = Float.floatToIntBits(val);
			getChild("bytes").intToBuf(intBits);;
		} else if (type == CONSTANT_Double) {
			double val = Double.valueOf(value);
			long longBits = Double.doubleToLongBits(val);
			

			long high_bytes = longBits >> 32;
			long mask = 0xFFFF;
			long lowBits = 0;
			for(int x =0; x < 2 ; x ++) {
				lowBits = (lowBits) |(longBits&mask);
				lowBits = lowBits << 16;
				mask = mask <<16;
			}
			long low_bytes = longBits & 0xFFFFFFFF;
			getChild("high_bytes").intToBuf((int) high_bytes);
			getChild("low_bytes").intToBuf((int) low_bytes);
			this.init();

		} else if (type == CONSTANT_Long) {

            long longBits = Long.valueOf(this.value.replace("L", ""));
			

			long high_bytes = longBits >> 32;
			long low_bytes = longBits & ((long)0xFFFFFFFF);
			getChild("high_bytes").intToBuf((int) high_bytes);
			getChild("low_bytes").intToBuf((int) low_bytes);

		} else if (type == CONSTANT_String) {
			getChild("string_index").intToBuf(index1);
		

		} else if (type == CONSTANT_Utf8) {

			
			 getChild("bytes").stringToBuf(value);
			 getChild("length").intToBuf( getChild("bytes").buffer.length);
			

		} else if (type == CONSTANT_Methodref) {
			getChild("class_index").intToBuf(index1);
			getChild("name_and_type_index").intToBuf(index2);

		} else if (type == CONSTANT_MethodHandle) {
			getChild("reference_index").intToBuf(index1);
			

		} else if (type == CONSTANT_MethodType) {
			getChild("descriptor_index").intToBuf(index1);
			

		} else if (type == CONSTANT_InvokeDynamic) {
			getChild("bootstrap_method_attr_index").intToBuf(index1);
			getChild("name_and_type_index").intToBuf(index2);
			

		} else if (type == CONSTANT_InterfaceMethodref) {
			getChild("class_index").intToBuf(index1);
			getChild("name_and_type_index").intToBuf(index2);



		}

		else if (type == CONSTANT_Fieldref) {
			getChild("class_index").intToBuf(index1);
			getChild("name_and_type_index").intToBuf(index2);

		} else if (type == CONSTANT_NameAndType) {

			getChild("name_index").intToBuf(index1);
			getChild("descriptor_index").intToBuf(index2);
		

		} else {
			throw new RuntimeException("Unhanled type" + type);
		}

		// value = value.replace("Loliver/CodeBranch$LEVEL;", "LLEVEL;");
		this.typeAsInt = type;
	}
	Variable ret = null;
    
	public Variable getVariable() {

		if (ret != null) {
			return ret;
		}
		if (typeAsInt == CONSTANT_Class) {

			String name = consts.get(index1).value;

			ret = new Variable(name);
			
			classes.put(index, this);
			// ret.name = ret.name.replace("CodeBranch", "CodeBranch2");
			// ret.type = ret.type.replace("CodeBranch", "CodeBranch2");
		} else if (typeAsInt == CONSTANT_String) {

			String test = consts.get(index1).value;
			test = test.replace("\\", "" +REPLACE_BACKSLASH);
			boolean containsQ = false;
		
			
			ret = new Variable("",  test );
			ret.containsQuotes = true;
			strings.put(index, this);
			//ret = new Variable("", test );

		} else if (typeAsInt == CONSTANT_Utf8) {

			ret = new Variable("Utf8", value);
			utf8s.put(value, this);
		} else if (typeAsInt == CONSTANT_Integer) {

			ret = new Variable("int", value);

		} else if (typeAsInt == CONSTANT_Double) {

			ret = new Variable("double", value);

		} else if (typeAsInt == CONSTANT_Long) {

			ret = new Variable("long", value);

		} else if (typeAsInt == CONSTANT_Float) {

			ret = new Variable("float", value);

		} else if (typeAsInt == CONSTANT_InvokeDynamic) {

			Variable nameAndType = consts.get(index2).getVariable();
			// ConstEntry test = consts.get(index1);

			// test = consts.get(test.index1);
			String newName = "DYNAMIC" + System.currentTimeMillis();
			dynamicCalls.put(newName, new String(nameAndType.type));
			ret = new MethodVariable(newName, nameAndType.getName().toString(), nameAndType.type);

			ClassDescription2.containsDynamic = true;

		} else if (typeAsInt == CONSTANT_MethodHandle) {

			ret = consts.get(index1).getVariable();

		} else if (typeAsInt == CONSTANT_MethodType) {

			ret = consts.get(index1).getVariable();

		} else if (typeAsInt == CONSTANT_Methodref || typeAsInt == CONSTANT_InterfaceMethodref) {

			Variable nameAndType = consts.get(index2).getVariable();
			ConstEntry2 test = consts.get(index1);

			test = consts.get(test.index1);
			methodRefs.put(index, this);
			ret = new MethodVariable(test.value, nameAndType.getName().toString(), nameAndType.type);

		} else if (typeAsInt == CONSTANT_Fieldref) {

			Variable nameAndType = consts.get(index2).getVariable();
			Variable classVar = consts.get(index1).getVariable();
           
			ret = new Variable(classVar.name, nameAndType.getName(), nameAndType.getType());
			ret.className = classVar.fullName;
			fields.put(index, this);
		} else if (typeAsInt == CONSTANT_NameAndType) {
			nameAndTypes.put(index, this);
			ret = new Variable(consts.get(index2).value, consts.get(index1).value);
			;
			
		}

		return ret;

	}
	

	
	
	static HashMap<Integer,ConstEntry2> fields = new HashMap<Integer,ConstEntry2>();
	static HashMap<Integer,ConstEntry2> classes = new HashMap<Integer,ConstEntry2>();
	static HashMap<Integer,ConstEntry2> methodRefs = new HashMap<Integer,ConstEntry2>();
	static HashMap<Integer,ConstEntry2> nameAndTypes = new HashMap<Integer,ConstEntry2>();
	
	static HashMap<Integer,ConstEntry2> strings = new HashMap<Integer,ConstEntry2>();
	static HashMap<String,ConstEntry2> utf8s = new HashMap<String,ConstEntry2>();
	public String toString() {
		String ret = "";
		if (typeAsInt == CONSTANT_Class) {

			ret = "Class index " + index1 + "(" + consts.get(index1).toString() + ")";

		}
		if (typeAsInt == CONSTANT_Integer) {

			ret = "INTEGER " + value;

		} else if (typeAsInt == CONSTANT_String) {

			ret = "String index " + index1 + "(" + consts.get(index1).toString() + ")";

		} else if (typeAsInt == CONSTANT_Utf8) {

			ret = "UTF8 " + value;
		} else if (typeAsInt == CONSTANT_Methodref || typeAsInt == CONSTANT_InterfaceMethodref) {

			ret = "method class index " + index1 + "(" + consts.get(index1).toString() + ")" + " method index " + index2
					+ "(" + consts.get(index2).toString() + ")";

		} else if (typeAsInt == CONSTANT_NameAndType) {

			ret = "name and type " + index1 + "(" + consts.get(index1).toString() + ")" + " type " + index2 + "("
					+ consts.get(index2).toString() + ")";

		} else if (typeAsInt == CONSTANT_Fieldref) {

			ret = "fieldindex class index " + index1 + "(" + consts.get(index1).toString() + ")" + " field index "
					+ index2 + "(" + consts.get(index2).toString() + ")";

		} else if (typeAsInt == CONSTANT_InterfaceMethodref) {

			ret = "Name  index " + index1 + "(" + consts.get(index1).toString() + ")" + " desc index  " + index2 + "("
					+ consts.get(index2).toString() + ")";

		}
		return ret;
	}
	public static BytePart newPart(String type) {
		   TypeInfo typeInfo = TypeInfo.getType(type);
		      
	       BytePart myPart = new BytePart(myParts,typeInfo, codeIndex);
	      myParts.children.add(myPart);
            for(TypeInfo ch : typeInfo.children) {
            	BytePart byCh = new BytePart( myPart,ch,0);
            	myPart.children.add(byCh);
            	
	       }
            return myPart;
	}
	public static int addNewUTF8(String string) {

	        
		ConstEntry2 existing = utf8s.get(string);
		  if(existing != null) {
			  return existing.index;
		  }
	       BytePart myPart = newPart("CONSTANT_Utf8_info");
	      
         
	      
			
		    byte [] bytes = string.getBytes();
		    myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Utf8);
		    myPart.children.get(1).intToBuf(bytes.length);
		    myPart.children.get(2).stringToBuf(string);
		    ConstEntry2 typeValue = new ConstEntry2( myPart,ConstEntry2.consts.size());
		    typeValue.init();
		    typeValue.getVariable();
		    ConstEntry2.consts.add(typeValue);
		    return  ConstEntry2.consts.size() -1;
			
		}
	public static int addNewString(String string) {
		
		int stringIndex = addNewUTF8(string);
		for(ConstEntry2 cd : nameAndTypes.values()) {
			if(cd.index1 == stringIndex) {
				
				return cd.index;
			}
			
		}
		return addNewString(stringIndex);
		
	}
	
public static int addNewClass(String string) {
	
	ConstEntry2 existing = utf8s.get(string.replace(".", "/"));
	  if(existing != null) {
		  return addNewClass(existing.index);
		 
	  }
		if(!string.contains("/")) {
			string = string.replace(".", "/");
			string = "L"+ string +";";
		}
       
		int stringIndex = addNewUTF8(string);
		
		return addNewClass(stringIndex);
		
	}

public static int addNewClass(int stringIndex) {

	for(ConstEntry2 cd : classes.values()) {
		if(cd.index1 == stringIndex) {
			
			return cd.index;
		}
		
	}
    BytePart myPart = newPart("CONSTANT_Class_info");
    myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Class);
    myPart.children.get(1).intToBuf(stringIndex);
   
    ConstEntry2 typeValue = new ConstEntry2( myPart,ConstEntry2.consts.size());
    typeValue.init();
    typeValue.getVariable();
    ConstEntry2.consts.add(typeValue);
 
    return ConstEntry2.consts.size() -1;
    
	}
	public static int addNewString(int stringIndex) {

		
	    BytePart myPart = newPart("CONSTANT_String_info");
	    myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_String);
	    myPart.children.get(1).intToBuf(stringIndex);
	   
	    ConstEntry2 typeValue = new ConstEntry2( myPart,ConstEntry2.consts.size());
	    typeValue.init();
	    ConstEntry2.consts.add(typeValue);
	    typeValue.getVariable();
	    return ConstEntry2.consts.size() -1;
	    
		}
	
	
	  public static int addNewFieldRef(String className,String fieldName, String fieldType, boolean isArray) {
		  int classIndex = addNewClass(className);
			int stringIndex = addNewUTF8(fieldName);
			String  primType = Variable.getPrimativeType(fieldType);
			if(isArray) {
				primType = "["+primType;
			}
			int stringIndex2 = addNewUTF8(primType);
			int nameAndType = addNewNameAndType(stringIndex, stringIndex2);
			return addNewFieldRef(classIndex,nameAndType);
			
		}
	  public static int addMethodRef(int classIndex,String methodName, String  methodType) {
			
			int stringIndex = addNewUTF8(methodName);
			int stringIndex2 = addNewUTF8(methodType);
			int nameAndType = addNewNameAndType(stringIndex, stringIndex2);
			return addNewMethodRef(classIndex,nameAndType);
			
		}
      public static int addNewFieldRef(int classIndex, int nameAndType) {
    	  for(ConstEntry2 cd : fields.values()) {
				if(cd.index1 == classIndex && cd.index2 == nameAndType) {
					
					return cd.index;
				}
				
			}
		  
		  BytePart myPart = newPart("CONSTANT_Fieldref_info");
	       ConstEntry2 typeValue = new ConstEntry2( myPart,ConstEntry2.consts.size());
			myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Fieldref);
			myPart.children.get(1).intToBuf(classIndex);
			myPart.children.get(2).intToBuf(nameAndType);
			typeValue.init();
			 typeValue.getVariable();
			ConstEntry2.consts.add(typeValue); 
			return ConstEntry2.consts.size() -1;

			
		}
	  public static int addNewNameAndType(int nameIndex, int typeIndex) {

		  for(ConstEntry2 cd : nameAndTypes.values()) {
				if(cd.index1 == nameIndex && cd.index2 == typeIndex) {
					
					return cd.index;
				}
				
			}
		  BytePart myPart = newPart("CONSTANT_NameAndType_info");
	       ConstEntry2 typeValue = new ConstEntry2( myPart,ConstEntry2.consts.size());
			myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_NameAndType);
			myPart.children.get(1).intToBuf(nameIndex);
			myPart.children.get(2).intToBuf(typeIndex);
			typeValue.init();
			ConstEntry2.consts.add(typeValue); 
			return ConstEntry2.consts.size() -1;

			
		}
	  
	  public static int addNewMethodRef(int nameIndex, int typeIndex) {

		  for(ConstEntry2 cd : methodRefs.values()) {
				if(cd.index1 == nameIndex && cd.index2 == typeIndex) {
					
					return cd.index;
				}
				
			}
		  BytePart myPart = newPart("CONSTANT_Methodref_info");
	       ConstEntry2 typeValue = new ConstEntry2( myPart,ConstEntry2.consts.size());
			myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Methodref);
			myPart.children.get(1).intToBuf(nameIndex);
			myPart.children.get(2).intToBuf(typeIndex);
			typeValue.init();
			ConstEntry2.consts.add(typeValue); 
			return ConstEntry2.consts.size() -1;

			
		}
	  
		public static int addNewDouble(double d) {

			BytePart myPart = newPart("CONSTANT_Double_info");
		       ConstEntry2 typeValue = new ConstEntry2(myPart,ConstEntry2.consts.size());
		       myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Double);
		       typeValue.value="" + d;
		       typeValue.write();
				ConstEntry2.consts.add(typeValue); 
				return ConstEntry2.consts.size() -1;
		}
		public static int addNewInteger(int d) {

			BytePart myPart = newPart("CONSTANT_Long_info");
		       ConstEntry2 typeValue = new ConstEntry2(myPart,ConstEntry2.consts.size());
		       myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Long);
		       typeValue.value="" + d;
		       typeValue.write();
				ConstEntry2.consts.add(typeValue); 
				return ConstEntry2.consts.size() -1;
		}
		public static int addNewFloat(float d) {

			BytePart myPart = newPart("CONSTANT_Float_info");
		       ConstEntry2 typeValue = new ConstEntry2(myPart,ConstEntry2.consts.size());
		       myPart.children.get(0).intToBuf(ConstEntry2.CONSTANT_Float);
		       typeValue.value="" + d;
		       typeValue.write();
				ConstEntry2.consts.add(typeValue); 
				return ConstEntry2.consts.size() -1;
		}
		
	/*
	  public static int addNewString(String string) {
			
			int stringIndex = addNewUTF8(constPool,string);
			
			return addNewString(constPool,stringIndex);
			
		}


	   public static int addNewMethodRef(int classIndex,String methodName, String methodDesc) {
			
			int stringIndex = addNewUTF8(constPool,methodName);
			int stringIndex2 = addNewUTF8(constPool, methodDesc);
			int nameAndType = addNewNameAndType(constPool,stringIndex, stringIndex2);
			return addNewMethodRef(constPool,classIndex,nameAndType);
			
		}
	   public static int addNewFieldRef(int classIndex,String fieldName, String fieldType) {
			
			int stringIndex = addNewUTF8(constPool,fieldName);
			int stringIndex2 = addNewUTF8(constPool, fieldType);
			int nameAndType = addNewNameAndType(constPool,stringIndex, stringIndex2);
			return addNewFieldRef(constPool,classIndex,nameAndType);
			
		}
		
		public static int addNewClass(String string) {
			
			int stringIndex = addNewUTF8(constPool,string);
			
			return addNewClass(constPool,stringIndex);
			
		}
		public static int addNewClass(int stringIndex) {

	       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Class_info");
			ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
			typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Class);
			typeValue.children.get(1).intToBuff(stringIndex);
			constPool.children.add(typeValue);
			consts.add(typeValue);
			typeValue.init();
	       return constPool.children.size() ;
			
		}
		
		
		public static int addNewNameAndType(int nameIndex, int typeIndex) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_NameAndType_info");
				ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_NameAndType);
				typeValue.children.get(1).intToBuff(nameIndex);
				typeValue.children.get(2).intToBuff(typeIndex);
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
		       return constPool.children.size() ;
				
			}
		
		public static int addNewMethodRef(int classIndex,int nameTypeIndex) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Methodref_info");
				ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Methodref);
				typeValue.children.get(1).intToBuff(classIndex);
				typeValue.children.get(2).intToBuff(nameTypeIndex);
				
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
		       return constPool.children.size() ;
				
			}
		
		
		public static int addNewFieldRef(int classIndex,int nameTypeIndex) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Fieldref_info");
				ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Fieldref);
				typeValue.children.get(1).intToBuff(classIndex);
				typeValue.children.get(2).intToBuff(nameTypeIndex);
				
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
		       return constPool.children.size() ;
				
			}
		
		
		   public static int addNewField(TypeValue fieldArray,String fieldName, String fieldType, int accessFlags, String signature) {
				
				int stringIndex = addNewUTF8(constPool,fieldName);
				int stringIndex2 = addNewUTF8(constPool, fieldType);
				int stringIndex3  = addNewUTF8(constPool, signature);
				return addNewField(fieldArray,stringIndex,stringIndex2,accessFlags,stringIndex3);
				
			}
		public static int addNewField(TypeValue fieldArray,int typeLocalIndex,int nameIndex, int accessFlags, int sigIndex) {

		       TypeInfo typeInfo = TypeInfo.getType("field_info");
				TypeValue typeValue = new TypeValue(fieldArray,typeInfo);
				typeValue.children.get(0).intToBuff(accessFlags);
				typeValue.children.get(1).intToBuff(nameIndex);
				typeValue.children.get(2).intToBuff(typeLocalIndex);
				if(fieldArray.children == null) {
					fieldArray.children = new ArrayList<TypeValue>();
				}
				fieldArray.children.add(typeValue);
				
				TypeValue attributes =  typeValue.getChild("attributes");
				
				
				
		       return fieldArray.children.size() -1;
				
			}
		public static int addNewString(int stringIndex) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_String_info");
				ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_String);
				typeValue.children.get(1).intToBuff(stringIndex);
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
		       return constPool.children.size() ;
				
			}
		public static int addNewUTF8(String string) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Utf8_info");
		       ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Utf8);
				
				
			    byte [] bytes = string.getBytes();
				typeValue.children.get(1).intToBuff(bytes.length);
				typeValue.children.get(2).buffer = bytes;
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
				 return constPool.children.size();
				
			}

		public static int addNewDouble(double d) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Double_info");
		       ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Double);
				
				long rtt =Double.doubleToLongBits(d);
				long nhigh_bytes = rtt >> 32;
				
				long nlow_bytes = rtt << 32;
				 nlow_bytes =  nlow_bytes >> 32;
			
				
			  
				typeValue.children.get(1).intToBuff((int)nhigh_bytes);
				typeValue.children.get(2).intToBuff((int)nlow_bytes);
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
				 return constPool.children.size();
				
			}
		
		public static int addNewFloat(float d) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Float_info");
		       ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Float);
				
				int rtt = Float.floatToIntBits(d);
				
				
			  
				typeValue.children.get(1).intToBuff(rtt);
			
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
				 return constPool.children.size();
				
			}
		public static int addNewInteger(int d) {

		       TypeInfo typeInfo = TypeInfo.getType("CONSTANT_Integer_info");
		       ConstEntry typeValue = new ConstEntry(constPool,typeInfo);
				typeValue.children.get(0).intToBuff(ConstEntry2.CONSTANT_Integer);
				
			
				
			  
				typeValue.children.get(1).intToBuff(d);
			
				constPool.children.add(typeValue);
				consts.add(typeValue);
				typeValue.init();
				 return constPool.children.size();
				
			}
*/
}
