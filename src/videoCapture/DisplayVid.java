package videoCapture;
import imageanalysis.MatToBufferedImage;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;






public class DisplayVid {
	
	public static void main(String[] args) throws IOException{
	
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Mat frame1 = new Mat(), frame2 = new Mat(), mhi = new Mat(), framediff = new Mat(), mask = new Mat(), orient = new Mat(), segmask = new Mat()
					,dst = new Mat();

		VideoCapture cap = new VideoCapture();
		BufferedImage img = null;
		MatToBufferedImage conv = new MatToBufferedImage();
		int count = 0;
		double startTime = System.nanoTime();
		double magnitude = 0;
		final double MHI_DURATION = 1;
		final double MAX_TIME_DELTA = 0.5;
		final double MIN_TIME_DELTA = 0.05;
		int video_width, video_height;
		
		//cap.open(0);
		cap.open("videos/videoviewdemo.mp4");
		
		if(!cap.isOpened()){
			System.err.println("Error opening file!");
		}else{
			System.out.println("Camera ok");
		}
			
			//initiates Mats with zeros and appropriate types
		
			cap.read(frame1);
			video_width = frame1.width();
			video_height = frame1.height();
			mhi = Mat.zeros(new Size(video_width, video_height), CvType.CV_32FC1);
			orient = Mat.zeros(new Size(video_width, video_height), CvType.CV_32FC1);
			segmask = Mat.zeros(mhi.size(), CvType.CV_32FC1);
			mask = Mat.zeros(mhi.size(), CvType.CV_8UC1);
			cap.read(frame2);
			
			//getting the first image difference
			Imgproc.cvtColor(frame1, frame2, Imgproc.COLOR_RGB2GRAY);
			Imgproc.cvtColor(frame1, frame1, Imgproc.COLOR_RGB2GRAY);
			Core.absdiff(frame1, frame2, framediff);
			Imgproc.threshold(framediff, framediff, 30, 255,  Imgproc.THRESH_BINARY);
			
			Video.updateMotionHistory(framediff, mhi, startTime,  1);
			//mhi.convertTo(mask, mask.type(), 255.0 / MHI_DURATION,
			//                (MHI_DURATION - timestamp) * 255.0 / MHI_DURATION);
			conv.SetImage(mhi);
			img = conv.getImage();

			JFrame jf = new JFrame();
			jf.setSize(img.getWidth(), img.getHeight());
			jf.setTitle("Ronaldo Goal");
			cPanel panel = new cPanel(img);
			jf.setContentPane(panel);
			jf.setVisible(true);
			MatOfRect roi = new MatOfRect();
			
			while(true){

				double timestamp = (System.nanoTime() - startTime) / 1e9;
				double angle = 0;
				System.out.println("timestamp is " + timestamp + ", startTime is " + startTime);
				if(count % 2 == 0){
					cap.read(frame1);
				
					Imgproc.cvtColor(frame1, frame1, Imgproc.COLOR_RGB2GRAY);
					Core.absdiff(frame1, frame2, framediff);
					Imgproc.threshold(framediff, dst, 30, 255,  Imgproc.THRESH_BINARY);
					
					Video.updateMotionHistory(framediff, mhi, timestamp, 1);
					mhi.convertTo(mask, mask.type(), 255.0 / MHI_DURATION,
			                (MHI_DURATION - timestamp) * 255.0 / MHI_DURATION);
					Video.calcMotionGradient(mhi, mask,roi, MAX_TIME_DELTA, MIN_TIME_DELTA, 3);
					Video.segmentMotion(mhi, segmask, roi, timestamp, MAX_TIME_DELTA);
					int total = roi.toArray().length;
					Rect[] rois = roi.toArray();
					Rect comp_rect;
					Scalar color;
					for (int i = -1; i < total; i++) {
			            if (i < 0) {
			                comp_rect = new Rect(0, 0, video_width, video_height);
			                color = new Scalar(255, 255, 255);
			                magnitude = 100;
			            } else {
			                comp_rect = rois[i];
			                if (comp_rect.width + comp_rect.height < 100) // reject very small components
			                    continue;
			                color = new Scalar(0, 0, 255);
			                magnitude = 30;
			            }
			            

			            Mat silhROI = framediff.submat(comp_rect);
			            Mat mhiROI = mhi.submat(comp_rect);
			            Mat orientROI = orient.submat(comp_rect);
			            Mat maskROI = mask.submat(comp_rect);
			 
			            angle = Video.calcGlobalOrientation(orientROI, maskROI, mhiROI, timestamp, MHI_DURATION);
			            angle = 360.0 - angle;
			            count = (int) Core.norm(silhROI, Core.NORM_L1);
			 
			            silhROI.release();
			            mhiROI.release();
			            orientROI.release();
			            maskROI.release();
			            if (count < comp_rect.height * comp_rect.width * 0.05) {
			                continue;
			            }
			            Point center = new Point((comp_rect.x + comp_rect.width / 2),
			                    (comp_rect.y + comp_rect.height / 2));
			            Core.circle(dst, center, (int) Math.round(magnitude * 1.2), color, 3, Core.LINE_AA, 0);
			            Core.line(dst, center, new Point(
			                    Math.round(center.x + magnitude * Math.cos(angle * Math.PI / 180)),
			                    Math.round(center.y - magnitude * Math.sin(angle * Math.PI / 180))), color, 3, Core.LINE_AA, 0);
					}
			            
					img = conv.getImage(dst);
					panel.setImage(img);
					panel.repaint();
					count++;
				}else if(count % 2 != 0){
					cap.read(frame2);
					Imgproc.cvtColor(frame2, frame2, Imgproc.COLOR_RGB2GRAY);
					Core.absdiff(frame1, frame2, framediff);
					Imgproc.threshold(framediff, framediff, 30, 255,  Imgproc.THRESH_BINARY);
					
					Video.updateMotionHistory(framediff, mhi, timestamp, 1);
					mhi.convertTo(mask, mask.type(), 255.0 / MHI_DURATION,
			                (MHI_DURATION - timestamp) * 255.0 / MHI_DURATION);
					Video.calcMotionGradient(mhi, mask, orient, MAX_TIME_DELTA, MIN_TIME_DELTA,3);
					Video.segmentMotion(mhi, segmask, roi, timestamp, MAX_TIME_DELTA);
					int total = roi.toArray().length;
					Rect[] rois = roi.toArray();
					Rect comp_rect;
					Scalar color;
					for (int i = -1; i < total; i++) {
			            if (i < 0) {
			                comp_rect = new Rect(0, 0, video_width, video_height);
			                color = new Scalar(255, 255, 255);
			                magnitude = 100;
			            } else {
			                comp_rect = rois[i];
			                if (comp_rect.width + comp_rect.height < 100) // reject very small components
			                    continue;
			                color = new Scalar(0, 0, 255);
			                magnitude = 30;
			            }
			            

			            Mat silhROI = framediff.submat(comp_rect);
			            Mat mhiROI = mhi.submat(comp_rect);
			            Mat orientROI = orient.submat(comp_rect);
			            Mat maskROI = mask.submat(comp_rect);
			 
			            angle = Video.calcGlobalOrientation(orientROI, maskROI, mhiROI, timestamp, MHI_DURATION);
			            angle = 360.0 - angle;
			            count = (int) Core.norm(silhROI, Core.NORM_L1);
			 
			            silhROI.release();
			            mhiROI.release();
			            orientROI.release();
			            maskROI.release();
			            if (count < comp_rect.height * comp_rect.width * 0.05) {
			                continue;
			            }
			            Point center = new Point((comp_rect.x + comp_rect.width / 2),
			                    (comp_rect.y + comp_rect.height / 2));
			            Core.circle(dst, center, (int) Math.round(magnitude * 1.2), color, 3, Core.LINE_AA, 0);
			            Core.line(dst, center, new Point(
			                    Math.round(center.x + magnitude * Math.cos(angle * Math.PI / 180)),
			                    Math.round(center.y - magnitude * Math.sin(angle * Math.PI / 180))), color, 3, Core.LINE_AA, 0);
					}
					
					img = conv.getImage(dst);
					panel.setImage(img);
					panel.repaint();
					count++;
				}

			}
			
	
	
	}
}

class cPanel extends JPanel{
	BufferedImage img;
	
	public cPanel(BufferedImage image){
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

/*class KeyList extends KeyAdapter{
	 @Override
	 public void keyPressed(KeyEvent event) {
		 cap.release();
		 
	 }

}*/




