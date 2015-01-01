package imageanalysis;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class test extends Snapshot {

	private static BufferedImage img;
	private static String extension = ".jpg";
	private static String filename = "test_047possibeplate 1result";
	private static final int numberofcandidates = 5;
	private static final String readfolder = "images/";
	private static final String savefolder = "images/results/";
	private static final String trainingfolder = "train/characters/";
	
	public static void main(String args[]) throws IOException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Highgui.imread(savefolder + "possibleplates/positives/" + filename + extension);
		
		//initiate OCR training
		OCR.getTrainingFiles(trainingfolder);
		OCR.trainANN();
		
		System.out.println("training done");
		
		ArrayList<Mat> contours = new ArrayList<Mat>();
		ArrayList<Character> characters = new ArrayList<Character>();
		contours = OCR.SketchContours(src);
		
		for(Mat contour : contours){
			System.out.println("contours " + contour.toString());
			char character = OCR.GetCharacter(contour);
			characters.add(character);
		}
		
		for(char character : characters){
			System.out.print(character);
		}

	}
}
