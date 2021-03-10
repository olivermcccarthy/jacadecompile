package oliver;

public class VariableBytePart extends BytePart {

	
	public VariableBytePart(BytePart parent, String name, String descriptor, boolean isArray, int startPc, int length, int index) {
		super(parent,TypeInfo.getType("local_variable_table"),0);
		parent.children.add(this);
		this.parent = parent;
		int stringIndex = ConstEntry2.addNewUTF8(name);
		String  primType = Variable.getPrimativeType(descriptor);
		if(isArray) {
			primType = "["+primType;
		}
		int primIndex = ConstEntry2.addNewUTF8(primType);
		
		this.createChildren();
		//start_pc, length, name_index, descriptor_index, index
		this.getChild("start_pc").intToBuf(startPc);
		this.getChild("length").intToBuf(length);
		this.getChild("name_index").intToBuf(stringIndex);
		this.getChild("descriptor_index").intToBuf(primIndex);
		this.getChild("index").intToBuf(index);
	}
}
