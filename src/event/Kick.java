package event;

import java.util.regex.Matcher;

public class Kick 
{
	String kicker, host, channel, kicked, message;
	public Kick(Matcher m) 
	{
		kicker = m.group(1); 
		host = m.group(2); 
		channel = m.group(3);
		kicked = m.group(4);
		message = m.group(5);
	}
	public String getKicker() 
	{
		return kicker;
	}
	public String getHost() 
	{
		return host;
	}
	public String getChannel() 
	{
		return channel;
	}
	public String getKicked() 
	{
		return kicked;
	}
	public String getMessage() 
	{
		return message;
	}

}
