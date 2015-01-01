package imageanalysis;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;
/**
 * The SVMachine class is the core class for the implementation of Support Vector Machines.
 * The class supports SVM training, classifications and getting the training files.
 * 
 * Uses Open Source library OpenCV 2.4.7
 * 
 * @author Adam Goh
 *@see imageanalysis.SVMachine#init()
 *@see imageanalysis.SVMachine#getTrainingfiles(String)
 *@see imageanalysis.SVMachine#predict(Mat)
 *@see imageanalysis.SVMachine#close()
 */
public class SVMachine {
	private static Mat svm_trainingimages;
	private static Mat svm_trainingdata;
	private static Mat svm_traininglabels;
	private static Mat svm_classes;
	//Set SVM Parameters
	private static CvSVMParams svm_params;
	private CvSVM classifier;
	
	public SVMachine(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		svm_params = new CvSVMParams();
		//init all Mats
		svm_trainingdata = new Mat();
		svm_trainingimages = new Mat();
		svm_traininglabels = new Mat();
		svm_classes = new Mat();
	}
	
	/**
	 * Sets all svm parameters to the speific specifications before training and classifications 
	 */
	public void init(){
		svm_params.set_svm_type(CvSVM.C_SVC);
		svm_params.set_kernel_type(CvSVM.LINEAR);
		svm_params.set_degree(0);
		svm_params.set_gamma(1);
		svm_params.set_C(1);
		svm_params.set_nu(0);
		svm_params.set_p(0);
		svm_params.set_term_crit(new TermCriteria(TermCriteria.MAX_ITER, 1000, 0.01));
		
	}
	
	/**
	 * Close function
	 * 
	 */
	public void close(){
		classifier.clear();
		svm_trainingdata = new Mat();
		svm_traininglabels = new Mat();
		svm_classes = new Mat();
	}
	
	
	/**
	 * Classification procedure
	 * 
	 * @param possibleplate a possibleplate Mat with the right specifications only can go through here
	 * @return true only if the response is right
	 */
	public boolean predict(Mat possibleplate){
		int response = (int) classifier.predict(possibleplate);
		if(response == 1)
			return true;
		
		return false;
	}
	
	/**
	 * <h1> Training files retrieval </h1>
	 * All training files are stored in a specified folder and grouped to positive classifications and negative classifications.
	 * The training images are stored into a public array variable to be used for training.
	 * 
	 * @param path Trainingfiles URL is used as input
	 */
	public void getTrainingfiles(String path){
		svm_trainingimages = new Mat();
		
		File[] positives = new File(path + "positives/").listFiles();
		File[] negatives = new File(path + "negatives/").listFiles();
		
		for (int i = 1; i<positives.length; i++) {
			if(!positives[i].isHidden()){
		        Mat img = Highgui.imread(positives[i].getAbsolutePath(), 0);
		        img = img.reshape(1, 1);	//convert to 1 row of m features
		        img.convertTo(img, CvType.CV_32FC1);
		        svm_trainingimages.push_back(img);
		        svm_classes.push_back(Mat.ones(new Size(1, 1), CvType.CV_32FC1));
			}
	    }
	
	    for (int i = 1; i<negatives.length; i++){
	    	if(!negatives[i].isHidden()){
		        Mat img = Highgui.imread(negatives[i].getAbsolutePath(), 0);
		        img = img.reshape(1, 1);	//convert to 1 row of m features
		        img.convertTo(img, CvType.CV_32FC1);
		        svm_trainingimages.push_back(img);
		        svm_classes.push_back(Mat.zeros(new Size(1, 1), CvType.CV_32FC1));
		    }
	    }
	
	    svm_trainingimages.copyTo(svm_trainingdata);
	    svm_trainingdata.convertTo(svm_trainingdata, CvType.CV_32FC1);
	    svm_classes.copyTo(svm_traininglabels);
	    System.out.println(svm_trainingimages.toString());
	    System.out.println(svm_trainingdata.toString());
	    System.out.println(svm_classes.toString());
	    classifier = new CvSVM(svm_trainingdata, svm_traininglabels, new Mat(), new Mat(), svm_params);
	}
	

	
	
}
