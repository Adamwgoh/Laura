package GUI;



import imageanalysis.MatToBufferedImage;
import imageanalysis.OCR;
import imageanalysis.Preprocess;
import imageanalysis.Snapshot;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import videoCapture.Opticalflow;
import videoCapture.VidPanel;
import videoCapture.VideoHelper;

import com.mysql.jdbc.Connection;

/**
 *  The main Controller class.
 * The Controller takes in all screen classes as well as a connection from the Main class and expands it.
 * All listeners to all buttons with its functions are written here.
 * @param Login	The Login screen GUI
 * @param Home	The Home screen GUI
 * @param Model	The Model screen GUI
 * @param Camera The Camera screen GUI
 * @param About	 The About screen GUI
 * @param Settings	The Settings screen GUI
 * @param RequestAccount	The RequestAccount Screen GUI
 * @param conn	The SQL Connection passed form the GUI.
 * @param ManualAccount	The ManualAccount screen GUI.
 * 
 * @author Group four: GUI Team(Ho Swee Jim & Jamila)
 * @author Javadoc: Adam Goh
 */
public class Controller {
	private static final int CREDENTIAL_ADMIN = 3;
	private Login Login;
	private Home Home;
	private Model Model;
	private Camera Camera;
	private About About;
	private static Connection conn;
	private static BufferedImage plateclip;
	private ManualAccount ManualAccount;
	
	/**
	 * the urlpath contains the path to the plate clip image
	 * Will be cleared when ur
	 */
	private static String urlpath;
	/**
	 * the vidpath contains the path to the video clip
	 */
	private static String vidpath = "platepaths/screenshots/";
	private static String platepath = "platepaths/";
	private String plateS;
	private boolean hasImage = false;
	
	//private Settings Settings;
	Email Email = new Email();
	Settings settings;
	RequestAccount RequestAccount = new RequestAccount();
	Vehicle Vehicle = new Vehicle(conn);
	
	BufferedImage img;
	/**
	 * <p> The main Controller class. <p>
	 * The Controller takes in all screen classes as well as a connection from the Main class and expands it.
	 * All listeners to all buttons with its functions are written here.
	 * @param Login	The Login screen GUI
	 * @param Home	The Home screen GUI
	 * @param Model	The Model screen GUI
	 * @param Camera The Camera screen GUI
	 * @param About	 The About screen GUI
	 * @param Settings	The Settings screen GUI
	 * @param RequestAccount	The RequestAccount Screen GUI
	 * @param conn	The SQL Connection passed form the GUI.
	 * @param ManualAccount	The ManualAccount screen GUI.
	 * 
	 * @see GUI.Login
	 * @see GUI.Home
	 * @see GUI.Model
	 * @see GUI.Camera
	 * @see GUI.About
	 * @see GUI.Settings
	 * @see GUI.RequestAccount
	 * @see GUI.ManualAccount
	 */
	public Controller(Login Login, Home Home, Model Model, Camera Camera, Email Email, About About, 
			Settings Settings,RequestAccount RequestAccount, Connection conn, ManualAccount ManualAccount) {
        this.Login = Login;
        this.Home = Home;
        Controller.conn = conn;
        this.Camera = Camera;
        this.Model = Model;
        this.About = About;
        this.RequestAccount=RequestAccount;
        settings = new Settings(Controller.conn);
        this.ManualAccount = ManualAccount;
      
        
        
        
        this.Login.addLoginListener(new LogInListener());
        this.Login.addRequestListener(new RequestListener());
        //this.Login.addLoginListener(new AdminListener());
        this.RequestAccount.addRequestCancelListener(new RequestCancelListener());
       
        
        //Home Action Listeners
        this.Home.addHomeListener(new HomeListener());
       // this.Home.addDatabaseListener(new DatabaseListener());
        this.Home.addCameraListener(new CameraListener());
        this.Home.addLogOutListener(new LogoutListener());
        this.Home.addBrowseListener(new BrowseListener());
       // this.Home.addDeleteListener(new DeleteListener());
        this.Home.addAboutListener(new AboutListener());
       // this.Home.addHelpListener(new HelpListener());
       this.Home.addUserListener(new UserListener());
       this.Home.addExecuteListener(new ExecuteListener());
       this.Home.addAdminListener(new AdminListener());
       this.Home.addSearchListener(new SearchListener());
       this.Home.addEmailStudent(new EmailStudentListener()); 
       this.Home.addEmailSecurity(new EmailSecurityListener());
       this.Home.addClearListener(new ClearTable());
     //  this.Home.addExportListener(new ExportListener());
        //this.Home.addAddListener(new AddListener());
              
        //Database Action Listeners
        /*this.DataView.addAddListener(new AddListener());
        this.DataView.addHomeListener(new HomeListener());
        this.DataView.addDatabaseListener(new DatabaseListener());
        this.DataView.addCameraListener(new CameraListener());
        this.DataView.addLogOutListener(new LogoutListener());
        this.DataView.addBrowseListener(new DataBrowseListener());
        this.DataView.addEmailListener(new EmailListener());
        this.DataView.addDataExportListener(new DataExportListener());
        this.DataView.addAboutListener(new AboutListener());
        this.DataView.addDataExecuteListener(new DataExecuteListener());
        //this.Home.addAddListener(new AddListener());*/
        
        
        //Camera Action Listeners
        this.Camera.addHomeListener(new HomeListener());
      //  this.Camera.addDatabaseListener(new DatabaseListener());
        this.Camera.addCameraListener(new CameraListener());
        this.Camera.addLogOutListener(new LogoutListener());
        this.Camera.addAboutListener(new AboutListener());
        this.Camera.addUserListener(new UserListener());
        this.Camera.addBrowseVidListener(new BrowseVidListener());
        
        //About Action Listeners
        this.About.addHomeListener(new HomeListener());
        
        //ManualAccount Listeners
        this.ManualAccount.addCancelListener(new CancelListener());
        
}
	
	/**
	 * <p> CancelListener for Cancel Button</p>
	 * This is the listener that cancels the Manual Account Screen. Maybe a proper name would do fine with the javadoc editor
	 * <p> Sets ManualAccount to hide.
	 *
	 */
	class CancelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0){
			ManualAccount.setVisible(false);
		}
	}
	
	/**
	 * <h1> The login Listener </h1>
	 * The login listener checks if the given credentials are valid and matches records in the database.
	 * If it matches, The Login screen will be closed and the Home screen will be shown.
	 * 
	 * <p> A User previlege functionality is added. The Admin Button, which grant access to the administrator
	 * Panel is only available if the checked statusID is a valid Admin credentials.</p>
	 *
	 *@see GUI.Login#loginAuthentication()
	 */
	 class LogInListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {

				if (Controller.this.Login.loginAuthentication()) {
					Home.Admin.setVisible(false);
					Login.setVisible(false);
					Home.setVisible(true);
					
					//if its admin, sets admin button to true
					if(Controller.this.Login.getStatusID() == CREDENTIAL_ADMIN){
						//Home.Admin.setText("YOU DID IT");	//for debugging purposes
						Login.setVisible(false);
						ManualAccount.setVisible(false);
						Home.Admin.setVisible(true);
					}
				} else {
					Home.setVisible(false);
					Login.setVisible(true);
				}
			} catch (ClassNotFoundException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("ERROR! Error is " + e.getMessage());
				System.exit(1);
			}
		}
			/*Login.setVisible(false);
			Home.setVisible(true);*/

		}
	/**
	 * <h1> The Request Listener </h1>	 
	 * When request button is clicked, the Request Account is run.
	 * </br>
	 * <p></p>
	 * 
	 * @see GUI.RequestAccount
	 *
	 */
	 class RequestListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						RequestAccount window = new RequestAccount();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Error! message : " + e.getMessage());
						
					}
				}
			});
		}
	 }
	 
		/**
		 * <h1> The Request Cancel Listener </h1>	 
		 * When cancel button in request account button is clicked, the Request Account is closed.
		 * </br>
		 * <p></p>
		 * 
		 * @see GUI.RequestAccount
		 *
		 */
	 class RequestCancelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Login.setVisible(true);
			RequestAccount.frame.setVisible(false);
			
		}
	 }
	 
//	 class OpenListener implements ActionListener {
//
//	
//		public void actionPerformed(ActionEvent arg0) {
//			
//			String p = urlpath ;
//			ImageIcon icon = new ImageIcon(p);
//			Image img = icon.getImage();
//			img = img.getScaledInstance(381, 386, Image.SCALE_DEFAULT);
//			
//			BufferedImage buffimg = new BufferedImage(381, 386, BufferedImage.TYPE_INT_ARGB);
//			
//			Graphics g = buffimg.createGraphics();
//			g.drawImage(img, 0, 0, 381, 386, null);
//			ImageIcon newicon = new ImageIcon(img);
//			
//			Home.pic.setIcon(newicon);
//		}
//		 
//	 }

	 /**
	  * @deprecated <b> Not used anymore due to the change in GUI. </b>
	  * <h1> Add Listener for Browse button </h1>
	  * <p> When clicked on the Add to Field button, the Plate input as well as the image location is added to the table.
	  * An image of the clipped plate image will be saved to the specified platepath </p>
	  * 
	  * @see GUI.Home#gettblImage()
	  * 
	  */
	 @Deprecated
	class AddListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			DefaultTableModel model= (DefaultTableModel)Home.gettblImage().getModel();		
			if(!GUI.Home.getManuallyInputPlate().trim().equals("")|| !Home.getRecResult().trim().equals("") ){
				//TODO: plate path here, time here is also wrong. Need the time of image capture.
				model.addRow(new Object[]{Model.getDate(),Model.getTime(), GUI.Home.getManuallyInputPlate(), platepath + GUI.Home.getManuallyInputPlate() + ".jpg"});
				try {
					System.out.println("saving image.... " +Snapshot.SaveImage(img, GUI.Home.getManuallyInputPlate(), platepath));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.err.println("Error! saving image failed. Error message is " + e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Home.displayErrorMessage("Please Insert the Number Plate Before Clicking Add Button");
			}
		}
	 }

	 /**
	  * <h1> Admin Listener for Admin Button </h2>
	  * <p> displays ManualAccount screen when pressed </p>
	  * 
	  * @see ManualAccount
	  *
	  */
	 class AdminListener implements ActionListener{
		 @Override
		public void actionPerformed(ActionEvent e){
				ManualAccount.setVisible(true);
				
		 }
	 }
	 
	 /**
	  * <h1> Home Listener for Home Button </h2>
	  * <p> displays Home screen when pressed </p>
	  * 
	  * @see Home
	  * 
	  *
	  */
	 class HomeListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Home.setVisible(true);
			Camera.setVisible(false);
			About.setVisible(false);
			}	 
	 }
	 
	 /**
	  * <h1> Search Listener for Search button </h1>
	  * <p>Searches if the numberplate exist in the database. If record found, all attributes belonging to the number plate will be extracted.
	  * Extracted attributes together wtih the screenshot image url will be appended onto the activetable arraylist(acting as a mem cache)
	  * <p>Once the record is appended, the table will be updated with the updatetable function. If a plate clip is produced, the plate clip will 
	  * be saved onto the specified url </p>
	  *
	  *@see GUI.Home#checkDatabase()
	  *@see GUI.Home#updateActiveTable()
	  *@see GUI.Home#updateNonActiveTable()
	  *@see GUI.Home#activetable
	  *@see GUI.Home#non_active_table
	  */
	 class SearchListener implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				
					try {
						if(Home.checkDatabase()){
							//System.out.println("YOU GOT ME");	for debugging purposes

							String plate = urlpath;
							String[] person = Home.getActiveTableEntry();
							String[] entry = {person[0], person[1], person[2], person[3], plate};

							try {
								if(plateclip != null){
									System.out.println("saving image.... " +Snapshot.SaveImage(plateclip, person[3], platepath + "plateclips/"));
								}
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							GUI.Home.activetable.add(entry);
							Home.updateActiveTable();

						}else{
							//System.out.println("LOLLL");	for debugging purposes
							
							String plate = urlpath;
							String anon = Home.getNonActiveTable();
							String[] entry = {anon, plate};
							
							GUI.Home.non_activetable.add(entry);
							Home.updateNonActiveTable();
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

			}
	 }
	 
	 /**
	  * <h1> Execute Listener for Execute Button </h1>
	  * <p> if An image had been uploaded, the exeute button will perform plate clipping and OCR, and return both results to the screen.
	  * Due to the need to train OCR, each execution of the functions take time </p>
	  * 
	  * <p> If no plate is found, The result will return a black image. Also if OCR doesn't return a string, the warning message will be shown </p>
	  *
	  *@see imageanalysis.Preprocess
	  *@see imageanalysis.OCR
	  */
	 class ExecuteListener implements ActionListener{
		 ArrayList<Character> characters = new ArrayList<Character>();
		 @Override
		public void actionPerformed(ActionEvent e){
			 if(hasImage){
				 try {
					
					img = Preprocess.run(urlpath);
					
					//get image from here and paste onto there
					ImageIcon newicon = new ImageIcon(img);
					Home.RecResult.setIcon(newicon);
					plateclip = img;
					OCR ocr = new OCR();
					characters = ocr.getPlateCharacter(img);
					char[] chars = new char[characters.size()];
					plateS = new String(chars);
					if(plateS.isEmpty()){
						JOptionPane.showMessageDialog(null, "plate characters not detected. Manual Input is available, however");
					}
					
					Home.setManuallyInputPlate(plateS);
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					System.err.println("Error getting image! Error message is " + e1.getMessage());
				}
			 }else{
				 JOptionPane.showMessageDialog(null,"Can't execute! No image loaded");	
			 }
			 //System.out.println("Execute pressed");
		 }
	 }//end func
	 
	/* class DatabaseListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
		
		
			Home.setVisible(false);
			Camera.setVisible(false);
		}
		 
	 }*/

	 /**
	  * <h1> Camera Listener for camera button </h1>
	  * <p> Camera button that leads to the camera GUI screen </p>
	  * @author dolphin
	  *
	  */
	 class CameraListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Camera.setVisible(true);
			Home.setVisible(false);
		}
	 }
	 
	 /**
	  * <h1> Logout Listener for Logout button </h1>
	  * <p> Logout button that leads to the camera GUI screen </p>
	  *
	  */
	 class LogoutListener implements ActionListener{

	
		@Override
		public void actionPerformed(ActionEvent e) {
			Login.setVisible(true);
			Home.setVisible(false);
		
			Camera.setVisible(false);
			
			
			
		}
		 
	 }
	 
	 /**
	  * <h1> BrowseVideo Listener for browseVid button </h1>
	  * <p> BrowseVid button lets user choose a video file and load it onto the screen. </p>
	  * The chosen video will then be used in the opticalflow to detect wrong direction.
	  * @author dolphin
	  *
	  *@see VideoCapture.Opticalflow
	  */
	 class BrowseVidListener implements ActionListener{
		 @Override
		public void actionPerformed(ActionEvent e){
			 	//System.out.println("clicked on yea!");
			 	String path;
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				path = f.getAbsolutePath();
				
				Opticalflow optflow = new Opticalflow();
	            //file you want to play
				try {
					Camera.video.setText("running video...\n");
					runOpticalFlow(path);
					
				} catch (IOException | InterruptedException e1) {
					// DONE Auto-generated catch block
					e1.printStackTrace();
					System.err.println("Error! the message is " + e1.getMessage());
				}
	          
		 }
	 }
	 
	 /**
	  * <h1> BrowseListener for Browse button </h1>
	  * <p> Browse button lets user choose an image file and load it onto the screen. </p>
	  * The image url is then saved onto a public static variable to be used by other methods
	  *
	  **/
	 class BrowseListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String path;
			JFileChooser chooser = new JFileChooser();
			chooser.showOpenDialog(null);
			File f = chooser.getSelectedFile();
			path = f.getAbsolutePath();
			urlpath = path;
			hasImage = true;
			Home.path.setText(path);
			
			ImageIcon icon = new ImageIcon(path);
			Image img = icon.getImage();
			img = img.getScaledInstance(381, 386, Image.SCALE_DEFAULT);
			
			BufferedImage buffimg = new BufferedImage(381, 386, BufferedImage.TYPE_INT_ARGB);
			
			Graphics g = buffimg.createGraphics();
			g.drawImage(img, 0, 0, 381, 386, null);
			ImageIcon newicon = new ImageIcon(img);
			
			Home.pic.setIcon(newicon);
			//path.split("\\");			
			//DataView.path.setText(path);	
		}
	 }
	 
	 /**
	  * @deprecated Clear button is not used in the GUI anymore
	  * <h1> Clear Listener for Clear button </h1>
	  * Clears the plate character textfield
	  *
	  */
	 @Deprecated
	class ClearListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {	
			Home.setManuallyInputPlate("");
		} 
	 }
	 
	 /**
	  * @deprecated Delete button is not used in the GUI anymore
	  * <h1> Delete listener for Delete button
	  * Delete button that deletes a row from the table
	  *
	  */
	 @Deprecated
	class DeleteListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultTableModel model= (DefaultTableModel)Home.gettblImage().getModel();
			int numRows = Home.gettblImage().getRowCount();
			//for (int i=numRows-1;i>0;i++) {
			model.removeRow(numRows-1); 
			Home.gettblImage().revalidate();	
		}		 
	 }
	 
	 /**
	  * Clear all tables and update it on the table
	  * @author Adam Goh
	  *
	  */
	 class ClearTable implements ActionListener{
		 @Override
		 public void actionPerformed(ActionEvent e){
			 System.out.println("clicked");
			 GUI.Home.activetable.clear();
			 GUI.Home.non_activetable.clear();
			 
			 Home.updateActiveTable();
			 Home.updateNonActiveTable();
		 }
	 }
	 
	/* class DataBrowseListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			
				String path;
				
			

				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				path = f.getAbsolutePath();
				DataView.path.setText(path);
				
			}
		 
			
		}*/
	/**
	 * @deprecated Email button is not used anymore
	 * <h1> Email Listener for Email Button </h1>
	 * 
	 * The email button launches the Email window when clicked.
	 *
	 */
	 @Deprecated
	class EmailListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
		//	Email Email = new Email();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						Email window = new Email();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}	
	}
	
	 /**
	  * <h1> EmailStudent Listener for Email Student Button </h1>
	  * <p> Email Student button when click calls the email student function, which sends an email to the target email address </p>
	  * @author dolphin
	  *
	  */
	 class EmailStudentListener implements ActionListener{
		 @Override
		public void actionPerformed(ActionEvent arg0){
			// System.out.println("YOU CALLED??");
			 for(String[] student : GUI.Home.activetable){
				 EmailStudent(student[3], student[4]);
			 }
			 GUI.Home.activetable.clear();
			 
		 }
	 }
	 
	 /**
	  * <h1> EmailSecurity Listener for Email Security Button </h1>
	  * <p> Email Security button when click calls the email security function, which sends an email to the target email address </p>
	  * 
	  *@see Controller#EmailSecurity()
	  */
	 class EmailSecurityListener implements ActionListener{
		 @Override
		public void actionPerformed(ActionEvent arg0){
			 EmailSecurity();
		 }
	 }
 /**
  * @deprecated	Export button had been taken out from the GUI.
  * 
  * Export button saves an image to a target location
  * @author Jamila Abdulsalam
  *
  */
	 @Deprecated
	class ExportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			JFileChooser schooser = new JFileChooser();
			//schooser.showSaveDialog(null);
			 int rVal = schooser.showSaveDialog(null);
			JTextComponent filename = null;
			JTextComponent dir = null;
			if (rVal == JFileChooser.APPROVE_OPTION) {
		        filename.setText(schooser.getSelectedFile().getName());
		        dir.setText(schooser.getCurrentDirectory().toString());
		      }
		      if (rVal == JFileChooser.CANCEL_OPTION) {
		        filename.setText("You pressed cancel");
		        //schooser.cancelSelection();
		        dir.setText("");
		      }
		    }
		}
		 
	 /**
	  * <h1> About Listener for About button </h1>
	  * <p> Launches the System's help text in the About screen </p>
	  * The About text shows instructions on how to use the system.
	  *
	  */
	 class AboutListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {	
				String file = "documents/GRP - Help and About.txt";
				FileReader r = new FileReader(file);
				/*BufferedReader reader = new BufferedReader(r);
				
				
				String inputFile = "";
			    String textFieldReadable = reader.readLine();

			    while (textFieldReadable != null){
			        inputFile += textFieldReadable;
			        textFieldReadable = reader.readLine();                    
			    }		*/	
				
				About.about.read(r, file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			About.setVisible(true);
			Home.setVisible(false);
		
			Camera.setVisible(false);	
		} 
	 }
	 
	 /**
	  * <h1> EmailStudent Listener for Email Student Button </h1>
	  * <p> Email student button sends an email to each student that are on the active table.
	  * The email is sent using Open Source Library javax mail. The email will include an attachment of the motion detected screencapture.
	  */
	 public synchronized void EmailStudent(String mail, String urlpath) {
			System.out.println("Testing");
			 Properties props = new Properties();
		        props.put("mail.smtp.starttls.enable", "true");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.host", "smtp.gmail.com");
		        props.put("mail.smtp.port", "587");
		        
		        
		        Session session = Session.getInstance(props,
		        new javax.mail.Authenticator() {
		        @Override
				protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication("g52grpgroup4@gmail.com", "groupfour"); 
		        	}	
		        }
		        );
		        
		        try {
		            Message message = new MimeMessage(session);
		            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail)); //<<<<<Queried Email 
		            message.setSubject("Road violation");
		            
		            MimeBodyPart messageBodyPart = new MimeBodyPart();
		            messageBodyPart.setText("Attached is the vehicle registered under your id commiting a road violation.");
		            Multipart multipart = new MimeMultipart();
		            multipart.addBodyPart (messageBodyPart);
		            
		            messageBodyPart = new MimeBodyPart();
		            String File = urlpath; //<<<<<<Image location
		            if(!(urlpath == null)){
		            	System.out.println("the image url to be sent is : " + urlpath);
		            	javax.activation.DataSource source = new FileDataSource(File); 

			            messageBodyPart.setDataHandler(new DataHandler(source));
			            multipart.addBodyPart(messageBodyPart);
			            
			            message.setContent(multipart);
		            }else{
		            	//attaches a dummy file
		            	System.out.println("getting a dummy file..");
		            	String file = "dummydataset/no screenshot.jpg";
		            	javax.activation.DataSource source = new FileDataSource(file); 

			            messageBodyPart.setDataHandler(new DataHandler(source));
			            multipart.addBodyPart(messageBodyPart);
			            
			            message.setContent(multipart);
		            }
		            System.out.println("Attempting to send message");
		            Transport.send(message);
		            System.out.println("Done");	
		         
		            
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
	
	 /**
	  * UserListener that is called at the press of {@link GUI.Home.User} button.
	  * When clicked, Opens the User screen where user password can be changed.
	  * @author Ho Swee Jim
	  * 
	  */
	 class UserListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("works");
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						Settings window = new Settings(conn);
						Settings.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		}
	 }//end UserListener
	 
	 /**
	  * <b>Emails to security email</b>
	  * <p> Sends an email to the specified email. To send an email from an email address, the needed 
	  *  elements are STMP host, port, email address and password </p>
	  *  
	  *  <p> The email will only be sent to the address when it attaches a file. In the example in the code,
	  *  The folder of all plate clips are sent to the email security as evidence, each time this function is called
	  *  </p>
	  * 
	  */
	 public void EmailSecurity() {
		 Properties props = new Properties();
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.host", "smtp.gmail.com");
	        props.put("mail.smtp.port", "587");
	        
	        Session session = Session.getInstance(props,
	        new javax.mail.Authenticator() {
	        @Override
			protected PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication("g52grpgroup4@gmail.com", "groupfour"); 
	        	}	
	        }
	        );
	        

	        try {
	            Message message = new MimeMessage(session);
	           // message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("securtyofficeg52grp4@gmail.com")); // Created gmail for security securtyofficeg52grp4@gmail.com, password: groupfour
	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("adamwgoh@gmail.com"));
	            message.setSubject("Non-students road violation");
	            ArrayList<String[]> names = GUI.Home.non_activetable;
	            ArrayList<String[]> namesagain = GUI.Home.activetable;
	            
	            
	            MimeBodyPart messageBodyPart = new MimeBodyPart();
	            
	            messageBodyPart.setText("The following is a list of non-student plates commiting road violation.\n");
	            System.out.println("The following is a list of non-student plates commiting road violation.\n");
	            for(int i= 0; i<names.size(); i++){
	            	for(String s : names.get(i)){
	            		messageBodyPart.setText(s + "   ");
	            		System.out.println(s + "   ");
	            	}
	            	messageBodyPart.setText("\n");
	            	System.out.println("\n");
	            }
	            messageBodyPart.setText("The following is a list of student plates commiting road violation.\n");
	            System.out.println("The following is a list of student plates commiting road violation.\n");
	            for(int i= 0; i<namesagain.size(); i++){
	            	for(String s : namesagain.get(i)){
	            		messageBodyPart.setText(s + "   ");
	            		System.out.println(s + "   ");
	            	}
	            	messageBodyPart.setText("\n");
	            	System.out.println("\n");
	            }

	            Multipart multipart = new MimeMultipart();
	            multipart.addBodyPart (messageBodyPart);
	            
	            
	            
	            File f = new File("platepaths/plateclips"); //<<<sends entire folder of images
	            File[] attachments = f.listFiles();
	            
	            Multipart multipart1 = new MimeMultipart();
	            multipart1.addBodyPart(messageBodyPart);
	            
	            for( int i = 0; i < attachments.length; i++ ) {
	            messageBodyPart = new MimeBodyPart();
	            FileDataSource fileDataSource = new FileDataSource(attachments[i]);
	            messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
	            multipart1.addBodyPart(messageBodyPart);
	            }

	            message.setContent(multipart);
	            System.out.println("Attempting to send message");
	            Transport.send(message);
	            System.out.println("Done");
	            
	            //clears cache
	            GUI.Home.activetable.clear();
	            GUI.Home.non_activetable.clear();
	           
	 
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}//end Email Security
	 
	 public void runOpticalFlow(String path) throws FileNotFoundException, IOException, InterruptedException{
			JFrame jf = new JFrame();
			VidPanel panel = null;
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			VideoHelper vid = new VideoHelper();
			Opticalflow.filename = new File(path).getName();
			System.out.println(Opticalflow.filename);

				//vid.loadfile(readfolder + filename + extension);
				vid.loadfile(path);

			Mat frame1 = new Mat();
			frame1 = vid.grabframe();
			//Core.flip(frame1, frame1, 1);
			//displayImage(prevgrey, "current frame");

			BufferedImage img;
			Mat diff = new Mat();
			Mat result = new Mat();
			MatToBufferedImage conv = new MatToBufferedImage();
			
			int count = 0;
			boolean hasNext = true;
			double startTime = System.nanoTime();
			double timediff;
			int offensecount = 0;
			int nofframes = 1;
			Mat firstframe = new Mat();
			double fps;
			
			Opticalflow.init(frame1);
			Opticalflow.prevgrey = frame1;
			//Opticalflow.displayImage(Opticalflow.prevgrey, "prevgrey");
			
			jf.setSize(Opticalflow.prevgrey.width(), Opticalflow.prevgrey.height());
			jf.setTitle("optical flow");
			Imgproc.cvtColor(Opticalflow.prevgrey, Opticalflow.prevgrey, Imgproc.COLOR_RGB2GRAY);
		
			while(hasNext){
				
				Opticalflow.nextgrey = vid.grabframe();
				//Core.flip(nextgrey, nextgrey, 1);
				if(Opticalflow.nextgrey.empty()){
					System.out.println("end of video");
					Camera.video.setText("end of video \n");
					hasNext = false;
					jf.setVisible(false);
					offensecount = 0;
					vid.close();
					return;
				}
				
				Imgproc.cvtColor(Opticalflow.nextgrey, Opticalflow.nextgrey, Imgproc.COLOR_RGB2GRAY);
				Thread.sleep(10);//to prevent fps from running too fast
				
				Core.absdiff( Opticalflow.prevgrey, Opticalflow.nextgrey, diff );
				double n = Core.norm(diff, Core.NORM_L2);
				//System.out.println("the n value is " + n);
				boolean wrongdirection = false;
				
				wrongdirection = Opticalflow.inWrongDirection(diff);
				if(wrongdirection){
					Camera.video.setText("offense capture\n");
					Opticalflow.WrongDirImg = Opticalflow.prevgrey;
					Opticalflow.filename = Opticalflow.filename + offensecount;
					Opticalflow.SaveImage(conv.getImage(Opticalflow.WrongDirImg), Opticalflow.filename, vidpath);
					
					Camera.video.setText("screenshot saved at" + vidpath);
					Camera.video.setVisible(true);
					offensecount++;
				}
				//displayImage(prevgrey, "hello");
				result = Opticalflow.calculateOptflow(Opticalflow.prevgrey, Opticalflow.nextgrey );
				
				//prevgrey = OptflowFarneBack(prevgrey, nextgrey);
				
				img = conv.getImage(result);
				if(panel == null){
					panel = new VidPanel(img);
				}else{
					panel.setImage(img);
				}
				jf.setContentPane(panel);
				jf.setVisible(true);
				
				Opticalflow.prevgrey = Opticalflow.nextgrey;
				timediff = (System.nanoTime() - startTime) / 1e9;
				count++;
				fps = count/timediff;
				nofframes++;
				//System.out.println(fps);
	 }
	 
	/* class DataExportListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			
			DataView.gettblImage().setModel(DataView.getnumberplate());
			
			
		}
		 
		 
	 }
	 
	 class DataExecuteListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		
			
			DataView.getpath();
			DataView.getnumberplatetable().setModel(DataView.getnumberplate());
			
		}
		 
		 
	 }*/
	 }}










	

