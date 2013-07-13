package core.utils;

import java.io.IOException;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class EMail 
{
	public static void sendEmail(String eMail_Address, String in_message, String topic) throws AddressException, MessagingException, IOException
	{
		Details details = Details.getIntance();
		
		// Get system properties
		Properties props = System.getProperties();

		// Setup mail server
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.host", details.getStmpHost());

		// Get the default Session object.
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() 
				{
					protected PasswordAuthentication getPasswordAuthentication() 
					{
						try
						{
							Details details = Details.getIntance();
							return new PasswordAuthentication(details.getStmpUser(),
									details.getStmpPassword());
						} catch (Exception ex){}
						return null;
					}
				});

		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(details.getStmpEmail()));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("tom.rosier92@gmail.com"));

		// Set Subject: header field
		message.setSubject(topic);

		// Now set the actual message
		message.setText(in_message);

		// Send message
		Transport.send(message);
	}
}
