package GUI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;



public class Vid extends JFrame
{
	private JButton Browse;
    public Vid()
    {
        setLayout(new BorderLayout());
        
        Browse = new JButton("Browse");
		Browse.setBounds(10, 557, 86, 28);
		Browse.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(Browse);
		
		
//        //file you want to play
//      URL mediaURL = C:\\Users\\Jay Jay\\Downloads\\2.broke.girls.219.hdtv-lol_wso;
//        //create the media player with the media url
//        Player mediaPlayer = Manager.createRealizedPlayer(mediaURL);
//        //get components for video and playback controls
//        Component video = mediaPlayer.getVisualComponent();
//        Component controls = mediaPlayer.getControlPanelComponent();
//        add(video,BorderLayout.CENTER);
//        add(controls,BorderLayout.SOUTH);
    }
    
	void addBrowseListener(ActionListener listenForBrowseButton){
		Browse.addActionListener(listenForBrowseButton); 
			}
}
