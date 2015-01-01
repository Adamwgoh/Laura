package videoCapture;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.Video;
/**
 * A simple image difference capture.
 * @author Adam Goh
 * uses Open Source library OpenCV 2.4.7
 */
public class videocapture {
	public static void main(String[] args) throws InterruptedException{
		
		System.out.println("hello OpenCV");
		//Load the native library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat frame1 = new Mat();
		Mat frame2 = new Mat();
		Mat frame3 = new Mat();
		VideoCapture camera = new VideoCapture(0);
		
		camera.open(0);

		System.out.println("taking frame1 now in 5 seconds.");
		Thread.sleep(1000);
		System.out.println("taking frame1 now in 4 seconds.");
		Thread.sleep(1000);
		System.out.println("taking frame1 now in 3 seconds.");
		Thread.sleep(1000);
		System.out.println("taking frame1 now in 2 seconds.");
		Thread.sleep(1000);
		System.out.println("taking frame1 now in 1 seconds.");
		Thread.sleep(1000);
		if(camera.isOpened()){
			
			camera.read(frame1);
			camera.read(frame2);
			Core.absdiff(frame1, frame2, frame3);
			while(true){
				System.out.println("frame1 taken. 3 seconds to another take.");
				Thread.sleep(3000);
				frame1 = frame2;
				camera.read(frame2);
				
				//convert to grayscale
				//Imgproc.cvtColor(frame1, frame1,Imgproc.COLOR_RGB2GRAY);
				//Imgproc.cvtColor(frame2, frame2,Imgproc.COLOR_RGB2GRAY);
				Core.absdiff(frame2, frame1, frame3);
				//new LoadImage("FRAME.PNG", frame3);
				
				Video.calcMotionGradient(frame3, frame1, frame2, 0.2, 0.5);
				new LoadImage("F.PNG", frame2);
				
			}

			
		}
		camera.release();
		VideoCapture cap = new VideoCapture();
		//cap.open(filename);
	}
	

	
}
