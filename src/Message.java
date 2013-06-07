import java.util.Date;


public class Message
{
	private Date date;
	private String user, message;
	public Message(String user, String message)
	{
		this.date = new Date();
		this.user = user;
		this.message = message;
	}
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
