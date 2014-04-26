package core.event;

import java.util.Date;
import java.util.regex.Matcher;

/**
 * A Message Event
 *
 * @author Tom Rosier (XeTK)
 */
public class Message {
	private Date    date_      = new Date();
	private String  user_      = new String();
	private String  host_      = new String();
	private String  channel_   = new String();
	private String  message_   = new String();
	private boolean isPrivMsg_ = false;

	/**
	 * Constructor
	 *
	 * @param m the Matcher object
	 */
	public Message(Matcher m) {
		this.host_    = m.group(2);

		String user    = m.group(1);
		String channel = m.group(3);
		String message = m.group(4);

		this(user, channel, message);
	}

	public Message(String username, String channel, String message) {
		this.date_    = new Date();

		this.user_    = username;
		this.channel_ = channel;
		this.message_ = message;

		if (!channel_.startsWith("#") && !channel_.startsWith("&")) {
			self.isPrivMsg_ = true;
			self.channel_   = user_;
		}
	}

	public Date getDate() {
		return this.date_;
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

	public String getMessage() {
		return this.message_;
	}

	public boolean isPrivMsg() {
		return this.isPrivMsg_;
	}
}
