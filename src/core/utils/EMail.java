package core.utils;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;
/**
 * This is the static email class, it sends emails over secure SMTP, which 
 * makes it compatible with GmailL & Hotmail but should also work with others.
 * @author Tom Rosier(XeTK)
 */
public class EMail 
{
	/**
	 * This sends an email using the details given in the Details.json
	 * @param eMail_Address this is the email of the person receiving the email.
	 * @param in_message this is the body of the email, and the context that the user will read.
	 * @param topic this is the title of the email and the subject line.
	 * @throws Exception if there is an error sending the email issue and an exceptipn.
	 */
	public static void sendEmail(String eMail_Address, String in_message, String topic) 
			throws Exception
	{
		// Get an instance of details so we can set up are authentication to the mail server.
		Details details = Details.getInstance();
		
		// Get system properties ready to send the email.
		Properties props = System.getProperties();

		// Assign the properties to the properties instance, ready to be passed to the mail lib.
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.host", details.getSmtpHost());

		// Create a session and open the socket to the mail server, we also authenticate the user here.
		Session session = Session.getInstance(props,
				new Authenticator() 
				{
					// Override the current password authentication.
					protected PasswordAuthentication getPasswordAuthentication() 
					{
						// Get an instance of Details. To get the username and pass.
						Details details = Details.getInstance();
						// Return the authenticated hash that is sent to the server.
						return new PasswordAuthentication(details.getSmtpUser(),
								details.getSmtpPassword());
					}
				});

		// Create a new message from the session we have opened.
		MimeMessage message = new MimeMessage(session);

		// Set the Senders address in the header of the email.
		message.setFrom(new InternetAddress(details.getSmtpEmail()));

		// This is the Recipient of the email, there address is converted to an InternetAddress.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(eMail_Address));

		// Give are email an subject that there end user can read.
		message.setSubject(topic);

		// Finally populate the content of the email with the message.
		message.setText(in_message);

		// Blastoff lets send that email.
		Transport.send(message);
	}
}
