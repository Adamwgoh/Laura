package videoCapture;

import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.highgui.Highgui.CV_CAP_PROP_FRAME_WIDTH;

import imageanalysis.MatToBufferedImage;
import imageanalysis.Snapshot;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

/**
 * A VideoHelper class to view and load video. Not practical in terms of its functionality. Deprecated.
 * Not used in the final implementation of the system.
 * 
 * Uses Open Souce Code OpenCV 2.4.7
 * @author Adam Goh
 *
 */
public class VideoHelper extends Snapshot {

	private static String extension = ".mp4";
	private static String filename  = "videoviewdemo";
	private static final String readfolder = "videos/";
	private static final String savefolder = "videos/saved";
	public  static final int	Cv_BW 	   = 1;
	public  static final int	Cv_FRAMEDIFF = 2;
	
	private VideoCapture cap;
	private BufferedImage img;
	private static MatToBufferedImage conv;
	
	private int video_width, video_height;
	private static Mat frame1,frame2;
	private static boolean displayOn = true;
	

	public VideoHelper(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		conv = new MatToBufferedImage();
		this.cap = new VideoCapture();
	}
	public VideoHelper(String path){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		conv = new MatToBufferedImage();
		this.cap = new VideoCapture();
		this.cap.open(path);
		video_width = (int) cap.get(CV_CAP_PROP_FRAME_WIDTH);
        video_height = (int) cap.get(CV_CAP_PROP_FRAME_HEIGHT);
	}
	
	/**
	 * <p> Returns width and height of the video</p>
	 * @return
	 */
	public Size getSize(){
		return new Size(video_width, video_height);
	}
	
	public Mat grabframe(){
		Mat frame = new Mat();
		cap.read(frame);
		
		return frame;
	}
	
	public boolean onCamera(){
		if(this.cap.isOpened()) this.cap.release();
		
		this.cap.open(0);
		if(!this.cap.isOpened())	return false;
		return true;
	}
	
	public boolean loadfile(String path){
		if(this.cap.isOpened()) this.cap.release();
		
		cap.open(path);
		if(!cap.isOpened())	return false;
		video_width = (int) cap.get(CV_CAP_PROP_FRAME_WIDTH);
        video_height = (int) cap.get(CV_CAP_PROP_FRAME_HEIGHT);
		return true;
	}
	
	
	
	public void view(String path, String windowcaption) throws IOException, InterruptedException{
		loadfile(path);
		BufferedImage img;
		JFrame jf = new JFrame();
		frame1 = new Mat();
		Mat frame2 = new Mat();
		boolean hasNext = true;
		double startTime = System.nanoTime();
		double timediff;
		int count = 0;
		double fps;
		
		while(hasNext){
			cap.read(frame1);
			if(frame1.empty()){
				hasNext = false;
				jf.setVisible(false);
				close();
				return;
			}
			switch(4){
				case 1: frame1.assignTo(frame1, CvType.CV_8U);	Imgproc.cvtColor(frame1, frame1, Imgproc.COLOR_RGB2GRAY); break;
				case 2: frame1.assignTo(frame1, CvType.CV_8U);	Imgproc.cvtColor(frame1, frame1, Imgproc.COLOR_RGB2GRAY); 
						cap.read(frame2);
						if(frame2.empty()){
							hasNext = false;
							jf.setVisible(false);
							close();
							return;
						}
						frame2.assignTo(frame2, CvType.CV_8U);
						Imgproc.cvtColor(frame2, frame2, Imgproc.COLOR_RGB2GRAY);
						Core.absdiff(frame1, frame2, frame1);	break;
				default: break;
			}
			
			Thread.sleep(10);//to prevent fps from running to fast
			img = conv.getImage(frame1);
			
			jf.setSize(img.getWidth(), img.getHeight());
			jf.setTitle(windowcaption);
			VidPanel panel = new VidPanel(img);
			jf.setContentPane(panel);
			jf.setVisible(true);
			timediff = (System.nanoTime() - startTime) / 1e9;
			count++;
			fps = count/timediff;
			System.out.println(fps);
		}
	}
	
	public void repaint(Mat frame, JFrame jframe) throws IOException{
		BufferedImage img;
		img = conv.getImage(frame);
		
		jframe.setSize(img.getWidth(), img.getHeight());
		VidPanel vpanel = new VidPanel(img);
			jframe.setContentPane(vpanel);
			jframe.setVisible(true);
		
	}
	
	public void close(){
		cap.release();
		frame1 = new Mat();
		frame2 = new Mat();
		video_width = 0;
		video_height = 0;
	}
	
	public void view() throws IOException, InterruptedException{
		BufferedImage img;
		JFrame jf = new JFrame();
		frame1 = new Mat();
		boolean hasNext = true;
		double startTime = System.nanoTime();
		double timediff;
		int count = 0;
		double fps;
		
		
		while(hasNext){
			cap.read(frame1);
			Thread.sleep(10);//to prevent fps from running to fast
			if(frame1.empty()){
				hasNext = false;
				jf.setVisible(false);
				close();
				return;
			}
			img = conv.getImage(frame1);
			
			jf.setSize(img.getWidth(), img.getHeight());
			VidPanel panel = new VidPanel(img);
			jf.setContentPane(panel);
			jf.setVisible(true);
			timediff = (System.nanoTime() - startTime) / 1e9;
			count++;
			fps = count/timediff;
			//System.out.println(fps);
		}
			
	}
	
	/**
	 * <p>changes video view mode</p>
	 * <p> Cv_BW : Video in greyscale</p>
	 * 
	 * @param mode various modes that are supported in this function are as follows:
	 * <p><b> Cv_BW</b> Video in greyscale mode</p>
	 * <p><b> Cv_IMGDIFF</b> Video in image difference mode</p>
	 * <p><b> Cv_THRES </b> Video in threshold mode</p>
	 * 
	 *
	 *
	 **/
	public Mat viewMode(int mode, Mat src, Mat dst, Mat nextframe){
		switch(mode){
			case Cv_BW:	Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY); break;
			
		}
		return dst;
	}
	
	/**
	 * <p>Captures a Mat screen shot every specified frame for the specified video file.</p>
	 * <p> put "camera for video </p>
	 * @param eachframe
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public ArrayList<Mat> ScreenCap(int eachframe, String filepath) throws InterruptedException, IOException{
		ArrayList<Mat> scrns = new ArrayList<Mat>();
		Mat screencapt = new Mat();
		int count = 0;
		boolean hasNext = true;
		BufferedImage img;
		JFrame jf = new JFrame();
		frame1 = new Mat();
		double startTime = System.nanoTime();
		double timediff;
		double fps;
		System.out.println("I'm in");
		
		if(cap.isOpened()) cap.release();
		loadfile(filepath);	
		while(hasNext){
				
				cap.read(frame1);
				Thread.sleep(10);//to prevent fps from running to fast
				if(frame1.empty()){
					hasNext = false;
					jf.setVisible(false);
					close();
					break;
				}
				img = conv.getImage(frame1);
				
				jf.setSize(img.getWidth(), img.getHeight());
				VidPanel panel = new VidPanel(img);
				jf.setContentPane(panel);
				jf.setVisible(true);
				timediff = (System.nanoTime() - startTime) / 1e9;
				count++;
				if(count%eachframe == 0){
					scrns.add(screencapt);
					//System.out.println("screen captured at " + System.currentTimeMillis());
				}
				fps = count/timediff;
				//System.out.println(fps);
			}

			

		
		return scrns;
	
	}
		
	public static void displayImage(Mat mat, String windowcaption) throws IOException{
		MatToBufferedImage conv = new MatToBufferedImage();
		BufferedImage image = conv.getImage(mat);
		SetImage(image);
		if(displayOn)	ViewImage(windowcaption);
	}
	
	public static void displayImage(Mat mat, String windowcaption, boolean savepic) throws IOException{
		if(!savepic){
			displayImage(mat,windowcaption);
		}else{
			MatToBufferedImage conv = new MatToBufferedImage();
			BufferedImage image = conv.getImage(mat);
			SetImage(image);
			if(displayOn)	ViewImage(windowcaption);
			System.out.println("saving image..." + SaveImage(image, filename + windowcaption + "result", savefolder + "/" + filename + "/"));
		}
	}
	
	
}

