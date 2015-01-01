package GUI;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;

import com.mysql.jdbc.Connection;

import java.awt.event.ActionEvent;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * <p> The Home class contains all graphic elements visible in the frame with listeners attaches to it </p>
 * <p> For functions of each Button elements, please refer to the Controller class </p>
 * 
 * @author Group four: GUI Team(Ho Swee Jim & Jamila)
 * @author Javadoc: Adam Goh
 */
public class Home extends JFrame{
	JTextField path;
	JButton Admin;
	private JButton Clear;
	private JButton Home;
	private JButton Camera;
	private JButton About;
	private JButton LogOut;
	private JLabel Preview;
	private JLabel RecognitionResult;
	private JButton Browse;
	private JButton Export;
	private JButton Execute;
	private static JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	public static JTable table; //= new JTable();
	private JTable table_1;
	private JTable tblImage;
	private JButton button;
	private JLabel lblNewLabel;
	private JButton User;
	private JButton Search;
	JLabel RecResult;
	JLabel pic;
	private static JTextField ManuallyInputPlate;
	private JButton button_1;
	private JTextPane textPane;
	private JTextPane textPane_1;
	private JButton btnEmail;
	private JButton btnEmailStudents;
	private JTextPane txtpnEmailOffenders;
	private JTextPane txtpnEmailSecurityOffice;
	static Connection con;
	private JTable table_2;
		
	
	//ACTIVE_TABLE
	/**
	 * Active table is a shared static variables where found user entries are saved in here as an array of attributes
	 * The attributes are stored as a string array and will be extracted for table construction when the method see called
	 * 
	 * <p>  This variables is emptied each time an email is sent to the found students </p>
	 * @see  #updateActiveTable()
	 */
	public static ArrayList<String[]> activetable = new ArrayList<String[]>();
	/**
	 * Similar to activetable, non_activetable is also a shared static variables where not found user entries are saved in here as an array of attributes
	 * The attributes are stored as a string array and will be extracted for table construction when the method see called
	 * 
	 * <p>	This variable is emptied each time an email is sent to the head of security
	 * @see  #updateNonActiveTable()
	 */
	public static ArrayList<String[]> non_activetable = new ArrayList<String[]>();
	
	
	/**
	 * activecolumn are attribute names for the non-active table seen on the home screen
	 * @see  #initializeaTable()
	 */
	 public static String[] activecolumn = {"Number_Plate",
							    		 "id",
							    		 "name",
							    		 "email",
							    		 "Image_location"
	 														};
	 
	 //NON-ACTIVE_TABLE
	 	/**
		 * non_activecolumn are attribute names for the non-active table seen on the home screen
		 * 
		 * @see  #updateNonActiveTable()
		 */
	 public static String[] non_activecolumn = {"Number_Plate",
		 							  "Image_location"
	 												  };
	
	public Home(Connection con) throws SQLException {
		this.con = con;
		//connect();
		Initialize();
		initializeaTable();
		initializenaTable();
		
	}
	
	/**
	 * Initializes all graphic elements to their positions on the screen when called.
	 */
	public void Initialize(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("ANPR");
		getContentPane().setLayout(null);
		
		Home = new JButton("Home");
		Home.setBounds(7, 7, 73, 28);
		Home.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Home.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(Home);
		
		Camera = new JButton("Camera");
		Camera.setBounds(90, 7, 79, 28);
		Camera.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Camera.setMargin(new Insets(0,1,-12, -4));
		getContentPane().add(Camera);
		
		About = new JButton("About");
		About.setBounds(179, 7, 73, 28);
		About.setFont(new Font("Gabriola", Font.PLAIN, 16));
		About.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(About);
		
		lblNewLabel = new JLabel("Signed in as");
		lblNewLabel.setBounds(683, 11, 61, 28);
		lblNewLabel.setFont(new Font("Gabriola", Font.PLAIN, 16));
		
		getContentPane().add(lblNewLabel);
		
		User = new JButton("User");
		User.setBounds(748, 7, 76, 28);
		User.setFont(new Font("Gabriola", Font.PLAIN, 16));
		User.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(User);
		
		LogOut = new JButton("Log out");
		LogOut.setBounds(748, 45, 76, 28);
		LogOut.setFont(new Font("Gabriola", Font.PLAIN, 16));
		LogOut.setMargin(new Insets(0,1,-12, -4));
		getContentPane().add(LogOut);
		
		Preview = new JLabel("Preview");
		Preview.setBounds(19, 55, 39, 30);
		Preview.setFont(new Font("Gabriola", Font.PLAIN, 17));
		getContentPane().add(Preview);
		
		RecognitionResult = new JLabel("Recognition Result");
		RecognitionResult.setBounds(450, 56, 95, 28);
		RecognitionResult.setFont(new Font("Gabriola", Font.PLAIN, 16));
		getContentPane().add(RecognitionResult);
		
		RecResult = new JLabel();
		RecResult.setBounds(520, 96, 234, 28);
		
		getContentPane().add(RecResult);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(407, 96, 2, 520);
		separator_1.setOrientation(SwingConstants.VERTICAL);
		getContentPane().add(separator_1);
		
		//scrollpane
		scrollPane = new JScrollPane();
		scrollPane.setBounds(419, 191, 405, 121);
		getContentPane().add(scrollPane);
		scrollPane.setViewportView(table);
		
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(419, 403, 405, 121);
		getContentPane().add(scrollPane_1);

	
		path = new JTextField();
		path.setBounds(7, 526, 369, 30);
		path.setFont(new Font("Gabriola", Font.PLAIN, 16));
		path.setMargin(new Insets(-0,1,-0, 0));
		getContentPane().add(path);
		path.setColumns(10);
		
		Browse = new JButton("Browse");
		Browse.setBounds(10, 557, 86, 20);
		Browse.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Browse.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(Browse);
		
		Execute = new JButton("Execute");
		Execute.setBounds(105, 557, 86, 20);
		Execute.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Execute.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(Execute);
		 
//		Export = new JButton("Export");
//		Export.setBounds(201, 557, 86, 20);
//		Export.setFont(new Font("Gabriola", Font.PLAIN, 16));
//		Export.setMargin(new Insets(1,1,-12, 0));
//		getContentPane().add(Export);
		
		Admin = new JButton("Admin");
		Admin.setBounds(748, 70, 76, 28);
		Admin.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Admin.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(Admin);
		
		pic = new JLabel();
		pic.setBounds(7, 135, 381, 366);
		pic.setForeground(new Color(255, 0, 0));
		getContentPane().add(pic);
		
		ManuallyInputPlate = new JTextField();
		ManuallyInputPlate.setFont(new Font("Gabriola", Font.PLAIN, 16));
		ManuallyInputPlate.setColumns(10);
		ManuallyInputPlate.setBounds(429, 130, 179, 30);
		ManuallyInputPlate.setText("Type plate number here");
		ManuallyInputPlate.setMargin(new Insets(0,1,-1000, -200));
		getContentPane().add(ManuallyInputPlate);
		
		Search = new JButton("Search");
		Search.setFont(new Font("Gabriola", Font.PLAIN, 16));
		Search.setBounds(718, 132, 106, 28);
		Search.setMargin(new Insets(1,1,-12, 0));
		getContentPane().add(Search);
	
		JTextPane textPane = new JTextPane();
		textPane.setText("active table\r\n");
		textPane.setFont(new Font("Tahoma", Font.BOLD, 11));
		textPane.setBounds(419, 166, 105, 20);
		
		getContentPane().add(textPane);
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setText("not_active table");
		textPane_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		textPane_1.setBounds(419, 375, 105, 20);
		getContentPane().add(textPane_1);
		
		btnEmail = new JButton("Email security");
		btnEmail.setBounds(701, 535, 123, 25);
		getContentPane().add(btnEmail);
		
		btnEmailStudents = new JButton("Email students\r\n");
		btnEmailStudents.setBounds(701, 318, 123, 28);
		getContentPane().add(btnEmailStudents);
		
		Clear = new JButton("Clear all Table\r\n");
		Clear.setBounds(701, 340, 123, 28);
		getContentPane().add(Clear);
		
		txtpnEmailOffenders = new JTextPane();
		txtpnEmailOffenders.setText("Email all student offenders on activate table");
		txtpnEmailOffenders.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnEmailOffenders.setBounds(419, 318, 272, 20);
		getContentPane().add(txtpnEmailOffenders);
		
		txtpnEmailSecurityOffice = new JTextPane();
		txtpnEmailSecurityOffice.setText("Email security office on non-student offenders on not_activate table");
		txtpnEmailSecurityOffice.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnEmailSecurityOffice.setBounds(419, 526, 272, 34);
		getContentPane().add(txtpnEmailSecurityOffice);
	}
	
		
		
		
	/**
	 * @deprecated Not used anymore because connection is passed from the Main class </p>
	 * Connects to the jdbc driver with the given port, username and password.
	 * 
	 * 
	 */
	@Deprecated
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
	
	/**
	 * Checks database if the given String number plate, whether automatic detected or typed, is available in the database
	 * @return true if the query returns positive results. Otherwise returns false
	 * @throws SQLException throws SQLException when query fails.
	 */
	public boolean checkDatabase() throws SQLException{
		PreparedStatement checkStatement = con.prepareStatement("Select Number_Plate FROM numberplate WHERE Number_Plate = '"+getManuallyInputPlate()+"'");
		ResultSet result = checkStatement.executeQuery();	
		if(result.next()){
			return true;	
		}
		return false;
	}
	
	/**
	 * Initiliases the table to be empty with column names on it.
	 * The default table as five columns and six rows
	 */
	private static void initializeaTable() {	
			String actable[][] = {
							{"","","","",""},
					    	{"","","","",""},
					    	{"","","","",""},
					    	{"","","","",""},
					    	{"","","","",""},
					    	{"","","","",""}	};
		   	table = new JTable(actable,activecolumn);
			scrollPane.setViewportView(table);

	} //end initialize active table
	
	/**
	 * <p>Updates active table</p>
	 * Updates the table on the screen with the current stored values in the variable {@link #activetable}
	 */
	public void updateActiveTable(){
		//arr[row][column]
		ArrayList<String[]> entries = activetable;
		String actable[][] = new String[entries.size()][5];
		
		for(int r=0; r<entries.size(); r++){
			for(int e=0; e<5; e++){
				actable[r][e] = entries.get(r)[e];
				//System.out.println(actable[r][e]);
			}
		}
		
		JTable active_table = new JTable(actable, activecolumn);
		scrollPane.setViewportView(active_table);
	}
	
	/**
	 * <p>Updates nonactive table</p>
	 * Updates the table on the screen with the current stored values in the variable {@link #non_activetable}
	 */
	public void updateNonActiveTable(){
		ArrayList<String[]> entries = non_activetable;
		String actable[][] = new String[entries.size()][2];
		
		for(int r=0; r<entries.size(); r++){
			for(int e=0; e<2; e++){
				actable[r][e] = entries.get(r)[e];
			}
		}
		
		JTable non_active_table = new JTable(actable, non_activecolumn);
		scrollPane_1.setViewportView(non_active_table);
	}
	
	/**
	 * Searches all attributes related to the table column names from the database.
	 * Only called when database has a matching number plate
	 * @return A String array containg all data related to the given number plate. The data are as follows
	 * <ul>Numberplate</ul>
	 * <ul>Student_id</ul>
	 * <ul>Studentname</ul>
	 * @throws SQLException	throws exception when query fails
	 */
	public String[] getActiveTableEntry() throws SQLException{
			   String[] entry = new String[5];
			   
			   PreparedStatement sqltable1 = con.prepareStatement("SELECT Number_Plate FROM numberplate WHERE Number_Plate ='"+getManuallyInputPlate()+"'");
			   ResultSet rs1 = sqltable1.executeQuery();
				
				while(rs1.next()){
					String getnumberplate = rs1.getString("Number_Plate");		
					entry[0] = getnumberplate;
				}
				
				PreparedStatement sqltable2 = con.prepareStatement("SELECT id FROM numberplate WHERE Number_Plate ='"+getManuallyInputPlate()+"'");
				ResultSet rs2 = sqltable2.executeQuery();
				while(rs2.next()){
					String getid = rs2.getString("id");		
					entry[1] = getid;
				}
				
				PreparedStatement sqltable3 = con.prepareStatement("SELECT name FROM identification, numberplate WHERE numberplate.Number_Plate ='"+getManuallyInputPlate()+"' AND identification.id=numberplate.id");
				ResultSet rs3 = sqltable3.executeQuery();
				while(rs3.next()){
					String getname = rs3.getString("name");		
					entry[2] = getname;
				}
				
				PreparedStatement sqltable4 = con.prepareStatement("SELECT email FROM identification, numberplate WHERE numberplate.Number_Plate ='"+getManuallyInputPlate()+"' AND identification.id=numberplate.id");
				ResultSet rs4 = sqltable4.executeQuery();
				while(rs4.next()){
					String getemail = rs4.getString("email");		
					entry[3] = getemail;
				}
				
		return entry;
	}
	
	/**
	 * Initiliases the table to be empty with column names on it.
	 * The table has two columns with default six rows
	 * 
	 */
	 private void initializenaTable() throws SQLException {
		 Object data[][] = {
					    	{"",""},
					    	{"",""},
					    	{"",""},
					    	{"",""},
					    	{"",""},
					    	{"",""},
					
				   					};

		   table_1 = new JTable(data,non_activecolumn);
		   scrollPane_1.setViewportView(table_1);
	} //end intialize nonactive table
	
	/**
	 * returns the plate number entered into the textfield
	 * Only called when database has no matching number plate
	 * @return A String of the plate number
	 */
	public String getNonActiveTable(){
		String plate = getManuallyInputPlate();
		
		return plate;
	}
	
	public JTable gettblImage() {
		return tblImage;
	}
	
	/**
	 * retrieves text from plate number input textfield
	 * @return	A string from the input textfield {@link #ManuallyInputPlate}
	 */
	public static String getManuallyInputPlate(){
		return ManuallyInputPlate.getText();
	}
	
	/**
	 * retrieves text from plate number input textfield ({@link #ManuallyInputPlate})
	 * 
	 */
	public void setManuallyInputPlate(String text){	
			ManuallyInputPlate.setText(text);
	}
	
/**
 * Gets url path from the textfield {@link #path}
 * @return	returns path in String format
 */
public String getpath(){
	
	return path.getText();
}

/**
 * Gets RecognitionResult from the textfield {@link #RecResult}
 * @return recognition result in String format
 */
public String getRecResult(){
	
	return RecResult.getText();
}

/**
 * Sets RecognitionResult from the textfield {@link #RecResult}
 */
public void setRecResult(){
	
	RecResult.setText("");
}

//public void setpic(){
//	
//	Icon icon = null;
//	pic.insertIcon(icon);
//}


/*public String setpath(){
	String s = path.setText(Controller.);
	return s;
	
}*/
	/*public Object settblImage() {
		return tblImage;
	}
	*/
	
	
	
	//Button Listeners
/**
 * Button listener for {@link #Home} Button
 * @param listenForHomeButton	ActionListener from Controller
 * @see GUI.Controller.HomeListener
 */
	void addHomeListener(ActionListener listenForHomeButton){
		Home.addActionListener(listenForHomeButton); 
			}
	

	/*void addDatabaseListener(ActionListener listenForDatabaseButton){
		Database.addActionListener(listenForDatabaseButton); 
			}
	*/
	
	/**
	 * Button listener for {@link #Camera} Button
	 * @param listenForCameraButton	ActionListener from Controller
	 * @see GUI.Controller.CameraListener
	 */
	void addCameraListener(ActionListener listenForCameraButton){
		Camera.addActionListener(listenForCameraButton); 
			}
	
	/**
	 * Button listener for {@link #About} Button
	 * @param listenForAboutButton	ActionListener from Controller
	 * 
	 * @see #GUI.Controller.AboutListener;
	 */
	void addAboutListener(ActionListener listenForAboutButton){
		About.addActionListener(listenForAboutButton); 
			}
	
	/**
	 * Button listener for {@link #LogOut} Button
	 * @param listenForLogoutButton	ActionListener from Controller
	 * @see GUI.Controller.LogoutListener
	 */
	void addLogOutListener(ActionListener listenForLogOutButton){
		LogOut.addActionListener(listenForLogOutButton); 
			}
	
	/**
	 * Button listener for {@link #Browse} Button
	 * @param listenForBrowseButton	ActionListener from Controller
	 * @see GUI.Controller.BrowseListener
	 */
	void addBrowseListener(ActionListener listenForBrowseButton){
		Browse.addActionListener(listenForBrowseButton); 
			}
	
	/*void addClearListener(ActionListener listenForClearButton){
		Clear.addActionListener(listenForClearButton); 
			}*/
	
/*	void addDeleteListener(ActionListener listenForDeleteButton){
		Delete.addActionListener(listenForDeleteButton); 
			}*/
	
	/*void addHelpListener(ActionListener listenForHelpButton){
		Help.addActionListener(listenForHelpButton); 
			}
*/
	/**
	 * Button listener for {@link #User} Button
	 * @param listenForUserButton	ActionListener from Controller
	 * @see GUI.Controller.UserListener
	 */
	void addUserListener(ActionListener listenForUserButton){
		User.addActionListener(listenForUserButton); 
			}

	
	/**
	 * Button listener for {@link #Execute} Button
	 * @param listenForExecuteButton	ActionListener from Controller
	 * @see GUI.Controller.ExecuteListener
	 */
	void addExecuteListener(ActionListener listenForExecuteButton){
		Execute.addActionListener(listenForExecuteButton);
	}
	
	/**
	 * Button listener for {@link #Admin} Button
	 * @param listenForAdminButton	ActionListener from Controller
	 * @see GUI.Controller.AdminListener
	 */
	void addAdminListener(ActionListener listenForAdminButton){
		Admin.addActionListener(listenForAdminButton);
	}
	
	/**
	 * Button listener for {@link #Search} Button
	 * @param listenForSearchButton	ActionListener from Controller
	 * @see GUI.Controller.SearchListener
	 */
	void addSearchListener(ActionListener listenForSearchButton){
		Search.addActionListener(listenForSearchButton);
	}
	
	/**
	 * Button listener for {@link #btnEmailStudents} Button
	 * @param listenForEmailStudentButton	ActionListener from Controller
	 * @see GUI.Controller.EmailStudentListener
	 */
	void addEmailStudent(ActionListener listenForEmailStudentButton){
		btnEmailStudents.addActionListener(listenForEmailStudentButton);
	}
	
	/**
	 * Button listener for {@link #btnEmail} Button
	 * @param listenForEmailSecurityButton	ActionListener from Controller
	 * @see GUI.Controller.EmailSecurityListener
	 */
	void addEmailSecurity(ActionListener listenForEmailSecurityButton){
		btnEmail.addActionListener(listenForEmailSecurityButton);
	}
	
	/**
	 * @deprecated Export button is no longer used.
	 * Button listener for {@link #Export} Button
	 * @param listenForExportButton	ActionListener from Controller
	 * @see GUI.Controller.ExportListener
	 */
	@Deprecated
	void addExportListener(ActionListener listenForExportButton){
		Export.addActionListener(listenForExportButton);
	}
	
	void addClearListener(ActionListener listenForClearButton){
		Clear.addActionListener(listenForClearButton);
	}
	
	//error message
	/**
	 * displays error message when needed
	 * @param errorMessage error message to be shown on the popup 
	 */
	public void displayErrorMessage(String errorMessage) {
		 JOptionPane.showMessageDialog(this, errorMessage);
	}
}
