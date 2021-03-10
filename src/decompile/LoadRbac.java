package decompile;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadRbac {

	public static void main(String[] args) {
		InputStream is2;
		try {
			is2 = new FileInputStream("src/decompile/role.yaml");
			int h = 0x53;
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(is2));
			String line = bufReader.readLine();
			
			while (line != null) {
				
				if(line.contains("apiGroups")) {
					line = bufReader.readLine();
					
					String group = line.replaceAll("  - ", "");
					line = bufReader.readLine();
					String resources="";
					line = bufReader.readLine();
					while (!line.contains("verb")) {
						resources+= line.replaceAll("  - ", ";");
						line = bufReader.readLine();
					}
					line = bufReader.readLine();
					String verbs="";
					while (line != null && !line.contains("apiGroups")) {
						verbs+= line.replaceAll("  - ", ";");
						line = bufReader.readLine();
					}
					System.out.println(String.format("+kubebuilder:rbac:groups=%s,resources=%s,verbs=%s", group,resources.substring(1),verbs.substring(1)));
					continue;
				}
				line = bufReader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
