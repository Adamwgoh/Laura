package GUI;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.SystemColor;

import javax.swing.JButton;

import java.awt.Font;
import java.awt.event.ActionListener;

public class About extends JFrame{
	 JTextArea about;
	 private JButton Home;
	
	
	
	public About() {
		setResizable(false);
		getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 41, 795, 556);
		getContentPane().add(scrollPane);
		
		about = new JTextArea();
		about.setFont(new Font("Monospaced", Font.PLAIN, 12));
		about.setBackground(SystemColor.control);
		about.setEditable(false);
		scrollPane.setViewportView(about);
		
		Home = new JButton("Exit About");
		//Home.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Home.setBounds(728, 7, 89, 23);
		getContentPane().add(Home);
	}
	
	
	public String getabouttextarea(){
		
		return about.getText();
	}
	
	public void setabouttextarea(String s){
		
	s=null;
		
		return;
	}
	
	void addHomeListener(ActionListener listenForHomeButton){
		Home.addActionListener(listenForHomeButton); 
			}
	
}
