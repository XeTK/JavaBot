package event;

import java.util.regex.Matcher;

public class Quit 
{
	private String user, host, channel;
	public Quit(Matcher m) 
	{
		user = m.group(1).toLowerCase();
		host = m.group(2);
		channel = m.group(3);
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
	
}
