package imageanalysis;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage; 
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Snapshot class
 * <p>contains general input image accessors and mutators.</p>
 * <p>image processing functions are done here</p>
 * 
 * 
 * @author Adam Goh
 *
 */
public class Snapshot {
	private static BufferedImage image;
	private Mat mat;
	private MatToBufferedImage conv;	//do you need this?
	private Histogram hist;
	
	
	//DONE: FEED THEM THE KIDS Reply: NOO
	//constructor
	public Snapshot(){
		
	}
	
	public void SetImage(String filename) throws IOException{
		//loads image from src file
		image = ImageIO.read(new File(filename));
	}
	/**
	 * Initialize a histogram with the given input image and assign it into local variable.
	 * @param img a BufferedImage type variable
	 */
	public static void SetImage(BufferedImage img){
		image = img;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	/**
	 * <p>Loads image available in the class. If none is available, return error.  </p>
	 * <p>If specific coords are given, a cropped image will be produced </p>
	 * @param xstart starting coordinate for x-axis
	 * @param xend (optional) ending coordinate for x-axis. will be end of image if not provided
	 * @param ystart starting coordinate for y-axis
	 * @param yend (optional) ending coordinate for y-axis. Will be end of image if not provided
	 * 
	 * @return	opens the image in a JFrame window
	 **/
	public static void ViewImage(String name){

		JFrame jf = new JFrame();
		if(image == null){
			//TODO: better catch this dude with a proper warrant/exception
			System.err.println("No image found!");
		}
		jf.setSize(image.getWidth(), image.getHeight());
		jf.setTitle(name);
		ColorPanel panel = new ColorPanel(image);
		jf.setContentPane(panel);
		jf.setVisible(true);
	}
	
	public static void ViewImage(int xstart, int xend, int ystart, int yend){

		JFrame jf = new JFrame();
		if(image == null){
			//TODO: better catch this dude with a proper warrant/exception
			System.err.println("No image found!");
		}
		image = image.getSubimage(xstart, ystart, xend-xstart, yend-ystart);
		jf.setSize(image.getWidth(), image.getHeight());
		jf.setTitle("Image");
		ColorPanel panel = new ColorPanel(image);
		jf.setContentPane(panel);
		jf.pack();
		jf.setVisible(true);
	}
	
	public static void ViewImage(BufferedImage img, String title){
		JFrame jf = new JFrame();
		if(img == null){
			//TODO: better catch this dude with a proper warrant/exception
			System.err.println("No image found!");
		}
		jf.setSize(img.getWidth(), img.getHeight());
		jf.setTitle(title);
		ColorPanel panel = new ColorPanel(img);
		jf.setContentPane(panel);
		jf.pack();
		jf.setVisible(true);
	}
	
	
	//DONE: do this
	/**
	 * <p> Save image as the given filename at the given savepath.</p>
	 * @param filename	the file name
	 * @param savepath  the file path. It is relative to the source code's path
	 * @return a boolean value to check if ImageIO's write function succeed
	 * @throws IOException error in sving image with the given image, extension.
	 */
//	public static boolean SaveImage(String filename, String savepath) throws IOException{	deleted for wrong logic
//		File outputfile = new File(savepath + filename + ".jpg");
//		if(	ImageIO.write(image, "jpg", outputfile))	return true;
//		
//		return false;
//	}
	
	public static boolean SaveImage(BufferedImage img, String filename, String savepath)
			throws FileNotFoundException,IOException{
		
		File outputfile = new File(savepath + filename + ".jpg");
		if(ImageIO.write(img, "jpg", outputfile))	return true;
		
		return false;
	}
	
	public Mat getMatFromImage(BufferedImage img){
		Mat mat = new Mat();
		byte[] data = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		
		return mat;
	}
	
	public Mat setMatBW(Mat mat){
		Mat bw = new Mat();
		Imgproc.threshold(mat, bw, 130, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
		
		return bw;
	}
	
	/**
	 * Scales image to a given input size
	 * @param src	image to be resized
	 * @param w		desired size of image width
	 * @param h		desired size of image height
	 * @return		resized image in BufferedImage type
	 */
	public static BufferedImage getScaledImage(BufferedImage src, int w, int h){
	    int finalw = w;
	    int finalh = h;
	    double factor = 1.0d;
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor);                
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor);
	    }   

	    BufferedImage resizedImg = new BufferedImage(finalw, finalh, Transparency.TRANSLUCENT);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(src, 0, 0, finalw, finalh, null);
	    g2.dispose();
	    return resizedImg;
	}

	/**
	 * Scales image to a given input size
	 * @param src	image to be resized
	 * @param scale a double typed scaling number for the desired resized image.
	 * @return		resized image in BufferedImage type
	 */
	public static BufferedImage getScaledImage(BufferedImage src, double scale){
	    int finalw = (int) (src.getWidth()*scale);
	    int finalh = (int) (src.getHeight()*scale);
	    double factor = 1.0d;
	    if(src.getWidth() > src.getHeight()){
	        factor = ((double)src.getHeight()/(double)src.getWidth());
	        finalh = (int)(finalw * factor);                
	    }else{
	        factor = ((double)src.getWidth()/(double)src.getHeight());
	        finalw = (int)(finalh * factor);
	    }   

	    BufferedImage resizedImg = new BufferedImage(finalw, finalh, Transparency.TRANSLUCENT);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(src, 0, 0, finalw, finalh, null);
	    g2.dispose();
	    return resizedImg;
		
	}
	
	public static BufferedImage getCropImage(BufferedImage img, int xstart, int xend, int ystart, int yend){

		if(img == null){
			//TODO: better catch this dude with a proper warrant/exception
			System.err.println("No image found!");
		}
		
		img = img.getSubimage(xstart, ystart, xend-xstart, yend-ystart);
		return img;
	}
	
	
	public static void displayImage(Mat mat, String windowcaption) throws IOException{
		MatToBufferedImage conv = new MatToBufferedImage();
		BufferedImage image = conv.getImage(mat);
		SetImage(image);
		ViewImage(windowcaption);
	}
	
//	
//	
//	public static void displayImage(Mat mat, String windowcaption, boolean savepic) throws IOException{
//		if(!savepic){
//			displayImage(mat,windowcaption);
//		}else{
//			MatToBufferedImage conv = new MatToBufferedImage();
//			BufferedImage image = conv.getImage(mat);
//			SetImage(image);
//			SaveImage(img, filename, savepath)
//			ViewImage(windowcaption);
//		
//		}
//	}
	
	/**
	 * Returns details about the input image. In a printed text box. The provided details are width, height, area and ratio
	 * 
	 * @param img	input image
	 * @return	resulting text with provided details
	 */
		
	public static String getImageDetail(BufferedImage img){
		return "====== Image details  ====== \n" +
				"image width: " + img.getWidth() +"\n" + 
				"image height:" + img.getHeight() + "\n" + 
				"image area: "  + img.getWidth()*img.getHeight() + "\n" +
				"image ratio: "   + (double) img.getWidth()/(double)img.getHeight() + "\n" + 
				"===========================\n";
	}
	

	/**
	 * Returns details about the input image. In a printed text box. The provided details are width, height, area and ratio
	 * 
	 * @param img	input image
	 * @return	resulting text with provided details
	 */
	public static String getMatDetail(Mat mat){
		return "====== Image details  ====== \n" +
				"image width: " + mat.width() +"\n" + 
				"image height:" + mat.height() + "\n" + 
				"image area: "  + mat.width()*mat.height() + "\n" +
				"image ratio: "   + (double) mat.width()/(double)mat.height() + "\n" + 
				"===========================\n";
	}


/**
 * <p> ColorPanel class <p>
 * <p> overrides the original paintcomponent method to draw BufferedImage.
 *  allows BufferedImage to be set onto the panel						</p>
 * @author dolphin
 *
 */
public static class ColorPanel extends JPanel{
	BufferedImage img;
	
	public ColorPanel(BufferedImage image){
		img = image;
	}
	
	public void setImage(BufferedImage image){
		img = image;
	}
 
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(img, null, 0,0);
	}
}
}

