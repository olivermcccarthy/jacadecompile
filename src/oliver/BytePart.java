package oliver;

import java.util.ArrayList;

import java.util.Collections;
/**
 * Section of a class. Example constant pool.
 * Constant pool will then have children contant pool entries 
 * @author OLIVERMCCARTHY
 *
 */
public class BytePart {

	public byte[] buffer = new byte[0];

	public ArrayList<BytePart> children = new ArrayList<BytePart>();
	public String name;
	public int bufferSize = -1;
	public TypeInfo typeInfo;

	public boolean isArray;

	public boolean decompiled;

	int startIndex;

	public BytePart countMe;
	public BytePart parent;

	public boolean isStackMap;

	public boolean manipulated = false;

	public int funnyCount = 0;

	public static int uniqueID =0;
	
	int myUniqueID =uniqueID++;;
	public void createChildren() {
		if (children.size() == 0) {
			for (TypeInfo tf : typeInfo.children) {
				BytePart child = new BytePart(this, tf, 0);
				child.parent = this;
				this.children.add(child);
			}
		}
	}

	public BytePart(BytePart parent, TypeInfo typeInfo, int startIndex) {
		if(myUniqueID == 75170) {
			int debugME =0;
		}
		this.name = typeInfo.name;
		this.bufferSize = typeInfo.bufferSize;
		this.parent = parent;
		if (bufferSize > 0) {
			buffer = new byte[bufferSize];

		}

		this.typeInfo = typeInfo;
		this.startIndex = startIndex;
		// createChildren();
	}

	public BytePart(BytePart parent, BytePart copy) {
		this.name = copy.name;
		this.bufferSize = copy.bufferSize;
		this.parent = parent;
		if (bufferSize > 0) {
			buffer = new byte[bufferSize];
			for (int x = 0; x < bufferSize; x++) {
				buffer[x] = copy.buffer[x];
			}
		}

		copy.typeInfo = typeInfo;
		copy.startIndex = startIndex;
		for (BytePart copyChild : copy.children) {
			this.children.add(new BytePart(this, copyChild));
		}
		// createChildren();
	}

	public int fill(byte[] buffer, int startIndex) {
		if (bufferSize < 1) {
			return startIndex;
		}
		for (int x = 0; x < bufferSize; x++) {
			this.buffer[x] = buffer[x + startIndex];
		}
		return startIndex + this.bufferSize;
	}

	public int count() {
		if (this.bufferSize > 0) {

			return bufferSize;
		}
		int offSet = 0;
		if (this.typeInfo.countOffSet > 0) {
			int debugME = 0;
			offSet = this.typeInfo.countOffSet;
		}
		offSet += this.funnyCount;
		return this.children.size() + offSet;
	}

	public int countBytes() {
		int countB = 0;
		if (this.children.size() > 0) {
			for (BytePart ch : this.children) {
				countB += ch.countBytes();
			}

			return countB;
		}
		if (bufferSize == -1) {
			return 0;
		}
		return this.bufferSize;
	}

	public void updateLocalVariable(int newIndex, int indexLength) {
		for (BytePart localVarPart : children) {

			int startPC = localVarPart.getChild("start_pc").bufAsInt();

			int lengthPC = localVarPart.getChild("length").bufAsInt();

			if (startPC + lengthPC < newIndex) {

			} else {
				if (startPC < newIndex && (startPC + lengthPC) >= newIndex) {
					int newlength = lengthPC + indexLength;
					localVarPart.getChild("length").intToBuf(newlength);
				} else {
					int newStartPC = startPC + indexLength;
					localVarPart.getChild("start_pc").intToBuf(newStartPC);

				}

			}

		}
	}

	public int write(byte[] buffer, int startIndex) {
		if (this instanceof VariableBytePart) {
			int debugME = 0;
		}
		int startStart = startIndex;
		if (startIndex > 2790) {
			int debugME = 0;
		}

		if (name.equalsIgnoreCase("XXXX")) {
			int debugMe = 0;
		}
		if (countMe != null) {
			int countMER = countMe.count();

			this.intToBuf(countMER);
		}
		if (this.children.size() > 0) {
			int attrStartIndex = 0;
			int attrlength = 0;
			BytePart attrLengthBPart = null;
			for (BytePart ch : this.children) {
				if (ch.name.equals("attribute_length")) {
					attrStartIndex = startIndex;
					attrLengthBPart = ch;
				}
				if (ch.name.equals("info")) {
					int endIndex = ch.write(buffer, startIndex);
					attrlength = endIndex - startIndex;
					startIndex = endIndex;
				} else {
					startIndex = ch.write(buffer, startIndex);
				}
			}
			if (attrLengthBPart != null) {
				attrLengthBPart.intToBuf(attrlength);
				attrLengthBPart.countMe = null;
				attrLengthBPart.write(buffer, attrStartIndex);
			}
			int diff = startIndex - startStart;
			if (this.isStackMap && diff != this.bufferSize) {
				int debugME = 0;
			}
			return startIndex;
		}
		if (this.bufferSize == -1) {
			return startIndex;
		}

		for (int x = 0; x < bufferSize; x++) {
			buffer[x + startIndex] = this.buffer[x];
		}

		return startIndex + this.bufferSize;
	}

	public String toString() {
		return name;
	}

	public BytePart getChild(String typeLookFor) {
		for (BytePart child : this.children) {
			if (child.name.equals(typeLookFor)) {
				return child;
			}
		}
		return null;
	}

	public int intToBuf(int val) {
		int i2 = 0;

		for (int t = buffer.length - 1; t >= 0; t--) {
			buffer[t] = (byte) (val & 0xff);
			val = val >> 8;
		}
		return i2;
	}

	public int bufAsInt() {
		int i2 = 0;

		for (int t = 0; t < buffer.length; t++) {
			int it = buffer[t] & 0xff;
			i2 = i2 << 8;
			i2 = i2 | it;
		}

		return i2;
	}

	void stringToBuf(String newVal) {
		buffer = newVal.getBytes();
		this.bufferSize = buffer.length;
	}

	String bufAsString() {
		if (buffer == null) {
			return "";
		}
		return new String(buffer);
	}
}
