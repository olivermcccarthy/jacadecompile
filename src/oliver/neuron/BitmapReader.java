package oliver.neuron;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BitmapReader {

	
	public BitmapReader(String file) {
		
		File inFile = new File(file);
		try {
			InputStream inStream = new FileInputStream(inFile);
			byte [] header = new byte[10000];
			int numRead= inStream.read(header);
			int pixelOffset = bufAsInt(header,0xA,2);
			int bitsPerPixel = bufAsInt(header,0x1C,2);
			int width = bufAsInt(header,0x12,2);
			int heigth = bufAsInt(header,0x16,2);
			int numPixels = width *heigth;
			int rowWidth = width*bitsPerPixel;
			int remainder = rowWidth%32;
			// we need to round up to 32;
			remainder = 32 -remainder;
			rowWidth += remainder;
			int numBytes =  (heigth *rowWidth/8);
			long startTime = System.currentTimeMillis();
			for(int trial =0; trial < 100; trial ++) {
			   int [][] rgb = getRGB(header,pixelOffset,  bitsPerPixel, width,heigth);
			}
			long diff = System.currentTimeMillis() - startTime;
			System.out.println("It took " + diff);
			int testMe =0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	int[][] getRGB(byte[] buffer, int startByte, int bitsPerPixel, int width, int heigth){
		int[][] listRGB = new int[heigth][width];
		int bitMask = 0x1;
		for(int i =0; i < bitsPerPixel -1; i++) {
			bitMask = bitMask<<1;
			bitMask = bitMask | 0x1;
		}
		
		int rigthShift = 8 -  bitsPerPixel;
		int bitIndex = 0;
		int byteIndex = startByte;
		
		for(int h =0; h < heigth; h++) {
			
			for(int w =0; w < width; w++) {
				if(bitsPerPixel == 24) {
					int rgb = bufAsInt(buffer,byteIndex,3);
					listRGB[h][w] = rgb;
					byteIndex +=3;
				}
				else if(bitsPerPixel == 8) {
					int rgb =  buffer[byteIndex] &0xff;
					listRGB[h][w] = rgb;
					byteIndex++;
				}
				else if(bitsPerPixel == 1) {
					
					int lastIndex = w +7;
					int numBits =8;
					if(lastIndex >= width) {
						numBits -= (lastIndex -width);
						lastIndex = width -1;	
					}
					for(int bit = 0; bit < numBits; bit ++) {
						int rgb =  buffer[byteIndex] & 0x1;
						buffer[byteIndex] = (byte)(buffer[byteIndex] >>1);
						listRGB[h][lastIndex] = rgb;
						lastIndex --;
					}
					byteIndex ++;
					w += 7;
				}else if(bitsPerPixel ==4) {
					
					int rgb = buffer[byteIndex] & 0xf;
					buffer[byteIndex] = (byte) (buffer[byteIndex] >> 4);
					listRGB[h][w+1] = rgb;
				
					rgb = buffer[byteIndex] & 0xf;
					listRGB[h][w] = rgb;
					byteIndex ++;
					w++;
				}else {
					
					throw new RuntimeException("Bits per pixel " + bitsPerPixel  +" Not supported");
				}
			}
			
			
            int remainder = ((byteIndex - startByte)%4);
            if(remainder != 0) {
            	byteIndex += 4-remainder;
            }
          
            
            bitIndex=0;
            
		}
		
		return listRGB;
		
	}
	public int bufAsInt(byte[] buffer, int startIndex, int length) {
		int i2 = 0;
        
		for (int t = startIndex + length -1; t > startIndex -1; t--) {
			int it = buffer[t] & 0xff;
			i2 = i2 << 8;
			i2 = i2 | it;
		}
		
		return i2;
	}
public static void main(String [] args) {
		
	BitmapReader bmpFile = new BitmapReader("C:\\workarea\\One.bmp");
}
}
