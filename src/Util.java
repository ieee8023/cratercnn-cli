

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Util {
	
    public static BufferedImage processBitmap(final BufferedImage origin, int size) {

        final Image scaledr = origin.getScaledInstance(size, size, Image.SCALE_SMOOTH);
        
		BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		
		scaled.getGraphics().drawImage(scaledr, 0, 0, null);
        
		return scaled;
		
    }

	public static byte[] readRawFile(String name) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int size = 0;
		byte[] buffer = new byte[1024];
		try (InputStream ins = new FileInputStream(name)) {
			while ((size = ins.read(buffer, 0, 1024)) >= 0) {
				outputStream.write(buffer, 0, size);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}

	public static List<String> readRawTextFile(String name) throws FileNotFoundException {
		List<String> result = new ArrayList<>();
		InputStream inputStream = new FileInputStream(name);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;

		try {
			while ((line = buffreader.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			return null;
		}
		return result;
	}
	
	public static String smallNum(double in){
		
		return String.format("%.5f", in);
	}
    
}
