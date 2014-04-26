package core.event;

import java.util.regex.Matcher;

/**
 * A Quit Event
 *
 * @author Tom Rosier(XeTK)
 */
public class Quit {
	private String user_     = new String();
	private String host_     = new String();
	private String channel_  = new String();
	private String exitType_ = new String();
	private String message_  = new String();

	/**
	 * Constructor
	 * 
	 * @param m the Matcher object
	 */
	public Quit(Matcher m) {
		this.user_        = m.group(1);
		this.host_        = m.group(2);
		this.exitType_    = m.group(3);

		if (exitType_.equals("PART")) {
			this.channel_ = m.group(4);
		}

		this.message_     = m.group(5);
	}

	public String getUser() {
		return this.user_;
	}

	public String getHost() {
		return this.host_;
	}

	public String getChannel() {
		return this.channel_;
	}

	public String getExitType() {
		return this.exitType_;
	}

	public String getMessage() {
		return this.message_;
	}

}
