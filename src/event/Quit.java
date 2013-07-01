package event;

import java.util.regex.Matcher;
/**
 * This is a quit object, it contains all the relevant 
 * information for when a user is kicked.
 * @author Tom Rosier(XeTK)
 */
public class Quit 
{
	// Variables we need for this operation
	private String user, host, channel;
	
	/**
	 * Default constructor takes in are Regex matcher
	 * and turn it into the relevant strings
	 * @param m this is the Regex passed into the constructor
	 */
	public Quit(Matcher m) 
	{
		user = m.group(1).toLowerCase();
		host = m.group(2);
		channel = m.group(3);
	}
	// Getters
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
