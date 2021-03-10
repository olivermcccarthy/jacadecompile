package oliver.neuron;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import oliver.neuron.TruthTable.TruthRow;
import oliver.neuron.pd.Equation;
import oliver.neuron.pd.Result;

public class RunEquation {

	
	
	public static double [] randomVals(int num, int max, int min) {
		
		double [] res = new double[num];
		double range = max -min;
		for(int index =0; index < num; index ++) {
			double rand = Math.random() *range;
			
			rand += min;
			rand = Math.floor(rand);
			res [index] = rand;
		}
		
		return res;
		
	}
	public static void main(String [] args) {
		
		File bmpFile = new File("C:\\workarea\\One.bmp");
		try {
			BufferedImage image = ImageIO.read(bmpFile);
			int height = image.getHeight();
			ColorModel cm =image.getColorModel();
			
			int width = image.getWidth();
			
			int[][] values = new int[height][width];
			for(int h =0; h< height; h ++) {
				for(int w =0; w< width; w ++) {
					values[h][w] =image.getRGB(w, h);
					
				}	
			}
			
			int x =0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Result result = new Result();
		TruthTable tt = new TruthTable("adder.csv");
		for(int x =0; x < 10; x++) {
		for (TruthRow tr : tt.rows) {
			double [] inpus = tr.inpputs;
			double res = tr.normalize();
			  result.addResult(inpus, res);
		}
		}
		/// 3,2,4
		//  1,1,1  = 3 + 2 + 4/Sqrt3 
		//result.print();
		long startTime= System.currentTimeMillis();
		Equation trialEq = new Equation("x + x*y + y + x*z + z + y*z");
			
		
		
		double [] solved = result.solveEquation(trialEq);
		long diff = System.currentTimeMillis() - startTime;
	System.out.println(trialEq  + " solved " +Equation.toString(solved) +" in " + diff);
	}
}
