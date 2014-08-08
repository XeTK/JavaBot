package plugin.stats.user;

import java.util.ArrayList;
import java.util.Date;

/**
 * This is a data class to hold all relevant information about a IRC User.
 * 
 * @author Tom Rosier (XeTK)
 */
public class User {

	// Other data members about the user.
	private String user_  = new String();
	private String host_  = new String();
	private String email_ = new String();

	private long msgSent_ = 0;
	private long joins_   = 0;
	private long quits_   = 0;
	private long kicks_   = 0;

	private Date lastOnline_ = new Date();

	private boolean isDirty_ = false;

	private byte[] encyptedPasswordHash_ = new byte[16];

	// List of reminders and quotes tied to a average user.
	private ArrayList<String> quotes_    = new ArrayList<String>();
	private ArrayList<String> reminders_ = new ArrayList<String>();

	/**
	 * Set the username and host on creation of the class.
	 * 
	 * @param user this is the username of the user.
	 * @param host this is the address of the user.
	 */
	public User(String user, String host) {
		this.user_ = user;
		this.host_ = host;
	}

	/**
	 * This is the method to increment the number of messages sent by a user. It
	 * also updates the time the user was last seen around the IRC session.
	 * 
	 * @param host this is the address the user is working from.
	 */
	public void incMsgSent(String host) {
		msgSent_++;
		lastOnline_ = new Date();
		checkDirtyness(host);
	}

	/**
	 * Quick method to increment the number of quits a user has performed
	 */
	public void incQuits() {
		quits_++;
	}

	/**
	 * Quick method to increment the number of times a user has been kicked
	 */
	public void incKicks() {
		kicks_++;
	}

	/**
	 * Simple method to increment the number of joins a user has done.
	 * 
	 * @param host this is the address of the user is using, it needs to be
	 *             passed to the dirty check
	 */
	public void incjoins(String host) {
		joins_++;
		checkDirtyness(host);
	}

	/**
	 * Check if the user has any drastic problems, E.G. host change or over due reminders.
	 * 
	 * @param host is the user's host address so we can check if it has changed.
	 */
	private void checkDirtyness(String host) {
		if (reminders_.size() > 0)
			isDirty_ = true;
		else
			isDirty_ = false;
	}

	/**
	 * Return a list of current reminders for the user, and also reset list
	 * ready for any new reminders that may be added.
	 * 
	 * @return's list of reminders for the user to view.
	 */
	public String[] getReminders() {
		String[] reminders = reminders_.toArray(new String[0]);
		reminders_ = new ArrayList<String>();
		return reminders;
	}

	public void removeQuote(String message) {
		for (int i = 0; i < quotes_.size(); i++)
			if (quotes_.get(i).equals(message))
				quotes_.remove(i);
	}

	// Return all the users quotes as an array converted from the ArrayList.
	public String[] getQuotes() {
		return quotes_.toArray(new String[0]);
	}

	// Add a new quote to a user.
	public void addQuote(String message) {
		quotes_.add(message);
	}

	// Add a new reminder for the user
	public void addReminder(String message) {
		reminders_.add(message);
	}

	// Remove a quote from a user
	public void delQuote(String message) {
		quotes_.remove(message);
	}

	// Set user id.
	public void setUser(String user) {
		this.user_ = user;
	}

	public void setEncyptedPasswordHash(byte[] encyptedPasswordHash) {
		this.encyptedPasswordHash_ = encyptedPasswordHash;
	}

	public void setEmail(String email) {
		this.email_ = email;
	}

	// Getters.
	public String getUser() {
		return user_;
	}

	public long getMsgSent() {
		return msgSent_;
	}

	public Date getLastOnline() {
		return lastOnline_;
	}

	public long getJoins() {
		return joins_;
	}

	public long getQuits() {
		return quits_;
	}

	public boolean isDirty() {
		return isDirty_;
	}

	public long getKicks() {
		return kicks_;
	}

	public byte[] getEncryptedPasswordHash() {
		return encyptedPasswordHash_;
	}

	public String getEmail() {
		return email_;
	}
}
