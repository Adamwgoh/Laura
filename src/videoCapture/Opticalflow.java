package videoCapture;

import imageanalysis.MatToBufferedImage;
import imageanalysis.Snapshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

/**
 * <h1> OpticalFlow </h2>
 *  The optical flow contains codes that are used to detect wrong direction, implementations of OpticalFlow Lucas-Kanade, and OpticalFlow Farneback
 * 
 *  Uses Open Source OpenCV 2.4.7
 */
public class Opticalflow extends Snapshot{
	//ALL FIXED VARIABLES HERE
	//maximum number of points for the algorithm
	private final static int MAX_CORNERS 	  = 500;
	private final static int WINDOW_SIZE 	  = 15;
	private final static double QUALITY 	  = 0.10;
	private final static double MIN_DISTANCE  = 10;
	private final static int FLOWSTEP	      = 50;
	private final static String savefolder	  = "videos/saved";
	private final static String readfolder	  = "videos/";
//	private final static String filename  	  = "vid1776";
	private final static String extension 	  = ".mp4";
	private final static double quadrant_perc = 60.0;

	
	public static Mat prevgrey;//CvType.CV_8UC1
	public static Mat nextgrey;//CvType.CV_8UC1
	public  static Mat WrongDirImg;
	public static String filename;
	
	//CvPoint2D32F Arrays are 32 bit points to track features
	public static MatOfPoint prevpointS;
	public static MatOfPoint2f prevpoint;
	public static MatOfPoint2f nextpoint;
	public static MatOfByte  featuresfound;
	public static MatOfFloat feature_errors;
	
	public static boolean HasFirstQuadrant = false;
	public static boolean HasFourthQuadrant = false;
	public static boolean HasThirdQuadrant = false;
	
//	public static JFrame jf = new JFrame();
//	public static VidPanel panel;
	
	private final static boolean onCamera = false;

	
	public Opticalflow(){
		
	}
	/**
	 * <p> this function is a general function of the implementation. It derived from the debugging scene's main function.
	 *  It provides a black-box interaction for other modules to implement the wrong direction feature </p>
	 * @param path the url to the video
	 * @throws IOException thrown when the video can't be loaded
	 * @throws InterruptedException thrown when thread sleeping were disturbed
	 * 
	 * @see VideoCapture.Opticalflow#inWrongDirection
	 * @see VideoCapture.Opticalflow#calculateOptflow
	 */
	public void runn(String path) throws IOException, InterruptedException{
		JFrame jf = new JFrame();
		VidPanel panel;
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoHelper vid = new VideoHelper();
		filename = new File(path).getName();
		if(onCamera){
			vid.onCamera();
		}else{
			//vid.loadfile(readfolder + filename + extension);
			vid.loadfile(path);
		}
		Mat frame1 = new Mat();
		frame1 = vid.grabframe();
		//Core.flip(frame1, frame1, 1);
		//displayImage(prevgrey, "current frame");

		BufferedImage img;
		Mat diff = new Mat();
		Mat result = new Mat();
		MatToBufferedImage conv = new MatToBufferedImage();
		
		
		boolean hasNext = true;
		double startTime = System.nanoTime();
		double timediff;
		int count = 0;
		int nofframes = 1;
		Mat firstframe = new Mat();
		double fps;
		
		init(frame1);
		prevgrey = frame1;
		displayImage(prevgrey, "prevgrey");
		
		jf.setSize(prevgrey.width(), prevgrey.height());
		jf.setTitle("optical flow");
		Imgproc.cvtColor(prevgrey, prevgrey, Imgproc.COLOR_RGB2GRAY);
	
		while(hasNext){
			
			nextgrey = vid.grabframe();
			//Core.flip(nextgrey, nextgrey, 1);
			if(nextgrey.empty()){
				System.out.println("end of video");
				
				hasNext = false;
				jf.setVisible(false);
				vid.close();
				return;
			}
			
			Imgproc.cvtColor(nextgrey, nextgrey, Imgproc.COLOR_RGB2GRAY);
			Thread.sleep(10);//to prevent fps from running too fast
			
			Core.absdiff( prevgrey, nextgrey, diff );
			double n = Core.norm(diff, Core.NORM_L2);
			//System.out.println("the n value is " + n);
			boolean wrongdirection = false;
			
			wrongdirection = inWrongDirection(diff);
			if(wrongdirection){
				//displayImage(prevgrey, "offense capture");
				WrongDirImg = prevgrey;
				SaveImage(conv.getImage(WrongDirImg), filename, savefolder);
			}
			//displayImage(prevgrey, "hello");
			result = calculateOptflow(prevgrey, nextgrey );
			
			//prevgrey = OptflowFarneBack(prevgrey, nextgrey);
			
			img = conv.getImage(result);
			
			panel = new VidPanel(img);
			
			jf.setContentPane(panel);
			jf.setVisible(true);
			
			prevgrey = nextgrey;
			timediff = (System.nanoTime() - startTime) / 1e9;
			count++;
			fps = count/timediff;
			nofframes++;
			//System.out.println(fps);
			
		}
		System.exit(1);
	}
	
	public static void init(Mat src){
		//initiate all variables for calculation
		
		prevpointS = new MatOfPoint();
		prevpoint = new MatOfPoint2f();//MAX_COUNT
		nextpoint = new MatOfPoint2f();//MAX_COUNT
		featuresfound = new MatOfByte();
		feature_errors = new MatOfFloat();
		prevgrey = new Mat(src.size(), CvType.CV_8UC1);
		nextgrey = new Mat(src.size(), CvType.CV_8UC1);
		
	}
	
	/**
	 * <h1>Drawing Optical Flow Map </h1>
	 * <p> This function takes in an image, as well as a motion vectors image and draw the lines.
	 * This is used for the dense optical flow solution, the Farneback algorithm </p>
	 * @param flow the motion vectors image
	 * @param cflowmap the default image
	 * @param step Where to draw the line in every <step> number of pixels
	 * @param color The color of the drawn motion vector.
	 * @return
	 */
	public static Mat drawOptFlowMap(Mat flow, Mat cflowmap, int step, Scalar color){
		
		for(int y = 0; y < cflowmap.rows(); y += step){
			for(int x = 0; x < cflowmap.cols(); x += step){
			    Point fxy = new Point(flow.get(y, x));
			   // System.out.println("point fxt. X is: " + fxy.x + ", Y is: " + fxy.y);
			    Core.line(cflowmap, new Point(x,y), new Point(Math.round(x+fxy.x), Math.round(y+fxy.y)),
			         color);
			    //Core.circle(cflowmap, new Point(x,y), 2, color, -1);
			}
		}
		
		return cflowmap;
		
	}
	
	/**
	 * <p> Dense optical flow using Opticalflow FarneBack algorithm. </p>
	 * returns the map of motion vectors
	 * @param prevgray one before frame of the video
	 * @param gray one after frame of the video
	 * @return map of motion vector
	 * 
	 * @see Video.calcOpticalFlowFarneback
	 */
	public static Mat OptflowFarneBack(Mat prevgray, Mat gray){
		
		 Mat cflow = new Mat(prevgray.size(), CvType.CV_8UC3);
		 Mat flow = new Mat(prevgray.size(), CvType.CV_8UC3);
		// System.out.println("prevgrey " + prevgray.toString());
		// System.out.println("cflow " + cflow.toString());
		// Video.calcOpticalFlowFarneback(prev, next, cflow, pyr_scale, levels, winsize, iterations, poly_n, poly_sigma, flags)
		Video.calcOpticalFlowFarneback(prevgray, gray, flow, 0.5, 3, WINDOW_SIZE, 3, 5, 1.5, 0);
        //Imgproc.cvtColor(prevgray, cflow, Imgproc.COLOR_GRAY2BGR);
        drawOptFlowMap(flow, cflow, FLOWSTEP, new Scalar(0, 255, 0));
        //cflow.assignTo(cflow, prevgrey.type());
         return cflow;
	}
	
	/**
	 * <h1> Find features to track </h1>
	 * <p> Takes in an image, converts it into black and white and uses {@link Imgproc#goodFeaturesToTrack(Mat, MatOfPoint, int, double, double)}
	 * to get points where features are tracked for motion </p>
	 * @param src an image difference Mat as input
	 * @return Array of Pointers that are the features to be tracked
	 */
	public static MatOfPoint2f findFeatures(Mat src){
		MatOfPoint2f features;
		Mat img = src;
		assert(img.channels() == 1 || img.type() == CvType.CV_8U);
		
		prevpoint.alloc((int) (MAX_CORNERS*(prevpoint.elemSize1())));
		prevpointS.alloc((int) (MAX_CORNERS*(prevpointS.elemSize1())));
		
		//System.out.println("prevpointS is" + prevpointS.toString());
		
		//Video.buildOpticalFlowPyramid(src, imgpyramid, new Size(WINDOW_SIZE, WINDOW_SIZE), 1);
		Imgproc.goodFeaturesToTrack(img, prevpointS, MAX_CORNERS, QUALITY, MIN_DISTANCE);
		
		//System.out.println("prevpointS stats: " + prevpointS.toString());
		features = new MatOfPoint2f(prevpointS.toArray());
		//Imgproc.cornerSubPix(src, prevpoint, new Size(WINDOW_SIZE*2+1, WINDOW_SIZE*2+1), new Size(-1,-1),
		//					new TermCriteria(TermCriteria.MAX_ITER | TermCriteria.EPS, 20,0.3));
		
		return 	features;
	}

	/**
	 * <h1> findfeatures, calculate the optical flow motion vectors and draw on a new Mat </h1>
	 * The function takes in two frames and find for features that can be tracked, used them for
	 * Optical flow calculation and points found are used to draw lines on the image.
	 * 
	 * @param prevframe	previous frame
	 * @param nextframe next frame
	 * @return frame with motion vectors drawn
	 */
	public static Mat calculateOptflow(Mat prevframe, Mat nextframe){
		  MatOfPoint2f prevpt = findFeatures(prevframe);
		  Video.calcOpticalFlowPyrLK(prevframe, nextframe, prevpt, nextpoint, featuresfound, 
									feature_errors, new Size(WINDOW_SIZE, WINDOW_SIZE), 5);
		
		  Mat dots = new Mat(prevframe.size(), CvType.CV_16U);
		  // Draw optical flow vectors
          Point[] prevpoints = prevpt.toArray();
          Point[] nextpoints = nextpoint.toArray();
          
          
	      for (int i = 0; i < MAX_CORNERS && i < prevpoints.length; i++) {  
	    	
	        Point point1 = new Point(Math.round(prevpoints[i].x), Math.round(prevpoints[i].y));
	        Point point2 = new Point(Math.round(nextpoints[i].x), Math.round(nextpoints[i].y));
	        //System.out.println("point1: " + point1.toString() + ", point2 " + point2.toString());
	        if(point2.x < 0 || point2.y < 0){
	        	Core.line(prevframe, point1, point2, new Scalar(0, 255, 0), 1, 8, 0);
	        }else{
	        	Core.line(prevframe, point1, point2, new Scalar(255, 0, 0), 1, 8, 0);
	        }
	      }
	      
	    prevpt.release();
	    nextpoint.release();
	    prevpointS.release();
	    featuresfound.release();
	    feature_errors.release();
	    
	    return prevframe;
	}
	
	/**
	 * <p> A move-to-Q-quadrant means that there is a difference in the placement of features of a specified percentage </p>
	 * <p> The position of major features at First quadrant is determined by the AtFirstquadrant function </p>
	 * @param firstframe	the first frame to look for features
	 * @param nframe		The N frame to look for features
	 * @return return true if there is a movement of majority features from other quadrants to the first quadrant
	 */
	private static boolean MoveToFirstQuadrant(Mat firstframe, Mat nframe){
		if(!AtFirstQuadrant(firstframe) && AtFirstQuadrant(nframe)){
			return true;
		}
		
		
		return false;
	}
	
	/**
	 * <p> A move-to-Q-quadrant means that there is a difference in the placement of features of a specified percentage </p>
	 * <p> The position of major features at Second quadrant is determined by the AtSecondquadrant function </p>
	 * @param firstframe	the first frame to look for features
	 * @param nframe		The N frame to look for features
	 * @return return true if there is a movement of majority features from other quadrants to the second quadrant
	 */
	private static boolean MoveToSecondQuadrant(Mat firstframe, Mat nframe){
		if(!AtSecondQuadrant(firstframe) && AtSecondQuadrant(nframe)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * <p> A move-to-Q-quadrant means that there is a difference in the placement of features of a specified percentage </p>
	 * <p> The position of major features at Third quadrant is determined by the AtThirdquadrant function </p>
	 * @param firstframe	the first frame to look for features
	 * @param nframe		The N frame to look for features
	 * @return return true if there is a movement of majority features from other quadrants to the Third quadrant
	 */
	private static boolean MoveToThirdQuadrant(Mat firstframe, Mat nframe){
		if(!AtThirdQuadrant(firstframe) && AtThirdQuadrant(nframe)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * <p> A move-to-Q-quadrant means that there is a difference in the placement of features of a specified percentage </p>
	 * <p> The position of major features at Fourth quadrant is determined by the AtFourthquadrant function </p>
	 * @param firstframe	the first frame to look for features
	 * @param nframe		The N frame to look for features
	 * @return return true if there is a movement of majority features from other quadrants to the Fourth quadrant
	 */
	private static boolean MoveToFourthQuadrant(Mat firstframe, Mat nframe){
		if(!AtFourthQuadrant(firstframe) && AtFourthQuadrant(nframe)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * <p>The features are considered to be at a quadrant when a specified percentage of features are at first quadrant</p>
	 * <p>The percentage is fixed a certain rate, and calculated by calculating the number of Points with specific Xs and Ys over total number of points</p>
	 *  
	 * @return returns true if features at the first quadrant exceeds the number of specified percentage
	 */
	private static boolean AtFirstQuadrant(Mat frame){
		Mat img = frame;
		MatOfPoint2f points_to_track = findFeatures(img);
		
		Point[] points = points_to_track.toArray();
		//System.out.println("Points? we have " + points.length + " of them.");
		int first_count = 0;
		//count all points at first quadrant, that is
		// x = less than half of width, y = less than half of height
		for(Point point : points){
			//System.out.println("their x is " + point.x + ", and their y is " + point.y);
			if(point.x <= (frame.cols()/2) && point.y <= (frame.rows()/2)){
				first_count++;
			}
		}
		//System.out.println("perc is " +  ((double) first_count/points.length)*100);
		if(((double) first_count/points.length)*100 > quadrant_perc){
			return true; 
		}
		return false;
	}
	
	/**
	 * <p>The features are considered to be at a quadrant when a specified percentage of features are at second quadrant</p>
	 * <p>The percentage is fixed a certain rate, and calculated by calculating the number of Points with specific Xs and Ys over total number of points</p>
	 *  
	 * @return returns true if features at the second quadrant exceeds the number of specified percentage
	 */
	private static boolean AtSecondQuadrant(Mat frame){
		Mat img = frame;
		MatOfPoint2f points_to_track = findFeatures(img);
		Point[] points = points_to_track.toArray();
		
		//System.out.println("Points? we have " + points.length + " of them.");
		
		int second_count = 0;
		//count all points at second quadrant, that is
		// x = more than half of width, y = less than half of height
		for(Point point : points){
			//System.out.println("their x is " + point.x + ", and their y is " + point.y);
			if(point.x >= (frame.cols()/2) && point.y <= (frame.rows()/2)){
				
				second_count++;
			}
		}
		//System.out.println("perc is " +  ((double) second_count/points.length)*100);
		if(((double) second_count/points.length)*100 > quadrant_perc){
			return true; 
		}
		return false;
	}
	
	/**
	 * <p>The features are considered to be at a quadrant when a specified percentage of features are at third quadrant</p>
	 * <p>The percentage is fixed a certain rate, and calculated by calculating the number of Points with specific Xs and Ys over total number of points</p>
	 *  
	 * @return returns true if features at the third quadrant exceeds the number of specified percentage
	 */
	private static boolean AtThirdQuadrant(Mat frame){
		Mat img = frame;
		MatOfPoint2f points_to_track = findFeatures(img);
		Point[] points = points_to_track.toArray();
		
		//System.out.println("Points? we have " + points.length + " of them.");
		
		int third_count = 0;
		//count all points at second quadrant, that is
		//x is less than half of width, y = more than half of height
		for(Point point : points){
			//System.out.println("their x is " + point.x + ", and their y is " + point.y);
			if(point.x <= (frame.cols()/2) && point.y >= (frame.rows()/2)){
				third_count++;
			}
		}
		//System.out.println("perc is " +  ((double) third_count/points.length)*100);
		if(((double) third_count/points.length)*100 > quadrant_perc){
			return true; 
		}
		
		return false;
	}
	
	/**
	 * <p>The features are considered to be at fourth quadrant when a specified percentage of features are at the fourth quadrant</p>
	 * <p>The percentage is fixed a certain rate, and calculated by calculating the number of Points with specific Xs and Ys over total number of points</p>
	 * 
	 * @return returns true if features at the fourth quadrant exceeds the number of specified percentage
	 */
	private static boolean AtFourthQuadrant(Mat frame){
		Mat img = frame;
		MatOfPoint2f points_to_track = findFeatures(img);
		Point[] points = points_to_track.toArray();
		
		//System.out.println("Points? we have " + points.length + " of them.");
		
		int fourth_count = 0;
		//count all points at second quadrant, that is
		// x = more than half of width, y = more than half of height
		for(Point point : points){
			//System.out.println("their x is " + point.x + ", and their y is " + point.y);
			if(point.x >= (frame.cols()/2) && point.y >= (frame.rows()/2)){
				fourth_count++;
			}
		}
		//System.out.println("perc is " +  ((double) fourth_count/points.length)*100);
		if(((double) fourth_count/points.length)*100 > quadrant_perc){
			return true; 
		}
		
		return false;
	}
	
	/**
	 * <p> Checks if a motion is going in a wrong direction, and returns true if it does. </p>
	 * <p> A wrong direction is defined by first having features at Third Quadrant, and later found to be at Fourth Quadrant </p>
	 * <p> If a wrong direction is detected, the static boolean variables are reset and returns true </p>
	 * @param frame Frame to be tracked with features
	 * @return return true if a wrong direction is detected
	 * @throws IOException For displayImage function. Throws if unable to read BufferedImage file.
	 */
	public static boolean inWrongDirection(Mat frame) throws IOException{
		
		//Imgproc.GaussianBlur(frame, frame, new Size(5,5), 0);
		
		if( AtFirstQuadrant(frame) ){
			System.out.println("They're at First Quadrant.");
			HasFirstQuadrant = true;
		}else if( AtSecondQuadrant(frame) ){
			System.out.println("They're at Second Quadrant.");
		}else if( AtThirdQuadrant(frame)){
			System.out.println("They're at Third Quadrant.");
			
			if(HasFourthQuadrant && !HasThirdQuadrant){
				System.out.println("has right direction");
				//clear count
				HasFourthQuadrant = false;
			//	return true;
			}
			HasThirdQuadrant = true;
		}else if( AtFourthQuadrant(frame)){
			System.out.println("They're at Fourth Quadrant.");
			HasFourthQuadrant = true;
			if(HasThirdQuadrant && HasFourthQuadrant){
				System.out.println("WRONG WAY");
				//displayImage(frame, "wrong way capture");
				
				//restart count
				HasThirdQuadrant = false;
				return true;
			}
		}
		
		return false;
	}
	
}
