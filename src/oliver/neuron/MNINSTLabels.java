package oliver.neuron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
/**
 * Load the MNINST labels ( A list of numbers equating to the actual values of the 60000 images in MNINST data)
 * @author oliver
 *
 */
public class MNINSTLabels {

	int[] labels;
	public MNINSTLabels(String file) {
		
		File inFile = new File(file);
		try {
			InputStream inStream = new FileInputStream("C:\\workarea\\train-labels.idx1-ubyte");
			byte [] header = new byte[70000];
			int numRead= inStream.read(header);
			int numImages = bufAsInt(header,4,4);
			
			int byteIndex =8;
			long startTime = System.currentTimeMillis();
			labels = new int[numImages];
			for(int image =0; image < numImages; image ++) {
				labels[image] = header[byteIndex] & 0xff;
				// System.out.println("Image " + image);
				byteIndex ++;
			}
			long diff = System.currentTimeMillis() - startTime;
			System.out.println("It took " + diff);
			int f =0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public int bufAsInt(byte[] buffer, int startIndex, int length) {
		int i2 = 0;
        
		for (int t = 0; t < startIndex + length; t++) {
			int it = buffer[t] & 0xff;
			i2 = i2 << 8;
			i2 = i2 | it;
		}
		
		return i2;
	}
public double[] asDoubleArray(){
		
		double[] doubleArray = new double[labels.length];
		for(int image =0 ; image < labels.length; image ++) {
			doubleArray[image] = labels[image];
			doubleArray[image] = doubleArray[image]/9;
		}
		return doubleArray;
	}
	public double[][] asTenBitArray(){
		
		double[][] tenBitArray = new double[labels.length][10];
		for(int image =0 ; image < labels.length; image ++) {
			tenBitArray[image][labels[image]] =1;
		}
		return tenBitArray;
	}
public static void main(String [] args) {
		
	MNINSTLabels bmpFile = new MNINSTLabels("C:\\workarea\\train-labels.idx1-ubyte");
}
}
