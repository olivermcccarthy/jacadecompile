package oliver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Set;

public class Variable {

	public String type;
	public boolean containsQuotes = false;
	public String name;

	public String className;

	String fullName = "";
	boolean varAssigned = false;
	static ArrayList<Variable> varTable = new ArrayList<Variable>();

	int startPC;
	int endPC;
	int index;

	public boolean isRebranded() {
		return rebranded;
	}

	public void setRebranded(boolean rebranded) {
		this.rebranded = rebranded;
	}

	public String newType = null;

	protected boolean isLambda;

	private boolean rebranded;

	public static void clearVariable() {
		varTable.clear();
		maxIndex = 0;
		multiVars = new MultiVar[80];
	}

	public static void clearImports() {

		imports.clear();

	}

	public static MultiVar[] multiVars = new MultiVar[80];
	public static int maxIndex = 0;

	public static void addVariable(Variable var) {

		if (multiVars[var.index] == null) {
			multiVars[var.index] = new MultiVar(var);
		} else {
			multiVars[var.index].addVariable(var);
		}
		if (var.index > maxIndex) {
			maxIndex = var.index;
		}
		varTable.add(var);
	}

	static int unusedCount = 0;

	static String lastImport;

	public static String decodeSig(String signature) {

		if (signature.equals("TObject;")) {
			return "Object";
		}
		return decodeSig(signature, new ArrayList<String>());
	}

	public static String decodeClassStr(String classStr) {

		classStr = classStr.replace("/", ".");
		addImport(classStr);
		Variable.lastImport = classStr;
		if (ClassDescription2.dollarClasses.contains(classStr + ".class")) {

			classStr = classStr.substring(classStr.lastIndexOf(".") + 1);
		} else {
			classStr = classStr.substring(classStr.lastIndexOf(".") + 1);
			classStr = classStr.replace("$", ".");

		}
		return classStr;
	}

	public static String decodeSig(String signature, ArrayList<String> params) {
		return decodeSig(signature, params, true);
	}

	public static String decodeSig(String signature, ArrayList<String> params, boolean decodeClass) {

		String decodedSig = "";

		for (int c = 0; c < signature.length(); c++) {
			char charS = signature.charAt(c);
			boolean isArray = false;
			boolean isDoubleArray = false;
			if (charS == '[') {

				isArray = true;
				c++;
				charS = signature.charAt(c);
				if (charS == '[') {
					isDoubleArray = true;
					c++;
					charS = signature.charAt(c);
				}
			}
			switch (charS) {

			case '(': {
				decodedSig += "(";
				break;
			}
			case ')': {
				decodedSig += ")";
				break;
			}
			case '<': {
				int end = signature.lastIndexOf(">");
				String templateStr = signature.substring(c + 1, end);

				decodedSig += "<" + decodeSig(templateStr) + ">";

				c = end + 1;
				break;
			}
			case '>': {
				decodedSig += ">";
				break;
			}

			case 'L': {
				int end = signature.indexOf(";", c + 1);
				if (end == -1) {
					return signature;
				}
				int templateStart = signature.indexOf("<", c + 1);
				String classStr = signature.substring(c + 1, end);
				String templateStr = "";
				if (templateStart < end && templateStart > -1) {
					classStr = signature.substring(c + 1, templateStart);
					int openCount = 1;
					for (int cx = templateStart + 1; cx < signature.length(); cx++) {
						char testC = signature.charAt(cx);
						if (testC == '<') {
							openCount++;
						}
						if (testC == '>') {
							openCount--;
						}
						if (openCount == 0) {
							end = cx;
							break;
						}
					}

					templateStr = signature.substring(templateStart + 1, end);
					templateStr = "<" + decodeSig(templateStr) + ">";
					end++;
				}

				if (templateStr.equals("<Object>")) {
					// templateStr="";
					// int debugMe =0;
				}
				if (decodeClass) {
					classStr = decodeClassStr(classStr);
				}
				c = end;
				classStr += templateStr;
				if (isArray) {
					classStr += " []";
				}
				if (isDoubleArray) {
					classStr += " []";
				}
				params.add(classStr);
				decodedSig += classStr;
				if (c < signature.length() - 1) {
					decodedSig += ',';
				}
				break;
			}

			default: {
				String classStr = "long";
				switch (charS) {
				case 'D':
					classStr = "double";
					break;
				case 'J':
					classStr = "long";
					break;
				case 'I':
					classStr = "int";
					break;
				case 'F':
					classStr = "float";
					break;
				case 'C':
					classStr = "char";
					break;
				case 'B':
					classStr = "byte";
					break;
				case 'V':
					classStr = "void";
					break;
				case 'S':
					classStr = "short";
					break;
				case 'Z':
					classStr = "boolean";
					break;
				case 'T': {
					int semiIndex = signature.indexOf(";", c + 1);
					if (semiIndex > -1) {
						c = signature.length();
					}
					classStr = "T";
					break;

				}
				case '*':
					classStr = "?";
					break;
				default:

					if (!signature.contains("/")) {
						return signature;
					} else {
						classStr = decodeClassStr(signature);
						return classStr;
					}

				}
				if (isArray) {
					classStr += " []";
				}
				if (isDoubleArray) {
					classStr += " []";
				}
				decodedSig += classStr;
				if (c < signature.length() - 1) {
					decodedSig += ',';
				}
				params.add(classStr);

				break;
			}

			}
		}
		decodedSig = decodedSig.replace(",)", ")");
		return decodedSig;
	}

	public static String getPrimativeType(String type) {
		if (type.equals("int")) {
			return "I";
		}
		if (type.equals("long")) {
			return "J";
		}
		if (type.equals("double")) {
			return "D";
		}
		if (type.equals("float")) {
			return "F";
		}
		if (type.equals("char")) {
			return "C";
		}
		if (type.equals("void")) {
			return "V";
		}
		if (type.equals("byte")) {
			return "B";
		}
		if (type.equals("short")) {
			return "S";
		}
		if (type.equals("boolean")) {
			return "Z";
		}
		type = type.replace(".", "/");
		type = "L" + type + ";";
		return type;
	}

	public static void updateSig(int codeIndex, int index, String signature) {

		// Ljava/util/SortedSet<Ljava/util/Set<Ljava/lang/Integer;>;>;
		signature = decodeSig(signature);
		if (multiVars[index] != null) {
			Variable var = multiVars[index].getVariable(codeIndex);
			if (var != null) {
				if (var.type.endsWith("[]")) {
					var.type = var.type.replace("[]", "");
					var.type = signature;

				} else {
					var.type = signature;
				}
			}
		}
	}

	public static Variable getVariable(int codeIndex, int index) {
		if (multiVars[index] != null) {
			Variable var = multiVars[index].getVariable(codeIndex);
			if (var != null) {
				return var;
			}
		}

		for (Variable var : varTable) {
			if (var.index == index) {

				if (var.name.startsWith("unused")) {
					return var;
				}

				unusedCount++;
				if (unusedCount == 82) {
					int debugME = 0;
				}
				Variable vs = new Variable(var.type, "unusedVariable" + unusedCount, codeIndex, 10000, index);
				addVariable(vs);
				return vs;
			}
		}
	
		Variable vs = new Variable("REPLACEME", "lambdaVar" + index, codeIndex, 10000, index);
		vs.isLambda = true;
		addVariable(vs);
		return vs;
	}

	public static Variable getVariable(int codeIndex, int index, String pop) {
		return getVariable(codeIndex, index, pop, false);
	}

	public static Variable getVariable(int codeIndex, int index, String pop, boolean assign) {
		if (multiVars[index] != null) {
			Variable var = multiVars[index].getVariable(codeIndex);
			if (var != null) {
				if (assign) {

					var.varAssigned = true;

				}

				if (var.name.startsWith("unusedVariable")) {
					String typeS = pop;

					if (typeS != null && !typeS.equals("void") && !var.type.equals(typeS)) {
						var.type = typeS;
						var.name += "XOOPP";
						var.rebranded = true;

					}

				}
				return var;
			}
		}
		String typeS = pop;

		unusedCount++;
		if (unusedCount == 106) {
			int debugME = 0;
		}
		// Variable(String type, String name, int startPC, int lengthPC, int index)
		Variable vs = new Variable(typeS, "unusedVariable" + unusedCount, codeIndex, 10000, index);

		addVariable(vs);
		return vs;

	}

	public Variable(String className, String name, String type) {
		super();
		if (type.contains("[D") && !type.contains("(")) {
			this.type = Variable.getTheType(type);
		}
		this.type = getTheType(type);
		this.name = name;
		if (type.contains("$")) {
			className = this.type;
			return;
		}
		this.className = getTheType(className);

	}

	public Variable(String className, String name, String type, boolean yes) {
		super();
		if (type.contains("[") && !type.contains("(")) {
			this.type = Variable.getTheType(type);
		}
		this.type = type;
		this.name = name;
		this.className = className;

	}

	public Variable(String type) {
		super();
		if (type.contains("[") && !type.contains("(")) {
			this.type = Variable.getTheType(type);
		}
		if (type.indexOf("/") > 0) {
			Variable.lastImport = "";
			type = getTheType(type);
			if (Variable.lastImport.length() > 0) {
				fullName = Variable.lastImport;
			}
		}

		this.type = type;
		this.name = type;
		this.className = "";

	}

	public Variable(String type, String name) {
		super();
		this.type = type;

		if (type.contains("[") && !type.contains("(")) {
			this.type = Variable.getTheType(type);
		}
		this.name = name;
		this.className = "";

	}

	public Variable(String type, String name, int startPC, int lengthPC, int index) {
		super();
		if (type == null) {
			type = "Object";
		}
		if (type.contains("[I")) {
			int debugME = 0;
		}
		this.type = getTheType(type);
		this.name = name;
		this.startPC = startPC;
		this.endPC = this.startPC + lengthPC;
		this.index = index;

	}

	public String toString() {
		return name;
	}

	public String getName() {

		if (this.containsQuotes) {
			return this.name.replaceAll(ConstEntry2.replaceQuote, "\\\\\"");

		}
		return name;

	}

	public String getNameType() {

		return getType();

	}

	public static String getClassName(String val) {

		return getTheType(val);
	}

	public String getType() {

		if (type.contains("[")) {
			int debugME = 0;
		}
		return type;

	}

	public String getSimpleType() {

		return type.replaceAll("<.+", "");

	}

	private static HashSet<String> imports = new HashSet<String>();

	public static void addImport(String imp) {
		if (!imp.contains(".")) {
			return;
		}
		imports.add(imp);
	}

	public static Set<String> getImports() {
		return Collections.unmodifiableSet(imports);
	}

	public static String getTheType(String type) {
		if (type.contains("[[")) {
			int debugME = 0;
		}
		type = decodeSig(type);
		return type;

	}

	static final int ACC_PUBLIC = 1;
	static final int ACC_PRIVATE = 2;
	static final int ACC_PROTECTED = 4;
	static final int ACC_STATIC = 8;
	static final int ACC_FINAL = 16;
	static final int ACC_VOLATILE = 0x0040;
	static final int ACC_TRANSIENT = 0x0080;
	static final int ACC_SYNTHETIC = 0x1000;
	static final int ACC_ENUM = 0x4000;

	public static String getAccess(int access) {
		String ret = "";
		if ((access & ACC_PUBLIC) != 0) {
			ret += "public ";

		}
		if ((access & ACC_PRIVATE) != 0) {
			ret += "public ";

		}
		if ((access & ACC_PROTECTED) != 0) {
			ret += "protected ";

		}
		if ((access & ACC_STATIC) != 0) {
			ret += "static ";

		}
		if ((access & ACC_FINAL) != 0) {
			ret += "final ";

		}

		if ((access & ACC_TRANSIENT) != 0) {
			ret += "transient ";

		}
		if ((access & ACC_ENUM) != 0) {
			ret += "enum ";

		}
		int testAccess = getAccess(ret);
		if (testAccess != access) {
			int debugME = 0;
		}
		return ret;
	}

	public static int getAccess(String accessStr) {
		int ret = 0;
		for (String oneStr : accessStr.split(" ")) {
			if (oneStr.equals("public")) {
				ret = ret | ACC_PUBLIC;
			} else if (oneStr.equals("private")) {
				ret = ret | ACC_PRIVATE;
			} else if (oneStr.equals("protected")) {
				ret = ret | ACC_PROTECTED;
			} else if (oneStr.equals("static")) {
				ret = ret | ACC_STATIC;
			} else if (oneStr.equals("final")) {
				ret = ret | ACC_FINAL;
			} else if (oneStr.equals("transient")) {
				ret = ret | ACC_TRANSIENT;
			} else if (oneStr.equals("enum")) {
				ret = ret | ACC_ENUM;
			} else if (oneStr.equals("enum")) {
				ret = ret | ACC_ENUM;
			}
		}

		return ret;
	}

	public Object getClassName() {
		if (className == null) {
			return this.type;
		}
		// TODO Auto-generated method stub
		return className;
	}
}
