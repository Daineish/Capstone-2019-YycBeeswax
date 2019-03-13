package database;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationHandler {
	//TODO: move this email and pass into a config file
	//private String from = "hivenotificationalert@gmail.com";
	//private String fromPass = "Capstone2019";
	private Session session;
	private Properties properties;
	
	public NotificationHandler(String fromEmail, String fromPass){
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
                return new PasswordAuthentication(fromEmail,fromPass);  
                }    
               });      
	}
	public void notifyStakeholders(ResultSet emailList, int hiveId, String notificationType, float temp, float humid, float blockTime)throws SQLException{
		String to;
		while(emailList.next()){
			String messageBody = "";
			boolean validMessage = false;
			to = emailList.getString("Email");
			//System.out.println(to + " is in emailList\n"); //this is used for debugging without sending real emails
			//check if there is a temp notification and stakeholder is watching temp, if both are true add notification to message body
			if(notificationType.contains("T") && emailList.getString("NotificationType").contains("T")){
				messageBody += "Anomalous Temperature of " + temp + "°C detected in hive #" + hiveId + "\n";
				validMessage = true;
			}
			//check if there is a humidity notification and stakeholder is watching humidity, if both are true add notification to message body
			if(notificationType.contains("H") && emailList.getString("NotificationType").contains("H")){
				messageBody += "Anomalous Humidity of " + humid + "% detected in hive #" + hiveId + "\n";
				validMessage = true;
			}
			//check if there is a bloackage notification and stakeholder is watching bloackages, if both are true add notification to message body
			if(notificationType.contains("B") && emailList.getString("NotificationType").contains("B")){
				messageBody += "Hive blockage of " + blockTime + " seconds detected in hive #" + hiveId + "\n";
				validMessage = true;
			}
			if(validMessage){
				try{
					MimeMessage message = new MimeMessage(session);    
					message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
			        message.setSubject("Hive #" + hiveId + " Notification Alert");    
			        message.setText(messageBody);
			        //send message  
			        Transport.send(message); 
			        //System.out.println(messageBody +  "was sent to " + to + "\n"); //this is used for debugging without sending real emails
			        System.out.println("message successfully sent to " + to);    
				} catch (MessagingException e) {throw new RuntimeException(e);} 
			}
			
		}
	}
	
	public static void main(String [] args) {
		Database db = new Database();
		db.storeSensorData(65, 65, 65);
		db.storeSensorData(20, 100, 65);
		db.closeConnection();
             
    } 
	
}
