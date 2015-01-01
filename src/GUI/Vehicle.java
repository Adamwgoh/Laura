package GUI;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Vehicle {
    static int i = 0;
    private static Connection con;
    
    public Vehicle(Connection con){
    	Vehicle.con = con;
    }
	public static void main(String[] args) throws Exception
	{
		con = DriverManager.getConnection("jdbc:mysql://localhost:8889/login","ronaldo","ronaldo");
		System.out.println(returnEmail("AAA 1234"));
		//System.out.println(studId("AAA 1234"));
		System.out.println(studName("AAA 1234"));
		
	}
	private static String studName(String Number_Plate) throws SQLException, ClassNotFoundException {
		if(!checkExist(Number_Plate)){
			return Number_Plate;
		}
		Class.forName("com.mysql.jdbc.Driver");
		//load the jdbc driver class
		//dear jamila, please use only one connection. thank you
		//Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/grp","root","");/* red colored part has to be as per your database*/
		/*make connection with the database(db name ecommerce, user is root and password is not set in my case put yours in those places with password if you have set password for the database*/
		PreparedStatement idStatement = con.prepareStatement("Select name from identification WHERE id='"+studId(Number_Plate)+"'");
		/*sql structure to select instances from the table*/
		ResultSet result = idStatement.executeQuery();
		/*execution of the database query*/ 
		
		while(result.next()){
			String name = result.getString("name");
			return name;
		}
		return "error";
	}
	private static boolean checkExist(String Number_Plate) throws SQLException {
		//dear jamila, please use only one connection. thank you
		//Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/grp","root","");/* red colored part has to be as per your database*/
		/*make connection with the database(db name ecommerce, user is root and password is not set in my case put yours in those places with password if you have set password for the database*/
		PreparedStatement idStatement = con.prepareStatement("Select Id from numberplate WHERE Number_Plate='"+Number_Plate+"'");
		/*sql structure to select instances from the table*/
		ResultSet result = idStatement.executeQuery();
		int exists = 0;
		while(result.next()){
			exists++;
		}
		if(exists==1){
			return true;
		}
		return false;
	}
	private static String studId(String Number_Plate) throws SQLException {
		if(!checkExist(Number_Plate)){
			return Number_Plate;
		}
		Help.generalUpdate("vehicle", "numberplate", "Number_of_violations=Number_of_violations+1", "Number_Plate='"+Number_Plate+"'");
		//dear jamila, please use only one connection. thank you
		//Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/grp","root","");/* red colored part has to be as per your database*/
		/*make connection with the database(db name ecommerce, user is root and password is not set in my case put yours in those places with password if you have set password for the database*/
		PreparedStatement idStatement = con.prepareStatement("Select Id from numberplate WHERE Number_Plate='"+Number_Plate+"'");
		/*sql structure to select instances from the table*/
		ResultSet result = idStatement.executeQuery();
		while(result.next()){
			String Id = result.getString("Id");
			return Id;
		}
		return "error";
	}
	static String returnEmail(String Number_Plate) throws ClassNotFoundException, SQLException {
		//main class
		if(!checkExist(Number_Plate)){
			return Number_Plate;
		}
		Class.forName("com.mysql.jdbc.Driver");
		//load the jdbc driver class
		//dear jamila, please use only one connection. thank you
		//Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/grp","root","");/* red colored part has to be as per your database*/
		/*make connection with the database(db name ecommerce, user is root and password is not set in my case put yours in those places with password if you have set password for the database*/
		PreparedStatement idStatement = con.prepareStatement("Select Id from numberplate WHERE Number_Plate='"+Number_Plate+"'");
		/*sql structure to select instances from the table*/
		ResultSet result = idStatement.executeQuery();
		/*execution of the database query*/ 
		
		while(result.next()){
			String Id = result.getString("Id");
			PreparedStatement emailStatement = con.prepareStatement("SELECT email FROM identification WHERE id='"+Id+"'");
			ResultSet result1 = emailStatement.executeQuery();
			while(result1.next()) {
				String email = result1.getString("email");
				//System.out.println(email);
				return email;
			}
			
		}
		return "doesn't exist";
		
	}
	
	
}

