package oliver.action;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

import oliver.ByteCode;
import oliver.ClassDescription2;
import oliver.Variable;

public class ActionStack {

	ArrayList<Action> ifBackActions = new ArrayList<Action>();
	ByteCode bytecode;

	public ActionStack(ByteCode bytecode) {
		this.bytecode = bytecode;
	}

	Stack<Action> actualStack = new Stack<Action>();
	public ArrayList<Action> otherActions = new ArrayList<Action>();

	public Action pushOther(Action action) {
		
		this.otherActions.add(action);
		return action;
	}

	public Action actualPush(Action action) {
		return this.actualStack.push(action);
	}

	public String debug() {
		String ret ="";
		if (this.actualStack.size() > 0) {
			ret+=("Actual:" + this.actualStack.peek());
		}
		if (this.otherActions.size() > 0) {
			ret+=("Other:" + this.otherActions.get(this.otherActions.size() - 1).toString());
		}
   return ret;
	}

	static HashMap<Integer, ArrayList<Action>> dubiousContinues = new HashMap<Integer, ArrayList<Action>>();

	public Action push(Action action) {

		
		if (action.getPc() == 5629) {
			int debugME = 0;
		}
		/// 5112->5678
		if (action.getPc() == 5112 && action.goToLabel == 5678) {
			int debugMe = 0;
		}
		if (action instanceof InstanceOfAction) {

		}
		if (action instanceof MethodAction) {
			MethodAction methodAction = (MethodAction) action;
			if (methodAction.getReturnType().equals("void")) {
				// otherActions.add(action);
				// return action;
			}
		}
		
		if (action instanceof AssignAction) {
			AssignAction assignAction = (AssignAction) action;
			if (assignAction.rightHS instanceof CatchAction) {
				CatchAction catAct = (CatchAction) assignAction.rightHS;
				catAct.expName = assignAction.leftHS.print();
				return actualStack.push((Action) catAct);

			} else if (assignAction.rightHS instanceof FinallyAction) {
				FinallyAction catAct = (FinallyAction) assignAction.rightHS;

				return actualStack.push((Action) catAct);

			} 
			if (this.otherActions.size() > 1) {
				Action lastAction = this.otherActions.get(this.otherActions.size() - 1);
				if (lastAction instanceof GotToAction && lastAction.goToLabel == action.getPc()) {
					Action secondLastAction = this.otherActions.get(this.otherActions.size() - 2);
					if (secondLastAction instanceof IfAction) {
						Action lastOnStack = this.actualStack.peek();
						if (lastOnStack.getPc() > secondLastAction.getPc()
								&& lastOnStack.getPc() < secondLastAction.goToLabel) {
							this.actualStack.pop();
							this.otherActions.remove(this.otherActions.size() - 1);
							this.otherActions.remove(this.otherActions.size() - 1);
							action = new FunkyAction(action.getPc(), ((AssignAction) action).getLeftHS(),
									(IfAction) secondLastAction, lastOnStack, ((AssignAction) action).getRightHS());
						}
					}
				}
			}
		}
		if (action instanceof AssignArrayIndexAction) {

			AssignArrayIndexAction assignAction = (AssignArrayIndexAction) action;

			ArrayIndexAction arrayIndexAction = assignAction.leftHS;
			if (arrayIndexAction.array instanceof NewArrayAction) {
				NewArrayAction newArrayAction = (NewArrayAction) arrayIndexAction.array;

				newArrayAction.fullArray = true;
				newArrayAction.ArrayParts.add(assignAction.rightHS);
				return actualStack.push((Action) newArrayAction);

			}

		}

		if (action instanceof IfAction) {
			IfAction iff = (IfAction) action;
			if (iff.goToLabel < iff.getPc()) {
				ifBackActions.add(action);
			}
		}
		if (this.bytecode.isPushToStack()) {
			return actualStack.push(action);
		} else {
			otherActions.add(action);
		}
		return action;
	}

	protected ArrayList<Action> getOrderredList() {
		ArrayList<Action> ret = new ArrayList<Action>();
		Iterator<Action> otherIt = this.otherActions.iterator();
		Action nextAction = null;
		if (otherIt.hasNext()) {
			nextAction = otherIt.next();
			if (nextAction.getPc() == 0) {
				ret.add(nextAction);
				if (otherIt.hasNext()) {
					nextAction = otherIt.next();
				} else {
					nextAction = null;
				}
			}
		}

		int lastPc = 0;
		for (Action test : this.actualStack) {
			while (otherIt.hasNext() && nextAction.getPc() < test.getPc()) {
				ret.add(nextAction);
				lastPc = nextAction.getPc();
				nextAction = otherIt.next();
			}
			if (nextAction != null && nextAction.getPc() > lastPc && nextAction.getPc() < test.getPc()) {
				ret.add(nextAction);
				lastPc = nextAction.getPc();
			}
			lastPc = test.getPc();
			ret.add(test);
		}
		if (nextAction != null && nextAction.getPc() > lastPc) {
			ret.add(nextAction);

		}
		while (otherIt.hasNext()) {
			ret.add(nextAction);
			lastPc = nextAction.getPc();
			nextAction = otherIt.next();
		}

		this.orderre = ret;

		return ret;
	}

	public Action popOther() {
		return this.otherActions.remove(this.otherActions.size() - 1);
	}

	public Action pop() {

		if(actualStack.size()  ==0) {
			int debugME =0;
			Action other = this.otherActions.get(this.otherActions.size() -1);
			return other;
			
		}
		Action pop = actualStack.pop();

		return pop;

	}

	ArrayList<Action> orderre;

	public void findWhiles(String methodName) {

		Action.sortBySorto = true;
		Collections.sort(this.ifBackActions);
		Action.sortBySorto = false;
		ListIterator<Action> iter = this.orderre.listIterator();
		Stack<GotToAction> lastWhiles = new Stack<GotToAction>();
		GotToAction currentWhile = null;
		ListIterator<Action> iterBack = this.ifBackActions.listIterator();
		Action backAction = null;
		if (iterBack.hasNext()) {
			backAction = iterBack.next();
		}
		int lastGoTo = -1;
		while (iter.hasNext()) {
			Action testAction = iter.next();
			if(testAction instanceof SwitchAction) {
				if(testAction.goToLabel == -1) {
					testAction.goToLabel = lastGoTo;
				}
			}
			if(testAction.goToLabel != -1) {
				lastGoTo = testAction.goToLabel;
			}
			if (testAction.getPc() == 137) {
				int debugME = 0;// 312->365
			}
			while(currentWhile != null && testAction.isIf && testAction.getPc() >= currentWhile.goToLabel  && testAction.getPc() < currentWhile.lastBack) {
				currentWhile.otherIf.otherIfs.add((IfAction)testAction);
				   testAction.setValid(false);
				   int debugME =0;
				   testAction = iter.next();
				
			}
			while (currentWhile != null && testAction.getPc() > currentWhile.goToLabel) {
				if (lastWhiles.size() > 0) {
					currentWhile = lastWhiles.pop();

				} else {
					currentWhile = null;
				}
			}

			if (currentWhile != null && currentWhile.leastContinue > 0 && testAction.getPc() >= currentWhile.leastContinue && testAction.getPc() < currentWhile.goToLabel) {
				int debugME =0;
				
				ArrayList<Action> dubiousContinuesForThisWhile = dubiousContinues
						.get(currentWhile.getPc());
				if (dubiousContinuesForThisWhile == null) {
					dubiousContinuesForThisWhile = new ArrayList<Action>();
					dubiousContinues.put(currentWhile.getPc(), dubiousContinuesForThisWhile);
				}
				dubiousContinuesForThisWhile.add(testAction);	
				
			}
			if (backAction != null && testAction.getPc() >= backAction.goToLabel) {
				Action previuosAction = iter.previous();
				previuosAction = iter.previous();
				if (previuosAction instanceof GotToAction) {
					GotToAction whileAct = (GotToAction) previuosAction;
					whileAct.isWhile = true;
					if (currentWhile != null) {
						lastWhiles.push(currentWhile);
					}
					currentWhile = whileAct;
					while (backAction != null && testAction.getPc() >= backAction.goToLabel) {

						if (currentWhile.otherIf != null) {
							currentWhile.otherIf.otherIfs.add((IfAction) backAction);
						} else {
							currentWhile.otherIf = (IfAction) backAction;
						}
						currentWhile.lastBack = backAction.getPc();
						if (iterBack.hasNext()) {
							backAction = iterBack.next();
						} else {
							backAction = null;
						}
					}
				}
				iter.next();
				iter.next();

			}
			if (currentWhile != null && testAction.goToLabel > 0
					&& testAction.goToLabel >= currentWhile.goToLabel - 5) {
				if (testAction.goToLabel > currentWhile.goToLabel) {

					testAction.isBreak = true;
				} else {
					if (testAction.goToLabel < currentWhile.goToLabel) {
						
						testAction.dubious = true;
						testAction.isContinue = true;
						currentWhile.posibleContinues.add(testAction);
						if(currentWhile.leastContinue ==0) {
							currentWhile.leastContinue =testAction.goToLabel; 
						}else {
							if(testAction.goToLabel < currentWhile.leastContinue) {
								currentWhile.leastContinue =testAction.goToLabel; 
							}else {
								if(testAction.goToLabel > currentWhile.leastContinue) {
									throw new RuntimeException("Hueston we have a problem");
								}
							}
						}
					} else {
						if(currentWhile.leastContinue > 0) {
							currentWhile.leastContinue =  currentWhile.goToLabel;
							for(Action posibleCont : currentWhile.posibleContinues) {
								posibleCont.isContinue = false;
							}
							int debugMe =0;
						}
						testAction.isContinue = true;
					}
				}
			}
			
		}

		groupsIFs(methodName);
	}

	public void groupsIFs(String methodName) {
		ListIterator<Action> iter = this.orderre.listIterator();
		while (iter.hasNext()) {
			Action testAction = iter.next();
			if (testAction.getPc() == 137) {
				int debugME = 0;
			}
			if (testAction instanceof GotToAction) {
				GotToAction fg = (GotToAction) testAction;
				fg.nextAction = iter.next();
				iter.previous();
				if (fg.isBreak || fg.isWhile || fg.isContinue) {

				} else {
					fg.isElse = true;
				}
			}
			if (testAction instanceof IfAction) {

				if (testAction.getPc() == 470) {
					int debugME = 0;// 312->365
				}
				IfAction ourIFAction = (IfAction) testAction;
				Action nextAction = iter.next();

				boolean woundBack = false;
				if (nextAction.goToLabel == ourIFAction.goToLabel) {
					while (nextAction != null && nextAction instanceof IfAction
							&& nextAction.goToLabel == ourIFAction.goToLabel) {
						IfAction nextIFAction = (IfAction) nextAction;
						ourIFAction.otherIfs.add(nextIFAction);
						nextIFAction.setValid(false);
						if (iter.hasNext()) {
							nextAction = iter.next();
						}
					}
					iter.previous();
					woundBack = true;
				}
				if (ourIFAction.goToLabel > ourIFAction.getPc() && nextAction != null
						&& nextAction.isIf && nextAction.goToLabel > ourIFAction.goToLabel) {
					int orGoTo = ourIFAction.goToLabel;
					int andGoto = nextAction.goToLabel;
					Action nextnextAction = iter.next();
					boolean hitIt = false;
					while (nextnextAction != null) {
						if (nextnextAction instanceof IfAction) {
							if (nextAction.goToLabel == orGoTo || nextAction.goToLabel == andGoto) {
								nextnextAction = iter.next();
								continue;
							} else {
								if (nextnextAction.getPc() >= orGoTo) {
									hitIt = true;
								}
								break;
							}
						}
						if (nextnextAction.getPc() >= orGoTo) {
							hitIt = true;
						}
						break;

					}
					if (!hitIt) {
						while (iter.previous().getPc() > nextAction.getPc()) {
							iter.previous();
						}
						iter.next();
					} else {
						ourIFAction.andGoTo = orGoTo;
						ourIFAction.goToLabel = andGoto;
						while (iter.previous().getPc() > nextAction.getPc()) {
							iter.previous();
						}

						nextnextAction = iter.next();
						while (nextnextAction.getPc() < orGoTo) {
							if ((nextnextAction instanceof IfAction)) {

								ourIFAction.otherIfs.add((IfAction) nextnextAction);
								if(nextnextAction.getRealGoto() > ourIFAction.getRealGoto()) {
									ourIFAction.originalGoTo =ourIFAction.getRealGoto();  
									ourIFAction.setRealGoto(nextnextAction.getRealGoto());								
									}
								nextnextAction.setValid(false);
							} else {
								break;
							}
							nextnextAction = iter.next();
						}
						iter.previous();
					}
					// try hit orGoto without hitting anything that goes to something other than
					// orGoto and andGoTo

				} else {
					if (!woundBack) {
						iter.previous();
					}
				}
			}
		}

	}

	public void ifsAndWhatNot(String methodName) {
		orderre = this.getOrderredList();
		dubiousContinues.clear();
		findWhiles(methodName);
	}

	boolean allOK = false;

	public void printStack(String clazzName, String methodName, ArrayList<String> varsss, PrintStream outStream) {
		ByteArrayOutputStream str = new ByteArrayOutputStream();
		allOK = true;
	
		this.printStackInner(clazzName, methodName, varsss, new PrintStream(str));
		if (!allOK) {
			str = new ByteArrayOutputStream();
			this.printStackInner(clazzName, methodName, varsss, new PrintStream(str));
			if (!allOK) {
				str = new ByteArrayOutputStream();
				this.printStackInner(clazzName, methodName, varsss, new PrintStream(str));
			}
		}
		try {
			String outStr = str.toString("UTF8");
			outStream.println(outStr);
			if(ClassDescription2.closeCatch) {
				outStream.println("}");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class CurrentInfo {
		Stack<oliver.action.Action> currentLevelStack = new Stack<oliver.action.Action>();

		Stack<ArrayList<String>> variables = new Stack<ArrayList<String>>();
		ArrayList<String> currentVariables = new ArrayList<String>();
		Stack<GotToAction> currentWhiles = new Stack<GotToAction>();
		Action funkyElse = null;
		GotToAction currentWhile = null;
		oliver.action.Action currentLevel = null;
		private void solveElse(Action testAction) {
			
			if(currentLevel instanceof SwitchAction) {
				return;
			}
			currentLevel.goToLabel =testAction.getPc() +1;
			int valueToRemove= currentLevel.getRealGoto();
			for(int x = currentLevelStack.size() -1; x > 0; x --) {
				Action existing = currentLevelStack.get(x);
				if(existing.isIf) {
					
					  existing.gotTos.remove(new Integer(valueToRemove));
	
					break;
				}else{
					existing.goToLabel = testAction.getPc() +1;
				}
			}
			
				
			
		}
	
		
		private boolean deadEnd(Action testAction) {
			if(testAction instanceof ThrowAction ) {
				return true;
			}
			if(testAction instanceof GotToAction &&( testAction.isContinue || testAction.isBreak)) {
				if(currentLevel.isElse) {
				   return true;
				}
			}
			
			return false;
		}
		public String dropLevel(Action testAction, PrintStream outStream, ListIterator<Action> iterator, String pad) {

			if(testAction.getPc() == 470) {
				int debugMe =0;
			}
			if(testAction instanceof GotToAction && (testAction.isContinue  || testAction.isBreak ) && currentLevel.isIf) {
				if(currentLevel.getRealGoto() == testAction.getRealGoto()) {
					currentLevel.goToLabel = testAction.getPc();
					int debugMe =0;
				}
			}
			if(currentLevel!= null && testAction.isWhile && currentLevel.isIf && currentLevel.goToLabel == testAction.goToLabel) {
				int debugME =0;
				currentLevel.goToLabel = testAction.getPc();
				currentLevel.setRealGoto(testAction.getPc());
			}
			Action lastAction = null;
			if(currentLevel != null &&  deadEnd(testAction) && currentLevel.goToLabel >= testAction.getPc() ) {
				solveElse(testAction);
			}
			
			while (currentLevel != null && testAction.getPc() >= currentLevel.goToLabel) {
				boolean ifBreak = currentLevel.isBreak;
				boolean ifContinue = currentLevel.isContinue;
				

				if (currentLevelStack.size() == 0) {

					lastAction = currentLevel;
					currentLevel = null;

					currentVariables = variables.pop();

				} else {

					lastAction = currentLevel;
					currentLevel = currentLevelStack.pop();

					currentVariables = variables.pop();
				}
				if (pad.length() > 3) {
					pad = pad.substring(3);
					outStream.println(pad + "}");
					if (ifBreak) {
						outStream.println(pad + "else { break;}");
						/*f (lastAction.elseTo > 0) {
							outStream.println(pad + "else { break;}");
						} else {
							if(!testAction.isBreak) {
							outStream.println(pad + "break;");
							}
						}*/
					}
					if (ifContinue) {
						if (lastAction.elseTo > 0) {
							outStream.println(pad + "else { continue;}");
						} else {
							//outStream.println(pad + "continue;");
						}
					}
				}
			}
			if (lastAction != null && lastAction.isIf && lastAction.gotTos.size() > 0 && lastAction.elseTo == 0) {
				Collections.sort(lastAction.gotTos);
				for (int testGotTo : lastAction.gotTos) {
					if (testGotTo > lastAction.goToLabel) {
						lastAction.elseTo = testGotTo;
						break;
					}
				}
			}

			if (lastAction != null && lastAction.isIf && lastAction.elseTo > 0) {
				GotToAction newElse = new GotToAction(testAction.getPc(), lastAction.elseTo);
				newElse.isElse = true;
				this.pushLevel(newElse);
				outStream.println(pad + newElse.print() + "{//" + newElse.goToLabel);
				pad += "   ";
			}

			return pad;
		}

		public void dropWhile(GotToAction gotTo) {
			while (currentWhile != null && gotTo.getPc() > currentWhile.goToLabel) {
				if (currentWhiles.size() > 0) {
					currentWhile = currentWhiles.pop();
				} else {
					currentWhile = null;
				}
			}
			if (currentWhile != null) {
				currentWhiles.push(currentWhile);
			}
			currentWhile = gotTo;
		}

		public void pushLevel(Action testAction) {
			ArrayList<String> lastVars = currentVariables;

			
			variables.push(currentVariables);
			if (currentLevel != null) {
				currentLevelStack.push(currentLevel);
				if(testAction.isElse && currentLevel.isIf && testAction.goToLabel > currentLevel.goToLabel) {
					int debugME =0;
					currentLevel.elseTo = testAction.goToLabel;
					testAction.goToLabel = currentLevel.goToLabel;
					testAction.setRealGoto(testAction.goToLabel);
							
				}
				if(testAction.isBreak || testAction.isContinue) {
					
				}
				else {
					currentLevel.gotTos.add(testAction.getRealGoto());
				}
				if (testAction.isIf && testAction.goToLabel > currentLevel.goToLabel) {

					testAction.setGoToLabel(currentLevel.goToLabel);
				}
			}
			currentVariables = new ArrayList<String>();
			// System.out.println("CurrentStack size" + variables.size());

			currentVariables.addAll(lastVars);

			currentLevel = testAction;
		}

	}

	public void printStackInner(String clazzName, String methodName, ArrayList<String> varsss, PrintStream outStream) {
		String pad = "   ";
		if (methodName.equals(clazzName)) {
			Action secondAction = actualStack.get(0);
			if (secondAction instanceof MethodAction) {
				outStream.println(pad + secondAction.print() + ";");
				secondAction.setValid(false);
			}
		}

		CurrentInfo currentInfo = new CurrentInfo();
		currentInfo.currentVariables.addAll(varsss);

		ListIterator<Action> iterator = orderre.listIterator();
        if(methodName.equals("assertMagicNumber")) {
        	int debugME =0;
        }
		while (iterator.hasNext()) {
			Action action = iterator.next();
			if (action.getPc() == 137) {
				int debugME = 0;
			}
			if (action.isValid() == false) {
				continue;
			}
			oliver.action.Action testAction = (oliver.action.Action) action;
			
			if (action instanceof IgnoreAction) {
				int ignoreTo = ((IgnoreAction) action).goToLabel;
				while (iterator.hasNext() && testAction.getPc() < ignoreTo) {
					action = iterator.next();
					testAction = (oliver.action.Action) action;
				}
			}
			pad = currentInfo.dropLevel(testAction, outStream, iterator, pad);

			if (currentInfo.currentLevel != null && currentInfo.currentLevel instanceof FinallyAction
					&& testAction instanceof ThrowAction) {
				if (testAction.print().contains("unusedVariab")) {
					int ignoreTo = currentInfo.currentLevel.goToLabel;
					while (iterator.hasNext() && testAction.getPc() < ignoreTo) {
						action = iterator.next();
						testAction = (oliver.action.Action) action;
					}

				}

			}
			if (testAction.getPc() == 731 && testAction.getGoToLabel() == 702) {
				int debugMe = 0;
			}
			if (!testAction.isValid()) {

				continue;
			}
			if (testAction instanceof VariableAction || testAction instanceof ValueAction
					|| testAction instanceof FieldAction || testAction instanceof InstanceOfAction) {
				outStream.println(pad + "//" + testAction.print() + testAction.getPc() + ":" + testAction.id);
				continue;
			}

			if (testAction.goToLabel != -1) {
				if (testAction instanceof GotToAction) {
					GotToAction gotTo = (GotToAction) testAction;
					if (gotTo.isBreak() || gotTo.isContinue) {

						if (gotTo.isBreak()) {
							int debugME = 0;
						}
						if (gotTo.isContinue) {

							ArrayList<Action> dubiousContinuesForThisWhile = dubiousContinues
									.get(currentInfo.currentWhile.getPc());
							if (dubiousContinuesForThisWhile != null) {
								for (Action actg : dubiousContinuesForThisWhile) {
									outStream.println(pad + actg.print() + ";//dubious");
								}
							}

						}
						if ((!(currentInfo.currentLevel instanceof SwitchAction))
								&& !currentInfo.currentLevel.isWhile) {
							currentInfo.currentLevel.goToLabel = gotTo.getPc() + 1;
						}
						outStream.println(pad + testAction.print() + "//" + testAction.getPc() + ":" + testAction.id );
						/*
						 * TODO if (currentLevel.goToLabel > gotTo.nextAction.getPc()) { if
						 * (currentLevel instanceof IfAction) { currentLevel.goToLabel =
						 * gotTo.nextAction.getPc() - 1; }
						 * 
						 * }
						 */
						continue;
					} else if (gotTo.isWhile) {

						currentInfo.dropWhile(gotTo);

					} else {
						currentInfo.currentLevel.goToLabel = gotTo.getPc();
						currentInfo.currentLevel.elseTo = gotTo.goToLabel;
						continue;

					}
				} else {

				}
				if (testAction.getPc() > testAction.goToLabel) {
					continue;
				}
				currentInfo.pushLevel(testAction);

				if (testAction instanceof TryAction) {
					TryAction tryIt = (TryAction) testAction;
					if (tryIt.isFunky) {
						testAction = iterator.next();
						String extraInfo = "";
						if (testAction instanceof AssignAction) {
							AssignAction assignAction = (AssignAction) testAction;
							oliver.action.Action lhs = assignAction.leftHS;

							if ((lhs instanceof VariableAction)) {

								VariableAction valueAction = (VariableAction) lhs;

								String assignStr = lhs.print().trim();
								if (!currentInfo.currentVariables.contains(assignStr)) {
									currentInfo.currentVariables.add(assignStr);
									extraInfo = valueAction.getType() + " ";
								}
							}
						}
						outStream.println(pad + "try (" + extraInfo + " " + testAction.print() + "){ //"
								+ testAction.getPc() + ":" + testAction.id + "->" + testAction.goToLabel);
						pad += "   ";
					} else {
						outStream.println(pad + testAction.print() + "{ //" + testAction.getPc() + ":" + testAction.id + "->"
								+ testAction.goToLabel + " " + testAction.getRealGoto());
						pad += "   ";
					}
				} else {
					outStream.println(pad + testAction.print() + "{ //" + testAction.getPc() + ":" + testAction.id + "->"
							+ testAction.goToLabel + " " + testAction.getRealGoto());
					pad += "   ";
				}

			} else {
				String extraInfo = "";
				if (testAction instanceof AssignAction) {
					AssignAction assignAction = (AssignAction) testAction;
					oliver.action.Action lhs = assignAction.leftHS;

					if ((lhs instanceof VariableAction)) {

						VariableAction valueAction = (VariableAction) lhs;

						String assignStr = lhs.print().trim();
						if (!currentInfo.currentVariables.contains(assignStr)) {
							currentInfo.currentVariables.add(assignStr);
							extraInfo = valueAction.getType() + " ";
							if (extraInfo.trim().equals("")) {
								continue;
							}
						}
						if (valueAction.type.startsWith("Iterator") && valueAction.name.startsWith("unusedVariable")) {
							oliver.action.Action whileActt = (oliver.action.Action) iterator.next();
							if (whileActt instanceof GotToAction) {
								GotToAction gotTo = (GotToAction) whileActt;
								if (gotTo.isWhile) {
									oliver.action.Action getActt = (oliver.action.Action) iterator.next();
									if (getActt instanceof AssignAction) {
										String objType = "Object";
										AssignAction getACT = (AssignAction) (getActt);
										CastAction catAct = null;
										if (!(getACT.rightHS instanceof CastAction)) {

											MethodAction metAct = (MethodAction) getACT.rightHS;
											if (metAct.objAction instanceof CastAction) {
												catAct = (CastAction) metAct.objAction;
												objType = catAct.getCastTo();
											} else {
												int debugMe = 0;
											}
										} else {
											catAct = (CastAction) getACT.rightHS;
											objType = catAct.getCastTo();
										}
                                        if(objType.contains("[")) {
                                        	objType = Variable.getTheType(objType);
                                        	int debume=0;
                                        }
										String objName = getACT.leftHS.name;
										MethodAction metAct = (MethodAction) assignAction.rightHS;
										metAct.objAction.print();
										gotTo.print();
										String methodStr = metAct.objAction.print();
										String newForStr = String.format("for ( %s %s : %s)", objType, objName,
												methodStr);
										outStream.println(pad + newForStr + "{//" + testAction.getPc() + ":" + testAction.id + "->"
												+ gotTo.goToLabel + " " + testAction.getClass().getName());
										currentInfo.pushLevel(gotTo);
										currentInfo.currentVariables.add(objName);

										pad += "   ";
										gotTo.isFor = true;

										if (gotTo.goToLabel == 736) {
											int debugME = 0;
										}
										currentInfo.dropWhile(gotTo);

										continue;

									} else {
										iterator.previous();
									}
								}
							} else {
								iterator.previous();
							}

						} else {
							int debugME = 0;
						}

					}
				}
				
				outStream.println(pad + extraInfo + testAction.print() + ";//" + testAction.getPc() + ":" + testAction.id + " "
						+ testAction.getClass().getName());

			}

		}
		int debugMe = 0;
	}

	public void clear() {
		this.actualStack.clear();

	}
	public void mark2AsDuplicate() {

		Action old = this.actualStack.pop();
				this.markAsDuplicate();
		this.actualStack.push(old);
		this.markAsDuplicate();
	}
	public void markAsDuplicate() {
		int debugME =0;
		if(actualStack.peek() instanceof NewObjectAction || actualStack.peek() instanceof NewArrayAction) {
			
		}else {
			this.actualStack.push(actualStack.peek());
		}
		//actualStack.peek().setValid(false);
	  //this.actualStack.push(actualStack.peek());
//       this.actualStack.peek().markAsDuplicate();
       
	}
}
