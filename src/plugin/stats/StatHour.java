package plugin.stats;

import java.util.ArrayList;

/**
 * This holds the stats for the hour in time. This is encapsulated by StatDay.
 * 
 * @author Tom Rosier (XeTK)
 */
public class StatHour {
	/*
	 * Keep a list of the users from the last hour and how many messages they
	 * have sent, this is useful for leader board stats.
	 */
	private ArrayList<StatUser> users_ = new ArrayList<StatUser>();

	// Hold the other stats for the hour.
	private int joins_ = 0;
	private int quits_ = 0;
	private int kicks_ = 0;
	
	private boolean displayedStats_ = false;

	/**
	 * Quick method to increment the number of users that have joined.
	 */
	public void incJoins() {
		joins_++;
	}

	/**
	 * Quick method to increment the number of users that have left the channel.
	 */
	public void incQuits() {
		quits_++;
	}

	/**
	 * Quick method to increment the number of users that have been kicked.
	 */
	public void incKicks() {
		kicks_++;
	}

	/**
	 * This increment the number of messages a user has sent in the last hour.
	 * 
	 * @param user
	 *            this is the user that needs to there total incrementing.
	 */
	public void incMsgSent(String user) {
		// Keep a flag to see if the user has been found.
		boolean userExists = false;
		// Loop through all the users that we already have.
		for (int i = 0; i < users_.size(); i++) {
			// If the user we are incrementing over equals the user we want.
			if (users_.get(i).getUsername().equalsIgnoreCase(user)) {
				// Increment the messages that they have sent.
				users_.get(i).incMsgSent();
				// Set are flag to know that we have found the user.
				userExists = true;
				// Found the user no need to loop through the rest of the loop.
				break;
			}
		}
		// If the user has not been found then we create a new instance of a
		// user
		if (!userExists)
			users_.add(new StatUser(user));
	}

	// Getters
	public int getJoins() {
		return joins_;
	}

	public int getQuits() {
		return quits_;
	}

	public int getKicks() {
		return kicks_;
	}

	// This totals the number of messages sent in the last hour.
	public int getMsgSent() {
		int msgs = 0;
		for (int i = 0; i < users_.size(); i++)
			msgs += users_.get(i).getMsgSent();
		return msgs;
	}

	public boolean isDisplayedStats() {
		return displayedStats_;
	}

	public void setDisplayedStats(boolean displayedStats_) {
		this.displayedStats_ = displayedStats_;
	}
}
