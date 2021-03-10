package decompile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CopyYaml {

	public static ArrayList<String> getLines(String file) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader bufReader = new BufferedReader(new FileReader(file));
		String line = bufReader.readLine();
		while (line != null) {
			lines.add(line);
			line = bufReader.readLine();
		}
		return lines;

	}

	public static void updateFile(String file, String yaml, String replaceFile, int indent) throws Exception {
		BufferedReader bufReader = new BufferedReader(new FileReader(file));
		FileWriter outwriter = new FileWriter(replaceFile + ".out");
		String line;

		line = bufReader.readLine();
		String indentString = "";
		for (int c = 0; c < indent; c++) {
			indentString += " ";
		}
		String[] lookfor = yaml.split("\\.");
		int lookforI = 0;

		ArrayList<String> lines = new ArrayList<String>();
		String firstIndent = "";
		while (line != null) {

			if (line.startsWith(firstIndent + lookfor[lookforI])) {
				if (lookforI < lookfor.length - 1) {
					firstIndent += indentString;
					lookforI++;
				} else {
					firstIndent += indentString;
					line = bufReader.readLine();
					while (line != null && line.startsWith(firstIndent)) {
						lines.add(line);
						line = bufReader.readLine();
					}
					break;
				}
			}
			line = bufReader.readLine();
		}

		bufReader = new BufferedReader(new FileReader(replaceFile));
		line = bufReader.readLine();
		lookforI = 0;

		firstIndent = "";
		while (line != null) {
			outwriter.write(line +"\n");
			if (line.startsWith(firstIndent + lookfor[lookforI])) {
				if (lookforI < lookfor.length - 1) {
					firstIndent += indentString;
					lookforI++;
				} else {
					firstIndent += indentString;
					
					line = bufReader.readLine();
					
					while (line != null && line.startsWith(firstIndent)) {
						line = bufReader.readLine();
					}
					for(String newLine : lines) {
						outwriter.write(newLine +"\n");
					}
					outwriter.write(line +"\n");
				
				}
			}
			line = bufReader.readLine();
			
		}

		outwriter.flush();

	}

	public static void main(String[] args) {
		try {
			updateFile(args[0], args[1], args[2], Integer.valueOf(args[3]));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
