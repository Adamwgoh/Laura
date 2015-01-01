package GUI;

import java.sql.DriverManager;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import javax.swing.JTable;
import javax.swing.JScrollPane;



import javax.swing.JTextPane;
import java.awt.Font;

public class ManualAccount extends JFrame{

	//private JFrame frame;
	private JTextField getloginid;
	private JTextField getname;
	private JTextField getemail;
	private JTextField getpassword;
	private JTextField getpassword2;
	private JButton Cancel;
	private JLabel label_4;
	private static Connection con;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane1;
	private JTable table_2;
	private JTextField getloginidactivation;
	private JTable table_3;


	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ManualAccount window = new ManualAccount();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 * @throws SQLException 
	 */
	public ManualAccount(Connection con) throws SQLException {
		//connect();
		ManualAccount.con = con;
		initialize();
		initializenaTable();
		initializeaTable();
		
		
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
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	private void initialize() throws SQLException {
		setResizable(false);
		setTitle("Manage User Accounts");
		setBounds(100, 100, 561, 667);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("Login_id");
		label.setBounds(23, 480, 59, 14);
		getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Name");
		label_1.setBounds(23, 505, 59, 14);
		getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("Email");
		label_2.setBounds(23, 528, 59, 14);
		getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("Password");
		label_3.setBounds(23, 553, 59, 14);
		getContentPane().add(label_3);
		
		getloginid = new JTextField();
		getloginid.setColumns(10);
		getloginid.setBounds(138, 477, 386, 20);
		getContentPane().add(getloginid);
		
		getname = new JTextField();
		getname.setColumns(10);
		getname.setBounds(138, 502, 386, 20);
		getContentPane().add(getname);
		
		getemail = new JTextField();
		getemail.setColumns(10);
		getemail.setBounds(138, 525, 386, 20);
		getContentPane().add(getemail);
		
		getpassword = new JTextField();
		getpassword.setColumns(10);
		getpassword.setBounds(138, 550, 386, 20);
		getContentPane().add(getpassword);
		
		JButton btnCheck = new JButton("Create account");
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
		btnCheck.setBounds(305, 606, 137, 23);
		getContentPane().add(btnCheck);
		
		getpassword2 = new JTextField();
		getpassword2.setColumns(10);
		getpassword2.setBounds(138, 575, 386, 20);
		getContentPane().add(getpassword2);
		
		Cancel = new JButton("Cancel");
		Cancel.setBounds(450, 606, 74, 23);
		getContentPane().add(Cancel);
		
		label_4 = new JLabel("Retype Password");
		label_4.setBounds(23, 578, 105, 14);
		getContentPane().add(label_4);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 209, 501, 137);
		getContentPane().add(scrollPane);
		
		scrollPane1 = new JScrollPane();
		scrollPane1.setBounds(23, 36, 500, 137);
		getContentPane().add(scrollPane1);
		
		
		getloginidactivation = new JTextField();
		getloginidactivation.setBounds(138, 390, 386, 20);
		getContentPane().add(getloginidactivation);
		getloginidactivation.setColumns(10);
		
		JLabel lblLoginid = new JLabel("Login_id");
		lblLoginid.setBounds(23, 393, 84, 14);
		getContentPane().add(lblLoginid);
		
		JButton btnActivateAccount = new JButton("Activate");
		btnActivateAccount.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ActivateAccount();
			}
		});
		
		JButton btnDeactivateAccount = new JButton("Deactivate");
		btnDeactivateAccount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DeactivateAccount();
			}
		});
		
		btnDeactivateAccount.setBounds(304, 418, 105, 23);
		getContentPane().add(btnDeactivateAccount);
		btnActivateAccount.setBounds(189, 418, 105, 23);
		getContentPane().add(btnActivateAccount);
		
		JButton btnDeleteAccount = new JButton("Delete");
		btnDeleteAccount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DeleteAccount();
			}
		});
		btnDeleteAccount.setBounds(419, 418, 105, 23);
		getContentPane().add(btnDeleteAccount);
		
		JTextPane txtpnUponActivationdeletionThe = new JTextPane();
		txtpnUponActivationdeletionThe.setEditable(false);
		txtpnUponActivationdeletionThe.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnUponActivationdeletionThe.setText("Upon activation/deactivation/deletion user will be automatically notified via email");
		txtpnUponActivationdeletionThe.setBounds(23, 367, 501, 20);
		getContentPane().add(txtpnUponActivationdeletionThe);
	
		
		JTextPane txtpnManualSignUp = new JTextPane();
		txtpnManualSignUp.setEditable(false);
		txtpnManualSignUp.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnManualSignUp.setText("Manual sign up; account will be available immediately");
		txtpnManualSignUp.setBounds(23, 452, 316, 20);
		getContentPane().add(txtpnManualSignUp);
		
		JTextPane txtpnActiveTable = new JTextPane();
		txtpnActiveTable.setEditable(false);
		txtpnActiveTable.setText("active table\r\n");
		txtpnActiveTable.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnActiveTable.setBounds(23, 11, 105, 20);
		getContentPane().add(txtpnActiveTable);
		
		JTextPane txtpnNotactiveTable = new JTextPane();
		txtpnNotactiveTable.setEditable(false);
		txtpnNotactiveTable.setText("not_active table");
		txtpnNotactiveTable.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtpnNotactiveTable.setBounds(23, 184, 105, 20);
		getContentPane().add(txtpnNotactiveTable);
	
	}
	
	//nonactive table
	private void initializenaTable() throws SQLException {
		
		   Object data[][] = {
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""}
		   };
			     
			     String[] column = {"Login_id",
			    		 "Name",
			    		 "Email",
			    		 "User_type",
			    		 "Status",
			     };
			     
 
		PreparedStatement sqltable = con.prepareStatement("SELECT Login_id FROM login WHERE Status='not_active'");
		ResultSet rs = sqltable.executeQuery();
		int i = 0;
		while(rs.next()){
			String getId = rs.getString("Login_id");		
			data[i++][0] = getId;
		}
		
		
		PreparedStatement sqltable2 = con.prepareStatement("SELECT name.Name FROM login, name WHERE name.Login_id=login.Login_id AND Status='not_active';");
		ResultSet rs2 = sqltable2.executeQuery();
		int ii = 0;
		while(rs2.next()){
			String getName = rs2.getString("Name");
			data[ii++][1] = getName;
		}
		
		PreparedStatement sqltable3 = con.prepareStatement("SELECT name.Email FROM login, name WHERE login.Status='not_active' AND name.Login_id=login.Login_id;");
		ResultSet rs3 = sqltable3.executeQuery();
		int iii = 0;
		while(rs3.next()){
			String getEmail = rs3.getString("Email");		
			data[iii++][2] = getEmail;
		}
		
		PreparedStatement sqltable4 = con.prepareStatement("SELECT login.User_type FROM login, name WHERE login.Status='not_active' AND name.Login_id=login.Login_id;");
		ResultSet rs4 = sqltable4.executeQuery();
		int iv = 0;
		while(rs4.next()){
			String getUsertype = rs4.getString("User_type");		
			data[iv++][3] = getUsertype;
		}
		
		PreparedStatement sqltable5 = con.prepareStatement("SELECT login.Status FROM login WHERE login.Status='not_active';");
		ResultSet rs5 = sqltable5.executeQuery();
		int v = 0;
		while(rs5.next()){
			String getStatus = rs5.getString("Status");		
			data[v++][4] = getStatus;
		}
		
		
		table_2 = new JTable(data, column);
		scrollPane.setViewportView(table_2);
	}
	
	
		
	private void initializeaTable() throws SQLException {
		
		   Object data[][] = {
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""},
			    	{"","","","",""}
		   };
			     
			     String[] column = {"Login_id",
			    		 "Name",
			    		 "Email",
			    		 "User_type",
			    		 "Status",
			     };
			     

		PreparedStatement sqltable = con.prepareStatement("SELECT Login_id FROM login WHERE Status='active'");
		ResultSet rs = sqltable.executeQuery();
		int i = 0;
		while(rs.next()){
			String getId = rs.getString("Login_id");		
			data[i++][0] = getId;
		}
		
		
		PreparedStatement sqltable2 = con.prepareStatement("SELECT name.Name FROM login, name WHERE name.Login_id=login.Login_id AND Status='active';");
		ResultSet rs2 = sqltable2.executeQuery();
		int ii = 0;
		while(rs2.next()){
			String getName = rs2.getString("Name");
			data[ii++][1] = getName;
		}
		
		PreparedStatement sqltable3 = con.prepareStatement("SELECT name.Email FROM login, name WHERE login.Status='active' AND name.Login_id=login.Login_id;");
		ResultSet rs3 = sqltable3.executeQuery();
		int iii = 0;
		while(rs3.next()){
			String getEmail = rs3.getString("Email");		
			data[iii++][2] = getEmail;
		}
		
		PreparedStatement sqltable4 = con.prepareStatement("SELECT login.User_type FROM login, name WHERE login.Status='active' AND name.Login_id=login.Login_id;");
		ResultSet rs4 = sqltable4.executeQuery();
		int iv = 0;
		while(rs4.next()){
			String getUsertype = rs4.getString("User_type");		
			data[iv++][3] = getUsertype;
		}
		
		PreparedStatement sqltable5 = con.prepareStatement("SELECT login.Status FROM login WHERE login.Status='active';");
		ResultSet rs5 = sqltable5.executeQuery();
		int v = 0;
		while(rs5.next()){
			String getStatus = rs5.getString("Status");		
			data[v++][4] = getStatus;
		}
		
		table_3 = new JTable(data, column);
		scrollPane1.setViewportView(table_3);
		
	}
	
	//Activate account method
	public void ActivateAccount(){
		
		String checkfornull = getloginidactivation.getText(); 
		
		try{
		if(checkfornull.equals("")){
			JOptionPane.showMessageDialog(null,"Field cannot be empty");
		}
		
		
		
		else{
		updateStatusActivate();
		updateStatusidActivate();
		
	
		initializeEmailapprove();
		JOptionPane.showMessageDialog(null,"Update Success, User has been notified via email");
		initializenaTable();
		initializeaTable();
		System.out.println(getEmaildetails());
		
		}	
		
		}
		catch (Exception e1)
		{
		e1.printStackTrace();
		}
	}
	
	
	//Deactivate account method
	public void DeactivateAccount(){
		String checkfornull = getloginidactivation.getText(); 
		try{
		if(checkfornull.equals("")){
			JOptionPane.showMessageDialog(null,"Field cannot be empty");
		}
		
		else{
		updateStatusDeactivate();
		updateStatusidDeactivate();
		
		initializeEmaildeactivate();
		JOptionPane.showMessageDialog(null,"Deactivate Success, User has been notified via email");
		initializenaTable();
		initializeaTable();
		}
		
		}
		catch (Exception e1)
		{
		e1.printStackTrace();
		}
	}
	
	
	public void DeleteAccount(){
		String checkfornull = getloginidactivation.getText(); 
		try{
		if(checkfornull.equals("")){
			JOptionPane.showMessageDialog(null,"Field cannot be empty");
		}
		
		else{
		updateStatusDelete();
		updateStatusidDelete();
		initializenaTable();
		initializeaTable();
		JOptionPane.showMessageDialog(null,"Delete Success");
		}
		
		}
		catch (Exception e1)
		{
		e1.printStackTrace();
		}
	}
	
	public void updateStatusDelete(){
		try
		{
			PreparedStatement checkStatement = con.prepareStatement("DELETE FROM login WHERE Login_id ='"+getLoginidinputdetails()+"'");
			int getdetails = checkStatement.executeUpdate();
			}		
				catch (Exception e)
				{
					e.printStackTrace();
				}
	}
	
	public void updateStatusidDelete(){
		try
		{
			PreparedStatement checkStatement2 = con.prepareStatement("DELETE FROM name WHERE Login_id ='"+getLoginidinputdetails()+"'");
			int getdetails2 = checkStatement2.executeUpdate();
			}
				catch (Exception e)
				{
					e.printStackTrace();
				}
	}
	
	
	//method to get user input
	public String getLoginidinputdetails(){
		String Login_id = getloginidactivation.getText();
		return Login_id;	
	}
	
	
	//activate account method
	public void updateStatusActivate(){
		try
		{
			PreparedStatement checkStatement = con.prepareStatement("UPDATE login SET Status='active' WHERE Login_id ='"+getLoginidinputdetails()+"'");
			int getdetails = checkStatement.executeUpdate();
			}		
				catch (Exception e)
				{
					e.printStackTrace();
				}
	}
	
	public void updateStatusidActivate(){
		try
		{
			PreparedStatement checkStatement2 = con.prepareStatement("UPDATE login SET Status_Id='1' WHERE Login_id ='"+getLoginidinputdetails()+"'");
			int getdetails2 = checkStatement2.executeUpdate();
			}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
	}
	
	

	public void updateStatusDeactivate(){
		try
		{
			PreparedStatement checkStatement = con.prepareStatement("UPDATE login SET Status='not_active' WHERE Login_id ='"+getLoginidinputdetails()+"'");
			int getdetails = checkStatement.executeUpdate();
			}		
				catch (Exception e)
				{
					e.printStackTrace();
				}
	}
	
	public void updateStatusidDeactivate(){
		try
		{
			PreparedStatement checkStatement2 = con.prepareStatement("UPDATE login SET Status_Id='0' WHERE Login_id ='"+getLoginidinputdetails()+"'");
			int getdetails2 = checkStatement2.executeUpdate();
			}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		
	}
	
	
	//String to hold email 
	public String getEmaildetails(){
			String getEmail = null;
			try
			{

				PreparedStatement checkStatement3 = con.prepareStatement("Select email FROM name WHERE Login_id ='"+getLoginidinputdetails()+"'");
				ResultSet getEmail2 = checkStatement3.executeQuery();
				while (getEmail2.next()){
					getEmail = getEmail2.getString("Email");
				}		
			}
					catch (Exception e)
					{
						e.printStackTrace();
					}
			return getEmail;
		}
	
	//String to hold password
	public String getPassworddetails(){
		String getPassworddetails = null;
		try
		{

			PreparedStatement checkStatement3 = con.prepareStatement("Select email FROM name WHERE Login_id ='"+getLoginidinputdetails()+"'");
			ResultSet getPassworddetails2 = checkStatement3.executeQuery();
			while (getPassworddetails2.next()){
				getPassworddetails = getPassworddetails2.getString("Email");
			}		
		}
				catch (Exception e)
				{
				}
		return getPassworddetails;
	}
	

	

	//Email approve
	public void initializeEmailapprove(){
	    Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        //Login authentication
        Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
        @Override
		protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("g52grpgroup4@gmail.com", "groupfour"); 
        	}
        		
        }
        );
    
        try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("g52grpgroup4@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(getEmaildetails()));
			
			message.setSubject("Account status to active");
			message.setText("The account '" + getLoginidinputdetails() + "' has been set to activte. The account is as follows  \n Login_id: \t" + getLoginidinputdetails() + "\n Password: \t" + getPassworddetails());
			Transport.send(message);
			System.out.println("Done");
			
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
        
        
	//Email delete 
	public void initializeEmaildeactivate(){
	    Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        //Login authentication
        Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
        @Override
		protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("g52grpgroup4@gmail.com", "groupfour"); 
        	}
        		
        }
        );
    
        try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("g52grpgroup4@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(getEmaildetails()));
			message.setSubject("Account status to not_active");
			message.setText("The account '" + getLoginidinputdetails() + "' has been set to not_active. The account is as follows  \n Login_id: \t" + getLoginidinputdetails() + "\n Password: \t" + getPassworddetails());
			Transport.send(message);
			System.out.println("Done");
			Transport.send(message);
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	//Check if loginid exists
	public boolean CheckLoginid() throws SQLException{
		String Login_id = getloginid.getText();

		PreparedStatement checkStatement = con.prepareStatement("Select Login_id FROM name WHERE Login_id='"+Login_id+"'");
		ResultSet result = checkStatement.executeQuery();	
		if(result.next()){
			return false;
		}
		
		return true;
	}
	
	//Check if email exists
	public boolean CheckEmail() throws SQLException{
		String Email = getemail.getText();

		PreparedStatement checkStatement = con.prepareStatement("Select email FROM name WHERE email ='"+Email+"'");
		ResultSet result = checkStatement.executeQuery();	
		if(result.next()){
			return false;
		}
		
		return true;
	}
	
	
	public void CreateAccount() throws ClassNotFoundException, SQLException {
		try
		{
	
		
	if (CheckLoginid() == false){
		 
			JOptionPane.showMessageDialog(null,"Username already exists");
		}
		
		if (CheckEmail() == false){
			JOptionPane.showMessageDialog(null,"Email already exists");
		}
		
		
		
		String Login_id = getloginid.getText();
		String Name = getname.getText();
		String Email = getemail.getText();
		String Password = getpassword.getText();
		String Password2 = getpassword2.getText();
		
		if(!Password.equals(Password2)){
			JOptionPane.showMessageDialog(null,"Passwords do not match");
		}
		
		if(Login_id.equals("") || Name.equals("") || Email.equals("") || Password.equals("")){
			JOptionPane.showMessageDialog(null,"Fields cannot be empty");
		}
			
		
		//Success Scenerio 	
		if ((CheckEmail() && Password.equals(Password2) && CheckLoginid()) && (!Login_id.equals("") && !Name.equals("") && !Email.equals("") || !Password.equals(""))){
			PreparedStatement updateStatement2 = con.prepareStatement("INSERT INTO name (Login_id,Name,Email,Password) VALUES ('"+Login_id+"','"+Name+"','"+Email+"','"+Password+"')");
			int updatename = updateStatement2.executeUpdate();	
			PreparedStatement updateStatement1 = con.prepareStatement("INSERT INTO login (Login_id,User_type,Status,Status_Id) VALUES ('"+Login_id+"','normal_user','active',1)");
			int updatelogin = updateStatement1.executeUpdate();
			initializenaTable();
			initializeaTable();
			JOptionPane.showMessageDialog(null,"Account successfully created");
		}
		
		} // end try
		
		
		catch (Exception e1)
		{
		e1.printStackTrace();
		}
		
	} //end create account
	
	void addCancelListener(ActionListener listenForCancelButton){
		Cancel.addActionListener(listenForCancelButton); 
			}	

}