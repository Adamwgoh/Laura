package imageanalysis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.jfree.ui.RefineryUtilities;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
/**
 * The preprocess class is the main class for running the plate clipping and plate classification functionality. Using {@link imageanalysis.Preprocess#run(String)},
 * Other module is able to produce a black-box like process in implementing the functionalities.
 * @author Adam Goh
 *
 * @see imageanalysis.Histogram
 * @see imageanalysis.SVM
 * @see imageanalysis.OCR
 */
public class Preprocess extends Snapshot{
	
	private static BufferedImage img;
//	private static String extension = ".jpg";
//	private static String filename = "car7336";
	private static final int numberofcandidates = 3;
	private static final String readfolder = "dummydataset/images/";
	private static final String savefolder = "dummydataset/images/results/";
	private static ArrayList<Mat> bands = new ArrayList<Mat>();
	private static ArrayList<Mat> possibleplates = new ArrayList<Mat>();
	private static SVMachine svm;
	
	private static boolean writerOn = false;
	private static boolean displayOn = false;

	private static int isplate 	 = 0;
	private static int notplate  = 0;
	private static int haveplate = 0;
	private static int noplate 	 = 0;
	
//	public static void main(String args[]) throws IOException{
//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		invertImage();
//	}
	
//	public static void main(String args[]) throws IOException{
//		svm = new SVMachine();
//		svm.getTrainingfiles(savefolder + "possibleplates/");
//		svm.init();
//		
//		int count = 1;
//		String filename = "test_00" + count;
//		File image = new File(readfolder + filename + ".jpg");
//		while(new File(readfolder + filename + ".jpg").exists()){
//			System.out.println("******** " + filename + "*******");
//			run(filename);
//			count++;
//			if(count >= 10){
//				
//				filename = "test_0" + count;
//			}else{
//				filename = "test_00" + count;
//			}
//		}
//		svm.close();
//		
//		System.out.println("total successful SVM prediction possible plates " + isplate +  "/" + (isplate+notplate) + 
//				",\n while total unsuccessful SVM prediction possible plates are " + notplate + "/" + (isplate+notplate));
//		System.out.println("\n\n");
//	}
	/**
	 * A black box function that performs plate clipping and SVM. For more internal working function, please see recommendation area
	 * @param filepath filepath of the image
	 * @return returns a plate clip image classified as a clip by SVM
	 * @throws IOException thrown when image cannot be loaded
	 * @see imageanalysis.Histogram
	 */
	public static BufferedImage run(String filepath) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		svm = new SVMachine();
		svm.getTrainingfiles(savefolder + "possibleplates/");
		svm.init();
		
		//Writer writer = new Writer(savefolder + filepath + ".txt");
		
		//if(writerOn)	writer = new Writer(savefolder + "/" + filepath + "/" + filepath + ".txt");	//for quick generating log files
		
		MatToBufferedImage conv = new MatToBufferedImage();
		
		//Mat source = Highgui.imread(readfolder + filename + extension);
		Mat source = Highgui.imread(filepath);
		if(source.width() > 800 && source.height() > 800){
			double new_perc = (double) 800/source.width();
			Imgproc.resize(source, source, new Size(source.width()*new_perc, source.height()*new_perc));
		}else{
			Imgproc.resize(source, source, new Size(source.width(), source.height()));
		}
		//Imgproc.resize(source, source, new Size(source.width()*0.2, source.height()*0.2));
		//displayImage(source, "original image");
		
		Mat processed = new Mat(source.width(), source.height(), CvType.CV_64FC3);
		Coordinates[] ycoords = new Coordinates[2];//0 is ypeak, 1 is yband
		Coordinates[] xcoords = new Coordinates[2];//0 is xpeak, 1 is xband
		//System.out.println("width: " + m.width() + "height: " + m.height());	//debugging purposes

		//Preprocess image
		vertProcess(source, processed, 150);
		img = conv.getImage(processed);
		System.out.println("original image");	//if(writerOn)	writer.writeln("original image");//for quick generating log files
		//displayImage(source, new Coordinates(0, source.width()), new Coordinates(0, source.height()), "Original Image", filename);
		

		
		//Vertical Projection histogram & Image
		//if(writerOn)	writer.writeln("================   VERTICAL PROJECTION  ============");	//for quick generating log files
		//if(writerOn)	writer.writeln("0");
		System.out.println("================   VERTICAL PROJECTION  ============");
		System.out.println("0");
		ycoords = displayHistogram(img, "Vertical Projection Histogram", "vertical", 0.55);
		//displayImage(source, new Coordinates(0, source.width()), ycoords[1], "vertical projection 0th", filename);
		Mat band = source.submat(ycoords[1].getX(), ycoords[1].getY(), 0, source.width());
		if(bands.add(band))	System.out.println("band added");//if(writerOn)	writer.writeln("band added\n\n");
		System.out.print("\n\n");
		
		
		//produce more possible plates
		if(numberofcandidates > 1){
			for(int i = 1; i < numberofcandidates; i++){
				System.out.println(i);
				Coordinates[] coords = displayHistogram(img, "Vertical Projection Histogram " + i + "th", "vertical", 0.55, i);
				band = source.submat(coords[1].getX(), coords[1].getY(), 0, source.width());
				if(bands.add(band)){
					System.out.println("band added");//writer.writeln("band added \n\n");
				}
				//displayImage(source, new Coordinates(0, source.width()), coords[1], "vertical projection " + i + "th", filename);
				System.out.print("\n\n");
			}

		}
		
	
		//Horizontal Projection
		Mat horizontal = new Mat(processed.height(), processed.width(), CvType.CV_64FC3);
		
		Iterator it = bands.iterator();
		
		if(it.hasNext()){
			int count = 1;
			//if(writerOn)	writer.writeln("================ HORIZONTAL PROJECTION  ===============");
			System.out.println("================ HORIZONTAL PROJECTION  ===============");
			while(it.hasNext()){
				horizontal = (Mat) it.next();
				horiProcess(horizontal, processed, 150);
				img = conv.getImage(processed);
				xcoords = displayHistogram(img, "Horizontal Projection " + count + "th" + " Histogram", "horizontal", 0.1);
				//displayImage(horizontal, xcoords[1], new Coordinates(0,horizontal.height()), "horizontal projection " + count + "th", filename);
				Mat possibleplate = horizontal.submat(0, horizontal.height(), xcoords[1].getX(), xcoords[1].getY());
				//displayImage(possibleplate, "horizontal projection " + count + "th");
				System.out.print(getMatDetail(possibleplate)); 
				//if(writerOn)	writer.writeln(getMatDetail(possibleplate));
				//if(writerOn)	writer.writeln("The cropped image is " + verifySizes(possibleplate) + " possible plate.\n\n" );
				System.out.println("The cropped image is " + verifySizes(possibleplate) + " possible plate" + "\n\n");
				if(verifySizes(possibleplate))	possibleplates.add(possibleplate);
				count++;
			}
		}else{
			horizontal = source.submat(ycoords[1].getX(), ycoords[1].getY(), 0, source.width());
			horiProcess(horizontal, processed, 150);
			img = conv.getImage(processed);
			xcoords = displayHistogram(img, "Horizontal Projection Histogram", "horizontal", 0.1);
			//displayImage(horizontal, xcoords[1], new Coordinates(0,horizontal.height()), "horizontal projection", filename);
			Mat possibleplate = horizontal.submat(0, horizontal.height(), xcoords[1].getX(), xcoords[1].getY());
			//if(writerOn)	writer.writeln(getMatDetail(possibleplate));
			//if(writerOn)	writer.writeln("The cropped image is " + verifySizes(possibleplate) + " possible plate.\n\n" );
			System.out.print(getMatDetail(possibleplate));
			System.out.println("The cropped image is " + verifySizes(possibleplate) + " possible plate" + "\n\n");
			if(verifySizes(possibleplate)){
				possibleplates.add(possibleplate);
			}
		}
		
		if(possibleplates.size() == 0){
			//if(writerOn)	writer.writeln("no possible plate found!");
			System.out.println("No possible plate found!\n\n");
			noplate++;
		}else{
			for(int i = 0; i<possibleplates.size(); i++){
				
					Mat plate = new Mat();
					possibleplates.get(i).copyTo(plate);
					//System.out.println(possibleplates.get(i).channels());		//for debugging purposes
					plate = resizePossiblePlate(plate);
					possibleplates.set(i, plate);
					System.out.println("possible plate no " + (i+1) + "\n");
					//displayImage(plate, filename + "possible plate no " + (i+1), true, "plates");
					//displayImage(plate, "possibleplate");
					System.out.println(getMatDetail(plate));
					plate = plate.reshape(1,1);
					plate.convertTo(plate, CvType.CV_32FC1);
					System.out.println(plate.toString());
					
					System.out.println("Support Vector Machine prediction found : " + svm.predict(plate));
					//if(writerOn)	writer.writeln("Support Vector Machine prediction found : " + svm.predict(plate));
					if(svm.predict(plate)){
						
						System.out.println("Plate found! \n");// if(writerOn) writer.writeln("Plate found! \n");
						isplate++;
						BufferedImage result = conv.getImage(possibleplates.get(i));

						bands.clear();
						possibleplates.clear();
						return result;
					}else{
						notplate++;
					}
			}

			BufferedImage result = conv.getImage(possibleplates.get(0));
			haveplate++;
			return result;
			
		}
		System.out.println("total successful captures with possible plates are " + haveplate + "/ " + (haveplate+noplate) + ",\n while no successful possible plates are " + noplate + "/" + (haveplate+noplate));
		System.out.println("\n\n");
		
	//	writer.closefile();
		bands.clear();
		possibleplates.clear();
		//return a blank image
		BufferedImage blank = new BufferedImage(144,33,BufferedImage.TYPE_INT_RGB );
		
		return blank;
		
	}
	
	/**
	 * Processes the image to be prepared for vertical projection histogram. Here, a different Sobel operator is used compared to the other preprocessing function
	 * <p>The image is first converted to grayscale, then convolutioned with Sobel filter, Blurred to reduce noise and finally
	 *  thresholded with the given threshold value. To produce a more solid image, the input Mat is then went through a mathematical morphology(erode)
	 * @param src	input image
	 * @param dst	output image
	 * @param Threshold	threshold value
	 * @return	processed image
	 * @throws IOException thrown when image can't be loaded
	 */
	private static Mat vertProcess(Mat src, Mat dst, double Threshold) throws IOException{
		Mat bw = new Mat(src.width(), src.height(), CvType.CV_64FC2);
		Imgproc.cvtColor(src, bw, Imgproc.COLOR_RGB2GRAY);
		Mat morphelem = new Mat(src.width(), src.height(), CvType.CV_64FC2);
		Mat threshold = new Mat(src.width(), src.height(), CvType.CV_8UC1);
		Imgproc.Sobel(bw, threshold, src.depth(), 1, 0);
		Imgproc.GaussianBlur(threshold, threshold, new Size(5,5), 0);
		Imgproc.threshold(threshold, threshold, Threshold, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY);
		morphelem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
		Imgproc.morphologyEx(threshold, dst, 3, morphelem);
		//displayImage(dst, new Coordinates(0, dst.width()), new Coordinates(0, dst.height()), "processed picture");
		
		return dst;
	}
	
	/**
	 * Processes the image to be prepared for vertical projection histogram. Here, a different Sobel operator is used compared to the other preprocessing function
	 * <p>The image is first converted to grayscale, then convolutioned with Sobel filter, Blurred to reduce noise and finally
	 *  thresholded with the given threshold value. To produce a more solid image, the input Mat is then went through a mathematical morphology(erode)
	 * @param src	input image
	 * @param dst	output image
	 * @param Threshold	threshold value
	 * @return	processed image
	 * @throws IOException thrown when image can't be loaded
	 */
	private static Mat horiProcess(Mat src, Mat dst, double Threshold) throws IOException{
		Mat bw = new Mat(src.size(), CvType.CV_64FC2);
		Imgproc.cvtColor(src, bw, Imgproc.COLOR_RGB2GRAY);
		Mat morphelem = new Mat(src.size(), CvType.CV_64FC2);
		Mat threshold = new Mat(src.size(), CvType.CV_8UC1);
		Imgproc.Sobel(bw, threshold, src.depth(), 0, 2);
		Imgproc.GaussianBlur(bw, threshold, new Size(5,5), 0);
		
		Imgproc.threshold(threshold, threshold, Threshold, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY_INV);
		morphelem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
		Imgproc.morphologyEx(threshold, dst, 3, morphelem);
		//displayImage(dst, new Coordinates(0, dst.width()), new Coordinates(0, dst.height()), "processed picture");
		displayImage(dst, "image");
		return dst;
	}
	
	private static Coordinates[] displayHistogram(BufferedImage img, String title, String orientation, double bandthreshold){
		Coordinates[] coords = new Coordinates[2];	//coords[0] being peak coords, coords[1] being band coords
		Histogram hist = new Histogram(title, img, orientation);
		
		Coordinates peak_coord;
		Coordinates band_coord;
		if(orientation == "vertical"){
			int[] arr = hist.getImgArr();
			peak_coord = hist.getPeakCoord(arr);
			band_coord = hist.getBandCoords(arr, bandthreshold, "vertical");
		}else{
			System.out.println("hori");
			int[] arr = hist.getHoriImgArr(img);
			peak_coord = hist.getPeakCoord(arr);
			band_coord = hist.getBandCoords(arr, bandthreshold, "horizontal");
		}
		if(peak_coord == null || band_coord == null){
			System.err.println("Error. unable to find peak coordinates or band coordinates");
		}
		System.out.println("peak coordinates are: " + peak_coord.toString());
		System.out.println("band coordinates are: " + band_coord.toString());
		coords[0] = peak_coord;
		coords[1] = band_coord;
		
		hist.pack();
		RefineryUtilities.centerFrameOnScreen(hist);
		if(displayOn)	hist.setVisible(true);
		
		return coords;
	}
	
	private static Coordinates[] displayHistogram(BufferedImage img, String title, String orientation, double bandthreshold, int newPeak){
		Coordinates[] coords = new Coordinates[2];//coords[0] being peak coords, coords[1] being band coords
		Histogram hist = new Histogram(title, img, orientation);

		int[] arr;
		if(orientation == "vertical"){
			arr = hist.getImgArr();}
		else{
			arr = hist.getHoriImgArr(img);}
		
		for(int i = 0; i < newPeak; i++){
			Coordinates peak;
			Coordinates band;
			
			if(orientation == "vertical"){
				peak = hist.getPeakCoord(arr);
				band = hist.getBandCoords(arr, bandthreshold, "vertical");
			}else{
				System.out.println("hori");
				peak = hist.getPeakCoord(arr);
				band = hist.getBandCoords(arr, bandthreshold, "horizontal");
			}
			System.out.println("band to be zeroized: " + band.toString());
			arr = clearPeak(arr, band);
			System.out.println("band Zeroized");
		}
		
		Coordinates peak_coord = hist.getPeakCoord(arr);
		Coordinates band_coord;
		if(orientation == "vertical"){
			band_coord = hist.getBandCoords(arr, bandthreshold, "vertical");
		}else{
			System.out.println("hori");
			band_coord = hist.getBandCoords(arr, bandthreshold, "horizontal");
		}
		if(peak_coord == null || band_coord == null){
			System.err.println("Error. unable to find peak coordinates or band coordinates");
		}
		System.out.println("peak coordinates are: " + peak_coord.toString());
		System.out.println("band coordinates are: " + band_coord.toString());
		coords[0] = peak_coord;
		coords[1] = band_coord;
		
		hist.pack();
		RefineryUtilities.centerFrameOnScreen(hist);
		if(displayOn)	hist.setVisible(true);
		
		return coords;
	}
	
	private static int[] clearPeak(int[] imgarr, Coordinates band_coord){
		for(int i = band_coord.getX(); i<band_coord.getY(); i++){
			imgarr[i] = 0;
		}
		
		return imgarr;
		
	}
	
	public static void displayImage(Mat mat, String windowcaption) throws IOException{
		MatToBufferedImage conv = new MatToBufferedImage();
		BufferedImage image = conv.getImage(mat);
		SetImage(image);
		if(displayOn)	ViewImage(windowcaption);
	}
	
	
	/**
	 * <p> displays Mat in a JPanel frame on the screen. Allows quick view of Mat images </p>
	 * @param mat	The Mat image to be displayed
	 * @param windowcaption	The frame window caption
	 * @param savepic	A boolean value. Setting this to true saves the Mat image to the specified savefolder path
	 * @param filename	If savepic value is true, the file will be saved with this filename
	 * @throws IOException	To catch exception on unable to save the Mat image.
	 */
	public static void displayImage(Mat mat, String windowcaption, boolean savepic, String filename) throws IOException{
		if(!savepic){
			displayImage(mat,windowcaption);
		}else{
			MatToBufferedImage conv = new MatToBufferedImage();
			BufferedImage image = conv.getImage(mat);
			SetImage(image);
			if(displayOn)	ViewImage(windowcaption);
			System.out.println("saving image..." + SaveImage(image, windowcaption , savefolder + "/" + filename + "/"));
		}
	}
	
	private static void displayImage(Mat mat, Coordinates axisX, Coordinates axisY,String windowcaption) throws IOException{
		MatToBufferedImage conv = new MatToBufferedImage();
		BufferedImage image = conv.getImage(mat);
		BufferedImage result = getCropImage(image, axisX.getX(), axisX.getY(), axisY.getX(), axisY.getY());
		//System.out.println("image width: " + result.getWidth() + ", image height: " + result.getHeight());
		
		SetImage(result);
		//System.out.println(SaveImage(filename + "result"));
		if(displayOn)		ViewImage(windowcaption);
	}
	
	private static void displayImage(Mat mat, Coordinates axisX, Coordinates axisY, String windowcaption, String filename) throws IOException{
		MatToBufferedImage conv = new MatToBufferedImage();
		BufferedImage image = conv.getImage(mat);
		BufferedImage result = getCropImage(image, axisX.getX(), axisX.getY(), axisY.getX(), axisY.getY());
		//System.out.println(windowcaption + "image width: " + result.getWidth() + ", image height: " + result.getHeight());
		
		SetImage(result);
		//creates a new folder for this test image
		File folder = new File(savefolder + "/" + filename);
		if(!folder.exists() && !folder.mkdir()){	//if the folder already exist and mkdir failed
			System.err.println("Error creating Directory " + folder.toString());
		}
		System.out.println("saving image..." + SaveImage(result, filename + windowcaption + "result", savefolder + "/" + filename + "/"));
		if(displayOn)		ViewImage(windowcaption);
	}
	
	private static boolean verifySizes(Mat possibleplate){
		//TODO:	hardcoded value. Please change for flexibility
		double error = 0.4;	
		double ratio = 130/20;	//6.5
		//current min size, 130 x 20
		
		//min and max aspect ratio with the given error relief
		double rmin = ratio - (ratio*error);
		double rmax = ratio + (ratio*error);
		
		//set a min and max area
		//TODO: 15 and 70 are hardcoded, baseless value. Please consider refactoring
		int min = 15*15*(int) ratio;//minimum area, current val is 
		int max = 70*70*(int) ratio;//maximum area
		
		int area = possibleplate.width()*possibleplate.height();
		double rplate = (double) possibleplate.width()/(double) possibleplate.height();
		if(rplate < 1){
			rplate = 1/rplate;
		}
		if((area < min || area > max) || (rplate < rmin || rplate > rmax)){
			return false;
		}else{
			return true;
		}
	}
	/**
	 * resies plate to a standard plate size of 33x144 to be classified by the Support Vector Machine
	 * @param img_crop input possible plate
	 * @return resized possible plate
	 */
	private static Mat resizePossiblePlate(Mat img_crop){
		Mat resultResized = new Mat();
		resultResized.create(33,144, CvType.CV_64FC3);
		Imgproc.resize(img_crop, resultResized, resultResized.size(), 0, 0,Imgproc.INTER_CUBIC);
		//Equalize cropped image
		Mat grayResult = new Mat(33, 144, CvType.CV_64FC2);
		Imgproc.cvtColor(resultResized, grayResult, Imgproc.COLOR_RGB2GRAY);
		Imgproc.blur(grayResult, grayResult,new Size(3,3));
		grayResult.assignTo(grayResult, CvType.CV_8UC1);
		Imgproc.equalizeHist(grayResult, grayResult);
		
		return grayResult;
	}
	
/**
 * temporary usage to invert all train images
 */
//	private static void invertImage() throws IOException{
//		MatToBufferedImage conv = new MatToBufferedImage();
//		File folder = new File("train/characters");
//		
//		for(File file : folder.listFiles()){
//			if(!file.isHidden()){
//				Mat img = Highgui.imread(file.getAbsolutePath());
//				Core.bitwise_not(img, img);
//				System.out.println(img.size());
//				Imgproc.resize(img, img, new Size(40,50));
//				
//				//displayImage(img,"test");
//				BufferedImage image = conv.getImage(img);
//				//System.out.println(file.getAbsolutePath());
//				File outputfile = new File(file.getAbsolutePath());
//				ImageIO.write(image, "jpg", outputfile);
//			} 
//		}
//		 
//		
//	}


}
