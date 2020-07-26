package javaMail;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailUtil {
  public static void sendMail(String recipient, String title, String time, String log) {
    System.out.println("Preparing to send email");
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");
    
    String myAccount = "ENTER YOUR EMAIL ADDRESS THAT WILL BE SENDING THE EMAIL";
    String password = "ENTER YOUR PASSWORD FOR THE ABOVE ACCOUNT";
    
    Session session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(myAccount, password);
      }
    });
    
    Message message = prepareMessage(session, myAccount, recipient, title, time, log);
    
    try {
      Transport.send(message);
    } catch (MessagingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.print("Message sent sucessfully");
  }
  
  private static Message prepareMessage(Session session, String myAccountEmail, String recepient, String title, String time, String log) {
    try {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(myAccountEmail));
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(recepient));
    message.setSubject("Song Matched");
    message.setText("Title: " + title + "Time: " + time + " Log:" + log);
    return message;
    
  } catch (Exception ex) {
    Logger.getLogger(JavaMailUtil.class.getName()).log(Level.SEVERE, null, ex);
  }
    return null;
}
}