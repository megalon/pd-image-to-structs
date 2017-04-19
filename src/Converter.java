import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class Converter {

	public Converter(){
		
	}
	
	// Open the image file
	public BufferedImage openImageFile(String imagename){
		if(null == imagename)
			return null;
		
		BufferedImage img = null;
		
		try {
			System.out.println("Reading image " + imagename);
		    img = ImageIO.read(new File(imagename));
			System.out.println("Done reading image " + imagename);
			
			return img;
			
		} catch (IOException e) {
			System.out.println("ERROR: Could not find/read image file! " + imagename);
		}
		
		return null;
	}
	
	// Write the image data to the PD file
	public void writePDFile(BufferedImage img, String filename, int tilesize){
		try{
		    PrintWriter writer = new PrintWriter(filename, "UTF-8");
		    
		    int w = img.getWidth();
		    int h = img.getHeight();

			System.out.println("Writing PD file " + filename);
			
			// Build the surrounding patch
			writer.println("#N struct 1003-template float x float y;\n"
					+ "#N canvas 623 177 1178 745 12;\n"
					+ "#X msg 853 385 traverse \\$1 \\, bang;\n"
					+ "#X msg 853 273 clear;\n"
					+ "#X obj 680 203 t b b b;\n"
					+ "#X msg 680 178 1;\n"
					+ "#X obj 853 332 bng 15 250 50 0 empty empty empty 17 7 0 10 -262144 -1 -1;\n"
					+ "#X obj 853 410 pointer;\n"
					+ "#N canvas 1266 536 546 174 \\$0-template 0;");
			
			// Loop through all of the pixels in our image.
			for(int x = 0; x < w; ++x){
				for(int y = 0; y < h; ++y){
					
					// Convert integer RGB to a value between 0 and 9  
					// (16777216 / 9) = 1864135.1111111
					int rgb = img.getRGB(x, y);

					// Get individual R G B values. 
					// They are typically 0 - 255, so we convert to 0 - 9
					int red = ((rgb & 0xFF) * 9) / 256;
					int green = (((rgb >> 8) & 0xFF) * 9) / 256;
					int blue = (((rgb >> 16) & 0xFF) * 9) / 256;
					
					// With PD structs the color needs to be a 3 digit value. 
					// For example, 100% red would be:
					// 		R G B 
					// 		9 0 0
					rgb = red + green * 10 + blue * 100;
					
					writer.println("#X obj " + x*tilesize + " " + y*tilesize + " filledpolygon " + rgb + " " + rgb + " " + 0 + " "
								+ x*tilesize + " "
								+ y*tilesize + " "
								+ (x*tilesize + tilesize) + " "
								+ y*tilesize + " "
								+ (x*tilesize + tilesize) + " "
								+ (y*tilesize + tilesize) + " "
								+ x*tilesize + " "
								+ (y*tilesize + tilesize) + " "
								+ x*tilesize + " "
								+ y*tilesize
								+ ";");
				}
			}
			
			// Build the rest of the patch
			writer.println("#X obj 18 18 struct \\$0-template float x float y;\n"
						+ "#X restore 850 217 pd \\$0-template;\n"
						+ "#X obj 853 303 s pd-\\$0-data;\n"
						+ "#X obj 853 360 symbol pd-\\$0-data;\n"
						+ "#N canvas 147 576 450 300 \\$0-data 0;\n"
						+ "#X scalar 1003-template 0 0 \\;;\n"
						+ "#X coords 0 0 15 15 15 15 2 0 0;\n"
						+ "#X restore 0 0 pd \\$0-data;\n"
						+ "#X obj 680 438 append \\$0-template x y;\n"
						+ "#X obj 680 228 t b b;\n"
						+ "#X msg 680 392 0;\n"
						+ "#X msg 717 392 0;\n"
						+ "#X obj 680 109 inlet on-off;\n"
						+ "#X obj 680 134 sel 1;\n"
						+ "#X connect 0 0 5 0;\n"
						+ "#X connect 1 0 7 0;\n"
						+ "#X connect 2 0 11 0;\n"
						+ "#X connect 2 1 4 0;\n"
						+ "#X connect 2 2 1 0;\n"
						+ "#X connect 3 0 2 0;\n"
						+ "#X connect 4 0 8 0;\n"
						+ "#X connect 5 0 10 2;\n"
						+ "#X connect 8 0 0 0;\n"
						+ "#X connect 11 0 12 0;\n"
						+ "#X connect 11 1 13 0;\n"
						+ "#X connect 12 0 10 0;\n"
						+ "#X connect 13 0 10 1;\n"
						+ "#X connect 14 0 15 0;\n"
						+ "#X connect 15 0 3 0;\n"
						+ "#X connect 15 1 1 0;\n"
						+ "#X coords 0 0 1 1 23 47 2 0 0;");
			
			// Define the graph-on-parent window to fit the size of our image
			writer.println("#X coords 0 0 1 1 " + w * tilesize + " " + h * tilesize + " 2 0 0;");
			
			System.out.println("Done writing PD file " + filename);

		    writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: Could not create pd file! " + filename);
		}
	}
}
