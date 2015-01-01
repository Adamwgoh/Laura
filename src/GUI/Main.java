package GUI;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;


/**
 * <h1>The Main class file</h1>
 * The main class file. Connection begins from here
 * 
 * Jdbc Driver was used throughout the whole program to connect to mySQL database
 *
 */
public class Main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		Connection con = connect();
		Login Login = new Login(con);
		Home Home = new Home(con);
		Model Model= new Model();
		//DataView DataView = new DataView();
		Camera Camera = new Camera();
		Email Email = new Email();
		About About = new About();
		Settings Settings= new Settings(con);
		RequestAccount RequestAccount = new RequestAccount();
		ManualAccount ManualAccount = new ManualAccount(con);
		
	       Controller Controller = new Controller(Login,Home,Model,Camera,Email,About,Settings,RequestAccount, con, ManualAccount);
	         
	       Login.setVisible(true);
	       Login.setSize(451, 305);
	       Home.setSize(860, 652);
	     //  DataView.setSize(831,636);
	       Camera.setSize(860,652);
	       About.setSize(860,652);
	      // Settings.setSize(831,636);

	}
	
	private static Connection connect() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:8889/login","ronaldo","ronaldo");
		
		return con;
	}

}
