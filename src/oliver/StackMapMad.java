package oliver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class StackMapMad implements Comparable<StackMapMad>{

	int index;

	ArrayList<Type> types = new ArrayList<Type>();
	ArrayList<Type> stackTypes = new ArrayList<Type>();
	BytePart myPart;
	public String toString() {
		return String.format(" %d %s LOCAL= %s STACK = %s", index, type, types, stackTypes);
	}

	static class Type {
		int type;
		String newType;
		private int cpool_index;
		private int offset;
		BytePart myPart;
		public BytePart write(BytePart parent) {
			if(myPart == null) {
				 myPart = new BytePart(parent,TypeInfo.getType(this.newType),0);
				 parent.children.add(myPart);
				 myPart.createChildren();
			}
			
			
			
			for (BytePart child : myPart.children) {
				
			
				if (child.name.equals("tag")) {
					child.intToBuf(this.type);
				} else if (child.name.equals("offset")) {
					child.intToBuf(this.offset);
				} else if (child.name.equals("cpool_index")) {
					child.intToBuf(this.cpool_index);
				}

			}
			return myPart;
		}

		public String toString() {
			return String.format(" %d %s", type, newType);
		}

		public Type(int tyeOFFrame,BytePart myPart) {
			super();
			this.myPart = myPart;
			this.type = tyeOFFrame;

			if (tyeOFFrame == 0) {
				newType = "Top_variable_info";
			} else if (tyeOFFrame == 1) {
				newType = "Integer_variable_info";
			} else if (tyeOFFrame == 2) {
				newType = "Float_variable_info";
			} else if (tyeOFFrame == 3) {
				newType = "Double_variable_info";
			} else if (tyeOFFrame == 4) {
				newType = "Long_variable_info";
			} else if (tyeOFFrame == 5) {
				newType = "Null_variable_info";
			} else if (tyeOFFrame == 6) {
				newType = "UninitializedThis_variable_info";
			} else if (tyeOFFrame == 7) {
				newType = "Object_variable_info";
			} else if (tyeOFFrame == 8) {
				newType = "Uninitialized_variable_info";
			}

		}

	}

	String type;
	int typeAsInt = 0;
	int offSetDelta = 0;

	public StackMapMad(int index, int typeAsInt, String type, BytePart myPart) {
		super();
		this.index = index;
		this.type = type;
		this.typeAsInt = typeAsInt;
		this.myPart = myPart;
	}

	public void write(BytePart parent) {
		if(myPart == null) {
			 myPart = new BytePart(parent,TypeInfo.getType(this.type),0);
			 myPart.createChildren();
			 parent.children.add(myPart);
			 myPart.parent = parent;
			
		}
	
		
		/*
		 * u1 frame_type = FULL_FRAME; u2 offset_delta; u2 number_of_locals;
		 * verification_type_info locals[number_of_locals]; u2 number_of_stack_items;
		 * verification_type_info stack[number_of_stack_items];
		 */
		for (BytePart child : myPart.children) {
			
			
			if (child.name.equals("frame_type")) {
				child.intToBuf(this.typeAsInt);
			} else if (child.name.equals("offset_delta")) {
				child.intToBuf(this.offSetDelta);
			} else if (child.name.equals("number_of_locals")) {
				child.intToBuf(this.types.size());
			} else if (child.name.equals("locals")) {
				for(Type chType : this.types) {
					chType.write(child);
				}
				
			} else if (child.name.equals("number_of_stack_items")) {
				child.intToBuf(this.types.size());
			} else if (child.name.equals("stack")) {
				for(Type chType : this.stackTypes) {
					chType.write(child);
				}
			}

		}
		
	}

	static HashMap<String, ArrayList<StackMapMad>> stackMaps = new HashMap<String, ArrayList<StackMapMad>>();

	static public  ArrayList<StackMapMad> processStackMap(String methodName, BytePart bytePart) {
		int stackIndex = 0;

		
		ArrayList<StackMapMad> methodStackMap = new ArrayList<StackMapMad>();
		stackMaps.put(methodName, methodStackMap);

		ArrayList<StackMapMad> stackMap = new ArrayList<StackMapMad>();
		StackMapMad previousStackMap = new StackMapMad(0, 0, "",null);
		for (BytePart stackEntry : bytePart.children) {

			int tyeOFFrame = stackEntry.getChild("frame_type").bufAsInt();
			int origTypeOfFrame = tyeOFFrame;
			int offSetDeltaInt =0;
			BytePart offSetDelata = stackEntry.getChild("offset_delta");
			if (offSetDelata != null) {
				if (stackIndex > 0) {
					stackIndex++;
				}
				offSetDeltaInt = offSetDelata.bufAsInt();
				stackIndex += offSetDelata.bufAsInt();
			} else {
				if (tyeOFFrame > 63) {
					tyeOFFrame -= 64;
				}
				if (stackIndex > 0) {
					stackIndex++;
				}
				stackIndex += tyeOFFrame;

			}

			String newType = "";
			BytePart locals = stackEntry.getChild("locals");
			ArrayList<Type> localLocalTypes = new ArrayList<Type>();
			ArrayList<Type> localStackTypes = new ArrayList<Type>();
			if (locals != null) {
				for (BytePart localsChild : locals.children) {
					Type nType = new Type(localsChild.getChild("tag").bufAsInt(),localsChild);
					localLocalTypes.add(nType);
					if (localsChild.getChild("offset") != null) {
						nType.offset = localsChild.getChild("offset").bufAsInt();
					}
					if (localsChild.getChild("cpool_index") != null) {
						nType.cpool_index = localsChild.getChild("cpool_index").bufAsInt();
					}
				}
			}
			locals = stackEntry.getChild("stack");

			if (locals != null) {
				for (BytePart localsChild : locals.children) {
					Type nType = new Type(localsChild.getChild("tag").bufAsInt(),localsChild);
					localStackTypes.add(nType);
					if (localsChild.getChild("offset") != null) {
						nType.offset = localsChild.getChild("offset").bufAsInt();
					}
					if (localsChild.getChild("cpool_index") != null) {
						nType.cpool_index = localsChild.getChild("cpool_index").bufAsInt();
					}
				}
			}
			StackMapMad newStackMapMad = new StackMapMad(stackIndex, origTypeOfFrame, "",stackEntry);
			newStackMapMad.offSetDelta = offSetDeltaInt;
			if (tyeOFFrame < 64) {
				newType = "same_frame";

				newStackMapMad.types.addAll(previousStackMap.types);

			} else if (tyeOFFrame < 128) {
				newType = "same_locals_1_stack_item_frame";
				newStackMapMad.types.addAll(previousStackMap.types);

				newStackMapMad.stackTypes.addAll(localStackTypes);

			} else if (tyeOFFrame == 247) {
				newType = "same_locals_1_stack_item_frame_extended";
				newStackMapMad.types.addAll(previousStackMap.types);
				newStackMapMad.stackTypes.addAll(localStackTypes);

			} else if (tyeOFFrame < 251) {
				newType = "chop_frame";
				int localsToremove = 251 - tyeOFFrame;
				newStackMapMad.types.addAll(previousStackMap.types);
				while (localsToremove > 0) {
					newStackMapMad.types.remove(newStackMapMad.types.size() - 1);
					localsToremove--;
				}
			}

			else if (tyeOFFrame < 252) {
				newType = "same_frame_extended";
				newStackMapMad.types.addAll(previousStackMap.types);
			} else if (tyeOFFrame < 255) {

				newStackMapMad.types.addAll(previousStackMap.types);
				newStackMapMad.stackTypes.addAll(previousStackMap.stackTypes);
				newStackMapMad.types.addAll(localLocalTypes);
				newStackMapMad.stackTypes.addAll(localStackTypes);

				newType = "append_frame";

			} else if (tyeOFFrame < 256) {
				newType = "full_frame";
				newStackMapMad.types.addAll(localLocalTypes);
				newStackMapMad.stackTypes.addAll(localStackTypes);
			}
			previousStackMap = newStackMapMad;
			methodStackMap.add(newStackMapMad);
			newStackMapMad.write(bytePart);
			newStackMapMad.type = newType;
		}
		/**
		 * TEST
		byte[] newbuff = new byte[200];
		int sizeNew = bytePart.parent.write(newbuff, 0);
		for(int test =0; test < testArr.length; test ++) {
			if(newbuff[test] != testArr[test]) {
				int debugME = 0;
			}
		}
		**/
		int debugME = 0;
		return methodStackMap;
	}
	public static void updateStackMap(BytePart parent, int newIndex, int indexLength, boolean extend) {
		
		int debugME =0;
		int stackIndex =0;
		
		int newIndex2 = 0;
		for ( BytePart stackEntry : parent.children) {
		
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
			if(stackIndex > newIndex) {
				
				if(offSetDelata != null) {
					int offSet = offSetDelata.bufAsInt();
					  if(!extend) {
						  offSet -= indexLength; 
					  }else {
						  offSet += indexLength;
					  }
					
					offSetDelata.intToBuf(offSet);
				}else {
					  if(!extend) {
						  frType -= indexLength;
					  }else {
						  frType += indexLength;
					  }
					
					stackEntry.getChild("frame_type").intToBuf(frType);
				}
                if(!extend) {
					
					newIndex2 = stackIndex -1;
					break;
				}
				int deh =0;
				break;
			}
			newIndex2++;
		}
		
	
		if(!extend) {
		   ArrayList<StackMapMad> existingList = StackMapMad.processStackMap("xxx", parent);
		   
		   StackMapMad newMap = new  StackMapMad(newIndex,indexLength,"same_frame", null);
		   existingList.add(newMap);
		   
		   Collections.sort(existingList);
		   
		   parent.children.clear();
		  for (int existI =0; existI < existingList.size(); existI++) {
			  StackMapMad existingMad = existingList.get(existI);
			  existingMad.myPart = null;
			  existingMad.write(parent);
		  }
		   int debugME2 =0;
		}
		
		
}
	@Override
	public int compareTo(StackMapMad o) {
		// TODO Auto-generated method stub
		return  this.index - o.index ;
	}
}
