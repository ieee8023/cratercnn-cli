import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.dmlc.mxnet.Predictor;
import org.json.JSONException;
import org.json.JSONObject;

public class SlidingWindow {

	static BufferedImage image;
	public static void main(String[] args) throws Exception {
		String[] args2 = {"data/tile3_24s.png"};
		//String[] args2 = {"data/test2.png"};

		//args = args2;
		
		
		NavigableImagePanel panel2 = null;
		
		String filename = args[0];
		image = ImageIO.read(new File(filename));
//		if (BufferedImage.TYPE_BYTE_GRAY == image.getType()){
//			System.out.println("TYPE_BYTE_GRAY, OK");
//		}else{
//			throw new Exception("not gray image");
//		}
		
		
		
		
		//String model = "crater-inception-bn-fold10-100";
		
		String model = "cratercnn";
		int size = 15;
		
		if (args.length == 2){
			model = args[1];
		}

		System.err.println("Loaded Image: " + filename + ", using " + model + " model");
		
		long starttime = System.currentTimeMillis();
		
		
		
		
		final byte[] symbol = Util.readRawFile("models/" + model + "/symbol.json");
		final byte[] params = Util.readRawFile("models/" + model + "/params");
		final Predictor.Device device = new Predictor.Device(Predictor.Device.Type.CPU, 0);
		final int[] shape = { 1, 1, size, size };
		final String key = "data";
		final Predictor.InputNode node = new Predictor.InputNode(key, shape);

		Predictor predictor = new Predictor(symbol, params, device, new Predictor.InputNode[] { node });


		
		int height = image.getHeight();
		int width = image.getWidth();
		
		int craters = 0;
		long start = System.currentTimeMillis();
		int maxd = 45;
		int border = 0;
		for (int y = border; y < height-border; y+=2){
			for (int x = border; x < width-border; x+=2){
				ArrayList<Candidate> thispoint = new ArrayList<>();
				for (int d = 20; d < maxd; d+=5){
					
					Candidate c = new Candidate(); c.x = x; c.y = y; c.d = d;
					
					try{
					int xx = c.x-(c.d/2);
					int yy = c.y-(c.d/2);
					int dd = c.d;
					
					//System.out.println(xx + "," + yy + "," + dd);
					c.img = image.getSubimage(xx,yy, dd,dd);
					}catch(Exception e){
						continue;
					}
					resultsB.add(c);
					
					
					//c.img = ImageIO.read(new File("data/1_24-tp-726-524-51.png"));
					//c.img = ImageIO.read(new File("data/1_24-tn-1013-1388-49.png"));
					
					
					BufferedImage input = Util.processBitmap(c.img, size);

//					 NavigableImagePanel panel = new NavigableImagePanel(input);
//					 panel.setHighQualityRenderingEnabled(false);
//					 panel.setZoomDevice(NavigableImagePanel.ZoomDevice.NONE);
//					 panel.setZoom(2.0);
//					 JFrame frame = new JFrame();
//					 frame.setSize(1000, 1000);
//					 frame.add(panel);
//					 frame.setVisible(true);
//					 Thread.sleep(500);

			    	float[] colors = Predictor.inputFromImage(new BufferedImage[]{input});
			    	
			        predictor.forward("data", colors);
			        //final float[] result = predictor.getOutput(0);

			        
			        //Output[] results = new Output[result.length];
					
			        
					//c.img = Util.processBitmap(c.img)
			        float[] out = predictor.getOutput(0);
					//System.out.println(Arrays.toString(out));
					
					
					c.label = out;
					//resultsB.add(c);
					thispoint.add(c);
					
					
					
//					if (0.90 < c.label[1]){
//						System.out.println(Arrays.toString(out));
//						if (panel2 == null){
//							panel2 = new NavigableImagePanel(input);
//							panel2.setHighQualityRenderingEnabled(false);
//							panel2.setZoomDevice(NavigableImagePanel.ZoomDevice.NONE);
//							panel2.setZoom(2.0);
//							JFrame frame = new JFrame();
//							frame.setSize(500, 500);
//							frame.add(panel2);
//							frame.setVisible(true);
//						}else
//							panel2.setImage(input);
//					}
					

				}
				
				Candidate mean = new Candidate(); mean.x = x; mean.y = y; mean.d = 1;
				mean.label = new float[]{0,0};
				for (Candidate c : thispoint){
					mean.label[0] += c.label[0];
					mean.label[1] += c.label[1];
				}
				mean.label[0]/=(thispoint.size()*1.0);
				mean.label[1]/=(thispoint.size()*1.0);
				
				resultsB.add(mean);
				show();
			}
		}
		
		
		show();
	}
	
	
	
	
	static public ArrayList<Candidate> resultsB = new ArrayList<Candidate>();
	static NavigableImagePanel panel = null;
	public static void show() {

		try{
		
			boolean show = true;
			
			if (!show) return;
			
			BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			copy.createGraphics().drawImage(image, 0, 0, copy.getWidth(), copy.getHeight(), null);
			
			Graphics g = copy.getGraphics();
	
//			for (Candidate c: resultsG){
//				c = CraterCNNUtil.getImageCandidate(c);
//				g.setColor(Color.green);
//				g.drawOval(c.x,c.y,c.d,c.d);
//			}
//			
			for (Candidate c: resultsB){
				//System.out.println(c.label[0]*250);
				
				//if (c.label[0] < c.label[1]){
				g.setColor(new Color((int)(Math.max(c.label[1]*255,0)),0,0));
//				int x = (int) (c.x+(c.d/2.0));
//				g.drawOval(x,(int)(c.y+(c.d/2.0)),c.d,c.d);//c.d,c.d);
				//}
				
				if (c.label[0]*10 < c.label[1]){
					int xx = c.x-(c.d/2);
					int yy = c.y-(c.d/2);
					int dd = c.d;
					
					g.drawRect(xx,yy,dd,dd);
					
				}
				g.drawLine(c.x,c.y,c.x,c.y);
			}
			
//			for (Candidate c: resultsR){
//				c = CraterCNNUtil.getImageCandidate(c);
//				g.setColor(Color.green);
//				g.drawRect(c.x,c.y,c.d,c.d);
//			}
//			
//			for (Candidate c: resultsRC){
//				c = CraterCNNUtil.getImageCandidate(c);
//				g.setColor(Color.red);
//				g.drawOval(c.x,c.y,c.d,c.d);
//			}
//			
			
			if (panel == null){
				panel = new NavigableImagePanel(copy);
				panel.setHighQualityRenderingEnabled(false);
				panel.setZoomDevice(NavigableImagePanel.ZoomDevice.NONE);
				panel.setZoom(2.0);
				JFrame frame = new JFrame();
				frame.setSize(1000, 1000);
				frame.add(panel);
				frame.setVisible(true);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			}else
				panel.setImage(copy);
		}catch(Exception e){
			System.err.println("Cannot show");
			e.printStackTrace();
		}
			
	}

}
