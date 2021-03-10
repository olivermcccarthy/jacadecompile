package oliver;

public class FieldBytePart extends BytePart {

	
	public FieldBytePart(BytePart parent, String name, String descriptor, String accessFlags, boolean isArray) {
		super(parent,TypeInfo.getType("field_info"),0);
		parent.children.add(this);
		this.parent = parent;
		int stringIndex = ConstEntry2.addNewUTF8(name);
		
		String  primType = Variable.getPrimativeType(descriptor);
		if(isArray) {
			primType = "["+primType;
		}
		int primIndex = ConstEntry2.addNewUTF8(primType);
		int accessInt = Variable.getAccess(accessFlags);
		this.createChildren();
		this.getChild("access_flags").intToBuf(accessInt);
		this.getChild("name_index").intToBuf(stringIndex);
		this.getChild("descriptor_index").intToBuf(primIndex);
		
	}
}
