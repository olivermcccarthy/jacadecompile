package oliver.neuron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunCmd {
	public static void runLocalCommand(String cmd) throws IOException {
		
		ProcessBuilder pb = null;
		if (cmd.contains("|")) {
			pb = new ProcessBuilder(new String[]{"/bin/sh", "-c", cmd});
		} else {
			pb = new ProcessBuilder(cmd.split("\\s+"));
		}

		

		Process proc = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		StringBuilder sb = new StringBuilder();

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line + "\n");
		}

		br.close();
		String result = sb.toString().trim();
	

		try {
			proc.waitFor();
		} catch (InterruptedException arg8) {
			;
		}

		int retVal = proc.exitValue();
		

		System.out.println(result);
		
	}
	
	public static void main(String [] args) {
		
		String cm = "";
		for (String d : args) {
			cm += d + " ";
		}
		try {
			runLocalCommand(cm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
