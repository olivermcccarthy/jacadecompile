package oliver.neuron;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DrawPanel extends JPanel {

	
	static JButton button = new JButton("Click");
	public DrawPanel(ActionListener listener) {
		button.addActionListener(listener);
		this.add(button);
	}
	static int[][] input = null;
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int baseX = 60;
		int baseY = 60;
		
		if(input != null) {
			int scale =2;
			BufferedImage img = new BufferedImage(pictureWidth*scale, pictureHeight*scale, BufferedImage.TYPE_INT_ARGB);
			
			for(int h =0; h < pictureHeight; h++) {
				for(int w =0; w < pictureWidth; w++) {
					
					if(input[h][w] > 100) {
					  img.setRGB(w, h, Color.BLACK.getRGB());	
					}
					
				}
			}
			

			ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			int numc = space.getNumComponents() ;
		
			float maxX = space.getMinValue(0);
			
			g.drawImage(img, baseX , baseY, pictureWidth*scale, pictureHeight*scale, new ImageObserver() {

				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
					// TODO Auto-generated method stub
					return false;
				}});
			
		}
		int maxLevelSize =0;
		 for(Layer layer : Layer.layers) {
			 if(layer.neurons.size() > 20) {
				 continue;
			 }
			 int size = layer.neurons.size();
			 if(size >  maxLevelSize) {
				 maxLevelSize = size; 
			 }
		 }
		 int screenWidth = 1000;
		int diffX = screenWidth/Layer.layers.size();
		int shiftY=0;
		
		 for(Layer layer : Layer.layers) {
 if(layer.neurons.size() > 20) {
				continue; 
			 }
			 int diffY =0;
			 
			 int startLevel = (maxLevelSize - layer.neurons.size())/2;
			 shiftY= (startLevel *150);
			 diffY+=shiftY;
			 if(layer.neurons.size() > 20) {
				 
			 }else {
		        for(Neuron nu :layer.neurons) {
		    	 
		    	  paintNewronTopX(g, nu,baseX+ diffX,  baseY + diffY);
		    	  diffY += 2330;
		    	  int y =0;
		    	  y ++;
		       }
			 }
		     baseX += diffX;
		   
		 }
		
	}

	public static String getDBL(double value) {
		
		String doubleStr = ""+ (Math.floor(value*1000)/1000);
	  
	    return doubleStr;
	}
	static int neuronWidth =100;
	static int neuronHeight =100;
	protected void paintNewron(Graphics g, Neuron neuron, int baseX, int baseY) {

		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		String textStr = "  " + neuron.name ;
		char[] chararr = textStr.toCharArray();

		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 40);
		
		
		 
		textStr = " b " +getDBL(neuron.bias);
		 chararr = textStr.toCharArray();
		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 55);
		textStr = " err " + getDBL(neuron.errorVar);
		 chararr = textStr.toCharArray();
		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 70);
		
		textStr = " value " + getDBL(neuron.getValue());
		 chararr = textStr.toCharArray();
		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 85);
		g2d.drawRect(baseX, baseY, neuronWidth, neuronHeight);
		
		
		int numInputs = neuron.inputs.size();
		int newY = baseY +15;
		Color [] colors = new Color[] {Color.BLUE, Color.GREEN,Color.RED, Color.YELLOW, Color.PINK};
		int colorIndex = 0;
		
		if (numInputs > 0) {
		
			int nI = 0;
			
			for (Double inpu : neuron.weights) {
				colorIndex ++;
				if(colorIndex >= colors.length) {
					colorIndex =0;
				}
				 Neuron connNu = neuron.inputs.get(nI);
				 
				 //System.out.println(connNu.X + ":" + connNu.Y  + " to" + baseX + ":" + newY);
				 g2d.setColor( colors[colorIndex]);
				 
				 
               
			   
				textStr = " w "+ getDBL(inpu);
				chararr = textStr.toCharArray();
				g2d.setColor(Color.BLACK);
				g2d.drawChars(chararr, 0, chararr.length,baseX -100,newY);
				
			    newY += 15;
				nI++;
			}
		}
	}
	static int pictureHeight = 28;
	
	static int pictureWidth = 28;
	/**
	 * Paint a picture of the inputs neurons contribution to this neurons sigmoid
	 * If we have  100 input neurons we can paint a 10 by ten picture where the first row is neuron 0 to 9.
	 * The neurons with the biggest contribution get the brightest color.
	 * @param g
	 * @param neuron
	 * @param baseX
	 * @param baseY
	 */
	protected void paintInputsInSquare(Graphics g, Neuron neuron, int baseX, int baseY,int pictureHeight,  int pictureWidth) {
		int [] colors = new int[] {Color.WHITE.getRGB(), Color.YELLOW.getRGB(), Color.MAGENTA.getRGB(), Color.PINK.getRGB(),Color.RED.getRGB()};
		
		int [][] colorsI = new int[pictureHeight][pictureWidth];
		int w = 0;
		int h = 0;
		double maxSize =1;
		
		for (int x = 0; x < neuron.weights.size(); x++) {
			double weight = neuron.weights.get(x);
			double input = neuron.inputs.get(x).getValue();
			double mult = weight* input;
			if(mult < 0) {
				mult = mult *-1;
			}
			
			double size = mult*1000;
			if(size > maxSize) {
				maxSize =size;
			}
			colorsI[h][w] = (int)size;
			w ++;
			if(w >= pictureWidth) {
				w = 0;
				h++;
			}
		}
		g.setColor(Color.WHITE);
		int scale =2;
		
		if(pictureWidth == 1) {
			scale=4;
		}
		BufferedImage img = new BufferedImage(pictureWidth*scale, pictureHeight*scale, BufferedImage.TYPE_INT_RGB);
		int normalize =(int)(maxSize/colors.length);
		for(h =0; h < pictureHeight *scale; h++) {
			for(w =0; w < pictureWidth*scale; w++) {
				int colI = colorsI[h/scale][w/scale];
				if(normalize > 0) {
				colI = colI/normalize;
				}
				if(colI == 5) {
					colI =4;
				}
				img.setRGB(w, h, colors[colI]);
			}
		}
		

		g.drawImage(img, baseX -100, baseY, pictureWidth*scale, pictureHeight*scale, new ImageObserver() {

			@Override
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
				// TODO Auto-generated method stub
				return false;
			}});
		
		int df =0;
	}
	protected void paintNewronTopX(Graphics g, Neuron neuron, int baseX, int baseY) {

		
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		String textStr = "  " + neuron.name ;
		char[] chararr = textStr.toCharArray();

		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 40);
		
		
		 
		textStr = " b " + getDBL(neuron.bias);
		 chararr = textStr.toCharArray();
		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 55);
		textStr = " err " + getDBL(neuron.errorVar);
		 chararr = textStr.toCharArray();
		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 70);
		
		textStr = " value " + getDBL(neuron.getValue());
		 chararr = textStr.toCharArray();
		g2d.drawChars(chararr, 0, chararr.length, baseX + 10, baseY + 85);
		g2d.drawRect(baseX, baseY, neuronWidth, neuronHeight);
		
		
		int numInputs = neuron.inputs.size();
		int newY = baseY +15;
		Color [] colors = new Color[] {Color.BLUE, Color.GREEN,Color.RED, Color.YELLOW, Color.PINK};
		int colorIndex = 0;
		
		g2d.setFont(new Font("Monaco", Font.PLAIN, 10));
		if(numInputs > 100) {
			paintInputsInSquare(g, neuron, baseX, baseY,28,28);
			return;
		}
		if(numInputs > 10) {
			paintInputsInSquare(g, neuron, baseX, baseY,numInputs,1);
			return;
			
		}
		if (numInputs > 0) {
		
			int nI = 0;
			
			for (int n =0 ; n < neuron.inputs.size(); n++) {
				colorIndex ++;
				if(colorIndex >= colors.length) {
					colorIndex =0;
				}
				 Neuron connNu = neuron.inputs.get(n);
				 Double inpu = neuron.weights.get(n);
				 //System.out.println(connNu.X + ":" + connNu.Y  + " to" + baseX + ":" + newY);
				 g2d.setColor( colors[colorIndex]);
				 
				 
				//    g2d.drawLine(connNu.X, connNu.Y, baseX-60,  newY);
				 
               
			   
				textStr = " "+ getDBL(connNu.getValue())  +" w " + getDBL(inpu);
				chararr = textStr.toCharArray();
				g2d.setColor(Color.BLACK);
				g2d.drawChars(chararr, 0, chararr.length,baseX -100,newY);
				
			    newY += 15;
				nI++;
			}
		}
	}
	static JFrame frame;
	
	static Object waitForMe = new Object();;
	static boolean dontStop = false;
	static void stopAMinute(String msg) {
		if(dontStop) {
			return;
		}
		 try {
			 button.setText(msg);
			 frame.repaint();
			 synchronized(waitForMe){
					waitForMe.wait(100000);
				}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			int x =0;
			x++;
			
		}
			int x =0;
			x++; 
	}
	static void showNeurons() {
		 frame= new JFrame();
		frame.setSize(1000, 1000);

		
	
		
		
		 
		
		JPanel panel = new DrawPanel(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(waitForMe){
					waitForMe.notifyAll();
				}
				
			}});
		JScrollPane scroll = new JScrollPane(panel);
		panel.setPreferredSize(new Dimension(1000,2000));
		frame.setVisible(true);
		panel.setBackground(Color.white);
		frame.getContentPane().add(scroll);
		
		frame.repaint();
		
	}
}