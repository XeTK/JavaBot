package core.event;

import java.util.Date;
import java.util.regex.Matcher;

/**
 * This is a IRC message object. This holds all the information from an IRC
 * Message sent.
 * 
 * @author Tom Rosier (XeTK)
 */
public class Message {
	// Hold the date that the message was sent.
	private Date date_ = new Date();

	// Global variables for the users.
	private String user_ = new String();
	private String host_ = new String();
	private String channel_ = new String();
	private String message_ = new String();
	private boolean isPrivMsg_ = false;

	/**
	 * Default constructor converts are Regex Matcher to the various strings
	 * need for a IRC Message
	 * 
	 * @param m
	 *            this is the Regex that is passed in
	 */
	public Message(Matcher m) {
		// Create a new Date instance for the time the message was sent.
		date_ = new Date();

		// Get data from Regex groups.
		user_ = m.group(1);
		host_ = m.group(2);
		channel_ = m.group(3);
		message_ = m.group(4);

		if (!channel_.startsWith("#") && !channel_.startsWith("&")) {
			isPrivMsg_ = true;
			channel_ = user_;
		}
	}

	// Getters
	public Date getDate() {
		return date_;
	}

	public String getUser() {
		return user_;
	}

	public String getHost() {
		return host_;
	}

	public String getChannel() {
		return channel_;
	}

	public String getMessage() {
		return message_;
	}

	public boolean isPrivMsg() {
		return isPrivMsg_;
	}
}
