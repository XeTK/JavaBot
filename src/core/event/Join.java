package core.event;

import java.util.regex.Matcher;
/**
 * This is a data class for the users that 
 * join a channel, this is here to be used 
 * for encapsulation purposes. 
 * @author Tom Rosier(XeTK)
 */
public class Join 
{
	// Global variables for a Join action
	private String user, host, channel;
	/**
	 * This is the default Constructor
	 * converts Regex into usable strings
	 * @param m this is the Regex passed in
	 */
	public Join(Matcher m)
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
