package decompile;

import java.util.ArrayList;
import java.util.List;

import decompile.ByteCode.TYPE;
import oliver.ConstEntry2;

public class JavaAction implements Comparable<JavaAction>{
	
	public int whileTo = -1;

	public ByteCode.TYPE type;
	
	public int index;
	
	public String returnType;
	
	public String value;
	public String valueLeft;
	public String valueRight;
	public String ifCmp;
	public int goTo =-1;

	public int goToLine = -1;

	public int line;

	public boolean printed = false;

	public boolean elseBreak = false;

	public int count =0;

	public JavaAction other;

	public static int nextID= 0;
	
	public int id = nextID++;

	public boolean funnyCatch;
	

	public JavaAction(JavaAction other, int index) {
		this.returnType = other.returnType;
		this.type = other.type;
		this.value = other.value;
		this.index = index;
		this.count = other.count;
		this.other = other;
		other.other = this;
	}
	
	public JavaAction(TYPE type, int index, String returnType, String value) {
		super();
		if(id == 5654) {
			int debugME =0;
		}
		this.type = type;
		if(index == 0) {
			int debugME =0;
		}
		if(value.contains("REPLACEME")) {
			int debugME =0;
		}
		if(index == 45) {
			int debugME =0;
		}
		if(value.contains("ignore")) {
			int debugME =0;
		}
		this.index = index;
		this.returnType = returnType;
		
		this.value = value;
	}
	public JavaAction(TYPE type, int index, String returnType, String valueLeft, String ifCmp, String valueRight) {
		super();
		this.type = type;
		
		if(index == 45) {
			int debugME =0;
		}
	
		this.index = index;
		this.returnType = returnType;
		this.value = valueLeft +" " + ifCmp +" " + valueRight;
		if(value.contains("action.type equals decompile.ByteCode$TYPE.CASE")) {
			int debugME =0;
		}
		this.valueLeft = valueLeft;
		if(valueLeft.contains(" == ")) {
			int debugME=0;
		}
		this.valueRight = valueRight;
		this.ifCmp = ifCmp;
	}
	public String toString () {
		return value;
	}

	@Override
	public int compareTo(JavaAction o) {
		if(value.contains("newVars.add(varName)")) {
			int debugME =0;
		}
		return this.index - o.index;
	}

	public String getEnding() {
		switch (this.type) {
	    	case  CASE:
	    	case  IF:
	    	case  SWITCH:
	    	case  TRY:
	    	case  CATCH:
	    	case  FINALLY:
	    	case  SYNCHRONIZED:
	    	return "";	
		}
		return ";";
	}
}
