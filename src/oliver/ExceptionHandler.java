package oliver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import decompile.ByteCode;
import decompile.JavaAction;
import decompile.ByteCode.TYPE;
import oliver.action.Action;
import oliver.action.ActionStack;
import oliver.action.CatchAction;
import oliver.action.FinallyAction;
import oliver.action.GotToAction;
import oliver.action.IgnoreAction;
import oliver.action.SynchronizedAction;
import oliver.action.TryAction;
import oliver.action.VariableAction;

public class ExceptionHandler {

	int startPC;
	int endPC;

	int finallyPC = -1;

	ArrayList<CatchPiece> catchHandlers = new ArrayList<CatchPiece>();

	class CatchPiece {
		public CatchPiece(int handlerPC2, String catchType) {
			this.handlerPC = handlerPC2;
			this.catchType = catchType;
		}

		int handlerPC;
		String catchType;
		int goToLabel = -1;
		int ignoreLabel = -1;
	}

	class FinallyPiece {
		int handlerPC;
		int goToLabel = -1;
	}

	public static void clear() {
		handlers.clear();

		// uniguePC.clear();
	}

	static ArrayList<ExceptionHandler> handlers = new ArrayList<ExceptionHandler>();

	HashSet<Integer> uniguePC = new HashSet<Integer>();

	public static void addExp(BytePart value) {
		int startPC = value.getChild("start_pc").bufAsInt();
		int endPC = value.getChild("end_pc").bufAsInt();
		int handlerPC = value.getChild("handler_pc").bufAsInt();
		int type = value.getChild("catch_type").bufAsInt();
		if (type != 0) {
			String catchType = ConstEntry2.consts.get(type).getVariable().getName();
			for (ExceptionHandler hc : handlers) {
				if (hc.startPC == startPC && hc.endPC == endPC) {
					hc.addCatchType(handlerPC, catchType);
					return;
				}

			}
			ExceptionHandler handler = new ExceptionHandler(startPC, endPC);
			handler.addCatchType(handlerPC, catchType);
			handlers.add(handler);
		} else {
			for (ExceptionHandler hc : handlers) {

				if (hc.addFinally(handlerPC, startPC, endPC)) {
					return;
				}

			}
			ExceptionHandler handler = new ExceptionHandler(startPC, endPC);
			handler.uniguePC.add(handlerPC);
			handler.finallyPC = handlerPC;
			handlers.add(handler);
			// handler.finallyPC = handlerPC;
		}

	}

	private boolean addFinally(int handlerPC, int startPC, int endPC) {
		for (CatchPiece cp : this.catchHandlers) {
			if (endPC > cp.handlerPC && startPC <= cp.handlerPC) {
				if (cp.goToLabel == -1) {
					cp.goToLabel = handlerPC;
				}
				uniguePC.add(handlerPC);
				uniguePC.add(endPC);
				cp.ignoreLabel = endPC;
				this.finallyPC = handlerPC;
				return true;

			}
		}

		return false;
	}

	private void addCatchType(int handlerPC, String catchType) {
		for (CatchPiece cp : this.catchHandlers) {
			if (handlerPC > cp.handlerPC) {
				uniguePC.add(handlerPC);
				cp.goToLabel = handlerPC;
				break;
			}
		}
		uniguePC.add(handlerPC);
		CatchPiece cp = new CatchPiece(handlerPC, catchType);
		this.catchHandlers.add(cp);
	}

	public ExceptionHandler(int startPC, int endPC) {

		this.startPC = startPC;
		this.endPC = endPC;

		uniguePC.add(startPC);
	}

	static Action lastLine;
	private static JavaAction lastLine2;
	TryAction tryAction;
	JavaAction try2Action;
	boolean dodgySynch = false;

	private void handleThePC(ActionStack stackA, int pc, int label) {

		if(dodgySynch) {
			return;
		}
		if(pc == 91) {
			int debugMe =0;
		}
		if (pc == startPC) {

			int goToLabel = this.finallyPC;
			if (this.catchHandlers.size() > 0) {
				goToLabel = this.catchHandlers.get(0).handlerPC;
			}

			tryAction = new TryAction(pc, goToLabel);

			if (stackA.otherActions.size() > 0) {
				Action lastAction = stackA.otherActions.get(stackA.otherActions.size() - 1);
				if (lastAction instanceof SynchronizedAction) {
					int debugME = 0;
					lastAction.setGoToLabel(this.finallyPC);
					dodgySynch = true;
					return;
				}
			}
			// SmallBranch.bigBranch.addChild(newBr);
			stackA.pushOther(tryAction);

			

		} else if (pc == finallyPC) {

			int goToLabel = this.finallyPC;

			Action lastLine = stackA.popOther();
			if (lastLine instanceof GotToAction) {
				goToLabel = lastLine.getGoToLabel();
				ExceptionHandler.lastLine = lastLine;
			} else {
				stackA.pushOther(lastLine);
				if (ExceptionHandler.lastLine != null) {
					goToLabel = ExceptionHandler.lastLine.getGoToLabel() + 1;
					tryAction.isFunky = true;
					IgnoreAction finallyAction = new IgnoreAction(pc, goToLabel);

					// SmallBranch.bigBranch.addChild(newBr);

					stackA.pushOther(finallyAction);
					return;
				}

			}

			FinallyAction finallyAction = new FinallyAction(pc, goToLabel);

			// SmallBranch.bigBranch.addChild(newBr);

			stackA.actualPush(finallyAction);

		} else {
			for (CatchPiece cp : this.catchHandlers) {
				if (pc == cp.handlerPC) {
					Action lastLine = stackA.popOther();
					// String expName = lastLine.line.split("=")[0].trim();
					int goToLabel = cp.goToLabel;
					  if(! (lastLine instanceof GotToAction)) {
						  stackA.pushOther(lastLine);
						  int debugME =0;
						  goToLabel= 400000;
						  ClassDescription2.closeCatch=true;
					  }
					
					if (goToLabel == -1) {
						goToLabel = lastLine.getGoToLabel();
						
					}

					CatchAction catchAction = new CatchAction(pc, goToLabel, cp.catchType);

					if(goToLabel == -1) {
						int debugME =0;
					}
					stackA.actualPush(catchAction);
					MultiVar.isCatch =true;
					break;
				} else if (pc == cp.ignoreLabel) {

					int goToLabel = cp.goToLabel;
					IgnoreAction tryAction = new IgnoreAction(pc, goToLabel);

					stackA.pushOther(tryAction);

					break;
				}
			}
		}
	}
	private void handleThePC(Stack<JavaAction> stackA, ArrayList<JavaAction> store, int pc, int label) {

		if(dodgySynch) {
			return;
		}
		if(pc == 91) {
			int debugMe =0;
		}
		if (pc == startPC) {

			int goToLabel = this.finallyPC;
			if (this.catchHandlers.size() > 0) {
				goToLabel = this.catchHandlers.get(0).handlerPC;
			}

			try2Action = new JavaAction(ByteCode.TYPE.TRY, pc, "", "try");
			try2Action.goTo = goToLabel;
			if (store.size() > 0) {
				JavaAction lastAction = store.get(store.size() - 1);
//				if (lastAction instanceof SynchronizedAction) {
//					int debugME = 0;
//					lastAction.goTo =(this.finallyPC);
//					dodgySynch = true;
//					return;
//				}
			}
			// SmallBranch.bigBranch.addChild(newBr);
			store.add(try2Action);

			

		} else if (pc == finallyPC) {

			int goToLabel = this.finallyPC;

			JavaAction lastLine = stackA.pop();
			if (lastLine.goTo != -1) {
				goToLabel = lastLine.goTo;
				ExceptionHandler.lastLine2= lastLine;
			} else {
				stackA.push(lastLine);
				if (ExceptionHandler.lastLine != null) {
					goToLabel = ExceptionHandler.lastLine.getGoToLabel() + 1;
					tryAction.isFunky = true;
					JavaAction finallyAction = new JavaAction(ByteCode.TYPE.IGNORE, pc, "", "ignore{");
					finallyAction.goTo = goToLabel;
					// SmallBranch.bigBranch.addChild(newBr);

					store.add(finallyAction);
					return;
				}

			}

			JavaAction finallyAction =  new JavaAction(ByteCode.TYPE.FINALLY, pc, "", "finally");
			finallyAction.goTo = goToLabel;
			// SmallBranch.bigBranch.addChild(newBr);

			stackA.push(finallyAction);

		} else {
			for (CatchPiece cp : this.catchHandlers) {
				if (pc == cp.handlerPC) {
					JavaAction lastLine = store.remove(store.size() -1);
					// String expName = lastLine.line.split("=")[0].trim();
					int goToLabel = cp.goToLabel;
					  if(lastLine.goTo == -1) {
						  store.add(lastLine);
						  int debugME =0;
						  goToLabel= 400000;
						  ClassDescription2.closeCatch=true;
					  }
					
					if (goToLabel == -1) {
						goToLabel = lastLine.goTo;
						
					}

					JavaAction catchAction = new JavaAction(ByteCode.TYPE.CATCH,pc,"","catch(" + cp.catchType +")");
					catchAction.goTo = goToLabel;
					if(goToLabel == -1) {
						int debugME =0;
					}
					stackA.push(catchAction);
					MultiVar.isCatch =true;
					break;
				} else if (pc == cp.ignoreLabel) {

					int goToLabel = cp.goToLabel;
				
					JavaAction action = new JavaAction(ByteCode.TYPE.IGNORE, pc, "", "ignore{");
					action.goTo = goToLabel;
					store.add(action);

					break;
				}
			}
		}
	}
	public static List<Integer> getSortedPC() {
		List<Integer> sortedPC = new ArrayList<Integer>();
		for (ExceptionHandler hc : handlers) {
			sortedPC.addAll(hc.uniguePC);
		}

		return sortedPC;
	}

	public static int handlePC(ActionStack stackA, int pc, int label) {
		for (ExceptionHandler hc : handlers) {

			if (hc.uniguePC.contains(pc)) {
				if (pc == 0) {
					int debugMe = 0;
				}
				hc.handleThePC(stackA, pc, label);
				break;
			}
		}
		return 1;

	}
	public static int handlePC(Stack<JavaAction> stackA, ArrayList<JavaAction> store, int pc, int label) {
		for (ExceptionHandler hc : handlers) {

			if (hc.uniguePC.contains(pc)) {
				if (pc == 0) {
					int debugMe = 0;
				}
				hc.handleThePC(stackA,store, pc, label);
				break;
			}
		}
		return 1;

	}
	/*
	 * if(tries.containsKey(pc)) { ExceptionHandler handler = tries.get(pc); Line
	 * newLine = new Line(Line.Type.TRY,pc,0, "try {", handler.handlerPC, false, pc,
	 * "try { ") ; stackA.extraLines.add(newLine);
	 * 
	 * return handler.endPC; //Line(Type type, int label, int code, String line, int
	 * goToIndex, boolean assignment, int pc, String pop) }else
	 * if(catches.containsKey(pc)) { ExceptionHandler handler = catches.get(pc);
	 * Line line = (Line)stackA.pop(); String expName = "EXP"; stackA.push(expName);
	 * handler.goToLabel = line.gotoLabel;
	 * 
	 * Line newLine = new Line(Line.Type.CATCH,handler.handlerPC,0, "catch (" +
	 * expName +") {" , line.gotoLabel, false, pc, "catch (" + expName +") {") ;
	 * stackA.extraLines.add(newLine); handler.newLine = newLine;
	 * for(ExceptionHandler hc : catches.values()) { if(handler.endPC == hc.endPC &&
	 * handler.handlerPC != hc.handlerPC) { if(hc.newLine != null) { hc.goToLabel =
	 * handler.handlerPC; hc.newLine.gotoLabel = handler.handlerPC; } } } return
	 * line.gotoLabel; //Line(Type type, int label, int code, String line, int
	 * goToIndex, boolean assignment, int pc, String pop) }else
	 * if(finallies.containsKey(pc)) { ExceptionHandler handler = finallies.get(pc);
	 * Line line = (Line)stackA.pop();
	 * 
	 * Line newLine = new Line(Line.Type.FINALLY,handler.handlerPC,0, "finally {" ,
	 * line.gotoLabel, false, pc, "finally {") ; stackA.extraLines.add(newLine);
	 * ExceptionHandler hc2 = null; for(ExceptionHandler hc : catches.values()) {
	 * if(handler.startPC >= hc.handlerPC) { hc2 = hc;
	 * 
	 * } } if(hc2 != null && hc2.goToLabel > pc && hc2.newLine != null) {
	 * hc2.newLine.gotoLabel = handler.handlerPC; hc2.goToLabel = handler.handlerPC;
	 * } return line.gotoLabel; //Line(Type type, int label, int code, String line,
	 * int goToIndex, boolean assignment, int pc, String pop) }
	 * 
	 * return -1; }
	 */
	/*
	 * public static ArrayList<Line> processLines(ArrayList<Line> lines) {
	 * 
	 * List <PCLine> pcLines = ExceptionHandler.getExceptionInfo();
	 * if(pcLines.size() == 0) { return lines; } ArrayList<Line> linesWithTries =
	 * new ArrayList<Line>();
	 * 
	 * PCLine firstPC = null; if(pcLines.size() > 0) { firstPC = pcLines.remove(0);
	 * } Stack<Line> lastCatchStack = new Stack<Line>(); Line lastCatch = null;
	 * 
	 * 
	 * for(Line line : lines) {
	 * 
	 * if(firstPC != null && line.pc >= firstPC.PC) {
	 * 
	 * if (firstPC.type == PCLine.TYPE.CATCH) {
	 * 
	 * if(lastCatch != null) { lastCatch.gotoLabel = line.pc; } String expName =
	 * line.line.split("=")[0];
	 * 
	 * String catchStr = String.format(firstPC.toString(), expName, "lll"); Line
	 * newLine = new Line(Line.Type.CATCH,line.pc, line.code, catchStr,
	 * line.gotoLabel, false, firstPC.PC,catchStr); linesWithTries.add(newLine); //
	 * oldLine.goTo = true; firstPC = null; if (pcLines.size() > 0) { firstPC =
	 * pcLines.remove(0); } lastCatch = newLine; continue; } if(firstPC.type ==
	 * PCLine.TYPE.IGNORE) { Line oldLine = new Line(Line.Type.IGNORE,line.label,
	 * line.code, "IGNORE", -1, false, firstPC.PC,"IGNORE");
	 * 
	 * linesWithTries.add(oldLine); } Line.Type ty = Line.Type.TRY; if(firstPC.type
	 * == PCLine.TYPE.FINALLY) { if(lastCatch != null) { lastCatch.gotoLabel =
	 * line.pc; } lastCatch = null; if(line.gotoLabel != -1) {
	 * 
	 * } ty = Line.Type.FINALLY; Line newLine = new Line(ty,line.pc, line.code,
	 * firstPC.toString(), line.gotoLabel, false, firstPC.PC,firstPC.toString());
	 * 
	 * linesWithTries.add(newLine);
	 * 
	 * } if(firstPC.type == PCLine.TYPE.TRY) { if(lastCatch!=null) {
	 * lastCatchStack.push(lastCatch); } if(line.gotoLabel != -1) {
	 * 
	 * } ty = Line.Type.TRY; Line newLine = new Line(ty,line.label, line.code,
	 * firstPC.toString(), firstPC.endPC, false, firstPC.PC,firstPC.toString());
	 * 
	 * linesWithTries.add(newLine);
	 * 
	 * } firstPC = null; if (pcLines.size() > 0) { firstPC = pcLines.remove(0); } }
	 * linesWithTries.add(line); }
	 * 
	 * return linesWithTries; }
	 */
}
