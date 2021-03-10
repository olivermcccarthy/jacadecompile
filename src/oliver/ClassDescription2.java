package oliver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import decompile.ActionBranch;
import decompile.ByteCodeParser;
import oliver.action.Action;
import oliver.action.Instrument;
import oliver.action.ReturnAction;

public class ClassDescription2 {

	private static String PACKAGE = null;
	static String DEST = "C:\\Users\\OLIVERMCCARTHY\\eclipse-workspace\\Decompiled\\src";

	static HashMap<String, String> methodsToSignatures = new HashMap<String, String>();

	static boolean containsDynamic = false;
    static HashSet<String> methodsDefined = new HashSet<String>();
	static String methodName = "";

	static boolean firstRun = false;


	static class VariableInfo{
		String type;
		String name;
		int pc;
		public VariableInfo(String type, String name, int pc) {
			super();
			this.type = type;
			this.name = name;
			this.pc = pc;
		}
		
		
	}
	
	static public boolean closeCatch = false;
	
	static ArrayList<String> lambdas = new ArrayList<String>();
	
	static ArrayList<String> dollarClasses = new ArrayList<String>();
	
	public static void loadClassDescriptions() {
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();

			InputStream is2 = classloader.getResourceAsStream("oliver/ClassDescription");
             if(is2 == null) {
            	 is2 = new FileInputStream("src/oliver/ClassDescription");
             }
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(is2));
			String line = bufReader.readLine();
			TypeInfo typeInfo = null;

			boolean isUnion = false;

			while (line != null) {
				line = line.trim();
				if (line.endsWith("{")) {
					if (line.startsWith("union")) {
						isUnion = true;
						line = line.replace("union ", "").trim();
					} else {
						isUnion = false;
					}
					String type = line.replace("{", "").trim();

					if (isUnion) {
						typeInfo = new UnionDescription(type, type);
					} else {
						typeInfo = new TypeInfo(type, type);
					}

				} else if (line.endsWith("}")) {

					TypeInfo.addType(typeInfo.type, typeInfo);

				} else if (typeInfo != null) {
					String[] parts = line.replaceAll(" +", "~").split("~");
					if (parts.length == 2) {
						String type = parts[0];
						String name = parts[1].replace(";", "");
						if(name.length() > 0) {
						TypeInfo info = new TypeInfo(name, type);
						typeInfo.children.add(info);
						}
					} else if (parts.length == 1) {

						String name = parts[0].replace(";", "");
						if(name.length() > 0) {
						TypeInfo info = new TypeInfo(name, name);
						typeInfo.children.add(info);
						}
					} else if (parts.length == 7) {
						String type = parts[0];
						String name = parts[1];
					
						String exactValue = parts[5];
						String[] indexParts = exactValue.split("-");
						int lowerRange = Integer.valueOf(indexParts[0]) & 0xff;
						int upperRange = lowerRange;
						if (indexParts.length > 1) {
							upperRange = Integer.valueOf(indexParts[1]) & 0xff;
						}
						if(name.length() > 0) {
						TypeInfo info = new TypeInfo(name, type, lowerRange, upperRange);
						typeInfo.children.add(info);
						}
						

					}
				}

				line = bufReader.readLine();

			}
			TypeInfo.validateAll();
			TypeInfo classTypeInfo = TypeInfo.getType("ClassFile");
			classTypeInfo.fill();
			ByteCode.loadByteCodes();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static String handleDynamic(String methodCall, int index, String params, ArrayList<String> paramTypes) {
		if (firstRun) {
			return methodCall;
		}
		if(lambdas.size() == 0) {
			return methodCall;
		}
		
	
		methodCall = lambdas.remove(0);
		
		String [] paramArr = params.split(",");
		for(int varI =0; varI < paramArr.length; varI ++) {
			if(paramTypes.size() > varI) {
				methodCall = methodCall.replaceAll("REPLACEME (.*) = lambdaVar"+varI, paramTypes.get(varI) + " $1 = " + paramArr[varI]);
			}
			methodCall =  methodCall.replaceAll("lambdaVar"+varI,  paramArr[varI]);
			
		}
		
		
		
			return methodCall;
		
	}

	public static byte[] recompileClass(byte[] buf, int numRead) throws Exception {
		methodsToSignatures = new HashMap<String, String>();
		containsDynamic = false;
		lambdas.clear();
		methodsDefined.clear();
		Variable.clearImports();
		byte[] ret = recompileClass(buf, numRead, true);
		if (containsDynamic) {
			methodsDefined.clear();
			ret = recompileClass(buf, numRead, false);
		}
		return ret;
	}
	static ArrayList<VariableInfo> definedVars= null;
	public static HashMap<String, List<String>> enumes = new  HashMap<String, List<String>>();
	public static byte[] recompileClass(byte[] buf, int numRead, boolean firstRun) throws Exception {

		ClassDescription2.firstRun = firstRun;
		
		TypeInfo csInfo = TypeInfo.getType("ClassFile");
		BytePart actualClass = csInfo.decompile(buf,0);
		
		byte[] bufOut = new byte[numRead *2];
		//ConstEntry2.addNewString("How tehhell are you");
		
	//	ConstEntry2.addNewClass("com.how.Many");
//		ConstEntry2.writeConsts();
	//	int nbumOut =actualClass.write(bufOut, 0);
		//FileOutputStream testOut = new FileOutputStream("help/oliver/Me.class");
	//	testOut.write(bufOut, 0, nbumOut);
		//testOut.close();
		for(int testBit = 0; testBit < numRead; testBit ++) {
			if(buf[testBit] != bufOut[testBit]) {
				int debugMe =0;
			}
		}
		int inerterfaceCount = actualClass.getChild("interfaces_count").bufAsInt();
		String implStr = "";
		BytePart constPool = actualClass.getChild("constant_pool");
		
		int accessFlags = actualClass.getChild("access_flags").bufAsInt();
		String accessStr = "class ";
		boolean isEnum = false;
		if (accessFlags != 0) {
			accessStr = Variable.getAccess(accessFlags) + " class ";
			if (accessStr.contains("enum")) {
				accessStr = "public enum";
				isEnum = true;
			}

		}

		
	
		
		Variable clazzVar = ConstEntry2.consts.get(actualClass.getChild("this_class").bufAsInt()).getVariable();
		String clazzName = clazzVar.getName();

		BytePart sigType = actualClass.getChild("attributes").getChild( "Signature");
		ArrayList<String> sigParts = new ArrayList<String>();
	  String templatedStr = "";
		if (sigType != null) {
			
			int sigTypeInt = sigType.getChild("info").bufAsInt();
			Variable varSig = ConstEntry2.consts.get(sigTypeInt).getVariable();
			
			String type = varSig.name;
			boolean isTemplated = false;
			if(type.contains(":")) {
				
				
				isTemplated = true;
			}
			Variable.decodeSig(type, sigParts,true);
			if(isTemplated) {
			templatedStr = "<"+sigParts.remove(0) +":" +sigParts.remove(0)+">";
			
			}
		}

		int super_class = actualClass.getChild("super_class").bufAsInt();
		String superCzz = "";
		if (!isEnum && super_class != 0) {
			superCzz = " extends " + ConstEntry2.consts.get(super_class).getVariable().getType();
			if (sigParts.size() > 0) {
				superCzz = " extends " + sigParts.remove(0);
			}

		}

		if (inerterfaceCount > 0) {
			implStr = "implements ";
		}
		for (int i = 0; i < inerterfaceCount; i++) {
			int interfaceID = actualClass.getChild("interfaces").children.get(i).bufAsInt();
			Variable var = ConstEntry2.consts.get(interfaceID).getVariable();
			

			if (sigParts.size() > 0) {
				implStr += " " + sigParts.remove(0);

			} else {
				implStr += var.getName();
			}

			if (i < inerterfaceCount - 1) {
				implStr += ",";
			}
		}
		// String packageStr = clazzVar.getType().substring(0,
		// clazzVar.getType().lastIndexOf("."));
		new File(DEST + "/" + PACKAGE).mkdirs();
		// Thread.sleep(100);
		if (clazzName.equals("Equation$TYPE")){
			int debugmE=0;
		}
		PrintStream outStream = new PrintStream(new FileOutputStream(DEST + "/" + PACKAGE + "/" + clazzName + ".java"));
		PrintStream mainStream = outStream;
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		outStream = new PrintStream( byteArrayStream);
		// outStream.println("package " + packageStr + ";");
		String packageStr = PACKAGE.replaceAll("/", ".");
		if (packageStr.startsWith(".")) {
			packageStr = packageStr.substring(1);
		}
		 mainStream.println("package " + packageStr + ";");
		String packStr = PACKAGE.replaceAll("/", ".");
		isAbstract = false;
		allAbstract = true;
		for (BytePart method : actualClass.getChild("methods").children) {

			preProcessMethod(method, clazzName);
		}
	
		int numImports = Variable.getImports().size();
		
		if (isAbstract) {
			accessStr = accessStr.replace("class", " abstract class");
		}
		if (allAbstract) {
			
			accessStr = accessStr.replace("abstract class", "interface");
			superCzz = "";
		}
		outStream.println(accessStr + " " + clazzName + templatedStr +" " + superCzz + " " + implStr + " {");
		BytePart fields = actualClass.getChild("fields");
		ArrayList<String> fieldList = new ArrayList<String>();
		ArrayList<String> enumVals = new ArrayList<String>();
		ArrayList<String> otherFields = new ArrayList<String>();
		for (BytePart fieldVar : fields.children) {
			accessFlags = fieldVar.children.get(0).bufAsInt();
			accessStr = "";
			if (accessFlags != 0) {
				accessStr = Variable.getAccess(accessFlags);

				// accessStr = accessStr.replace("final enum", "final");
			}
			int typeLocalIndex = fieldVar.children.get(2).bufAsInt();
			int nameLocalIndex = fieldVar.children.get(1).bufAsInt();
			Variable typeStr = ConstEntry2.consts.get(typeLocalIndex).getVariable();
			Variable nameStr = ConstEntry2.consts.get(nameLocalIndex).getVariable();

			Variable fVar = new Variable(Variable.decodeSig(typeStr.getName()), nameStr.getName());
			fieldList.add(fVar.getType() + " " + fVar.getName());
			if (accessStr.contains("public static final enum")) {
				enumVals.add(fVar.getName());
				continue;
			}
			if (accessStr.contains("static final") && typeStr.name.equals("I")) {
				continue;
			}
			accessStr = accessStr.replace("final", "");
		
			sigType = fieldVar.getChild("attributes").getChild("Signature");

			String signature = fVar.getType();
			if (sigType != null) {
				int sigTypeInt = sigType.getChild("info").bufAsInt();
				Variable varSig = ConstEntry2.consts.get(sigTypeInt).getVariable();
				signature = Variable.decodeSig(varSig.name);

			}

			otherFields.add(accessStr + (signature + " " + fVar.getName()) + " ;");
		}
		if (enumVals.size() > 0) {
			enumes.put(packageStr.replace(".", "$") +"$"+clazzName, enumVals);
			String eStr = enumVals.toString().replace("[", "").replace("]", "");
			String mainFile =  byteArrayStream.toString();
			outStream = mainStream;
			outStream.println(mainFile);
			outStream.println(eStr);
			outStream.println("}");
			outStream.flush();
			outStream.close();
			
			return null;
		}
		for (String otherF : otherFields) {
			outStream.println(otherF.replace("<T>", "<Object>"));
		}
		// newread = actualClass.compile(0, bufout);

		for (BytePart method : actualClass.getChild("methods").children) {

			try {
				processMethod(method, outStream, clazzName);
			} catch (Throwable t) {
				outStream.println("/** DECOMPILEFAIL");
				outStream.println(ByteCode.allInfo);
				outStream.println(method.name + "  ** DECOMPILEFAIL" + t.getMessage());
				t.printStackTrace(outStream);
				t.printStackTrace();
				outStream.println("*/");
			}
		}

	
		
		String mainFile =  byteArrayStream.toString();
		outStream = mainStream;
		for (String importStr : Variable.getImports()) {
			if (packStr.contains(importStr)) {
				continue;
			}
			if (importStr.contains("[]")) {
				importStr = importStr.replace("[]", "");
			}
			if (importStr.contains("[L")) {
				importStr = importStr.replace("[L", "");
			}
			if (importStr.contains(";")) {
				importStr = importStr.replace(";", "");
			}
			if (importStr.contains("<")) {
				continue;
			}
			if (importStr.contains("$")) {
				if (!importStr.endsWith("$"))
					importStr = importStr.substring(0, importStr.indexOf("$"));
			}
			if (importStr.endsWith(".java")) {
				continue;
			}
			outStream.println("import " + importStr + ";");
		}
		outStream.println(mainFile);
		outStream.println("}");
		outStream.close();
		
		int nbumOut =actualClass.write(bufOut, 0);
		FileOutputStream testOut = new FileOutputStream("help/oliver/Me.class");
		testOut.write(bufOut, 0, nbumOut);
		testOut.close();
		return null;

	}
		public static String replaceValues(String fullString, String replaceID, String newID) {
		fullString = fullString.replaceAll(replaceID + "\":\"[^\"]+", replaceID + "\":\"" + newID + "");
		return fullString;
	}

	public static void perDirectory(String binDir, String packageDir, boolean dollars) {

		String decomp = "";
		try {
	
			System.out.println("looking in " + binDir + "/" + packageDir);
			for (String clzz : new File(binDir + "/" + packageDir).list()) {

				
				if (clzz.endsWith(".class")  &&  !clzz.startsWith("Me.class")) {
					if(dollars && !clzz.contains("$")) {
						continue;
					}
					if(!dollars && clzz.contains("$")) {
						continue;
					}
					decomp = binDir + "/" + packageDir + "/" + clzz;
					InputStream is = new FileInputStream(binDir + "/" + packageDir + "/" + clzz);

					byte[] buf = new byte[100000];
					int numRead = 0;
					numRead = is.read(buf);
					// System.out.println("Decompiling " + clzz);
					PACKAGE = packageDir;
					try {
						long startT = System.currentTimeMillis();
						recompileClass(buf, numRead);
						long diff = System.currentTimeMillis() - startT;
						System.out.println("To decompile " + packageDir + "/" + clzz + " took ms  " + diff);
					} catch (Throwable t) {
						System.out.println(decomp);
						t.printStackTrace();
					}
					is.close();
				}

				File testME = new File(binDir + "/" + packageDir + "/" + clzz);
				if (testME.isDirectory()) {
					perDirectory(binDir, packageDir + "/" + clzz,dollars);
				}

			}
		} catch (Throwable t) {
			System.out.println(decomp);
			t.printStackTrace();
		}

	}

	public static void main(String[] args) {

		ByteCodeParser.loadCodes();
	   String binDir ="bin";
       if(args.length > 0) {
    	   binDir = args[0];
       }
	for(String arg : args) {
		int df =90;
		System.out.println(arg);
	}
		loadClassDescriptions();
		// findDollarClasses("C:\\Users\\oliver\\/Events/related.events/build/classes/main/",
		// "");
		//../ea-related-events/bin
		findDollarClasses(binDir, "");
		try {
			// perDirectory("C:\\Users\\oliver\\/Events/related.events/build/classes/main/",
			// "");
			perDirectory(binDir, "",true);
			perDirectory(binDir, "",false);
			ByteCode.printNotTested();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	static boolean allAbstract = false;
	static boolean isAbstract = false;

	protected static void preProcessMethod(BytePart method, String clazzName) {
		if (method.getChild("attributes").getChild("CodeAttribute") == null) {
			isAbstract = true;
		}else {
		   allAbstract = false;
		}
		if(clazzName.equals("StackMapMad$Type")) {
			int debugME =0;
		}
		int nameIndex = method.getChild("name_index").bufAsInt();
		int descriptorIndex = method.getChild("descriptor_index").bufAsInt();
		Variable nameVariable = ConstEntry2.consts.get(nameIndex).getVariable();
		
	}

	public static String TheclazzName = "";

	protected static void processMethod(BytePart method, PrintStream outStream, String clazzName) {
		
		if (clazzName.equals("Equation$TYPE")){
			int debugmE=0;
		}
		TheclazzName = clazzName;
		BytePart expectionsAttr = method.getChild("attributes").getChild("Exceptions");
		String throwStr = "";
		if (expectionsAttr != null) {
			expectionsAttr = expectionsAttr.children.get(2);
			
			int numExcps = expectionsAttr.children.get(1).children.size();
			if (numExcps > 0) {
				throwStr = "throws ";
			}
			for (int e = 0; e < numExcps; e++) {
				BytePart expName = expectionsAttr.children.get(1).children.get(e);
				int nameIndex = expName.bufAsInt();
				Variable nameStr = ConstEntry2.consts.get(nameIndex).getVariable();
				throwStr += nameStr.name + " ";
				if (e < numExcps - 1) {
					throwStr += ",";
				}
			}
		}
		int accessFlags = method.getChild("access_flags").bufAsInt();
		String accessStr = "";
		Instrument.isStaticMethod= false;
		if (accessFlags != 0) {
			accessStr = Variable.getAccess(accessFlags);
			if(accessStr.contains("static")) {
				Instrument.isStaticMethod= true;
			}
		}
		int nameIndex = method.getChild("name_index").bufAsInt();
		int descriptorIndex = method.getChild("descriptor_index").bufAsInt();
		Variable nameVariable = ConstEntry2.consts.get(nameIndex).getVariable();
		Variable descriptionVariable = ConstEntry2.consts.get(descriptorIndex).getVariable();
		boolean isInit = false;
		
		methodName = nameVariable.getName();
		if(methodName.contains("SWITCH_TABLE$")){
		  return;		
		}
		if (methodName.equals("<init>") && clazzName.contains("$")) {
			methodName = clazzName;
			isInit = true;
		}

		
		MethodVariable methodVar = new MethodVariable(PACKAGE.substring(1) + "/" + clazzName, methodName,
				descriptionVariable.getName());
		definedVars = new ArrayList<VariableInfo>();
		if(clazzName.equals("DrawPanel") && methodName.equals("stopAMinute")) {
			int debugME =0;
		}
		BytePart codeAttribute = method.getChild("attributes").getChild("CodeAttribute");
		
		if (codeAttribute == null) {
			if(allAbstract) {
				outStream.println(accessStr + " " + methodVar.type + " " + nameVariable.name + "("
						+ methodVar.formatParams() + ") " + throwStr + ";");
				return;
			}
			outStream.println(accessStr + " abstract " + methodVar.type + " " + nameVariable.name + "("
					+ methodVar.formatParams() + ") " + throwStr + ";");
			return;
		}
		BytePart codeLengthAttribute = codeAttribute.children.get(1);
		codeAttribute = codeAttribute.getChild("info");
		BytePart signatureType = method.getChild("attributes").getChild("Signature");
		String sigReturn = "";
		if (signatureType != null) {
			
			int sigIndex = signatureType.getChild("info").bufAsInt();
			Variable sigStr = ConstEntry2.consts.get(sigIndex).getVariable();
			sigReturn = sigStr.name;
			if (sigReturn.contains("<")) {
				Variable.decodeSig(sigReturn);
				sigReturn = sigReturn.substring(sigReturn.lastIndexOf(")") + 1);
				if (sigReturn.contains("<")) {
					sigReturn = Variable.decodeSig(sigReturn);
					methodVar.type = sigReturn;

				} else if (sigReturn.equals("TT;")) {
					sigReturn = "<T> T";
					methodVar.type = "<T> T";

				}
			} else {
				sigReturn = "";
			}
		}
		closeCatch = false;

		BytePart expTable = codeAttribute.getChild("exception_table");
		ExceptionHandler.clear();
		if (expTable != null) {
			for (BytePart exp : expTable.children) {

				ExceptionHandler.addExp(exp);
			}
		}
		
		BytePart localVaraiableTable = codeAttribute.getChild("attributes").getChild("LocalVariableTable");
		if (localVaraiableTable != null) {
			localVaraiableTable = localVaraiableTable.getChild("info").getChild("table");
			Variable.clearVariable();
			for (BytePart localVarPart : localVaraiableTable.children) {
				int x = 0;

				int typeLocalIndex = localVarPart.getChild("descriptor_index").bufAsInt();
				int nameLocalIndex = localVarPart.getChild("name_index").bufAsInt();
				Variable o3 = ConstEntry2.consts.get(typeLocalIndex).getVariable();
				Variable o1 = ConstEntry2.consts.get(nameLocalIndex).getVariable();
				int startPC = localVarPart.getChild("start_pc").bufAsInt();
				int lengthPC = localVarPart.getChild("length").bufAsInt();
				int index = localVarPart.getChild("index").bufAsInt();
				Variable.addVariable(new Variable(o3.getName(), o1.getName(), startPC, lengthPC, index));
			}
		}
		BytePart localVaraiableTypeTable =  codeAttribute.getChild("attributes").getChild("LocalVariableTypeTable");
		
		if (localVaraiableTypeTable != null) {

			localVaraiableTypeTable = localVaraiableTypeTable.getChild("info").getChild("table");
			for (BytePart localVar : localVaraiableTypeTable.children) {

				int typeLocalIndex = localVar.children.get(3).bufAsInt();
				Variable o = ConstEntry2.consts.get(typeLocalIndex).getVariable();
				int startPC = localVar.children.get(0).bufAsInt();

				int index = localVar.children.get(4).bufAsInt();
				try {
					if (o.getName().contains("<"))
						Variable.updateSig(startPC, index, o.getName());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}

		if(methodName.equals("compareTo")){
			int debugME =0;
		}
		String methodDesc = methodVar.formatParamsFromVarTable(definedVars) + throwStr;
		if(methodDesc.indexOf("(") > 0) {
			String methodSigs = methodName +methodDesc.substring(methodDesc.indexOf("("));
			if(methodsDefined.contains(methodSigs)) {
				System.out.println(methodSigs + " Already Defined");
				return;
			}
			methodsDefined.add(methodSigs);
		}
		
		
		if (isInit) {
			methodDesc = methodDesc.replace("void", "");
		}
		
		if (methodDesc.contains("unusedVariable") && methodDesc.contains("compareTo")) {
			return;
		}
		if (!isInit && methodDesc.contains("unusedVariable")) {
			if(!methodDesc.contains("lambda") )
{
				if(methodDesc.contains("access$")){
					int startIndex = methodDesc.indexOf("unusedVariable");
					int endIndex = methodDesc.indexOf(",",startIndex);
					String part = methodDesc.substring(startIndex, endIndex);
					methodDesc=methodDesc.replaceFirst("unusedVariable[0-9]+", "lambdaVar0");
					methodDesc=methodDesc.replaceFirst("unusedVariable[0-9]+", part);
				}else {
				  return;
				}
}
			
		}
		if(methodName.contains("lambda$") && !ClassDescription2.firstRun ) {
			return;
		}
		if(methodDesc.equals("int  compareTo ()")) {
			int debugMNE =9;
		}
		if (methodVar.numParams > Variable.varTable.size()) {
			
			return;
		}
		accessStr = accessStr.replace("transient", "");
		
		codeAttribute = codeAttribute.getChild("code");
		ByteCode code2 = new ByteCode();
		//code2.decompileAll(codeAttribute.buffer, codeAttribute.buffer.length, clazzName, methodName);
		ByteCodeParser.decompileAll(codeAttribute.buffer,  codeAttribute.buffer.length, clazzName, methodName, methodDesc.startsWith("boolean"));
		
		outStream.println(accessStr + methodDesc + "{");
		ByteArrayOutputStream outBytes = null;
		
		PrintStream oldStream = outStream;
       if(methodName.contains("lambda$") && ClassDescription2.firstRun ) {
    	   outBytes = new ByteArrayOutputStream();
		 	outStream = new PrintStream(outBytes);
		 	String lambdaS = definedVars.get(0).name + " -> ";
		 	if(definedVars.size() >1) {
		 	  lambdaS = definedVars.get(1).name + " -> ";
		 	}
		 	outStream.println( lambdaS + "{");
		}
		
        

		outStream.println("//" + clazzName + "." + method.name + " Starting to decompile ");

		
	
	
		long start = System.currentTimeMillis();

		
		boolean returnsBoolean = false;
		if (methodDesc.startsWith("boolean")) {
			returnsBoolean = true;
		}
		boolean staticInit = false;
		if (methodVar.name.equals("<clinit>")) {
			staticInit = true;

		}
		ReturnAction.returnFloat = false;
		if (methodDesc.startsWith("float")) {
			ReturnAction.returnFloat = true;
		}
		if(clazzName.equals("ActionStack") && methodName.equals("printStackInner")) {
			int debugME =0;
		}
		ReturnAction.returnBoolean = returnsBoolean;
		ReturnAction.staticInit = staticInit;
	   Action.allActions.clear();
		
				
	    Instrument.goMad(codeAttribute, codeLengthAttribute,PACKAGE.substring(1)+"."+clazzName,methodName);
		ArrayList<String> varsss = new ArrayList<String>();
		for( VariableInfo info :definedVars) {
			varsss.add(info.name);
		}
		//code2.actionStack.printStack(clazzName,methodName,varsss,outStream);
		decompile.ByteCodeParser.parent.printAll(outStream,varsss,"  ",staticInit);
      
		long diff = System.currentTimeMillis() - start;
		outStream.println("//" + clazzName + "." + nameVariable.getName() + " To decompiple  took " + diff);

		start = System.currentTimeMillis();
		/*
		 * if(accessStr.contains("static") && methodDesc.length() == 0) {
		 * Branch.parentBranch.lines.remove(Branch.parentBranch.lines.size() -1); }
		 */
		

		
		
	
		methodsToSignatures.put(methodName, methodDesc);
		System.out.println("Decompliling" + clazzName + "." + methodName);
		// if(clazzName.equals("ByteCode")) {

	
		HashMap<String, VariableInfo> otherVariables = new HashMap<String, VariableInfo>();

		for( VariableInfo info :definedVars) {
			otherVariables.put(info.name, info);
		}

		
	
		
		//CodePrinter.printWithLines(InnerPiece.master, outStream, "   ");
		// StompingStone2.masterStone.printWithLines(outStream, " ");
		// StompingStone2.masterStone = null;

		outStream.println("}");
		if(methodName.contains("lambda$") && ClassDescription2.firstRun ) {
			outStream.flush();
			lambdas.add(new String(outBytes.toByteArray()));
			
		   
		
			 outStream = oldStream;
		}
		// System.out.println( nameStr.getName() + " To print took " + diff);

	}



	public static void findDollarClasses(String binDir, String packageDir) {

		String decomp = "";
		try {

			for (String clzz : new File(binDir + "/" + packageDir).list()) {

				if (clzz.endsWith(".class") && clzz.contains("$")) {
					String ne = packageDir + "." + clzz;
					dollarClasses.add(ne.replaceAll("/", ".").substring(1));
				}

				File testME = new File(binDir + "/" + packageDir + "/" + clzz);
				if (testME.isDirectory()) {
					findDollarClasses(binDir, packageDir + "/" + clzz);
				}

			}
		} catch (Throwable t) {
			System.out.println(decomp);
			t.printStackTrace();
		}

	}

}
