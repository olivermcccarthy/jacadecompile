package decompile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;

import oliver.ExceptionHandler;
import oliver.action.IfAction;

public class ByteCodeParser {

	public static HashMap<Integer, ByteCode> codes = new HashMap<Integer, ByteCode>();

	public static void loadCodes() {
		try {
			InputStream is2 = new FileInputStream("src/oliver/bytecode.csv");
			int h = 0x53;
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(is2));
			String line = bufReader.readLine();
			int numUnCoded = 0;
			while (line != null) {

				for (int x = 0; x < 20; x++) {
					line = line.replaceAll("\"([^\"]+),([^\"]+)\"", "\"$1comma$2\"");
				}
				System.out.println(line);

				String[] split = line.split(",");

				ByteCode code = new ByteCode(split[0], Integer.valueOf(split[1], 16), split[3], split[4], split[5]);
				if (code.type == null) {
					numUnCoded++;
				}
				line = bufReader.readLine();
				codes.put(code.code, code);
			}
			System.out.println(String.format(" %d codes uncoded %d ", codes.size(), numUnCoded));
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (ByteCode bCode : codes.values()) {
			if (bCode.type == null) {
				System.out.println(bCode.name + ": " + bCode.instruction);
			}
		}
		System.out.println("hello");
	}

	public static void main(String[] args) {

		loadCodes();

	}

	private static List<JavaAction> getSortedCommands(Stack<JavaAction> stack, ArrayList<JavaAction> store) {

		List<JavaAction> allActions = new ArrayList<JavaAction>();
		allActions.addAll(store);
		allActions.addAll(stack);
		Collections.sort(allActions);

		return allActions;
	}

	private static void howDidIgetHere(ArrayList<JavaAction> store) {

	}

	public static ActionBranch parent = null;

	public static void decompileAll(byte[] buf, int size, String clazzName, String methodName, boolean returnsBoolean) {

		int x = 0;
		Stack<JavaAction> stack = new Stack<JavaAction>();
		ArrayList<JavaAction> store = new ArrayList<JavaAction>();
		List<Integer> sortedPC = ExceptionHandler.getSortedPC();
		if(methodName.equals("printHeading") && clazzName.equals("ActionBranch")) {
			int debugME =0;
		}
		ByteCode.lastGoTo = -1;
		while (x < size) {
			int code = buf[x] & 0xff;
			try {
				boolean wide = false;
				if (code == 0xc4) {
					wide = true;
					x++;
					code = buf[x] & 0xff;
				}
				if (x == 2060) {
					int debugME = 0;
				}
				if (code == 0) {
					x++;
					continue;
				}
				ByteCode bCode = codes.get(code);
				System.out.println(x + " : " + bCode.instruction);
				if (x == 652) {
					int debugME = 0;
				}
				if (sortedPC.contains(x)) {
					int debugME = 0;
					ExceptionHandler.handlePC(stack, store, x, x);

				}

				x = bCode.decompile(buf, x, stack, store, wide,returnsBoolean);
			} catch (Exception e) {
				x = 0;
				stack = new Stack<JavaAction>();
				store = new ArrayList<JavaAction>();
				while (x < size) {
					boolean wide = false;
					if (code == 0xc4) {
						wide = true;
						x++;
						code = buf[x] & 0xff;
					}
					if (x == 1993) {
						int debugME = 0;
					}
					code = buf[x] & 0xff;
					ByteCode bCode = codes.get(code);
					System.out.println(x + " : " + bCode.instruction);
					if (x == 652) {
						int debugME = 0;
					}
					x = bCode.decompile(buf, x, stack, store, wide,returnsBoolean);
				}
				e.printStackTrace();
			}
		}
		
		List<JavaAction> allActions = getSortedCommands(stack, store);
		
		
	
		for (x = 0; x < allActions.size(); x++) {
			JavaAction action = allActions.get(x);
			action.line = x;
			if(action.goTo != -1) {
				ActionBranch.findGoTO(allActions, x);
			}
		}
	
		if(methodName.equals("dropLevel")) {
			int debugME =0;
		}
		ActionBranch.findWhiles(allActions);
		parent =new ActionBranch();
		ActionBranch.findIfs(allActions,parent ,0,allActions.size());
		parent.findElses();
		
	}
}
