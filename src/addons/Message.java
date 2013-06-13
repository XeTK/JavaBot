package addons;
import java.util.Date;

/**
 * This is a data class to hold the information for a message that will be used
 * with SED, and to correct various mistakes, it may later be used for more 
 * Intelligent spell checking or logging
 * @author Tom Rosier(XeTK)
 */
public class Message
{
	// Hold the date that the message was sent
	private Date date;
	
	// Also hold the user and message for it to be referred to later
	private String user, message;
	
	/**
	 * When the message is created then we set the attribute.
	 * @param user this is the user that the message was sent by.
	 * @param message this is the content of the message sent.
	 */
	public Message(String user, String message)
	{
		this.date = new Date();
		this.user = user;
		this.message = message;
	}
	
	/* Getters for the data within the class... we dont need to change it*/
	public Date getDate()
	{
		return date;
	}
	public String getUser()
	{
		return user;
	}
	public String getMessage()
	{
		return message;
	}
	
}
