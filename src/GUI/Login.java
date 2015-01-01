package GUI;


/*
 * This class represents the first page to be viewed by the user once the software is run. 
 */
import javax.swing.*;

import com.mysql.jdbc.Connection;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {
	
	// The components contained in the login page
	private static JTextField username;
	private static JPasswordField password;
	private JButton LogIn;
	private JButton Request;
	
	static Connection con;
	
	private static boolean loggedIn = false;
	
	public Login(Connection con) {
		System.out.println("initializing..");
		initialize();
		Login.con = con;
		System.out.println("connected");
	}
	
	public static boolean isLoggedIn() {
		return Login.loggedIn;
	}
	
	public void initialize()
	{
		
		//specific information for each component
		setFont(new Font("Gabriola", Font.PLAIN, 19));
		setTitle("ANPR");
		getContentPane().setLayout(null);
		
		//the label "system login"
		JLabel lblSystemLogin = new JLabel("System Login");
		lblSystemLogin.setFont(new Font("Gabriola", Font.PLAIN, 19));
		lblSystemLogin.setBounds(160, 38, 119, 30);
		getContentPane().add(lblSystemLogin);
		
		//the label username
		JLabel lblNewLabel = new JLabel("Username:");
		lblNewLabel.setFont(new Font("Gabriola", Font.PLAIN, 17));
		lblNewLabel.setBounds(60, 82, 70, 25);
		getContentPane().add(lblNewLabel);
		
		//the label password
		JLabel lblNewLabel_1 = new JLabel("Password:");
		lblNewLabel_1.setFont(new Font("Gabriola", Font.PLAIN, 17));
		lblNewLabel_1.setBounds(60, 120, 70, 25);
		getContentPane().add(lblNewLabel_1);
		
		//username textfield where the username is to be entered.
		username = new JTextField();
		//username.setFont(new Font("Gabriola", Font.PLAIN, 17));
		username.setBounds(160, 77, 86, 20);
		//username.setAlignmentY(-10);
		//username.setMargin(new Insets(0,,0, 0, 0);
		getContentPane().add(username);
		//username.setColumns(40);
		
		//password field where password is to be entered
		password = new JPasswordField();
		password.setBounds(160, 117, 86, 24);
		
		getContentPane().add(password);
		
		
		//Login button to move to the next view of the system if user is authenticated
		LogIn = new JButton("Log in");
		LogIn.setFont(new Font("Gabriola", Font.PLAIN, 19));
		LogIn.setBounds(236, 183, 89, 23);
		LogIn.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(LogIn);
		
		Request = new JButton("Request Account?");
		Request.setFont(new Font("Gabriola", Font.PLAIN, 19));
		Request.setBounds(27, 183, 167, 23);

		Request.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(Request);
	
		
		
}

	public void connect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			//con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/login","root","");
			con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/login","ronaldo","ronaldo");
			
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public int getStatusID(){
		int status_id = 0;
		try
		{
			String Username = username.getText();
			PreparedStatement loginStatement2 = con.prepareStatement("SELECT Status_Id FROM login WHERE Login_id='"+Username+"'");
			ResultSet result2 = loginStatement2.executeQuery();	
			while (result2.next()){
				status_id = result2.getInt("Status_Id");
			}
		}
		
		
		catch (Exception e1)
		{
		e1.printStackTrace();
		}
		return status_id;
	}


		public String getloginStatus(){
			String getloginStatus = null;
			try
			{
				String Username = username.getText();
				PreparedStatement loginStatement2 = con.prepareStatement("SELECT Status FROM login WHERE Login_id='"+Username+"'");
				ResultSet result2 = loginStatement2.executeQuery();	
				while (result2.next()){
					getloginStatus = result2.getString("Status");
				}
			}
			
			
			catch (Exception e1)
			{
			e1.printStackTrace();
			}
			return getloginStatus;
		}

	
	
	
		/**
		 * WHATT
		 * @return
		 * @throws ClassNotFoundException
		 * @throws SQLException
		 */
		public boolean loginAuthentication() throws ClassNotFoundException, SQLException {
			try
			{
				 
			String Username = username.getText();
			String Password = new String(password.getPassword());
			System.out.println(getloginStatus());
			
		
			PreparedStatement loginStatement = con.prepareStatement("SELECT Login_id AND Password FROM name WHERE Login_id='"+Username+"'AND Password='"+Password+"'");
			ResultSet result = loginStatement.executeQuery();
		
			
			int user = 0;
			while(result.next()){
			user = user + 1;	
			}
			
			if(user==1 && getloginStatus().equals("active")){
				JOptionPane.showMessageDialog(null,"Login Successful");	
				Login.loggedIn = true;
				return Login.isLoggedIn();
			}
			
			else {
				
				JOptionPane.showMessageDialog(null,"Login Failed");
			}
			
			
			if(user==1 && !getloginStatus().equals("active")){
				JOptionPane.showMessageDialog(null,"Account inactive - pending for approval from Admin");	
			}
			
			} //end try
	
			catch (Exception e1)
			{
			e1.printStackTrace();
			}
			return Login.isLoggedIn();	
		}	
	
	public static String getusername() { return username.getText();	}
	
	public static char[] getpassword() { return password.getPassword();	 }
		
	//Listener for Log in button
	void addLoginListener(ActionListener listenForloginButton){
		LogIn.addActionListener(listenForloginButton); 
			}
	
	void addRequestListener(ActionListener listenForRequestButton){
		Request.addActionListener(listenForRequestButton); 
			}
}

