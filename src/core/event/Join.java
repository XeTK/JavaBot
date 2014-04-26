package core.event;

import java.util.regex.Matcher;

/**
 * This is a data class for the users that join a channel
 *
 * @author Tom Rosier(XeTK)
 */
public class Join {
	private String user_    = new String();
	private String host_    = new String();
	private String channel_ = new String();

	/**
	 * This is the default Constructor converts Regex into usable strings
	 *
	 * @param m the Matcher object
	 */
	public Join(Matcher m) {
		this.user_    = m.group(1);
		this.host_    = m.group(2);
		this.channel_ = m.group(3);
	}

	// Getters
	public String getUser() {
		return this.user_;
	}

	public String getHost() {
		return this.host_;
	}

	public String getChannel() {
		return this.channel_;
	}

}
