import java.awt.image.BufferedImage;

class Candidate {
	
	int x, y, d; 
	float[] label; 
	public BufferedImage img;
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "," + d + "]";
	}
	
}