package core.event;

import java.util.regex.Matcher;

/**
 * This is a data class for a kicked user. This class is here for encapsulation
 * purposes.
 * 
 * @author Tom Rosier
 */
public class Kick {
	// Global Variables tied to a kicked user
	private String kicker_ = new String();
	private String host_ = new String();
	private String channel_ = new String();
	private String kicked_ = new String(); 
	private String message_ = new String();

	/**
	 * Default constructor that takes in are Regex and parses it out into
	 * various strings.
	 * 
	 * @param m
	 */
	public Kick(Matcher m) {
		kicker_ = m.group(1);
		host_ = m.group(2);
		channel_ = m.group(3);
		kicked_ = m.group(4);
		message_ = m.group(5);
	}

	// Getters
	public String getKicker() {
		return kicker_;
	}

	public String getHost() {
		return host_;
	}

	public String getChannel() {
		return channel_;
	}

	public String getKicked() {
		return kicked_;
	}

	public String getMessage() {
		return message_;
	}

}
