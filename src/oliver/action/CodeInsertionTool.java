package oliver.action;

import java.util.ArrayList;
import java.util.List;

import oliver.BytePart;
import oliver.StackMapMad;
import oliver.TypeInfo;

public class CodeInsertionTool {

	ArrayList<Integer> insertAtPoints = new ArrayList<Integer>();
	ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
	ArrayList<Integer> timeCounters = new ArrayList<Integer>();
	ArrayList<Integer> hitCounters = new ArrayList<Integer>();
	byte[] buffer = null;
	int codeSize = 0;
	byte[] initBuffer; int initialSize;
	PopulateMe populateMe;

	BytePart codeBytePart ;
	public CodeInsertionTool(String className, String methodName,BytePart codeBytePart,List<Action> gotos,  byte[] codeBlock, int codeSize, byte[] initBuffer, int initialSize) {
		this.buffer = codeBlock;
		this.codeBytePart = codeBytePart;
		this.codeSize = codeSize;
		for (Action act : gotos) {
			insertAtPoints.add(act.getPc());
		}
		this.initBuffer = initBuffer;
		this.initialSize = initialSize;
		
		 getLineNumbers(className);
			for(int lineNumber: this.lineNumbers) {
				String newFiledName=  methodName+"BigCountTime_"+lineNumber;
				String newFiledName2=  methodName+"BigCountHit_"+ lineNumber;
				int newCounter= Instrument.newField(codeBytePart, className,newFiledName,"long", "public static", false);
				this.timeCounters.add(newCounter);
				int newCounter3= Instrument.newField(codeBytePart, className,newFiledName2,"int", "public static", false);
				this.hitCounters.add(newCounter3);
			}
	}
	  void getLineNumbers(String classname) {
	    	BytePart lineNumberTable = codeBytePart.parent.getChild("attributes").getChild("LineNumberTable_attribute");
	    	if (lineNumberTable != null) {
				lineNumberTable = lineNumberTable.getChild("info").getChild("entries");
				
				int count = 0;
				int firstValue = this.insertAtPoints.get(count);
				for ( BytePart child : lineNumberTable.children) {
					int pc = child.getChild("start_pc").bufAsInt();
					pc += initialSize;
					int lineNumber = child.getChild("line_number").bufAsInt();
					int ff =0;
                 if(pc >= firstValue ) {
					 this.lineNumbers.add(lineNumber);
					 count ++;
					 if(count < this.insertAtPoints.size()) {
						 firstValue = this.insertAtPoints.get(count);
						 while(pc >= firstValue) {
							 int debugME =0;
							 this.insertAtPoints.remove(count);
							 count ++;
							 if(count < this.insertAtPoints.size()) {
								 firstValue = this.insertAtPoints.get(count);
							 }else {
								 break;
							 }
						 }
					 }else {
						 break;
					 }
					}
				}
				while(this.lineNumbers.size() < this.insertAtPoints.size()) {
					this.insertAtPoints.remove(this.insertAtPoints.size()-1);
				}
			}
	    }
	int getNumJumps(int startPC, int endPC) {
		boolean backwards = false;
		if (startPC > endPC) {
			int hold = endPC;
			endPC = startPC;
			startPC = hold;
			backwards = true;
		}
		int firstAct = insertAtPoints.get(0);
		int x = 0;
		int addMe = 0;

		for (; x < insertAtPoints.size(); x++) {
			firstAct = insertAtPoints.get(x);
			if (firstAct > startPC) {
				break;
			}
		}

		int y = x;
		for (; y < insertAtPoints.size(); y++) {
			firstAct = insertAtPoints.get(y);
			if (firstAct > endPC) {
				break;
			}
		}
		if (backwards) {
			return x - y + addMe;
		}
		return y - x + addMe;
	}

	void insertNewCode(String className, BytePart bPart, int newLocalIndex) {

		fixGotTos(bPart);
		fixLocalVariable(bPart);
		fixStackMap(className, bPart, newLocalIndex);
		fixExceptionTable( bPart,   initialSize);
		bPart.buffer = updateCode(bPart.buffer);
		bPart.bufferSize = bPart.buffer.length;
	}

	void fixExceptionTable(BytePart bPart,  int initialJump) {
		BytePart localVaraiableTable = bPart.parent.getChild("exception_table");
		if (localVaraiableTable != null) {

			updateExceptionTable(localVaraiableTable, initialJump);

		}

	}

	void fixLocalVariable(BytePart bPart) {
		BytePart localVaraiableTable = bPart.parent.getChild("attributes").getChild("LocalVariableTable");
		if (localVaraiableTable != null) {
			localVaraiableTable = localVaraiableTable.getChild("info").getChild("table");
			updateLocalVariable(localVaraiableTable);

		}

	}

	void fixStackMap(String classname, BytePart bPart, int newLocalIndex) {
		BytePart stackMapTable = bPart.parent.getChild("attributes").getChild("StackMapTable_attribute");
		if (stackMapTable != null) {
			stackMapTable = stackMapTable.getChild("info").getChild("entries");
			updateStackMap(classname, bPart, stackMapTable, newLocalIndex);

		}
	}
   void fixGotTos(BytePart bPart) {
		
		ArrayList<Action> all =Instrument.getGotTos(bPart);
		for (int x = 0; x < all.size(); x++) {
			Action firstAct = all.get(x);
			int firstGoto = firstAct.getGoToLabel();
			int firstFrom = firstAct.getPc();
			
			int numJumps = this.getNumJumps(firstFrom, firstGoto);

			if (numJumps != 0) {
				int goTo = Instrument.getSignedIndex(bPart.buffer, firstAct.getPc());

				int diff = numJumps * this.codeSize;
				goTo += diff;
				Instrument.writeSignedIndex(bPart.buffer, firstAct.getPc(), goTo);

			}

		}

	}
	/**
	 * Insert the block of code in src at each of the insertionPoints in The Code
	 * Byte Array. This will make a new array = size of initialArray + ( Num points
	 * * size of new code)
	 * 
	 * @param src
	 * @return
	 */
	byte[] updateCode(byte[] src) {
		byte[] newArray = new byte[src.length + (this.codeSize * this.insertAtPoints.size() + this.initialSize)];
		int x = 0;
		int toX = 0;
		for(toX=0; toX < this.initialSize; toX++) {
			newArray[toX] = this.initBuffer[toX];
		}
		for (int goi = 0; goi < this.insertAtPoints.size(); goi++) {
			int index = this.insertAtPoints.get(goi);
			if (this.populateMe != null) {
				this.populateMe.populateMe(this, goi);
			}

			for (; x < index; x++, toX++) {
				newArray[toX] = src[x];
			}
			for (int y = 0; y < this.codeSize; y++, toX++) {
				newArray[toX] = this.buffer[y];
			}

		}
		for (; x < src.length; x++, toX++) {
			newArray[toX] = src[x];
		}
		return newArray;

	}

	void fixGotTos(BytePart bPart, CodeInsertionTool newCode) {

		ArrayList<Action> all = Instrument.getGotTos(bPart);
		for (int x = 0; x < all.size(); x++) {
			Action firstAct = all.get(x);
			int firstGoto = firstAct.getGoToLabel();
			int firstFrom = firstAct.getPc();

			int numJumps = this.getNumJumps(firstFrom, firstGoto);

			if (numJumps != 0) {
				int goTo = Instrument.getSignedIndex(bPart.buffer, firstAct.getPc());

				int diff = numJumps * this.codeSize;
				goTo += diff;
				Instrument.writeSignedIndex(bPart.buffer, firstAct.getPc(), goTo);

			}

		}

	}

	void updateLocalVariable(BytePart localVar) {
		for (BytePart localVarPart : localVar.children) {

			int startPC = localVarPart.getChild("start_pc").bufAsInt();

			
			int lengthPC = localVarPart.getChild("length").bufAsInt();
           if(startPC == 0) {
	          localVarPart.getChild("length").intToBuf(lengthPC +this.initialSize );
			}else {
				localVarPart.getChild("start_pc").intToBuf(startPC +this.initialSize );
			}
			int numJumps = this.getNumJumps(startPC, startPC + lengthPC);

			if (numJumps != 0) {

				int diff = numJumps * this.codeSize;
				lengthPC += diff;
				if(startPC == 0) {
					lengthPC += this.initialSize;
				}
				localVarPart.getChild("length").intToBuf(lengthPC);

			}
			numJumps = this.getNumJumps(-1, startPC);
			if (numJumps != 0) {

				int diff = numJumps * this.codeSize;
				startPC += diff + this.initialSize;
				localVarPart.getChild("start_pc").intToBuf(startPC);

			}

		}
	}

	void updateExceptionTable(BytePart localVar, int initialJump) {
		for (BytePart localVarPart : localVar.children) {

			for (String pcSr : new String[] { "start_pc", "end_pc", "handler_pc" }) {
				int oldVal = localVarPart.getChild(pcSr).bufAsInt();
				oldVal += initialJump;
				int numJumps = this.getNumJumps(-1, oldVal);
				if (numJumps != 0) {

					int diff = numJumps * this.codeSize;
					oldVal += diff;
					localVarPart.getChild(pcSr).intToBuf(oldVal);

				} else {

					localVarPart.getChild(pcSr).intToBuf(oldVal);
				}
			}

		}
	}

	void updateStackMap(String className, BytePart codeBytePart, BytePart parent, int newLocalIndex) {

		int stackIndex = 0;
		// ArrayList<StackMapMad> ff = StackMapMad.processStackMap("ddd", parent);
		int newIndex2 = 0;

		int count = 0;
		
		int diff = 0;
		BytePart otherLocals = null;
		BytePart zeroEntry = parent.children.get(0);
		if (zeroEntry.getChild("locals") == null) {
			BytePart fullFrameStart = Instrument.getMethodStackMap(className, codeBytePart);
			BytePart dummyLocals = new BytePart(zeroEntry, fullFrameStart.getChild("locals").typeInfo, 0);
			dummyLocals.name = "locals";
			zeroEntry.children.add(dummyLocals);
		}
		for (BytePart stackEntry : parent.children) {

			if (count == 3) {
				int gg = 0;
			}
			count++;
			int frType = stackEntry.getChild("frame_type").bufAsInt();
			BytePart offSetDelata = stackEntry.getChild("offset_delta");
			if (offSetDelata != null) {
				if (stackIndex > 0) {
					stackIndex++;
				}
				stackIndex += offSetDelata.bufAsInt();
			} else {
				if (frType > 63) {
					frType -= 64;
				}
				if (stackIndex > 0) {
					stackIndex++;
				}

				stackIndex += frType;

			}
			int numJumps = this.getNumJumps(newIndex2 - 1, stackIndex);
			diff = 0;
			if (numJumps > 0) {
				diff = numJumps * this.codeSize;
				if (offSetDelata != null) {
					int offSet = offSetDelata.bufAsInt();

					offSet += diff;

					offSetDelata.intToBuf(offSet);
				} else {

					int oldFrType = frType;
					frType += diff;

					if (oldFrType < 64 && frType >= 64) {
						int debfugME = 0;
						TypeInfo useExtend = TypeInfo.getType("same_frame_extended");
						stackEntry.children.clear();
						stackEntry.typeInfo = useExtend;
						stackEntry.createChildren();
						if (count == 1) {
							BytePart fullFrameStart = Instrument.getMethodStackMap(className, codeBytePart);
							BytePart dummyLocals = new BytePart(stackEntry, fullFrameStart.getChild("locals").typeInfo,
									0);
							dummyLocals.name = "locals";
							stackEntry.children.add(dummyLocals);
						}
						stackEntry.getChild("offset_delta").intToBuf(frType);
						stackEntry.getChild("frame_type").intToBuf(251);
					} else {

						stackEntry.getChild("frame_type").intToBuf(frType);
					}
				}

			}
			BytePart locals = stackEntry.getChild("locals");
			if (locals != null) {

				if (frType != 255) {
					int offSet = stackEntry.getChild("frame_type").bufAsInt();
					BytePart offSetDelta = stackEntry.getChild("offset_delta");
					if (offSetDelta != null) {
						offSet = stackEntry.getChild("offset_delta").bufAsInt();
					}
					if(count == 1) {
						offSet += this.initialSize;
					}
					stackEntry.children.clear();
					int offSet2 = stackIndex - newIndex2;

					stackEntry.manipulated = true;
					BytePart fullFrameStart = Instrument.getMethodStackMap(className, codeBytePart);
					for (BytePart partOfFullFrame : fullFrameStart.children) {
						stackEntry.children.add(new BytePart(stackEntry, partOfFullFrame));
					}
					stackEntry.getChild("frame_type").intToBuf(255);
					stackEntry.getChild("offset_delta").intToBuf(offSet);
					if (otherLocals == null) {
						otherLocals = locals;
					} else {
						otherLocals.children.addAll(locals.children);
					}

					stackEntry.getChild("locals").children.addAll(otherLocals.children);
					int size = Instrument.getStackSize(stackEntry);
					int numTops =0;
					for (int y = size; y < newLocalIndex; y++) {
						BytePart valBytePrt = Instrument.newVerificationType(locals, className, "top");
						stackEntry.getChild("locals").children.add(valBytePrt);
						numTops ++;
					}
                    if(numTops == 0) {
                    	int debugME =0;
                    }
					BytePart valBytePrt = Instrument.newVerificationType(locals, className, "long");
					stackEntry.getChild("locals").children.add(valBytePrt);
					stackEntry.getChild("number_of_locals").intToBuf(stackEntry.getChild("locals").children.size());
					stackEntry.name = "ggggg" + count;
				} else if (stackEntry.manipulated == false) {
					int size = Instrument.getStackSize(stackEntry);
					// fill tthe gap wit tops
					int numTops =0;
					for (int y = size; y < newLocalIndex; y++) {
						BytePart valBytePrt = Instrument.newVerificationType(locals, className, "top");
						stackEntry.getChild("locals").children.add(valBytePrt);
						numTops ++;
					}
					 if(numTops == 0) {
	                    	int debugME =0;
	                    }
					BytePart valBytePrt = Instrument.newVerificationType(locals, className, "long");
					stackEntry.getChild("locals").children.add(valBytePrt);
					stackEntry.getChild("number_of_locals").intToBuf(stackEntry.getChild("locals").children.size());
					stackEntry.manipulated = true;
					int offSet = stackEntry.getChild("offset_delta").bufAsInt();
					if(count == 1) {
						offSet += this.initialSize;
						stackEntry.getChild("offset_delta").intToBuf(offSet);
					}
					
				  
				
				}
			}
			newIndex2 = stackIndex;
		}

		
		byte [] nrr = new byte[200];
		//int newSize = parent.write(nrr, 0);
		//StackMapMad.processStackMap("sss", parent);
	}
}