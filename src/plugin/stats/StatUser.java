package plugin.stats;

/**
 * This is a data class for statistic collecting. It holds the details to
 * determine an user and there activity.
 * 
 * @author Tom Rosier (XeTK)
 */
public class StatUser {
	// Hold the essential details.
	private String username_ = new String();
	private int msgSent = 1;

	// Set the class up on creation.
	public StatUser(String username) {
		this.username_ = username;
	}

	// Quick method to increment the count of the messages sent.
	public void incMsgSent() {
		msgSent++;
	}

	// Getters
	public String getUsername() {
		return username_;
	}

	public int getMsgSent() {
		return msgSent;
	}

}
