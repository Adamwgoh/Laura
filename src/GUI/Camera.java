package GUI;

import javax.swing.JFrame;

import javax.swing.JButton;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Timer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.media.*;
 
/**
 * The Camera screen. There was a huge challenge in implementing the opticalflow function onto the main system.
 * As a result, the video can be ran on the background while the wrong direction screenshots will be saved at the specified folder
 *
 * @author Adam Goh
 *
 */
public class Camera extends JFrame{
	private JButton	User;
	private JLabel  Camera1;
	private JButton BrowseVid;
	private JButton Home;
	private JButton Camera;
	private JButton LogOut;
	private JButton About;
	private JLabel lblNewLabel;
	JTextField video;
	
	public Camera() {
		setTitle("ANPR");
		setResizable(false);
		getContentPane().setLayout(null);
		
		Home = new JButton("Home");
		Home.setBounds(7, 7, 73, 26);
		Home.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(Home);
		
		Camera = new JButton("Camera");
		Camera.setBounds(90, 7, 73, 26);
		Camera.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(Camera);
		
		About = new JButton("About");
		About.setBounds(179, 7, 73, 28);
		About.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(About);
		
		User = new JButton("User");
		User.setBounds(748, 7, 76, 28);
		User.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(User);
		
		Camera1 = new JLabel("Camera1");
		Camera1.setBounds(49, 55, 60, 30);
		Camera1.setFont(new Font("Gabriola", Font.PLAIN, 17));
		getContentPane().add(Camera1);
        
        BrowseVid = new JButton("BrowseVid");
		BrowseVid.setBounds(10, 557, 86, 28);
		BrowseVid.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(BrowseVid);
		
		
		LogOut = new JButton("Log out");
		LogOut.setBounds(748, 45, 76, 28);
		LogOut.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(LogOut);
		
		lblNewLabel = new JLabel("Signed in as");
		lblNewLabel.setBounds(683, 11, 61, 28);
		lblNewLabel.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(lblNewLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(419, 89, 1, 499);
		separator.setOrientation(SwingConstants.VERTICAL);
		getContentPane().add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(7, 338, 801, 2);
		getContentPane().add(separator_1);
		
		
		
		video = new JTextField();
		video.setBounds(10, 110, 360, 220);
		//video.setBackground(new Color(150, 0, 0));
		getContentPane().add(video);
		
		
	}
	
	void addHomeListener(ActionListener listenForHomeButton){
		Home.addActionListener(listenForHomeButton); 
			}
	
//
//	void addDatabaseListener(ActionListener listenForDatabaseButton){
//		Database.addActionListener(listenForDatabaseButton); 
//			}
	

	void addCameraListener(ActionListener listenForCameraButton){
		Camera.addActionListener(listenForCameraButton); 
			}

	void addLogOutListener(ActionListener listenForLogOutButton){
		LogOut.addActionListener(listenForLogOutButton); 
			}
	
	void addAboutListener(ActionListener listenForAboutButton){
		About.addActionListener(listenForAboutButton); 
			}
    
	void addBrowseVidListener(ActionListener listenForBrowseVidButton){
		BrowseVid.addActionListener(listenForBrowseVidButton); 
			}
	
	void addUserListener(ActionListener listenForUserButton){
		User.addActionListener(listenForUserButton); 
			}
}
