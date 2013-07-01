package event;

import java.util.regex.Matcher;

public class Message 
{
	private String user, host, channel, message;
	public Message(Matcher m)
	{
		user = m.group(1).toLowerCase();
		host = m.group(2); 
		channel = m.group(3); 
		message = m.group(4);
		if (channel.charAt(0) != '#')
			channel = user;
	}
	public String getUser() 
	{
		return user;
	}
	public String getHost() 
	{
		return host;
	}
	public String getChannel() 
	{
		return channel;
	}
	public String getMessage() 
	{
		return message;
	}
}
