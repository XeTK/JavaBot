package core.event;

import java.util.Date;
import java.util.regex.Matcher;

import core.helpers.Colour;
/**
 * This is a IRC message object.
 * This holds all the information 
 * from an IRC Message sent.
 * @author Tom Rosier (XeTK)
 */
public class Message 
{
	// Hold the date that the message was sent.
	private Date date;
	
	// Global variables for the users.
	private String user, host, channel, message;
	
	private boolean privmsg = false;
	
	/**
	 * Default constructor converts are 
	 * Regex Matcher to the various 
	 * strings need for a IRC Message
	 * @param m this is the Regex that is passed in
	 */
	public Message(Matcher m)
	{
		// Create a new Date instance for the time the message was sent.
		date = new Date();
		
		// Get data from Regex groups.
		user = m.group(1);
		host = m.group(2); 
		channel = m.group(3); 
		message = m.group(4);
		
		// This removes the crappy ACTION and replaces it with the user's name.
		message = message.replace("ACTION", Colour.colour("* " + user, Colour.MAGENTA));
		
		// Converts a channel message to a PM
		if (channel.charAt(0) != '#')
		{
			channel = user;
			privmsg = true;
		}
	}
	
	// Getters
	public Date getDate()
	{
		return date;
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
	public boolean isPrivMsg()
	{
		return privmsg;
	}
}
