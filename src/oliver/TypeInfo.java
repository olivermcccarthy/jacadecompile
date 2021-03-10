package oliver;

import java.util.ArrayList;
import java.util.HashMap;


public class TypeInfo {

	private static HashMap<String, TypeInfo> types = new HashMap<String, TypeInfo>();
	String type;
	String name;
	
	int lowerRange = -1;
	int upperRange = -1;
	ArrayList<TypeInfo> children = new ArrayList<TypeInfo>();
	TypeInfo parent = null;
	
	boolean basedOnTag = false;
	public static TypeInfo getType(String name) {
		return types.get(name);
	}
	
	public static void  addType(String name, TypeInfo info) {
		types.put(name, info);
	}
	public TypeInfo(String name, String type, int lowerRange, int upperRange) {
		super();
		this.name = name;
		this.type = type;
		this.lowerRange = lowerRange;
		this.upperRange = upperRange;
		if (this.type.matches("u[0-9]+")) {
			bufferSize = Integer.valueOf(type.replace("u", ""));
		}
	}
	
	int bufferSize = -1;
	boolean isArray= false;
	int countOffSet = 0;
	private boolean isStackMap;
	public TypeInfo(TypeInfo other) {
		this.name = other.name;
		this.type = other.type;
		this.bufferSize = other.bufferSize;
		this.isArray = other.isArray;
		this.countOffSet = other.countOffSet;
		for(TypeInfo ch :other.children) {
			this.children.add(new TypeInfo(ch));
		}
	}
	public TypeInfo(String name, String type) {
		super();
		this.name = name;
		this.type = type;
		if (this.type.matches("u[0-9]+")) {
			bufferSize = Integer.valueOf(type.replace("u", ""));
		}
		
		if (name.contains("[")) {
			isArray = true;
			String countString =name.substring(name.indexOf("[") + 1);
		
			name = name.substring(0, name.indexOf("["));
			countString =countString.substring(0,countString.indexOf("]"));
			
			if (countString.contains("-")) {
				String test = countString.substring(countString.indexOf("-") + 1);
				countOffSet = Integer.valueOf(test);
				countString =countString.substring(0,countString.indexOf("-"));
			}
		}
		this.name = name;
		this.type = type;
	}

	public void validate() {
		if (this.type.matches("u[0-9]+")) {
			// System.out.println("      " + this.name + ". " + this.type + "  Valid");
		} else {
			if (!types.containsKey(this.type)) {

				// System.out.println("      " + this.name + ". " + this.type + "   Not Valid");
			} else {
				// System.out.println("      " + this.name + ". " + this.type + "  Valid");
			}
			
		}
		for (TypeInfo entry : children) {

			entry.validate();
		}
	}

	

	public String toString() {
		return name + ":" + type;
	}
	public static void validateAll() {
		for (TypeInfo info : types.values()) {
			// System.out.println("Validating " + info.type);
			info.validate();
		}
	}

	public void fill() {
		// TODO Auto-generated method stub
		if(this.bufferSize == -1) {
			if(this.children.size() == 0) {
                 TypeInfo myActualType = TypeInfo.getType(type);
                 if(myActualType != null) {
                	
                	 for(TypeInfo ch :  myActualType.children) {
                		 this.children.add(new TypeInfo(ch));
                	 }
                	 
                	 
                 }
			}
		}
		for (TypeInfo entry : children) {

		    entry.parent = this;
		    if(entry.bufferSize == -1) {
		  	   entry.fill();
		    }
		}
	}
	public BytePart decompile( byte[] buffer, int startIndex) {
		BytePart myPart = new BytePart(null,this,startIndex);
		this.decompileMe(myPart,buffer, startIndex);
		return myPart;
	}
	public int decompile(BytePart bytePart,byte[] buffer, int startIndex) {
		BytePart myPart = new BytePart(bytePart,this,startIndex);
		bytePart.children.add(myPart);
		
		return decompileMe( myPart,buffer,startIndex);
	}
	public int decompileMe(BytePart bytePart, int startIndex) {
		int val = decompileMe(bytePart,bytePart.buffer, startIndex);
		if(bytePart.children.size() > 0) {
			int debugME =0;
			bytePart.bufferSize = -1;
			bytePart.buffer = null;
		}
		return val;
	}
	public int decompileMe(BytePart bytePart,byte[] buffer, int startIndex) {
		
		String newType = type;
		int arraySize = -1;
		if(this.type.equals("stack_map_frame")) {
			 bytePart.parent.parent.isStackMap = true;
			this.children.clear();
			int tyeOFFrame= buffer[startIndex]&0xff;
			if(tyeOFFrame < 64) {
				newType = "same_frame";
			}else if(tyeOFFrame < 128) {
				newType = "same_locals_1_stack_item_frame";
				arraySize =1;
			}else if(tyeOFFrame == 247) {
				newType = "same_locals_1_stack_item_frame_extended";
				arraySize =1;
				}
			else if(tyeOFFrame < 251) {
				newType = "chop_frame";
			}
			
			else if(tyeOFFrame < 252) {
				newType = "same_frame_extended";
			}
			else if(tyeOFFrame < 255) {
				arraySize = tyeOFFrame - 251;
				newType = "append_frame";
			}
			else if(tyeOFFrame < 256) {
				newType = "full_frame";
			}
		}
		if(this.type.equals("verification_type_info")) {
			this.children.clear();
			int tyeOFFrame= buffer[startIndex]&0xff;
			if(tyeOFFrame == 0) {
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
			
			
		}
		if(children.size() == 0 && newType.contains("_")) {
			TypeInfo newTypes =TypeInfo.getType(newType);
			for(TypeInfo ch: newTypes.children) {
				this.children.add(ch);
			}
		}
		if(this.bufferSize > 0) {
			startIndex = bytePart.fill(buffer, startIndex);
		}
		int attributeNameIndex = -1;
		for (TypeInfo entry : children) {
			 if(entry.name.equalsIgnoreCase("interfaces_count")) {
				  int debugMe =0;
			  }
			if(entry.isArray) {
				BytePart lastBytePart = bytePart.children.get(bytePart.children.size() -1);
				int size = lastBytePart.bufAsInt();
				
				BytePart myPart = new BytePart(bytePart,entry,startIndex);
				lastBytePart.countMe = myPart;
				if(arraySize != -1) {
					size = arraySize;
					lastBytePart.countMe = null;
				}
				bytePart.children.add(myPart);
				if(entry.type.equals("u1")) {
					myPart.bufferSize = size;
					myPart.buffer = new byte[size];
					startIndex = myPart.fill(buffer, startIndex);
					if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.codeIndex) {
						TypeInfo newTypes =TypeInfo.getType("Code_attribute");
						myPart.decompiled= true;
						bytePart.name = "CodeAttribute";
					
						newTypes.decompileMe(myPart, 0);
					}
					else if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.localVariableIndex) {
						TypeInfo newTypes =TypeInfo.getType("LocalVariableTable_attribute");
						myPart.decompiled= true;
					
						bytePart.name = "LocalVariableTable";
						newTypes.decompileMe(myPart, 0);
					}
					else if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.localVariableTypeIndex) {
						TypeInfo newTypes =TypeInfo.getType("LocalVariableTypeTable_attribute");
						myPart.decompiled= true;
						
						bytePart.name = "LocalVariableTypeTable";
						newTypes.decompileMe(myPart, 0);
					}
					else if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.exceptionsIndex) {
						TypeInfo newTypes =TypeInfo.getType("Exceptions_attribute");
						myPart.decompiled= true;
						bytePart.name = "Exceptions";
						
						newTypes.decompileMe(myPart, 0);
					}
					else if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.stackMapIndex) {
						TypeInfo newTypes =TypeInfo.getType("StackMapTable_attribute");
						myPart.decompiled= true;
						bytePart.name = "StackMapTable_attribute";
						
						newTypes.decompileMe(myPart, 0);
					}
					else if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.lineNumberIndex) {
						TypeInfo newTypes =TypeInfo.getType("LineNumberTable_attribute");
						myPart.decompiled= true;
						bytePart.name = "LineNumberTable_attribute";
						
						newTypes.decompileMe(myPart, 0);
					}
					else if(attributeNameIndex != -1 && attributeNameIndex == ConstEntry2.signatureIndex) {
						bytePart.name = "Signature";
						
						
						
					}
				
					continue;
				}
				if(entry.type.equals("u2") && size == 0) {
					myPart.bufferSize = -1;
				}
				if(entry.countOffSet > 0) {
					int debugME =0;
				}
				if(entry.countOffSet == 1) {
					int debugME =0;
					size -= entry.countOffSet;
				}else if(entry.countOffSet > 1) {
					entry.countOffSet = 0;
				}
				
				myPart.isArray = true;
				if(entry.name.equals("interfaces")) {
					myPart.bufferSize = -1;
				}
				myPart.children.clear();
				for(int x =0; x < size ; x++) {
					if(x > 822) {
						int debugMe =0;
					}
					if(entry.name.equals("constant_pool")) {
						int tagInfo = buffer[startIndex];
						String constStr = ConstEntry2.getTypeString(tagInfo);
						TypeInfo newTypes =TypeInfo.getType(constStr +"_info");
						
						startIndex = newTypes.decompile(myPart, buffer, startIndex);
						if (tagInfo == ConstEntry2.CONSTANT_Double || tagInfo == ConstEntry2.CONSTANT_Long) {
							
							x++;
						}
					}else {
						startIndex = entry.decompile(myPart, buffer, startIndex);
					}
					
					
				}
				if(entry.name.equals("constant_pool")) {
					int index =1;
					ConstEntry2.initConsts(myPart);
					
				}
			}else {
				
			   BytePart myPart = new BytePart(bytePart,entry,startIndex);
				bytePart.children.add(myPart);
				
			  startIndex = entry.decompileMe(myPart, buffer, startIndex);
			  if(entry.name.equals("attribute_name_index")) {
				  attributeNameIndex = myPart.bufAsInt();
			  }
			}
			
		}
		return startIndex;
	}
}
