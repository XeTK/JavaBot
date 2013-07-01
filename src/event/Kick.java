package event;

import java.util.regex.Matcher;
/**
 * This is a data class for a kicked user.
 * This class is here for encapsulation purposes.
 * @author Tom Rosier
 */
public class Kick 
{
	// Global Variables tied to a kicked user
	String kicker, host, channel, kicked, message;
	
	/**
	 * Default constructor that takes in are Regex
	 * and parses it out into various strings.
	 * @param m
	 */
	public Kick(Matcher m) 
	{
		kicker = m.group(1).toLowerCase(); 
		host = m.group(2); 
		channel = m.group(3);
		kicked = m.group(4).toLowerCase();
		message = m.group(5);
	}
	
	//Getters
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
