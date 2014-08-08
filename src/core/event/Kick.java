package core.event;

import java.util.regex.Matcher;

/**
 * The Kick event
 *
 * @author Tom Rosier
 */
public class Kick {
	private String kicker_  = new String();
	private String host_    = new String();
	private String channel_ = new String();
	private String kicked_  = new String();
	private String message_ = new String();

	/**
	 * Constructor
	 */
	public Kick(Matcher m) {
		this.kicker_  = m.group(1);
		this.host_    = m.group(2);
		this.channel_ = m.group(3);
		this.kicked_  = m.group(4);
		this.message_ = m.group(5);
	}

	public String getKicker() {
		return this.kicker_;
	}

	public String getHost() {
		return this.host_;
	}

	public String getChannel() {
		return this.channel_;
	}

	public String getKicked() {
		return this.kicked_;
	}

	public String getMessage() {
		return this.message_;
	}

}
