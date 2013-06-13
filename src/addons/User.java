package addons;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is a data class to hold all relevant information about a IRC User.
 * @author Tom Rosier (XeTK)
 */
public class User
{
	
	// Other data members about the user.
	private String user, host;
	private long msgSent, joins, quits;
	private Date lastOnline;
	private boolean isDirty = false;
	
	// List of reminders and quotes tied to a average user.
	private ArrayList<String> quotes = new ArrayList<String>();
	private ArrayList<String> reminders = new ArrayList<String>();
	
	/**
	 * Set the username and host on creation of the class.
	 * @param user this is the username of the user.
	 * @param host this is the address of the user.
	 */
	public User(String user, String host)
	{
		this.user = user;
		this.host = host;
	}
	
	/**
	 * This is the method to increment the number of messages sent by a user.
	 * It also updates the time the user was last seen around the IRC session.
	 * @param host this is the address the user is working from.
	 */
	public void incMsgSent(String host)
	{
		msgSent++;
		lastOnline = new Date();
		checkDirtyness(host);
	}
	
	/**
	 * Quick method to increment the number of quits a user has performed
	 */
	public void incQuits()
	{
		quits++;
	}
	
	/**
	 * Simple method to increment the number of joins a user has done.
	 * @param host this is the address of the user is using, 
	 * it needs to be passed to the dirty check
	 */
	public void incjoins(String host)
	{
		joins++;
		checkDirtyness(host);
	}
	
	/**
	 * Check if the user has any drastic problems, E.G. host change or over due 
	 * reminders. 
	 * @param host is the user's host address so we can check if it has changed.
	 */
	private void checkDirtyness(String host)
	{
		if (reminders.size() > 0)
			isDirty = true;
		else
			isDirty = false;
		
		if (!this.host.equals(host))
		{
			this.host = host;
			isDirty = true;
		}
	}
	
	/**
	 * Return a list of current reminders for the user, and also reset list 
	 * ready for any new reminders that may be added.
	 * @return's list of reminders for the user to view.
	 */
	public String[] getReminders()
	{
		String[] reminderss = reminders.toArray(new String[0]);
		reminders = new ArrayList<String>();
		return reminderss;
	}
	
	// Return all the users quotes as an array converted from the ArrayList.
	public String[] getQuotes()
	{
		return quotes.toArray(new String[0]);
	}
	
	// Add a new quote to a user.
	public void addQuote(String message)
	{
		quotes.add(message);
	}
	
	// Add a new reminder for the user
	public void addReminder(String message)
	{
		reminders.add(message);
	}
	
	// Remove a quote from a user
	public void delQuote(String message)
	{
		quotes.remove(message);
	}
	
	// Set user id.
	public void setUser(String user)
	{
		this.user = user;
	}
	
	// Getters.
	public String getUser()
	{
		return user;
	}
	
	public long getMsgSent()
	{
		return msgSent;
	}
	
	public Date getLastOnline()
	{
		return lastOnline;
	}
	
	public long getJoins()
	{
		return joins;
	}
	
	public long getQuits()
	{
		return quits;
	}
	
	public boolean isDirty()
	{
		return isDirty;
	}
	
}