package oliver;

import java.util.ArrayList;

public class UnionDescription extends TypeInfo{

	
	
	public UnionDescription(String name, String type) {
		super(name, type);
		// TODO Auto-generated constructor stub
	}

	ArrayList<String> unionTypes = new ArrayList<String>();
	
	
	
	public TypeInfo  getActualType (byte [] buff, int bufIndex){
		
		int typeUnion = buff[bufIndex] &0xff;
		for(TypeInfo child : this.children) {
			
			TypeInfo actualType = TypeInfo.getType(child.name);
			int lowerRange = actualType.children.get(0).lowerRange;
			int upperRange = actualType.children.get(0).upperRange;
			if(typeUnion >= lowerRange && typeUnion <= upperRange) {
				return actualType;
			}	
		}
		return null;
	}
	
	public String getActualType() {
		
		if(this.type.equals("Integer_variable_info")) {
			return "int";
		}
		
		return "object";
	}
	public static ArrayList<Variable> getVariables(BytePart stackMap){
		
		for(BytePart child : stackMap.children) {
			
			if(child.name.equals("same_frame")) {
				
			}else if(child.name.equals("full_frame")) {
				
				
			
				
				
				
			}else if(child.name.equals("same_locals_1_stack_item_frame")) {
				
			}else if(child.name.equals("same_locals_1_stack_item_frame_extended")) {
				
			}else if(child.name.equals("chop_frame")) {
				
			}else if(child.name.equals("same_frame_extended")) {
				
			}else if(child.name.equals("append_frame")) {
				
			}else {
				throw new RuntimeException("Unsupprted type" + child.name);
			}
		}
		/*
		 * 
		 * same_frame;
    same_locals_1_stack_item_frame;
    same_locals_1_stack_item_frame_extended;
    chop_frame;
    same_frame_extended;
    append_frame;
    full_frame;
		 */
		return null;
	}
}
