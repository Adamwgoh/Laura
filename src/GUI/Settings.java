package GUI;

import javax.swing.JFrame;
import java.awt.EventQueue;


import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.mysql.jdbc.Connection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <h1> Settings </p>
 *  The setting class provides all Graphic elements of the Setting screen. The setting screen provides 
 *  the user with a way to change their passwords
 * @author Group 4 GUI Team: Ho Swee Jim and Jamila Abdulsalam
 * @author Javadoc: Adam Weisen Goh
 */
public class Settings {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					//Settings window = new Settings();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	static JFrame frame;
	private static JTextField currentpasswordinput;
	private static JTextField newpasswordinput;
	private Connection con;
	private JButton savesettingsbutton;
	private JTextField retypenewinput;
	private JLabel Username;
	private JLabel usernamelbl;
	private JLabel Email;
	private JLabel emaillbl;
	String loginId = Login.getusername();
	private JLabel lblUsertype;
	private JLabel lblNewLabel;
	
	
	/**
	 * @wbp.parser.entryPoint
	 * <p>The constructor retrieves all the needed information using function calls before initializing the GUI </p>
	 * 
	 * @see Settings#getEmaildetails()
	 * @see Settings#getNamedetails()
	 * @see Settings#getUsertypedetails()
	 * @see Settings#initialize()
	 */
	public Settings(Connection con) {
		this.con = con;
		getEmaildetails();
		getNamedetails();
		getUsertypedetails();
		initialize();
		
	}

	/**
	 * 
	 * Initialize the contents of the frame. All buttons will be pushed onto the Setting screen
	 */
	public void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 447, 277);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label_2 = new JLabel("Password");
		label_2.setBounds(25, 116, 70, 14);
		frame.getContentPane().add(label_2);
		
		currentpasswordinput = new JTextField();
		currentpasswordinput.setColumns(10);
		currentpasswordinput.setBounds(182, 113, 222, 20);
		frame.getContentPane().add(currentpasswordinput);
		
		newpasswordinput = new JTextField();
		newpasswordinput.setColumns(10);
		newpasswordinput.setBounds(182, 144, 222, 20);
		frame.getContentPane().add(newpasswordinput);
		
		JLabel label_3 = new JLabel("Current");
		label_3.setBounds(105, 116, 59, 14);
		frame.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("New");
		label_4.setBounds(105, 147, 59, 14);
		frame.getContentPane().add(label_4);
		
		savesettingsbutton = new JButton("Save Settings");
		savesettingsbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					ChangePassword2();
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		savesettingsbutton.setBounds(269, 206, 125, 23);
		frame.getContentPane().add(savesettingsbutton);
		
		retypenewinput = new JTextField();
		retypenewinput.setColumns(10);
		retypenewinput.setBounds(182, 175, 222, 20);
		frame.getContentPane().add(retypenewinput);
		
		JLabel label = new JLabel("Retype new");
		label.setBounds(105, 178, 77, 14);
		frame.getContentPane().add(label);
		
		Username = new JLabel("Username");
		Username.setBounds(25, 23, 59, 14);
		frame.getContentPane().add(Username);
		
		Email = new JLabel("Email");
		Email.setBounds(25, 48, 59, 14);
		frame.getContentPane().add(Email);
		
		usernamelbl = new JLabel(loginId);
		usernamelbl.setBounds(105, 23, 239, 14);
		frame.getContentPane().add(usernamelbl);
		
		emaillbl = new JLabel(getEmaildetails());
		emaillbl.setBounds(105, 48, 239, 14);
		frame.getContentPane().add(emaillbl);
		
		lblUsertype = new JLabel("Usertype");
		lblUsertype.setBounds(25, 73, 70, 14);
		frame.getContentPane().add(lblUsertype);
		
		lblNewLabel = new JLabel(getUsertypedetails());
		lblNewLabel.setBounds(105, 73, 200, 14);
		frame.getContentPane().add(lblNewLabel);
	}
	
	/**
	 * @deprecated Non-effective piece of code that will not be used other than the reason for debugging.
	 */
	@Deprecated
	public void connect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/login","ronaldo","ronaldo");
			//con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/login","root","");
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets name of the login username and password and return it
	 * @return returns the name with the matching LoginID
	 */
	public String getNamedetails(){
		String getName = null;
		
		try
		{
			PreparedStatement checkStatement = con.prepareStatement("SELECT name.Name FROM login, name WHERE login.Login_id ='"+loginId+"' AND login.Login_id = name.Login_id;");
			ResultSet rs = checkStatement.executeQuery();		
			
			while (rs.next()){
					getName = rs.getString("Name");
			}		
		}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
		return getName;
	}
	

	public String getUsertypedetails(){
		String Usertype = null;
		
		try
		{

			PreparedStatement checkStatement = con.prepareStatement("SELECT login.User_type FROM login, name WHERE login.Login_id ='"+loginId+"' AND login.Login_id = name.Login_id;");
			ResultSet rs = checkStatement.executeQuery();		
			
			while (rs.next()){
				Usertype = rs.getString("User_type");
			}		
		}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
		return Usertype;
	}
	
	
	public String getEmaildetails(){
		String getEmail = null;
		
		try
		{

			PreparedStatement checkStatement = con.prepareStatement("SELECT name.Email FROM login, name WHERE login.Login_id ='"+loginId+"' AND login.Login_id = name.Login_id;");
			ResultSet rs = checkStatement.executeQuery();		
			
			while (rs.next()){
				getEmail = rs.getString("Email");
			}		
		}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
		return getEmail;
	}
	
	
	

				
	
public void ChangePassword2() throws ClassNotFoundException, SQLException {
	try
	{
	String CurrentPassword = new String(Login.getpassword());
	String CurrentPasswordInput = currentpasswordinput.getText();
	String NewPasswordInput = newpasswordinput.getText();
	String RetypePasswordInput = retypenewinput.getText();
	
	if (CurrentPasswordInput.equals(CurrentPassword) && RetypePasswordInput.equals(NewPasswordInput)){
				PreparedStatement changeStatement = con.prepareStatement("UPDATE name SET Password='"+NewPasswordInput+"' WHERE Login_id='"+loginId+"'");
				changeStatement.executeUpdate();
				JOptionPane.showMessageDialog(null,"Password successfully changed");
			}
			
	else if(!CurrentPasswordInput.equals(CurrentPassword)) {
			System.out.println("currentpasswordinput " + CurrentPasswordInput + ", currentpassword is " + CurrentPassword.toString());
			JOptionPane.showMessageDialog(null,"Incorrect password");
			}
	
	else if(!RetypePasswordInput.equals(NewPasswordInput)){
			JOptionPane.showMessageDialog(null,"Passwords do not match");
			}
	

	}
	catch (Exception e1)
	{
	e1.printStackTrace();
	}
}


	public void addChangePasswordSettingsListener(ActionListener listenForChangePasswordSettingsButton){
		savesettingsbutton.addActionListener(listenForChangePasswordSettingsButton); 
		}

	
	
	
}
