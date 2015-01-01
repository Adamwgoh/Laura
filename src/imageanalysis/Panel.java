package imageanalysis;

import java.awt.*;  
import java.awt.image.BufferedImage;  
import javax.swing.*;  
import org.opencv.core.Core;
import org.opencv.core.Mat;  
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;  
import org.opencv.imgproc.Imgproc;

public class Panel extends JPanel{  
	private static final long serialVersionUID = 1L;  
	private BufferedImage image;  
	// Create a constructor method  
	public Panel(){  
		super();  
	}  
	private BufferedImage getimage(){  
		return image;  
	}  
	private void setimage(BufferedImage newimage){  
		image=newimage;  
		return;  
	}  
	/**  
	 * Converts/writes a Mat into a BufferedImage.  
	 *  
	 * @param matrix Mat of type CV_8UC3 or CV_8UC1  
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY  
	 */  
	public static BufferedImage matToBufferedImage(Mat matrix) {  
		int cols = matrix.cols();  
		int rows = matrix.rows();  
		int elemSize = (int)matrix.elemSize();  
		//convert Mat into bytesize data
		byte[] data = new byte[cols * rows * elemSize];  
		int type;  
		matrix.get(0, 0, data);  
		switch (matrix.channels()) {  
		case 1:  
			type = BufferedImage.TYPE_BYTE_GRAY;  
			break;  
		case 3:  
			type = BufferedImage.TYPE_3BYTE_BGR;  
			// bgr to rgb  
			byte b;  
			for(int i=0; i<data.length; i=i+3) {  
				b = data[i];  
				data[i] = data[i+2];  
				data[i+2] = b;  
			}  
			break;  
		default:  
			return null;  
		}  
		BufferedImage image2 = new BufferedImage(cols, rows, type);  
		image2.getRaster().setDataElements(0, 0, cols, rows, data);  
		return image2;  
	}  
	@Override
	public void paintComponent(Graphics g){  
		BufferedImage temp=getimage();
		if(temp!=null){
			g.drawImage(temp,10,10,temp.getWidth(),temp.getHeight(), this);  
		}
	} 
	
	public static void main(String arg[]){  
		// Load the native library.  
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		JFrame frame = new JFrame("BasicPanel");  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame.setSize(800,600);  
		Panel panel = new Panel();  
		frame.setContentPane(panel);       
		frame.setVisible(true);       
		
		Mat webcam_image=new Mat();  
		BufferedImage temp;  
		//open video capture with webcam
		VideoCapture capture =new VideoCapture(0);  

		if( capture.isOpened())  
		{  
			while( true )  
			{  
				//read capture into Mat object
				capture.read(webcam_image);  
				
				if( !webcam_image.empty() )  
				{   
					//opencv library
					Imgproc.resize(webcam_image, webcam_image, new Size(webcam_image.size().width,webcam_image.size().height));
					//JFrame
					frame.setSize(webcam_image.width()+40,webcam_image.height()+60);  
					//write Mat into BufferedImage with custom method
					temp=matToBufferedImage(webcam_image);  
					//display Buffered Image onto panel
					panel.setimage(temp);  
					panel.repaint();  
				}  
				else  
				{  
					System.out.println(" --(!) No captured frame -- ");  
				}  
			}  
		}  
		return;  
	}  
}  