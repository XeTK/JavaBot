package core.event;

import java.util.regex.Matcher;

/**
 * This is a data class for the users that join a channel, 
 * this is here to be used for encapsulation purposes.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Join {
	// Global variables for a Join action
	private String user_    = new String();
	private String host_    = new String();
	private String channel_ = new String();

	/**
	 * This is the default Constructor converts Regex into usable strings
	 * 
	 * @param m this is the Regex passed in
	 */
	public Join(Matcher m) {
		user_    = m.group(1);
		host_    = m.group(2);
		channel_ = m.group(3);
	}

	// Getters
	public String getUser() {
		return user_;
	}

	public String getHost() {
		return host_;
	}

	public String getChannel() {
		return channel_;
	}

}
