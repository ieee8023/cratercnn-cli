package org.dmlc.mxnet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Predictor {
  static {	  
	  System.loadLibrary("mxnet_predict");
  }

  public static class InputNode {
    String key;
    int[] shape;
	public InputNode(String key, int[] shape) {
		this.key = key;
		this.shape = shape;
	}
  }

  public static class Device {
    public enum Type {
      CPU, GPU, CPU_PINNED
    }

	public Device(Type t, int i) {
		this.type = t;
		this.id = i;
	}

    Type type;
    int id;
    int ctype() {
      return this.type == Type.CPU? 1: this.type == Type.GPU? 2: 3;
    }
  }

  private long handle = 0;

  public Predictor(byte[] symbol, byte[] params, Device dev, InputNode[] input) {
	String[] keys = new String[input.length]; 
	int[][] shapes = new int[input.length][];
	for (int i=0; i<input.length; ++i) {
		keys[i] = input[i].key;
		shapes[i] = input[i].shape;
	}
    this.handle = createPredictor(symbol, params, dev.ctype(), dev.id, keys, shapes);
  }

  public void free() {
    if (this.handle != 0) {
      nativeFree(handle);
      this.handle = 0;
    }
  }

  public float[] getOutput(int index) {
    if (this.handle == 0) return null;
    return nativeGetOutput(this.handle, index);
  }


  public void forward(String key, float[] input) {
      if (this.handle == 0) return;
      nativeForward(this.handle, key, input);
  }

  static public float[] inputFromImage(BufferedImage[] bmps) {
    if (bmps.length == 0) return null;

    int width = bmps[0].getWidth();
    int height = bmps[0].getHeight();
    
    float[] buf = new float[height * width * 1 * bmps.length];
    
    
    for (int x=0; x<bmps.length; x++) {
      BufferedImage bmp = bmps[x];
      if (bmp.getWidth() != width || bmp.getHeight() != height)
        return null;

      int start = width * height * 1 * x;
      for (int i=0; i<height; i++) {
        for (int j=0; j<width; j++) {
            int pos = i * width + j;
            int pixel = bmp.getRGB(i, j);
            
            Color c = new Color(pixel);
            float val = (float) ((c.getBlue() + c.getGreen() + c.getRed())/3.0);            
            buf[start + pos] = val;
        }
      }
    }
    
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for(float val : buf){
    	stats.addValue(val);
    	
    }
    
    
//    
//	/* a =0 b = 255
//	 * scale
//	 *         (b-a)(x - min)
//	 *	f(x) = --------------  + a
//     * 		 	max - min
//	 */
//
	float b = 200;
	float a = 50;
	float min = (float) stats.getMin();
	float max = (float) stats.getMax();
	
	//System.out.println("Max: " + max + ", " + "Min: " + min);
	
//	double mean = stats.getMean();
	for(int i = 0; i < buf.length; i++){
		float scaled = ((b-a)*(buf[i] - min))/(max-min);
		//float centered = (float) (buf[i]/mean);
		buf[i] = scaled;
		
	}
    
    //System.out.println(Arrays.toString(buf));
    return buf;
  }

  
  private native static long createPredictor(byte[] symbol, byte[] params, int devType, int devId, String[] keys, int[][] shapes);
  private native static void nativeFree(long handle);
  private native static float[] nativeGetOutput(long handle, int index);
  private native static void nativeForward(long handle, String key, float[] input);
}