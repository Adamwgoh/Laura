package GUI;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Help {
	private static Connection con;
	
	public Help(Connection conn){
		Help.con = conn;
	}
	
	public static void main(String[] args) throws SQLException
	{
	  System.out.println(generalSelect("vehicle","Number_Plate","numberplate","Number_of_violations>3"));
	  generalUpdate("vehicle","numberplate","Number_of_violations = Number_of_violations+1","Id=192635");
	}
	
	public static void connect(String database)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost/"+database,"root","");
		}	
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public static String generalSelect(String database, String Column, String From, String Where) throws SQLException {
		String finalString = "";
		//connect(database);
		String stat = "Select "+ Column + " "+"from "+ From +" "+"WHERE "+Where;
		System.out.println(stat);
		PreparedStatement generalStatement = con.prepareStatement(stat);
		ResultSet result = generalStatement.executeQuery();
		while(result.next()){
			String data = result.getString(Column);
			finalString = finalString + data + ", ";
		}
		return finalString;
	}
	
	public static void generalUpdate(String database, String table, String set, String where) throws SQLException {
		//connect(database);
		String stat = "Update "+ table + " "+"SET "+ set +" "+"WHERE "+where;
		System.out.println(stat);
		PreparedStatement updateStatement = con.prepareStatement(stat);
		updateStatement.executeUpdate();
	}
}
