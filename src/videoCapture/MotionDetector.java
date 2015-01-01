package videoCapture;

import imageanalysis.MatToBufferedImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Motion Detector based on grey image difference. Not used anymore.
 * Attempts to detect motion based on the difference in grey weight average of both side.
 * Uses OpenCV 2.4.7 library
 *
 */
public class MotionDetector extends Snapshot {
	private static final int framerate = 5;
	private static final int threshold = 0;
	private static String extension = ".mp4";
	private static String filename  = "vid0013";
	private static final String readfolder = "videos/";
	private static final String savefolder = "videos/saved";
	
	private static Mat left;
	private static Mat right;
	private static MatToBufferedImage conv;
	
	public MotionDetector(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		conv = new MatToBufferedImage();
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		VideoHelper vidcap = new VideoHelper();
		conv = new MatToBufferedImage();
		vidcap.loadfile(readfolder + filename + extension);
		//vidcap.view(readfolder + filename + extension, "video",VideoHelper.Cv_FRAMEDIFF);
		Mat oriframe = new Mat();
		Mat frame = new Mat();
		Mat nxtframe;
		Mat[] setofframes = new Mat[framerate];
		
		
		BufferedImage img;
		JFrame jf = new JFrame();
		boolean hasNext = true;
		int count = 0;
		
		while(hasNext){
			oriframe = vidcap.grabframe();
			oriframe.assignTo(oriframe, CvType.CV_8U);
			Imgproc.cvtColor(oriframe, frame, Imgproc.COLOR_RGB2GRAY);
			//Thread.sleep(10);//to prevent fps from running to fast
			nxtframe = vidcap.grabframe();
			if(nxtframe.empty()){
				hasNext = false;
				jf.setVisible(false);
				System.out.println("hei");
				vidcap.close();
				return;
			}
			nxtframe.assignTo(nxtframe, CvType.CV_8U);
			Imgproc.cvtColor(nxtframe, nxtframe, Imgproc.COLOR_RGB2GRAY);
			Core.absdiff(frame, nxtframe, frame);
			//Imgproc.threshold(framediff, framediff, 30, 255,  Imgproc.THRESH_BINARY);
			img = conv.getImage(oriframe);
			
			jf.setSize(img.getWidth(), img.getHeight());
			jf.setTitle("view video");
			VidPanel panel = new VidPanel(img);
			jf.setContentPane(panel);
			jf.setVisible(true);
			//timediff = (System.nanoTime() - startTime) / 1e9;
			//count++;
			//fps = count/timediff;
			System.out.println("left: " + calculateLeftWeight(frame) + ", right: " + calculateRightWeight(frame));
			if(count == framerate){
				if(hasLeftMotion(setofframes))	System.err.println("has left motion");
				if(hasRightMotion(setofframes))	System.err.println("has right motion");
				System.out.println("restart");
				count = 0;
			}else{
				setofframes[count] = frame;
			}
			count++;
		}
	}
	
	/**
	 * Calculates the grey weight average value of the left side of the image
	 * @param src	A frame
	 * @return	grey weight average value
	 * @throws IOException	
	 */
	public static double calculateLeftWeight(Mat src) throws IOException{
		Mat[] frames = splitFrame(src);
		int[][] leftframe = getImgArr(frames[0]);
		int greysum = 0;
		
		for(int y = leftframe[1].length; y > 0; y--){//from the top row to the bottom row
			for(int x = 0; x<leftframe[0].length/2; x++){//toprow first column to bottomrow last column
				greysum += leftframe[y][x];
			}
		}
		//returns the average greysum of the leftside of the image
		return  greysum/ (leftframe[0].length*leftframe[1].length);
	}
	
	
	/**
	 * Calculates the grey weight average value of the right side of the image
	 * @param src	A frame
	 * @return	grey weight average value
	 * @throws IOException	
	 */
	public static double calculateRightWeight(Mat src) throws IOException{
		Mat[] frames = splitFrame(src);
		int[][] rightframe = getImgArr(frames[1]);
		int greysum = 0;
		
		for(int y = rightframe[1].length; y > 0; y--){//from the top row to the bottom row
			for(int x = rightframe[0].length/2; x<frames[1].width(); x++){//toprow first column to bottomrow last column
				greysum += rightframe[y][x];
			}
		}	
		//returns the average greysum of the leftside of the image
		return  greysum/ (rightframe[0].length*rightframe[1].length);
	}
	
	/**
	 * Compares left and right side of the image weight value. Uses 1 and -1 to act as the binary return value
	 * @param src image frame
	 * @return	int binary value
	 * @throws IOException
	 */
	public static int compareLeftRight(Mat src) throws IOException{
		double leftweight = calculateLeftWeight(src);
		double rightweight = calculateRightWeight(src);
		//if leftweight is larger, return -1 as being left, else return 1 as being right
		if(leftweight - rightweight > 0)	return -1;
		
		return 1;
	}
	
	private static boolean hasLeftwardMotion(Mat[] setofframes) throws IOException{
		double currentweight = calculateLeftWeight(setofframes[0]);
		boolean hasLeftMotion = false;
		for(Mat frame : setofframes){

		}
		return hasLeftMotion;
	}
	
	/**
	 * <p> Leftward motion presents a motion moving from the right of the frame towards the left of the frame
	 * @param setofframes
	 * @return
	 * @throws IOException 
	 */
	public static boolean hasLeftMotion(Mat[] setofframes) throws IOException{
		boolean hasLeftMotion = false;
		double currentleftweight;
		double currentrightweight;
			for(Mat frame : setofframes){
				currentleftweight = calculateLeftWeight(frame);
				currentrightweight = calculateRightWeight(frame);
				if(currentleftweight > currentrightweight){
					System.out.println("difference value perc " + ((currentleftweight - currentrightweight)/(currentrightweight)));
					if( ((currentleftweight - currentrightweight)/currentrightweight) > 0.4){
						hasLeftMotion = true;
					}
				}
				
			}
		
		return hasLeftMotion;
	}
	
	public static boolean hasRightMotion(Mat[] setofframes) throws IOException{
		boolean hasRightMotion = false;
		double currentleftweight = calculateLeftWeight(setofframes[0]);
		double currentrightweight = calculateRightWeight(setofframes[0]);
			for(Mat frame : setofframes){
				currentleftweight = calculateLeftWeight(frame);
				currentrightweight = calculateRightWeight(frame);
				if(currentrightweight > currentleftweight){
					System.out.println("difference value perc " + ((currentrightweight - currentleftweight)/(currentleftweight)));
					if( ((currentrightweight - currentleftweight)/(currentleftweight)) > 0.4){
						hasRightMotion = true;
					}
				}
				
			}
		
		return hasRightMotion;
	}
	
	private static boolean hasRightwardMotion(Mat[] setofframes) throws IOException{
		double currentweight = calculateRightWeight(setofframes[0]);
		boolean hasRightMotion = false;
		for(Mat frame : setofframes){

		}
		return hasRightMotion;
	}
	
	private static int[][] getImgArr(Mat src) throws IOException{
		BufferedImage img = conv.getImage(src);
		
		 int[][] imgarr = new int[img.getHeight()][img.getWidth()];
		 int total = img.getHeight()*img.getWidth()*255;
		 int sum = 0;

		 //from top row to bottom row
		 for(int y = img.getHeight()-1; y>0; y--){
			 for(int x=0; x<img.getWidth()-1; x++){
				 //get pixel value and add into sum
				 imgarr[y][x] += (img.getRGB(x, y)  & 0x00ff0000 )>> 16;
			 }
		 }
		
		 return imgarr; 
	}
	
	/**
	 * 
	 * @return	returns an array of Mat with the first being the left submat and the second being the right submat
	 */
	private static Mat[] splitFrame(Mat src){
		Mat[] partitions = new Mat[2];
		Mat leftsplit = new Mat(src.width()/2, src.height(), src.type());
		Mat rightsplit = new Mat(src.width()/2, src.height(), src.type());
		
		src.submat(0, src.height(), 0, src.width()/2).assignTo(leftsplit);
		src.submat(0, src.height(), src.width()/2, src.width()).assignTo(rightsplit);
		partitions[0] = leftsplit; partitions[1] = rightsplit;
		
		return partitions;
	}
	
	public static void displayImage(Mat mat, String windowcaption) throws IOException{
		MatToBufferedImage conv = new MatToBufferedImage();
		BufferedImage image = conv.getImage(mat);
		SetImage(image);
		ViewImage(windowcaption);
	}
	
	public static void displayImage(Mat mat, String windowcaption, boolean savepic) throws IOException{
		if(!savepic){
			displayImage(mat,windowcaption);
		}else{
			MatToBufferedImage conv = new MatToBufferedImage();
			BufferedImage image = conv.getImage(mat);
			SetImage(image);
			ViewImage(windowcaption);
			
		}
	}
	
}
