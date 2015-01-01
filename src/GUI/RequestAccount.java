package GUI;


import java.awt.EventQueue;
import java.sql.DriverManager;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.WindowConstants;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RequestAccount {

	JFrame frame;
	private JTextField getloginid;
	private JTextField getname;
	private JTextField getemail;
	private JTextField getpassword;
	private JTextField getpassword2;
	private JButton button;
	private JLabel label_4;
	static Connection con;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					RequestAccount window = new RequestAccount();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RequestAccount() {
		connect();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	

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
	
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 447, 277);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("Login_id");
		label.setBounds(10, 42, 59, 14);
		frame.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Name");
		label_1.setBounds(10, 67, 59, 14);
		frame.getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("Email");
		label_2.setBounds(10, 92, 59, 14);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("Password");
		label_3.setBounds(10, 117, 59, 14);
		frame.getContentPane().add(label_3);
		
		getloginid = new JTextField();
		getloginid.setColumns(10);
		getloginid.setBounds(123, 39, 222, 20);
		frame.getContentPane().add(getloginid);
		
		getname = new JTextField();
		getname.setColumns(10);
		getname.setBounds(123, 64, 222, 20);
		frame.getContentPane().add(getname);
		
		getemail = new JTextField();
		getemail.setColumns(10);
		getemail.setBounds(123, 89, 222, 20);
		frame.getContentPane().add(getemail);
		
		getpassword = new JTextField();
		getpassword.setColumns(10);
		getpassword.setBounds(123, 114, 222, 20);
		frame.getContentPane().add(getpassword);
		
		JButton btnCheck = new JButton("Request account");
		btnCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					CreateAccount();
				} catch (ClassNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnCheck.setBounds(123, 170, 137, 23);
		frame.getContentPane().add(btnCheck);
		
		getpassword2 = new JTextField();
		getpassword2.setColumns(10);
		getpassword2.setBounds(123, 139, 222, 20);
		frame.getContentPane().add(getpassword2);
		
		button = new JButton("Cancel");
		button.setBounds(271, 170, 74, 23);
		frame.getContentPane().add(button);
		
		label_4 = new JLabel("Retype Password");
		label_4.setBounds(10, 142, 105, 14);
		frame.getContentPane().add(label_4);
	}
	
	public void CreateAccount() throws ClassNotFoundException, SQLException {
		try
		{
		String Login_id = getloginid.getText();
		String Name = getname.getText();
		String Email = getemail.getText();
		String Password = getpassword.getText();
		String Password2 = getpassword2.getText();
		
		//Checks Username
		PreparedStatement checkStatement = con.prepareStatement("Select Login_id FROM name WHERE Login_id='"+Login_id+"'");
		ResultSet userexist = checkStatement.executeQuery();	
		
		int loopcheck = 0;
		int usercheck = 0;
		while(userexist.next()){
		loopcheck = loopcheck + 1;	
		}
		
		
		
		if(loopcheck==1){
			JOptionPane.showMessageDialog(null,"Username already exists");
		}
		
		else{
			usercheck = 1;
		}
		
		String stringusercheck = Integer.toString(usercheck);
		
		//Checks Email
		PreparedStatement checkStatement2 = con.prepareStatement("Select email FROM name WHERE email ='"+Email+"'");
		ResultSet emailexist = checkStatement2.executeQuery();
		
		int loopcheck2 = 0;
		int emailcheck = 0;
		while(userexist.next()){
			loopcheck2 = loopcheck2 + 1;	
			}
			
			if(loopcheck2==1){
				JOptionPane.showMessageDialog(null,"Email already exists");
			}
			
			else{
				emailcheck = 1;
			}
			
		String stringemailcheck = Integer.toString(emailcheck);	
		
		if(!Password.equals(Password2)){
			JOptionPane.showMessageDialog(null,"Passwords do not match");
		}
		
		
		if(Login_id.equals("") || Name.equals("") || Email.equals("") || Password.equals("")){
			JOptionPane.showMessageDialog(null,"Fields cannot be empty");
		}
			
		
		//Success Scenerio 	
		if (stringusercheck.equals("1") && Password.equals(Password2) && stringemailcheck.equals("1") && !Login_id.equals("") && !Name.equals("") && !Email.equals("") || !Password.equals("")){
			
			PreparedStatement updateStatement2 = con.prepareStatement("INSERT INTO name (Login_id,Name,Email,Password) VALUES ('"+Login_id+"','"+Name+"','"+Email+"','"+Password+"')");
			int updatename = updateStatement2.executeUpdate();	
			PreparedStatement updateStatement1 = con.prepareStatement("INSERT INTO login (Login_id,User_type,Status,Status_Id) VALUES ('"+Login_id+"','normal_user','not_active',0)");
			int updatelogin = updateStatement1.executeUpdate();
	
			JOptionPane.showMessageDialog(null,"Account successfully requested. You will be emailed upon approval.");
		}
		
		
		
		
		
		
		
		}
		
		
		catch (Exception e1)
		{
		e1.printStackTrace();
		}
		
		
		
}

	void addRequestCancelListener(ActionListener listenForRequestCancelButton){
		button.addActionListener(listenForRequestCancelButton); 
			}
} 
	
