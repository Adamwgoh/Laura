package videoCapture;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;



class ColorPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage img;
	
	public ColorPanel(BufferedImage image){
		img = image;
	}
	
	public void setImage(BufferedImage image){
		img = image;
	}
 
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(img, null, 50,50);
	}
}