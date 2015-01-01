package GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JTextArea;

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

import java.awt.Button;
import java.io.File;

import javax.swing.JLabel;
//import javax.swing.JPanel;



public class Email {
	JFrame frame;
	private JTextField from;
	private JTextField to;
	private JTextField subject;
	private JTextArea textarea;
	private JTextField attachmentpath;
	String attachment_path;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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

	/**
	 * Create the application.
	 */
	public Email() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 454, 366);
		frame.setTitle("Email Application - G52GRP Group 4"); 
	
		frame.getContentPane().setLayout(null);
		
		
		//'Send' Button
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String From = from.getText();
				String To = to.getText();
				String Subject = subject.getText();
				String Text = textarea.getText();
				
				
		        Properties props = new Properties();
		        props.put("mail.smtp.starttls.enable", "true");
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.host", "smtp.outlook.office365.com");
		        props.put("mail.smtp.port", "587");
		        
		        //Login authentication
		        Session session = Session.getInstance(props,
		        new javax.mail.Authenticator() {
		        @Override
				protected PasswordAuthentication getPasswordAuthentication() {
				//Input Nottingham Email and password here,
		        return new PasswordAuthentication("EMAIL", "PASSWORD"); 
		        	}
		        }
		        );
		    
		        try {

		            Message message = new MimeMessage(session);
		            message.setFrom(new InternetAddress(From));
		            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(To));
		            message.setSubject(Subject);
		            
		            
		            MimeBodyPart messageBodyPart = new MimeBodyPart();
		            messageBodyPart.setText(Text);
		            Multipart multipart = new MimeMultipart();
		            multipart.addBodyPart (messageBodyPart);
		            
		            messageBodyPart = new MimeBodyPart();
		            javax.activation.DataSource source = new FileDataSource(attachment_path);
		            messageBodyPart.setDataHandler(new DataHandler(source));
		            multipart.addBodyPart(messageBodyPart);
		            //String filename = "filename.txt?";
		            //messageBodyPart.setFileName(filename);
		            message.setContent(multipart);
		            
		            //message.setText(Text);
		            
		            System.out.println("Attempting to send message");
		            Transport.send(message);
		            System.out.println("Message sent yawwww!");
		            
		        } catch (MessagingException e1) {
		            throw new RuntimeException(e1);
		        }
		        
		        
		        
			}	
		});
		
		//'Browse' Button
		Button attachmentbrowse = new Button("Browse");
		attachmentbrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				attachment_path = f.getAbsolutePath();
				attachmentpath.setText(attachment_path);
				
				/**
				 JFrame (File location) = attachmentpath
				 String (File location name) = attachment_path
				 */
			}
		});
		
		attachmentbrowse.setBounds(341, 144, 91, 23);
		frame.getContentPane().add(attachmentbrowse);
		
		
		btnNewButton.setBounds(341, 307, 91, 23);
		frame.getContentPane().add(btnNewButton);
		
		from = new JTextField();
		from.setBounds(117, 25, 315, 20);
		frame.getContentPane().add(from);
		from.setColumns(10);
				
		to = new JTextField();
		to.setColumns(10);
		to.setBounds(117, 56, 315, 20);
		frame.getContentPane().add(to);
		
		subject = new JTextField();
		subject.setColumns(10);
		subject.setBounds(117, 87, 315, 20);
		frame.getContentPane().add(subject);
		
		textarea = new JTextArea();
		textarea.setBounds(117, 182, 315, 114);
		frame.getContentPane().add(textarea);
		
		attachmentpath = new JTextField();
		attachmentpath.setColumns(10);
		attachmentpath.setBounds(117, 118, 315, 20);
		frame.getContentPane().add(attachmentpath);
		
		JLabel lblFrom = new JLabel("From");
		lblFrom.setBounds(20, 28, 46, 14);
		frame.getContentPane().add(lblFrom);
		
		JLabel lblTo = new JLabel("To");
		lblTo.setBounds(20, 59, 46, 14);
		frame.getContentPane().add(lblTo);
		
		JLabel lblSubject = new JLabel("Subject");
		lblSubject.setBounds(20, 90, 46, 14);
		frame.getContentPane().add(lblSubject);
		
		JLabel lblAttachment = new JLabel("Attachment");
		lblAttachment.setBounds(20, 121, 68, 14);
		frame.getContentPane().add(lblAttachment);
		
		JLabel lblMessage = new JLabel("Message");
		lblMessage.setBounds(20, 182, 68, 14);
		frame.getContentPane().add(lblMessage);
		
		
		
	}
}


