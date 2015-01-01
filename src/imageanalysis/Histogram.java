package imageanalysis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
/**
 * This class is essential in displaying the horizontal and vertical projection of an image, as well as displaying the histogram visually
 * using jfreechart.
 * 
 * The plate clipping techniques are implemented here as well.
 * 
 * Uses Open Source Library OpenCV 2.4.7
 * Uses Open Source library jfreeChart
 * @author Adam Goh
 * @see imageanalysis.Histogram#getBandCoords(int[], double, String)
 */
public class Histogram extends ApplicationFrame {
	//TODO: A properties class that takes in whether to initiate vertical or horizontal dataset
	
	private int[] img_arr;
	
	   public Histogram(final String title, String imgname) throws IOException {
		    super(title);
		    BufferedImage image = ImageIO.read(new File(imgname));
		    IntervalXYDataset dataset = createVertDataset(image);
		    //InteralXYDataset dataset = createRedDataset();
		    JFreeChart chart = createChart(dataset);
		    final ChartPanel chartPanel = new ChartPanel(chart);
		    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		    setContentPane(chartPanel);
	   }
	   
	   public Histogram(final String title, BufferedImage img){
		    super(title);
		    BufferedImage image = img;
		    IntervalXYDataset dataset = createVertDataset(image);
		    //InteralXYDataset dataset = createRedDataset();
		    JFreeChart chart = createChart(dataset);
		    final ChartPanel chartPanel = new ChartPanel(chart);
		    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		    setContentPane(chartPanel);
	   }
	   //TODO: need proper javadoc for all these constructors
	   public Histogram(BufferedImage img){
		    super("histogram");
		    BufferedImage image = img;
		    IntervalXYDataset dataset = createVertDataset(image);
		    //InteralXYDataset dataset = createRedDataset();
		    JFreeChart chart = createChart(dataset);
		    final ChartPanel chartPanel = new ChartPanel(chart);
		    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		    setContentPane(chartPanel);
		   	   
	   }
	   
	   public Histogram(final String title, BufferedImage img, String dset){
		    super(title);
		    BufferedImage image = img;
		    IntervalXYDataset dataset = createVertDataset(image);
		    if(dset == "horizontal"){
		    	dataset = createHoriDataset(image);
		    }else if(dset == "vertical"){
		    	dataset = createVertDataset(image);
		    }

		    JFreeChart chart = createChart(dataset);
		    final ChartPanel chartPanel = new ChartPanel(chart);
		    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		    setContentPane(chartPanel);
	   }
	   
	   //TODO: proper catch for null variable
	   public int[] getImgArr(){

		   return img_arr;
	   }
	 
	  /**
	   * <p>Creates a dataset of vertical projection of the given input image</p>
	   * @param image			an input image to generate a vertical projection of the image
	   * @return dataset		a jfree library's dataset to be used for plottings
	   */
	 private IntervalXYDataset createVertDataset(BufferedImage image){
		 BufferedImage img = image;
		 
		 int[] height = new int[img.getHeight()];
		 int total = img.getWidth()*255;
		 int sum = 0;

		 //from top row to bottom row
		 for(int y = img.getHeight()-1; y>0; y--){
			 for(int x=0; x<img.getWidth()-1; x++){
				 //get pixel value and add into sum
				 sum += (img.getRGB(x, y)  & 0x00ff0000 )>> 16;
			 }
			 double avg = ((double) sum/ (double)total)*100;
			 height[y] = (int) avg;	//assign summation value in percentage to that height's row
			 sum = 0;			//restart value
		 }
		
		img_arr = height; 
		 
		return createDataset(height);
	 }

	 
	  /**
	   * <p>Creates a dataset of horizontal projection of the given input image</p>
	   * @param image			an input image to generate a horizontal projection of the image
	   * @return dataset		a jfree library's dataset to be used for plottings
	   **/
	 private IntervalXYDataset createHoriDataset(BufferedImage image){
		 BufferedImage img = image;

		 int[] width = new int[img.getWidth()];
		 int total = img.getHeight()*255;
		 int sum = 0;

		 //from first row to bottom row
		 for(int x = 0; x < img.getWidth()-1; x++){
			 for(int y=0; y<img.getHeight()-1; y++){
				 //get pixel value and add into sum
				 sum += (img.getRGB(x, y)  & 0x00ff0000 )>> 16;
			 }
			 double avg = ((double) sum/ (double)total)*100;
			 width[x] = (int) avg;	
			 //assign summation value in percentage to that height's row
			 sum = 0;			//restart value
		 }
		 
		 img_arr = width;
		 
		 return createDataset(width);
	 }
	 
	 /**
	  * Creates a XYSeriesCollection dataset from image's value array from given input
	  * @param img	an array input of values for an image
	  * @param legend(optional) legend of the value for the input image array
	  * @return a dataset to be used for plottings
	  */
	 private XYSeriesCollection createDataset(int[] img){
		 XYSeries series = new XYSeries("legend here");
		 for(int y=0; y<img.length; y++)
			 series.add(y, img[y] );
		 
		 XYSeriesCollection dataset = new XYSeriesCollection(series);
		 return dataset;
	 }
	 
	 private XYSeriesCollection createDataset(int[] img, String legend){
		 XYSeries series = new XYSeries("legend here");
		 for(int y=0; y<img.length; y++)
			 series.add(y, img[y] );
		 
		 XYSeriesCollection dataset = new XYSeriesCollection(series);
		 return dataset;
	 }

	 private JFreeChart createChart(IntervalXYDataset dataset) {
	  final JFreeChart chart = ChartFactory.createXYBarChart("histogram"
			  	,"X",false,"Y",dataset,PlotOrientation.VERTICAL,true,true,false);
	  XYPlot plot = (XYPlot) chart.getPlot();
	  return chart;    
	 }
	 /**
	  * converts an image to an array of image value. This one is for horizontal only.
	  * @param image
	  * @return returns the array of value of the input image
	  */
	 public int[] getHoriImgArr(BufferedImage image){
		   BufferedImage img = image;

			 int[] width = new int[img.getWidth()];
			 int total = img.getHeight()*255;
			 int sum = 0;

			 //from first row to bottom row
			 for(int x = 0; x < img.getWidth()-1; x++){
				 for(int y=0; y<img.getHeight()-1; y++){
					 //get pixel value and add into sum
					 sum += (img.getRGB(x, y)  & 0x00ff0000 )>> 16;
				 }
				 double avg = ((double) sum/ (double)total)*100;
				 width[x] = (int) avg;	//assign summation value in percentage to that height's row
				 sum = 0;			//restart value
			 }
			 

		   return width;
	   }
	   
	 /**
	  * converts an image to an array of image value. THis one is for vertical only
	  * @param image
	  * @return returns the array of value of the input image
	  */
	   public int[] getVertImgArr(BufferedImage image){
		   BufferedImage img = image;
		   
			 
			 int[] height = new int[img.getHeight()];
			 int total = img.getWidth()*255;
			 int sum = 0;

			 //from top row to bottom row
			 for(int y = img.getHeight()-1; y>0; y--){
				 for(int x=0; x<img.getWidth()-1; x++){
					 //get pixel value and add into sum
					 sum += (img.getRGB(x, y)  & 0x00ff0000 )>> 16;
				 }
				 double avg = ((double) sum/ (double)total)*100;
				 height[y] = (int) avg;	//assign summation value in percentage to that height's row
				 sum = 0;			//restart value
			 }
			
			
			
			return height;
	   }
	 
	 public int getMaxVal(int[] arr){
		 int max = 0;
		 
		 for(int i=0; i<arr.length; i++){
			 if(arr[i] > max){
				 max = arr[i];
			 }
		 }
		 return max;
	 }
	 
	 public int getMinVal(int[] arr){
		 int min = arr[0];
		 
		 for(int i=1; i<arr.length; i++){
			 if(arr[i] < min){
				 min = arr[i];
			 }
		 }
		 return min;
	 }
	 
	 public Coordinates getPeakCoord(int[] arr){
		 int max = getMaxVal(arr);
		 int YPeak;
		 
		 //loop through array to find for y coordinate
		 for(int y = 0; y<arr.length; y++)
			 if(arr[y] == max){
		//		 System.out.println("max is " + max + ", YPeak is " + y);
				 YPeak = y;	
				 Coordinates peak = new Coordinates(0, YPeak);  
				 return peak;
			 }	
		 Coordinates notfound = new Coordinates(0,0);
		 System.err.println("no coord found");
		 return notfound;
	 }
	 
	 /**
	  * <p> BandCoords is used to calculate the sides of a possible plate location by having a clipping range between a chose peak </p>
	  * @param yValues	The vertical projection accumulate values produced from VertDataset
	  * @param threshold	The threshold value to determine where to find the Band's sides
	  * @return	returns a Coordinate type with its X as Yb0 of a band and its Y as Yb1 of a band
	  */
	 public Coordinates getBandCoords(int[] values, double threshold, String orientation){
		 int[] yValues = values;
		 Coordinates Peak_coord = getPeakCoord(yValues);
		 Coordinates band_coord;
		 int Yb0_value = 0;
		 int Yb1_value = yValues[Peak_coord.getY()];
		 int Yb0 = 0;
		 int Yb1 = 0;
		 
		 if(orientation == "vertical"){
			 //find for Yb0 value, and get its Y coordinate
			 for(int y = Peak_coord.getY(); y>0; y--){
				 if(yValues[y] < threshold*yValues[Peak_coord.getY()]){
					 
						 Yb0_value = yValues[y];
						 Yb0 = Math.max(0, y);
						// System.out.println("Y value is " + Yb0_value + ", Yb0 is " + Yb0);
						 break;
				 }
			 }
			 
			 //find for Yb1 value, and get its Y coordinate
			 for(int y = Peak_coord.getY(); y<yValues.length; y++){
				 if(yValues[y] < threshold*yValues[Peak_coord.getY()] ){	
						 Yb1_value = yValues[y];
						 Yb1 = Math.min(yValues.length, y);
						// System.out.println("Y value is " + Yb1_value + ", Yb1 is " + Yb1);
						 break;
				 }
				 Yb1 = yValues.length-1;
			 }
			 
			 band_coord = new Coordinates(Yb0, Yb1);
		 }else{//horizontal
			 System.out.println("horizontal");
			 //find for Yb0 value, start from middle go towards left
			 for(int y = yValues.length/2; y > 0; y--){
				 if(yValues[y] == 0){
					 System.out.println("y value is " + yValues[y]);
					 Yb0_value = yValues[y];
					 Yb0 = y;
					 break;
				 }
			 }
			 
			 //find for Yb1 value, start from middle go towards right
			 for(int y = yValues.length/2; y < yValues.length; y++){
				 if(yValues[y] == 0){
					 Yb1_value = yValues[y];
					 Yb1 = y;
					 break;
				 }
			 }
			 
			 band_coord = new Coordinates(Yb0, Yb1);
			 return band_coord;
		 }
		 
		 
		// System.out.println(band_coord.toString());	for debugging purposes
		 int diff = band_coord.getY() - band_coord.getX();
		 int NewYb0 = Math.max(0, band_coord.getX() - (int) (0.2*diff));
		 int NewYb1 = Math.min(yValues.length, band_coord.getY() + (int) (0.2*diff));
		 
		// System.out.println("Diff is: " + diff);
		// System.out.println("NewYb0 is " + NewYb0);
		// System.out.println("NewYb1 is " + NewYb1);
		 
		 if(orientation == "vertical"){
			 while(diff > 50){//highly possible not a band
				 if(diff%50 == 0){
					 NewYb1 -= 50;
					 diff   -= 50;
				 }
				 NewYb1 -= (diff%50);
				 diff -= (diff%50);
			 }
		 }
		 
//		 }else if(orientation == "horizontal"){
//			 return gettheCoords(yValues);
//		 }
		 
		 
		 band_coord = new Coordinates(NewYb0, NewYb1);

		 //TODO: Band Coords may be -ve
		 return band_coord;
	 }
	 
	 /**
	  * Returns an img array with the given range of values set to 0
	  * 
	  * @param coord	coordinates of range for image to be set to 0
	  * @param arr		image array
	  * @return			image array with the given range value zeroized.
	  */
	 public int[] zeroizeInterval(Coordinates coord, int[] arr){
		for(int i = coord.getX(); i < coord.getY(); i++){
			arr[i] = 0;
		} 
		 
		return arr;
		 
	 }
	 
	 /**
	  * @deprecated not used because the change of algorithm was ineffective
	  * Takes in array of image value and find its derivation and get the band coords. This is done in motivation that
	  * the side of a plate can be defined as (from left-to-right) white-to-black and (from right-to-left) black-to-white spikes in the derivative histogram
	  * @param yValues image values of an image
	  * @return
	  */
	 @Deprecated
	public Coordinates getDerivativeCoords(int[] yValues){

		//second derivative
		int[] xValues = getsecondDerivative(yValues, 4);
		int maxVal = getMaxVal(xValues);
		int minVal = getMinVal(xValues);
		System.out.println("maxVal is" + maxVal + ", minVal is " + minVal);
		int minYb0 = maxVal;
		int maxYb1 = minVal;
		int Yb1 = 0;
		int Yb0 = 0;

		for(int x = 0; x < (xValues.length/2); x++){
			if(xValues[x] <= minVal*0.2 ){//hard coded 0.2 threshold
				if(xValues[x] > maxYb1){	
					maxYb1 = xValues[x];
					Yb0 = x;
				}
			}
		}
		
		for(int x = (xValues.length/2); x < xValues.length; x++){
			if(xValues[x] >= maxVal*0.2 ){//hard coded 0.2 threshold
				if(xValues[x] < minYb0){
					minYb0 = xValues[x];
					Yb1 = x;
				}
			}
		}
		Coordinates band_coord = new Coordinates(Yb0, Yb1);
		return band_coord;
	}
	 
	 public Coordinates gettheCoords(int[] yValues){
		 int a,b;
	     int maxVal = getMaxVal(yValues);
	     
		 for (a=2; -derivation(yValues,a,a+4) < maxVal*0.2 && a < yValues.length-2-2-4; a++);
	     for (b=yValues.length-1-2; derivation(yValues,b-4,b) < maxVal*0.2 && b>a+2; b--);
	     
	     Coordinates band_coord = new Coordinates(a,b);
	     return band_coord;
	 }

		 
/**
 * Used to get the secondderivative image array
 * @param arr input image array values
 * @param h threshold
 * @return second derivative image array values
 */
	public int[] getsecondDerivative(int[] arr, int h){
		int[] Values = arr;
//		for(int i=h-1; i>0; i--){
//			Values[i] = ((Values[i] - Values[Values.length-h+i])/h);
//		}
//		Values[3] = ((Values[3] - Values[Values.length-2])/h);
//		Values[2] = ((Values[2] - Values[Values.length-3])/h);
//		Values[1] = ((Values[1] - Values[Values.length-4])/h);
//		Values[0] = ((Values[0] - Values[Values.length-5])/h);
		//p2x(x) = (px(x) - px(x-h))/h
		for(int i = 4; i < Values.length; i++){
			Values[i] = ((Values[i] - (Values[i-h]))/h);
		}
		return Values;
	}
	
	/**
	 * counting derivation
	 * @param Values array values
	 * @param index1 first array value
	 * @param index2 second array value
	 * @return the difference of first and second array value is the derivative
	 */
    public int derivation(int[] Values, int index1, int index2) {
        return (Values[index1] - Values[index2]);
    }
	 
//depereciated, for testing purposes only
/*	 public static void main(final String[] args) throws IOException {	
	  final Histogram demo = new Histogram("Image Histogram", "dx.JPG");
	  demo.pack();
	  RefineryUtilities.centerFrameOnScreen(demo);
	  demo.setVisible(true);
	 }*/
	}
