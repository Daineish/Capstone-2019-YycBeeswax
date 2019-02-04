package database;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class NotificationHandler {
	private String from = "hivenotificationalert@gmail.com";
	private String fromPass = "Capstone2019";
	private String defaultSub = "Hive Notification Alert";
	private Session session;
	private Properties properties;
	
	public NotificationHandler(){
		//setup properties
		properties = new Properties();    
        properties.put("mail.smtp.host", "smtp.gmail.com");    
        properties.put("mail.smtp.socketFactory.port", "465");    
        properties.put("mail.smtp.socketFactory.class",    
                  "javax.net.ssl.SSLSocketFactory");    
        properties.put("mail.smtp.auth", "true");    
        properties.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(properties,    
                new javax.mail.Authenticator() {    
                protected PasswordAuthentication getPasswordAuthentication() {    
                return new PasswordAuthentication(from,fromPass);  
                }    
               });      
	}
	public void notifyStakeholders(ArrayList<String> emailList, String msg, int HiveID){
		String to;
		for(int i=0; i<emailList.size(); i++){
			to = emailList.get(i);
			try {    
				MimeMessage message = new MimeMessage(session);    
				message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
		        message.setSubject(defaultSub);    
		        message.setText(msg);    
		        //send message  
		        Transport.send(message);    
		        System.out.println("message successfully sent to " + to);    
			} catch (MessagingException e) {throw new RuntimeException(e);} 
		}
		
	}
	
	public static void main(String [] args) {
		NotificationHandler handler = new NotificationHandler();
		Database db = new Database();
		int hiveID = 65;
		ArrayList<String> list = db.getStakeholderEmail(65);
		handler.notifyStakeholders(list, "this is a test notification for hive #"+hiveID, hiveID);
		db.closeConnection();
             
    } 
	
}
