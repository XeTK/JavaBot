package plugin.stats.user;

import java.util.ArrayList;

import core.event.Message;
import core.plugin.Plugin;

/**
 * This Class encapsulate users objects and also interacts with them.
 * 
 * @author Tom Rosier(XeTK)
 */
public class UserList extends Plugin {

	// Keep a list of the users that are members of the channel
	private ArrayList<User> users_ = new ArrayList<User>();

	/**
	 * This returns the user object for a given username.
	 * 
	 * @param user
	 *            this is the username of the user as a string.
	 * @return's either null as it couldn't find the user or a user object for a
	 *           user of the name defined by user.
	 */
	public User getUser(String user) {
		for (int i = 0; i < users_.size(); i++)
			if (users_.get(i).getUser().equalsIgnoreCase(user))
				return users_.get(i);
		return null;
	}

	/**
	 * This updates the number of message that a user sends, Along with passing
	 * in the latest host address to check if the user, host has changed and
	 * this can be marked.
	 * 
	 * @param in_msg
	 *            this is the message object being passed into the class
	 */
	public void msgSent(Message in_msg) {
		String user = in_msg.getUser();
		String host = in_msg.getHost();

		User tempUser = getUser(user);

		if (tempUser != null)
			tempUser.incMsgSent(host);
		else
			users_.add(new User(user, host));
	}

	/**
	 * Gets the dirty state of a user.
	 * 
	 * @param user
	 *            this is the user we want to get the dirty state for.
	 * @return's a boolean of dirty state of the user.
	 */
	public boolean isUserDirty(String user) {
		return getUser(user).isDirty();
	}

	/**
	 * Checks if the user we are working with is new to the system.
	 * 
	 * @param user
	 *            this is the user we want to check if exists.
	 * @return's boolean value saying if a user exists.
	 */
	public boolean isNewUser(String user) {
		if (getUser(user) != null)
			return false;
		return true;
	}

	/**
	 * This adds a reminder to a user, along with creating a new user if one
	 * Doesn't already exist, the reminder has a message tagged along with it.
	 * 
	 * @param user
	 *            take in the user we want to add the reminder to.
	 * @param message
	 *            this is the message tagged on with the reminder
	 */
	public void addReminder(String user, String message) {
		User tempUser = getUser(user);

		// Checks if user exists or not.
		if (tempUser != null) {
			tempUser.addReminder(message);
		} else {
			// Add a new user if they are not already in the system.
			User newUser = new User(user, "");
			newUser.addReminder(message);
			users_.add(newUser);
		}
	}

	/**
	 * This gets all the quotes tied to a user.
	 * 
	 * @param user
	 *            this is the user we want to return the quotes from.
	 * @return's an array of quotes for the given user.
	 */
	public String[] getQuotes(String user) {
		return getUser(user).getQuotes();
	}

	/**
	 * This method adds a new quote to the users library of quotes.
	 * 
	 * @param user
	 *            this is the user we want to add the quote to.
	 * @param message
	 *            this is the quote we want to add to them
	 */
	public void addQuote(String user, String message) {
		User tempUser = getUser(user);

		// Check if the user exists.
		if (tempUser != null) {
			tempUser.addQuote(message);
		} else {
			// If the user dosnt exist in the sytem then we add them to it.
			User newUser = new User(user, "");
			newUser.addQuote(message);
			users_.add(newUser);
		}
	}

	/**
	 * If the user doesn't like the quote then they can remove it with this
	 * method
	 * 
	 * @param message
	 *            this is the quote we want to remove.
	 */
	public void removeQuote(String message) {
		for (int i = 0; i < users_.size(); i++)
			users_.get(i).removeQuote(message);
	}

	@Override
	public String getHelpString() {
		// TODO Auto-generated method stub
		return "";
	}
}
