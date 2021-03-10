package decompile;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oliver.ConstEntry2;

public class ActionBranch {

	ArrayList<JavaAction> actions = new ArrayList<JavaAction>();

	List<JavaAction> goTos = new ArrayList<JavaAction>();
	public List<JavaAction> orWhiles = new ArrayList<JavaAction>();
	public List<JavaAction> ifs = new ArrayList<JavaAction>();

	ByteCode.TYPE type;

	ActionBranch parent = null;
	int startIndex = 0;

	public int elseEnd;
	public int goToLine;
	List<ActionBranch> children = new ArrayList<ActionBranch>();

	public void addChild(ActionBranch child) {
		children.add(child);
		child.parent = this;
	}

	public static void findGoTO(List<JavaAction> actions, int index) {

		JavaAction action = actions.get(index);
		int goTo = action.goTo;
		if (goTo < action.index) {
			for (int x = index - 1; x > 0; x--) {
				JavaAction testAction = actions.get(x);
				if (testAction.index < goTo) {
					action.goToLine = x + 1;
					return;
				}
			}
			action.goToLine = 0;
		} else {
			for (int x = index + 1; x < actions.size(); x++) {
				JavaAction testAction = actions.get(x);
				if (testAction.index >= goTo) {
					action.goToLine = x;
					return;
				}
			}
			action.goToLine = actions.size() - 1;
		}

	}

	public int whileTo = -1;
	public boolean isValid = true;
	static int branchCount = 1;
	int branchID = 0;

	private boolean ifContinue;

	private ActionBranch elseBranch;

	private boolean ifBreak;

	public ActionBranch() {
		branchID = branchCount++;
		if (branchID == 425) {
			int debuME = 0;
		}
	}

	public String reverseIf(JavaAction action) {
		String prevIf = action.ifCmp;
		prevIf = prevIf.trim();
		if (prevIf.equals("==")) {
			prevIf = prevIf.replace("==", "!=");
		} else if (prevIf.equals("!=")) {
			prevIf = prevIf.replace("!=", "==");
		} else if (prevIf.equals("<=")) {
			prevIf = prevIf.replace("<=", ">");
		} else if (prevIf.equals("<")) {
			prevIf = prevIf.replace("<", ">=");
		} else if (prevIf.equals(">")) {
			prevIf = prevIf.replace(">", "<=");
		} else if (prevIf.equals(">=")) {
			prevIf = prevIf.replace(">=", "<");
		} else if (prevIf.equals("equals")) {
			prevIf = prevIf.replace("equals", "notequals");
			if (!action.value.contains("\"")) {
				if (action.value.contains("$")) {
					prevIf = prevIf.replace("notequals", "!=");
				}
			}
		} else if (prevIf.equals("notequals")) {
			prevIf = prevIf.replace("notequals", "equals");
			if (!action.value.contains("\"")) {
				if (action.value.contains("$")) {
					prevIf = prevIf.replace("equals", " ==");
				}
			}
		}

		return action.valueLeft + " " + prevIf + action.valueRight;
	}

	public void printif(PrintStream outStream, String pad) {

		String ifStr = "if (";
		Collections.sort(this.ifs);
		boolean wasAnd = true;
		for (int x = 0; x < this.ifs.size(); x++) {
			JavaAction ifAction = ifs.get(x);
			ifAction.printed = true;
			if (x > 0) {
				if (wasAnd) {
					ifStr += " && ";
				} else {
					ifStr += " || ";
				}
			}
			if (ifAction.goToLine == goToLine) {

				wasAnd = true;
				ifStr += "( " + this.reverseIf(ifAction) + ")";

			} else {
				wasAnd = false;
				ifStr += "( " + ifAction.value + ")";

			}
		}
		ifStr += "){";
		println(outStream, pad + ifStr);
	}

	public void printDo(PrintStream outStream, String pad) {

		String whileStr = "while (";
		Collections.sort(this.orWhiles);
		boolean wasAnd = true;
		for (int x = 0; x < this.orWhiles.size(); x++) {
			JavaAction ifAction = orWhiles.get(x);
			ifAction.printed = true;
			if (x > 0) {
				if (wasAnd) {
					whileStr += " && ";
				} else {
					whileStr += " || ";
				}
			}
			if (ifAction.goToLine > ifAction.line) {

				wasAnd = true;
				whileStr += "( " + this.reverseIf(ifAction) + ")";

			} else {
				wasAnd = false;
				whileStr += "( " + ifAction.value + ")";

			}
		}
		if(this.orWhiles.size() ==0) {
			whileStr += "true";
		}
		whileStr += ") {";
		println(outStream, pad + whileStr);
	}

	public void printHeading(PrintStream outStream, String pad) {
		switch (type) {
		case DO:
			printDo(outStream, pad);
			break;
		case IF:
			printif(outStream, pad);
			break;
		case SWITCH:
		case TRY:
		
		case CATCH:
			println(outStream, pad + "{");
			break;
		case FINALLY:
			println(outStream, pad + "finally{");
			break;
		case ELSE:
			println(outStream, pad + " else {");
		}

	}

	public static void println(PrintStream outStream, String value) {
		if(value.contains("default:")) {
			int debugME =0;
		}
		outStream.println(value.replace("" + ConstEntry2.REPLACE_BACKSLASH, "\\\\")
				.replace("" + ConstEntry2.REPLACE_QUOTE, "\\\"").replace("" + ConstEntry2.REPLACE_NEWLINE, "\\n"));
	}

	public void printAll(PrintStream outStream, ArrayList<String> varsss, String pad, boolean staticInit) {

		
		if (!this.isValid) {
			return;
		}
		if(branchID == 1208) {
			int debugME =0;
		}
		println(outStream, pad + "// branch : " + branchID);

		ArrayList<String> newVars = new ArrayList<String>();
		ArrayList<String> varsAtThisLevel = new ArrayList<String>();
		varsAtThisLevel.addAll(varsss);
		Iterator<ActionBranch> childIter = children.iterator();
		ActionBranch nextChild = null;
		int childPos = -1;
		if (children.size() > 0) {
			nextChild = childIter.next();
			childPos = nextChild.startIndex;
		}
        boolean wasSync = false;
		for (int x = 0; x < this.actions.size(); x++) {
			JavaAction action = actions.get(x);
			if(action.type == ByteCode.TYPE.NEWOBJECT) {
				continue;
			}
			if(action.type == ByteCode.TYPE.PUTFIELD&& !action.value.contains("=")) {
				continue;
			}
			if(action.type == ByteCode.TYPE.IGNORE) {
				x= actions.size();
				continue;
			}
			if(action.type == ByteCode.TYPE.LOADVAL) {
				continue;
			}
			if(action.type == ByteCode.TYPE.INTOVAR && !action.value.contains("=")) {
				continue;
			}
			if(action.id == 5590) {
				int debugME=0;
			}
			if (childPos != -1) {
				int debugMe = 0;
			}
			while (childPos != -1 && action.line >= childPos) {

				nextChild.printHeading(outStream, pad);
				nextChild.printAll(outStream, varsAtThisLevel, pad + "   ", staticInit);
				
				if(nextChild.elseBranch != null) {
					outStream.println(pad + "}");
					nextChild.elseBranch.printHeading(outStream, pad);
					nextChild.elseBranch.printAll(outStream, varsAtThisLevel, pad + "   ", staticInit);
				}
				if(nextChild.type != ByteCode.TYPE.CASE) {
			    	outStream.println(pad + "}");
				}
				
				if (childIter.hasNext()) {
					nextChild = childIter.next();
					childPos = nextChild.startIndex;
				} else {
					childPos = -1;
				}
				
			}

			if (action.value.contains("wasAnd=1")) {
				int debugME = 0;
			}
			String preText = "";
			if (action.type == ByteCode.TYPE.GOTO) {
				continue;
			}
			if (action.type == ByteCode.TYPE.INSTANCEOF) {
				continue;
			}
			
			if (action.type == ByteCode.TYPE.INTOVAR) {
				String varName = action.value.split("=")[0].trim();

				if (!varsAtThisLevel.contains(varName)) {
					varsAtThisLevel.add(varName);
					newVars.add(varName);
					if(this.type == ByteCode.TYPE.CASE) {
						varsss.add(varName);
					}
					preText = action.returnType + " ";
				}

			} else if (action.type == ByteCode.TYPE.CATCH) {
				int debugME = 0;
			}
			if (staticInit && action.type == ByteCode.TYPE.RETURN) {

			} else {
				if(action.id == 14206) {
					int debugME=0;
				}
				if(action.funnyCatch) {
					action.value = action.value.replaceAll(".*=", "");
					action.value = action.value.replace(")", " e ) {}");
				}
				if(action.type == ByteCode.TYPE.CASE && this.type != ByteCode.TYPE.SWITCH) {
					continue;
				}
				
				
				if(wasSync) {
					int debugME=0;
				}
				if(wasSync && action.type == ByteCode.TYPE.TRY) {
					continue;
				}if(wasSync && action.type == ByteCode.TYPE.FINALLY) {
					continue;
				}
				if(action.type == ByteCode.TYPE.IGNORE) {
					int debugme=0;
				}
				wasSync= action.type == ByteCode.TYPE.SYNCHRONIZED;
				println(outStream, pad + preText + action.value + action.getEnding() + "// " + action.id);
			}

		}
		while (childPos != -1) {
			nextChild.printHeading(outStream, pad);
			nextChild.printAll(outStream, varsAtThisLevel, pad + "   ", staticInit);
			if(nextChild.type != ByteCode.TYPE.CASE) {
		    	outStream.println(pad + "}");
			}
			if(nextChild.elseBranch != null) {
			
				nextChild.elseBranch.printHeading(outStream, pad);
				nextChild.elseBranch.printAll(outStream, varsAtThisLevel, pad + "   ", staticInit);
				outStream.println(pad + "}");
			}
			if (childIter.hasNext()) {
				nextChild = childIter.next();
				childPos = nextChild.startIndex;
			} else {
				childPos = -1;
			}
		}
		if(this.ifBreak) {
			outStream.println(pad + "} else {break;");
		}
	}

	public static void findWhiles(List<JavaAction> actions) {
		for (int x = 0; x < actions.size(); x++) {
			JavaAction action = actions.get(x);
			if (action.type == ByteCode.TYPE.IF) {
				if (action.goToLine < action.line) {
					if(action.goToLine == 0) {
						action.goToLine = 1;
					}
					JavaAction doAction = actions.get(action.goToLine - 1);
					doAction.type = ByteCode.TYPE.DO;
					if (doAction.whileTo == -1) {
						doAction.whileTo = doAction.goToLine;
					}
					doAction.goToLine = action.line;
				}
			}
		}
	}

	public void findElses() {
		if(this.branchID == 1164) {
			int debugME =0;
		}
		for (JavaAction action : actions) {
			if (action.goToLine > goToLine) {
				if(action.type != ByteCode.TYPE.BREAK) {
				this.goTos.add(action);
				}
			}
		}
		if (this.type == ByteCode.TYPE.CASE) {
			for (JavaAction gt : goTos) {
				gt.type = ByteCode.TYPE.BREAK;
				gt.value = "break";
			}
		}
		List<ActionBranch> toRemoveBranch = new ArrayList<ActionBranch>();
		
		for (ActionBranch child : children) {
			if(toRemoveBranch.contains(child)){
				continue;
			}
			child.findElses();
			int maxGoTo = child.goToLine;
			int minGoTo = this.goToLine;
			for (JavaAction gt : child.goTos) {
				if (gt.goToLine < goToLine) {
					if (this.type == ByteCode.TYPE.DO) {

						if (gt.goToLine >= this.whileTo) {
							if (gt.type == ByteCode.TYPE.IF) {
								this.ifContinue = true;
							} else {
								gt.type = ByteCode.TYPE.CONTINUE;
								gt.value = "continue";
							}
						} else {
							if (gt.goToLine < minGoTo) {
								minGoTo = gt.goToLine;
							}
							if (gt.goToLine > maxGoTo) {
								maxGoTo = gt.goToLine;
							}
							child.elseEnd = gt.goToLine;
						}
					} else {
						child.elseEnd = gt.goToLine;

					}

				} else {

					if (this.type == ByteCode.TYPE.ELSE) {

						child.elseEnd = gt.goToLine;
					} else {
					if (this.type == ByteCode.TYPE.DO) {

						gt.type = ByteCode.TYPE.BREAK;
						gt.value = "break";
					} else {
						if(gt.goToLine == goToLine) {
							child.elseEnd =  goToLine;
						}else {
						   this.goTos.add(gt);
						}
					}}
				}
			}
			if (minGoTo < this.goToLine) {
				if (minGoTo != maxGoTo) {
					int debugME = 0;
				}
			}
			
			if(child.elseEnd != 0 && child.type != ByteCode.TYPE.CASE&& child.type != ByteCode.TYPE.TRY) {
				List<JavaAction> toRemove = new ArrayList<JavaAction>();
				for(JavaAction act2 : actions) {
					if(act2.line >= child.goToLine && act2.line < child.elseEnd) {
						toRemove.add(act2);
					}
				}
				this.actions.removeAll(toRemove);
				ActionBranch elseBranch = new ActionBranch();
				elseBranch.type = ByteCode.TYPE.ELSE;
				if(child.type == ByteCode.TYPE.CATCH) {
					elseBranch.type = ByteCode.TYPE.FINALLY;
				}
				elseBranch.startIndex = child.goToLine;
				elseBranch.actions.addAll(toRemove);
				elseBranch.goToLine = child.elseEnd;
				for(ActionBranch act2 : children) {
					if(act2.startIndex >= child.goToLine && act2.startIndex < child.elseEnd) {
						toRemoveBranch.add(act2);
						elseBranch.children.add(act2);
					}
				}
				elseBranch.findElses();
				if(elseBranch.actions.size() > 0 || elseBranch.children.size() > 0) {
				  child.elseBranch = elseBranch;
				}
			
			}
		}
		this.children.removeAll(toRemoveBranch);
	}

	public static int findIfs(List<JavaAction> actions, ActionBranch parent, int start, int goTo) {
		parent.startIndex = start;
		parent.goToLine = goTo;
		for (int x = start; x < parent.goToLine; x++) {
			JavaAction action = actions.get(x);
			if(action.id >= 2863) {
				int debugME =0;
			}
			if (parent.type == ByteCode.TYPE.DO && action.line >= parent.whileTo) {
				parent.orWhiles.add(action);
				continue;
			}
			if (action.type == ByteCode.TYPE.IF) {

				if (action.goToLine > action.line) {

					if(action.value.contains("signature.contains(\"/\"")) {
						int debugME =0;
					}
					if(action.value.contains("this.code") && action.value.contains("3")) {
						int debugME=0;
					}
					boolean sameIf = true;
					int y = x;
					int possibleOr = -1;
					while (sameIf) {
						sameIf = false;
						y++;
						if (y < goTo) {
							JavaAction nextAction = actions.get(y);
							if (nextAction.type == ByteCode.TYPE.IF) {

								if (nextAction.goToLine == action.goToLine) {
									sameIf = true;
								} else {
									if (nextAction.goToLine > action.goToLine && nextAction.goToLine < goTo) {
										if (possibleOr == -1) {
											possibleOr = nextAction.goToLine;
											sameIf = true;
										} else {
											if (nextAction.goToLine == possibleOr) {
												sameIf = true;
											}
										}
									}
								}

							} else {
								if (possibleOr != -1) {
									if (nextAction.line == action.goToLine) {

									} else {
										possibleOr = -1;
									}
								}
							}

						}
					}
					
					int nextGoTo = action.goToLine;
					if (possibleOr != -1) {
						nextGoTo = possibleOr;
					}
					if (nextGoTo > parent.goToLine) {
						nextGoTo = goTo;
					}
					
					ActionBranch child = new ActionBranch();
					for (int z = x; z < y; z++) {
						child.ifs.add(actions.get(z));
					}
					if(parent.type == ByteCode.TYPE.DO && action.goToLine >= parent.goToLine) {
						action.goToLine = parent.whileTo;
						nextGoTo = parent.whileTo;
						child.ifBreak = true;
					}
					parent.addChild(child);
					child.type = ByteCode.TYPE.IF;

					int testY = findIfs(actions, child, y, nextGoTo);
					if(testY < nextGoTo) {
						child.goToLine = testY;
						nextGoTo=child.goToLine;
					}
					if(x >= nextGoTo) {
					int debugME = 0;	
					}else {
					x = nextGoTo - 1;
					}

				}else {
				 if(parent.parent != null) {
					 int debugME =0;
				 }
				}
			} else if (action.type == ByteCode.TYPE.FINALLY ||action.type == ByteCode.TYPE.CASE || action.type == ByteCode.TYPE.SWITCH|| action.type == ByteCode.TYPE.TRY|| action.type == ByteCode.TYPE.CATCH) {
				if(action.type == ByteCode.TYPE.FINALLY && parent.type == ByteCode.TYPE.TRY) {
					continue;
				}
				if(action.type == ByteCode.TYPE.TRY) {
					if(parent.type == ByteCode.TYPE.TRY) {
						if(action.goToLine == parent.goToLine) {
							continue;
						}
					}
				}
				parent.actions.add(action);
				if(action.goToLine <= action.line) {
					x= action.line;
					continue;
				}
				ActionBranch child = new ActionBranch();
				parent.addChild(child);

			
				if (action.value.contains("case CATCH")) {
					int debugME = 0;
				}
				
				child.type = action.type;		
				findIfs(actions, child, x + 1, action.goToLine);

				
				x = child.goToLine - 1;
						
				
				if (x < start) {
					x = action.line;
				}

			} else if (action.type == ByteCode.TYPE.DO) {
				if(parent.type != null && parent.type == ByteCode.TYPE.IF && parent.goToLine == action.whileTo) {
					return x;
				}
				ActionBranch child = new ActionBranch();
				parent.addChild(child);
				
				child.whileTo = action.whileTo;
				child.type = ByteCode.TYPE.DO;
				child.goToLine = action.goToLine;
				findIfs(actions, child, x + 1, action.goToLine + 1);

				x = action.goToLine;
				if (x < start) {
					int debugME = 0;
				}
			} else {
				if (action.type == ByteCode.TYPE.GOTO && parent.type == ByteCode.TYPE.CASE) {
					action.type =  ByteCode.TYPE.BREAK;
					action.value ="break";
					if(parent.parent.goToLine < action.goToLine) {
					   parent.parent.goToLine = action.goToLine;
					}
				}
				parent.actions.add(action);
			}

		}
		return goTo;

	}
}
