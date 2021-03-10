package oliver.neuron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TruthTable {

	List<TruthRow> rows = new ArrayList<TruthRow>();

	public TruthTable(String intable) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();// 6 oliver.action.AssignAction

		InputStream is2 = classloader.getResourceAsStream("oliver/neuron/" + intable);// 14 oliver.action.AssignAction
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(is2));// 30 oliver.action.AssignAction
		String lien;
		try {
			lien = bufReader.readLine();
			while (lien != null) {
				String first = lien.split(";")[0];
				String second = lien.split(";")[1];
				this.addRow(first, second);
				lien = bufReader.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	static String printArr(double [] inputs) {
		String ret = "";
		for (int x = 0; x < inputs.length; x++) {
			ret += inputs[x] + " ";
		}
		return ret;
	}
	class TruthRow {
		double[] inpputs;
		double[] outputs;

		public TruthRow(String in, String out) {
			int x = 0;
			this.inpputs = new double[in.split(",").length];
			for (String b : in.split(",")) {
				this.inpputs[x] = Double.valueOf(b);
				x++;
			}
			x = 0;
			this.outputs = new double[out.split(",").length];
			for (String b : out.split(",")) {
				this.outputs[x] =  Double.valueOf(b);
				x++;
			}
		}
		
		public boolean matches(double[] inputs) {

			for (int x = 0; x < this.inpputs.length; x++) {
				if (this.inpputs[x] != inputs[x]) {
					return false;
				}
			}
			return true;
		}

		public double normalize() {
			double ret =0;
			double max = Math.pow(2, this.outputs.length);
			for(int o =0; o < outputs.length; o++) {
				
				ret *=2;
				ret += outputs[o];
			}
			
			if(ret == 0) {
				return 0.01;
			}
			return ret/max;
		}
		public boolean matchesOut(double[] outputs) {

			
			
			for (int x = 0; x < this.outputs.length; x++) {
				double fl = Math.round(outputs[x]);
				if (this.outputs[x] != fl) {
					return false;
				}
			}
			return true;
		}

	
		public String toString() {
			String ret = printArr(this.inpputs);
			ret += "    ";
			ret += printArr(this.outputs);
			return ret;
		}
	}

	public boolean matches(double[] inputs, double outputs[]) {
		for (TruthRow row : this.rows) {
            if(row.matches(inputs)) {
            	if(row.matchesOut(outputs)) {
            		return true;
            	}
            	return false;
            }
		}
		return false;
	}
	public double[] getOuts(double[] inputs) {
		for (TruthRow row : this.rows) {
            if(row.matches(inputs)) {
            	return row.outputs;
            }
		}
		return null;
	}
	public void addRow(String inputs, String outputs) {
		TruthRow newRow = new TruthRow(inputs, outputs);
		this.rows.add(newRow);

	}

	public String toString() {
		String ret = "";
		for (TruthRow row : this.rows) {
			ret += row.toString() + "\n";
		}
		return ret;
	}
}
