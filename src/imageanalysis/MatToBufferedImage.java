package imageanalysis;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;


/**
 * A MatToBufferedImage is a general image class that convert between the two formats
 * @author Adam Goh
 *
 */
public class MatToBufferedImage {
        
        private Mat mat = new Mat();
        private BufferedImage img = null;
        private MatOfByte bytemat = new MatOfByte();
        private byte[] bytes;
        
        public MatToBufferedImage(){
                
        }

        
        public void SetImage(Mat inMat){
                this.mat = inMat;
        }
        
        public BufferedImage getImage(Mat Inmat) throws IOException{
                Highgui.imencode(".JPG", Inmat, bytemat);
                
                bytes = bytemat.toArray();
                InputStream in = new ByteArrayInputStream(bytes);
                
                BufferedImage image = ImageIO.read(in);
                return image;
                
        }
        
        public BufferedImage getImage() throws IOException{
                try{
                        Highgui.imencode(".JPG", mat, bytemat);
                }catch(Exception nullException){
                        System.out.print("No mat!");
                }
                bytes = bytemat.toArray();
                InputStream in = new ByteArrayInputStream(bytes);
                
                img = ImageIO.read(in);
                return img;
        }
       
        
        public BufferedImage getImage(String filename, Mat mat) throws IOException{
                SetImage(mat);
                Highgui.imencode(filename + ".JPG", mat, bytemat);
                
                bytes = bytemat.toArray();
                InputStream in = new ByteArrayInputStream(bytes);
                
                img = ImageIO.read(in);
                return img;
        }
        
    	public Mat getMatFromImage(BufferedImage image){
    		Mat mat = new Mat();
    		byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
    		mat.put(0, 0, data);
    		//mat.convertTo(mat, mattype);
    		
    		return mat;
    	}
        

}