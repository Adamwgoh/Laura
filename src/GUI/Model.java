package GUI;

import java.util.Calendar;


public class Model {
	
	String date;
	String time;
	
	public String getDate() {
		Calendar javaCalendar = null;
		date = "";
		javaCalendar = Calendar.getInstance();
		date = javaCalendar.get(Calendar.DATE) + "/" + (javaCalendar.get(Calendar.MONTH) + 1) + "/" + javaCalendar.get(Calendar.YEAR);

		return date;
	}

	public void setDate(String date){
		this.date = date;
	}

	
	public String getTime() {
		Calendar javaCalendar = null;
		time = "";
		javaCalendar = Calendar.getInstance();		
		time = javaCalendar.get(Calendar.HOUR) + ":" +javaCalendar.get(Calendar.MINUTE) + ":" + javaCalendar.get(Calendar.SECOND);
		
		return time;
	}
	
	public void settime(String time){
		this.time = time;
	}
	
	/*public static String getPath(){
		String p = DataView.getpath();
		System.out.println(p);
		return p;
	}
	*/
/*	public static String Split(){
		
		
		//C:/ProgramFIles/GRP/DB/picture.sql	
		String string = Model.getPath();
		System.out.println(string);
		String[] parts = string.split("/");
		
		
String part1 = parts[0]; // C:
	String part2 = parts[1]; // ProgramFiles
		String part3 = parts[2]; //GRP
		String part4 = parts[3]; //DB	
		String part5 = parts[4]; 
		
		return part5;
		
		
	}*/
		
	}
	
	
	

