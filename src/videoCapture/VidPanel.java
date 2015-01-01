package videoCapture;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A JPanel subclass that is used to show the video or any image on a new JFrame window
 * @author Adam Goh
 *
 */

public class VidPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage img;
	
	public VidPanel(BufferedImage image){
		img = image;
	}
	
	public void setImage(BufferedImage image){
		img = image;
	}
 
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics g2d = (Graphics) g;
		g2d.drawImage(img, 0, 0,null);
	}
}