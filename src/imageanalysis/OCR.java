package imageanalysis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvANN_MLP;
/**
 * The OCR Class provides essential functions to the Optical Character Recognition functionality,
 * examples of such functions are training neural network, classifying and resizing and pre-processing contours, 
 * as well as findcontours from a possible plate image
 * 
 * The Code here are modifed and followed the source codes of Mastering OpenCV with Practical Computer Vision Projects 2012 by Packt Publishing.
 * All source codes are translated from C++ repository linked here: https://github.com/MasteringOpenCV/code/tree/master/Chapter5_NumberPlateRecognition
 * @author Adam Goh
 *
 */
public class OCR extends Snapshot{
	public static final int numCharacters = 22;//0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z
	public static final char strCharacters[] = {'0','1','2','3','4','5','6','7','8','9',
												'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	public static boolean isTrained = false;
	public static CvANN_MLP ann;
	
	private static Mat trainingDataf5;
	private static Mat trainingDataf10;
	private static Mat trainingDataf15;
	private static Mat trainingDataf20;
	private static Mat classes;
	
	public static int charSizeW = 40;
	public static int charSizeH = 50;
	private static final String trainingfolder = "train/characters/";
	
	
	
	public OCR() throws IOException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		charSizeW = 40;
		charSizeH = 50;
		trainTheMachine();
		
	}
	
	public void trainTheMachine() throws IOException{
		//initiate OCR training
		OCR.getTrainingFiles(trainingfolder);
		OCR.trainANN();
		
		System.out.println("training done");
	
	}
	
	public ArrayList<Character> getPlateCharacter(BufferedImage img){
		System.out.println("img" + img.toString());
		MatToBufferedImage conv = new MatToBufferedImage();
		Mat src = conv.getMatFromImage(img);
		System.out.println("src " + src.toString());
		ArrayList<Mat> contours = new ArrayList<Mat>();
		ArrayList<Character> characters = new ArrayList<Character>();
		try {
			contours = OCR.SketchContours(src);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Mat contour : contours){
			System.out.println("contours " + contour.toString());
			char character = OCR.GetCharacter(contour);
			characters.add(character);
		}
		
		return characters;
//		
//		for(char character : characters){
//			System.out.print(character);
//		}
	}
	
	/**
	 * <p> takes a plate clip image and finds contours from it. Returns contours that are verified as plausible characters with pre-defined character
	 *  sizes. </p>
	 *  <p> The major risk in this function is that the image threshold value affects the success rate of getting the right contours. </p>
	 * @param src	The input image
	 * @return	An arrayList of contours that are plausible characters.
	 * @throws IOException	returns an exception when image is not able to be displayed with the displayImage function.
	 */
	public static ArrayList<Mat> SketchContours(Mat src) throws IOException{
		Mat img = src;
		
		Mat img_threshold = new Mat();
		Mat img_contours = new Mat();
		Mat img_morph = new Mat();
		ArrayList<Mat> characters = new ArrayList<Mat>();
		
		//img.convertTo(img, CvType.CV_8UC1);
		//Imgproc.cvtColor(img, img_threshold, Imgproc.COLOR_RGB2GRAY);
		Mat detected = new Mat();
		
		Imgproc.threshold(img_threshold, img_threshold, 120, 255, Imgproc.THRESH_BINARY);
		//img_threshold.copyTo(img_morph);
		//Mat morphelem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
		//Imgproc.morphologyEx(img_morph, img_morph, 3, morphelem);
		//img_morph.copyTo(img_contours);
		img_threshold.copyTo(img_contours);
		//displayImage(img_contours, "img_contours");
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		contours = segmentation(img_contours);
		if(contours.isEmpty()){
			return characters;
		}
		img_threshold.copyTo(detected);
		Imgproc.cvtColor(detected, detected, Imgproc.COLOR_GRAY2RGB);
		
		Imgproc.drawContours(detected, contours, -1, new Scalar(255,255,0));
				for(MatOfPoint contour : contours){
					//draws a rectangle around all rectangles contours found
					Rect box = Imgproc.boundingRect(contour);
					Core.rectangle(detected, box.tl(), box.br(), new Scalar(0,0,255));
					
					Mat character = new Mat(img_threshold, box);
					
					System.out.println("characters w: " + character.width() + ",characters h:" + character.height() + character.toString());
					try {
						if(verifyplatesize(character)){
							//all contours found that fits the estimated size of a character will be resized for ANN classification.
							character.convertTo(character, CvType.CV_32F);
							character = preprocessChar(character);
							characters.add(character);
							displayImage(character, "Windowcap");
							
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
		return characters;
	}
	
	/**
	 * <p> takes in a detected contour, which is a character, and returns the result as a character type. </p>
	 * <p> The function will only work if the MLP is trained with the given training files. </p>
	 * @param contour	Takes in a contour Mat, which should be given a set of contours by SketchContours function
	 */
	public static char GetCharacter(Mat contour){
		Mat feature = features(contour, 15);
		
		int character = classify(feature);
		
		return strCharacters[character];
		
	}
	
	/**
	 * <p> a function that segments the the input Mat into List of contours. A contour is stated as a MatofPoint
	 * </p>
	 * The function uses findContours to get its contours from the image
	 * @param img input image
	 * @return  list of Contours. if the input image is empty, returns empty contour
	 * 
	 * @see Imgproc#findContours(Mat, List, Mat, int, int)
	 */
	public static List<MatOfPoint> segmentation(Mat img){
		Mat threshold = new Mat();
		Mat img_contours = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//Imgproc.threshold(img, threshold, 60, 255, Imgproc.THRESH_BINARY);
		img.copyTo(img_contours);
		if(!img_contours.empty()){
			Imgproc.findContours(img_contours, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);	//new Mat() as a dummy mat for optional Mat
		
			return contours;
		}else{
			return contours;
		}
	}
	
	/**
	 * Verify a contour fits the characteristics of an character
	 * A contour is considered to pass if it is in the are of character aspect from a default character size
	 * of 45x77. An error rate of 35% is accepted and softened
	 * @param src input contour
	 * @return	true only if it passes the minimum and maximum areaAspect and min and maximum Heights.
	 */
	public static boolean verifyplatesize(Mat src)
	   {
	     //Char sizes 45x77
		 double aspect = 45.0f/77.0f;
	     double charAspect = (float)src.cols()/(float)src.rows();
	     double error = 0.35;
	     double minHeight =15;
	     double maxHeight =28;
	     //We have a different aspect ratio for number 1, and it can be
	     //~0.2
	     double minAspect = 0.2;
	     double maxAspect = aspect+aspect*error;
	     //area of pixels
	     double area= Core.countNonZero(src);
	     //bb area
	     double bbArea = src.cols()*src.rows();
	     //% of pixel in area
	     double percPixels = area/bbArea;
	    
	     if(percPixels < 0.8 && charAspect > minAspect && charAspect <
	     maxAspect && src.rows() >= minHeight && src.rows () < maxHeight)
	    	 return true;
	     else
	    	 return false;
	}
	
	/**
	 * <p> Produces a vertical projection of the input contour to be further used for neuralnetwork </p>
	 * @param img input contour
	 * @return vertical projection histogram
	 */
	private static Mat ProjectedVertHistogram(Mat img){
		int sz = img.cols();
		
		Mat mhist = Mat.ones(1, sz, CvType.CV_32FC1);
		
		for(int j=0; j<mhist.height(); j++){
			for(int i = 0; i<mhist.width(); i++){
				Mat data = img.col(i);
				
				//System.out.println(data.toString());
				//System.out.println("number non-Zero elements in each row are: " + Core.count(data));
				float value = Core.countNonZero(data);
				mhist.put(j, i, value);
			}
		}

//		try {
//			displayImage(mhist, "edited");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//System.out.println(mhist.get(0,1)[0]);
		
		//normailize histogram
		double minval, maxval;
		MinMaxLocResult minmaxlocR = Core.minMaxLoc(mhist);
		minval = minmaxlocR.minVal;
		maxval = minmaxlocR.maxVal;
		if(maxval > 0)	mhist.convertTo(mhist, -1, 1.0f/maxval, 0);
		
		return mhist;

	}
	
	/**
	 * <p> Produces a horizontal projection of the input contour to be further used for neuralnetwork </p>
	 * @param img input contour
	 * @return horizontal projection histogram
	 */
	private static Mat ProjectedHoriHistogram(Mat img){
		int sz = img.rows();
		
		Mat mhist = Mat.ones(1, sz, CvType.CV_32FC1);
		
		for(int j=0; j<mhist.height(); j++){
			for(int i = 0; i<mhist.width(); i++){
				Mat data = img.row(i);
				
				//System.out.println(data.toString());
				//System.out.println("number non-Zero elements in each row are: " + Core.countNonZero(data));
				float value = Core.countNonZero(data);
				mhist.put(j, i, value);
			}
		}
		
		//System.out.println(mhist.get(0,1)[0]);

		
		//normailize histogram
		double minval, maxval;
		MinMaxLocResult minmaxlocR = Core.minMaxLoc(mhist);
		minval = minmaxlocR.minVal;
		maxval = minmaxlocR.maxVal;
		if(maxval > 0)	mhist.convertTo(mhist, -1, 1.0f/maxval, 0);
		
		return mhist;
	}
	/**
	 * Generates a Mat with processed features of the input Contour.
	 * @param src	input Mat
	 * @param sizeData	size of the features character
	 * @return Mat with features found from histogram projections
	 * 
	 */
	private static Mat features(Mat src, int sizeData){

		
		//Histogram features
		Mat horihist = ProjectedHoriHistogram(src);
		Mat verthist = ProjectedVertHistogram(src);
		
		//Low data feature
		Mat lowData = new Mat();
		Imgproc.resize(src, lowData, new Size(sizeData, sizeData));
		//System.out.println("verthist cols" + verthist.cols() + ", horihist cols" + horihist.cols());
		int numofCols = verthist.cols() + horihist.cols() + lowData.cols()*lowData.cols();
		Mat out = Mat.zeros(1, numofCols, CvType.CV_32F);
		
		//Assign values to feature
		int j = 0;
		
		for(int i=0; i<verthist.cols(); i++){
			out.put(0, j, verthist.get(0, i)); 
			j++;
		}
		
		for(int i=0; i<horihist.cols(); i++){
			out.put(0, j, horihist.get(0, i)); 
			j++;
		}
		
		for(int x=0; x<lowData.cols(); x++){
			for(int y=0; y<lowData.rows(); y++){
				out.put(0, j,  lowData.get(x,y));
				j++;
			}
		}
		
		return out;
	}
	
	public boolean getTrainingfiles(String pathname){
		
		return false;
	}
	
	
	public static void trainANN(){
	//	System.out.println("trainingDataf15 has like " + trainingDataf15.toString());
	//	System.out.println("classes has like " + classes.toString());
		
		
		OCR.train(trainingDataf15, classes, 10);
	}
	
	/**
	 * Trains the given trainingdata input and classes through n number of layers of multilayer neural network
	 * @param TrainData	Training data set formed into a Mat object
	 * @param classes classses formed into a mat object
	 * @param nlayers number of layers in the neural network
	 */
	private static void train(Mat TrainData, Mat classes, int nlayers){
	
		Mat layerSizes = new Mat(1,3, CvType.CV_32SC1);
		System.out.println(" Traindata nofcols " + TrainData.cols());
		layerSizes.put(0,0, TrainData.cols());
		layerSizes.put(0,1, nlayers);
		layerSizes.put(0,2, numCharacters);
		
		ann = new CvANN_MLP();
		ann.create(layerSizes, CvANN_MLP.SIGMOID_SYM, 1, 1);
		
		//Preparing trainclasses by creating a mat with n trained data by m classes
		Mat trainClasses = new Mat();
		trainClasses.create(TrainData.rows(), numCharacters, CvType.CV_32FC1);
		//System.out.println("trainClaases of " + trainClasses.toString());
		for(int i=0; i< trainClasses.rows(); i++){
			for(int k=0; k < trainClasses.cols(); k++){
				//If class of data i is same as a k class
				//System.out.println("classes get i=" + i + " k = " + k +" " + classes.get(0, i)[0]);
				if(k == classes.get(i, k)[0] ){
					trainClasses.put(i,k, 1);
				}else{
					trainClasses.put(i,k, 0);
				}
			}
			
			Mat weights = new Mat(1, TrainData.rows(), CvType.CV_32FC1, Scalar.all(1) );
			
			//learn classifier
			ann.train(TrainData, trainClasses, weights);
			isTrained = true;
		}
	}
	
	/**
	 * Classifies the input contour to be one of the classes trained
	 * 
	 * @param src input Mat contour
	 * @return contour class value
	 */
	private static int classify(Mat src){
		Mat img = src;
		int result = -1;
		Mat output = new Mat(1, numCharacters, CvType.CV_32FC1);
		System.out.println("img " + img.toString() );
		ann.predict(img, output);
		Point maxLoc;
		double maxVal;
		MinMaxLocResult minmaxlocR = Core.minMaxLoc(output);
		maxVal = minmaxlocR.maxVal;
		maxLoc = minmaxlocR.maxLoc;
		
		return (int) maxLoc.x;
		
	}
	/**
	 * Preprocesses the value to be at a given set size
	 * @param char_in contour in Mat
	 * @return preprocessed contour
	 */
	private static Mat preprocessChar(Mat char_in){
		//remap image to a new Mat
		int h = char_in.rows();
		int w = char_in.cols();
		
		Mat transform_mat = Mat.eye(new Size(3,2), CvType.CV_32F);
		int m = Math.max(w, h);
		
		transform_mat.put(0, 2, m/2 - w/2);
		transform_mat.put(1, 2, m/2 - h/2);
		
		Mat warpImage = new Mat(new Size(m,m), char_in.type());
		//System.out.println("transform_mat: " + transform_mat.toString());
		Imgproc.warpAffine(char_in, warpImage, transform_mat, warpImage.size(), Imgproc.INTER_LINEAR, Imgproc.BORDER_CONSTANT, new Scalar(0));
		
		Mat out = new Mat();
		Imgproc.resize(warpImage, out, new Size(charSizeW, charSizeH));
		
		return out;
	}
	
	/**
	 * Get training data files from the folder and load it onto a Mat
	 * @param filepath	The given path to the folder with all training characters
	 * @throws IOException 
	 */
	public static void getTrainingFiles(String filepath) throws IOException{
		classes = new Mat();
		//4 classes of training labels with 5x5, 10x10, 15x15, and 20x20
		trainingDataf5 = new Mat();
		trainingDataf10 = new Mat();
		trainingDataf15 = new Mat();
		trainingDataf20 = new Mat();
		
		ArrayList<Integer> trainingLabels = new ArrayList<Integer>();
		File folder = new File(filepath);
		File[] files = folder.listFiles();
		System.out.println("number of files are " + files.length);
		
		if(!folder.isDirectory()){
			System.err.println("Error! folder given is not a directory");
		}
		
		//for each character, get the files and push their features in
			for(File file : files ){
				if(!file.isHidden()){
					Mat character = Highgui.imread(file.getAbsolutePath(), 0);
					
					Mat f5 =  features(character,  5);
					Mat f10 = features(character, 10);
					Mat f15 = features(character, 15);
					Mat f20 = features(character, 20);
					
					//do i need to convert to 32FC1?
					//System.out.println("f5 size is  " + f5.size());
					trainingDataf5.push_back(f5);
					//System.out.println("f5 push back successful");
					trainingDataf10.push_back(f10);
					//System.out.println("f10 push back successful");
					trainingDataf15.push_back(f15);
					//System.out.println("f15 push back successful");
					trainingDataf20.push_back(f20);
					//System.out.println("f20 push back successful");
					char c = file.getName().charAt(0);
					//System.out.println("This is for character " + c);
					//trainingLabels.add(i);
				}else{
					
				}
			}
		//}
			
		for(int i = 0; i< numCharacters; i++){
			for(int n=0; n<7; n++){
				trainingLabels.add(i);
			}
		}
		classes = new Mat(new Size(numCharacters, trainingLabels.size()), CvType.CV_32FC1);
		for(int i=0; i < numCharacters; i++){
			for(int label : trainingLabels){
				classes.put(i, label, 1);
				
			}
		}
		
		
	//	System.out.println("traning labels " + trainingLabels.size());
		//puts all label into a mat with numcharacters x noflabels
		
		
		//System.out.println("classes is here " + classes.toString() + "and it is " + classes.empty() + " empty, having " + classes.total() + " of elements");
		
		System.out.println("trainingDataf5 :" + trainingDataf5.toString());
		System.out.println("trainingDataf10 :" + trainingDataf10.toString());
		System.out.println("trainingDataf15 :" + trainingDataf15.toString());
		System.out.println("trainingDataf20 :" + trainingDataf20.toString());
		
	}
	
	/**
	 * cleans the static training variables and training labels. This should be done before fetching a new one.
	 * @return	status of the training files. Return trues if all static variables are null
	 */
	public static boolean clearTrainingFiles(){
		trainingDataf5.release();
		trainingDataf10.release();
		trainingDataf15.release();
		trainingDataf20.release();
		
		if(trainingDataf5.empty() && trainingDataf10.empty() && trainingDataf15.empty() && trainingDataf20.empty()){
			return true;
		}
		return false;
	}

}
