package addons.users;
import java.util.ArrayList;

import core.event.Message;

/**
 * This Class encapsulate users objects and also interacts with them.
 * @author Tom Rosier(XeTK)
 */
public class UserList
{
	// Keep instance of class for singleton methodology
	private static UserList userList;
	
	// Keep a list of the users that are members of the channel
	private ArrayList<User> users = new ArrayList<User>();
	
	/**
	 * Static method to return a instance of the class for the singleton methodology.
	 * @return's and instance of the class that can then be manipulated.
	 */
	public static UserList getInstance()
	{
		if (userList == null)
			userList = new UserList();
		return userList;
	}
	
	/**
	 * If we don't want the instance we have we can set the 
	 * singleton to one that already exists, this is useful for when setting
	 * the reference from a JSON file.
	 * @param instance this is the instance of the thing we want to set this class to.
	 */
	public static void setInstance(UserList instance)
	{
		userList = instance;
	}
	
	/**
	 * This returns the user object for a given username.
	 * @param user this is the username of the user as a string.
	 * @return's either null as it couldn't find the user or a 
	 * user object for a user of the name defined by user. 
	 */
	public User getUser(String user)
	{
		for (int i = 0; i < users.size();i++)
			if (users.get(i).getUser().equalsIgnoreCase(user))
				return users.get(i);
		return null;
	}
	
	/**
	 * This updates the number of message that a user sends,
	 * Along with passing in the latest host address to check if the user,
	 * host has changed and this can be marked.
	 * @param in_msg this is the message object being passed into the class
	 */
	public void msgSent(Message in_msg)
	{
		String user = in_msg.getUser();
		String host = in_msg.getHost();
		
		User usr = getUser(user);
		
		if (usr != null)
			usr.incMsgSent(host);
		else
			users.add(new User(user,host));
	}
	
	/**
	 * Gets the dirty state of a user.
	 * @param user this is the user we want to get the dirty state for.
	 * @return's a boolean of dirty state of the user.
	 */
	public boolean isUserDirty(String user)
	{
		return getUser(user).isDirty();
	}
	
	/**
	 * Checks if the user we are working with is new to the system.
	 * @param user this is the user we want to check if exists.
	 * @return's boolean value saying if a user exists.
	 */
	public boolean isNewUser(String user)
	{
		if (getUser(user) != null)
			return false;
		return true;
	}
	
	/**
	 * This adds a reminder to a user, along with creating a new user if one
	 * Doesn't already exist, the reminder has a message tagged along with it.
	 * @param user take in the user we want to add the reminder to.
	 * @param message this is the message tagged on with the reminder 
	 */
	public void addReminder(String user, String message)
	{
		User usr = getUser(user);
		
		// Checks if user exists or not.
		if (usr != null)
		{
			usr.addReminder(message);
		}
		else
		{
			// Add a new user if they are not already in the system.
			User uR = new User(user, "");
			uR.addReminder(message);
			users.add(uR);
		}
	}
	
	/**
	 * This gets all the quotes tied to a user.
	 * @param user this is the user we want to return the quotes from.
	 * @return's an array of quotes for the given user.
	 */
	public String[] getQuotes(String user)
	{
		return getUser(user).getQuotes();
	}
	
	/**
	 * This method adds a new quote to the users library of quotes.
	 * @param user this is the user we want to add the quote to.
	 * @param message this is the quote we want to add to them
	 */
	public void addQuote(String user, String message)
	{
		User usr = getUser(user);
		
		// Check if the user exists.
		if (usr != null)
		{
			usr.addQuote(message);
		}
		else
		{
			// If the user dosnt exist in the sytem then we add them to it.
			User uQ = new User(user, "");
			uQ.addQuote(message);
			users.add(uQ);
		}
	}
	
	/**
	 * If the user doesn't like the quote then they can remove it with this method
	 * @param message this is the quote we want to remove.
	 */
	public void removeQuote(String message)
	{
		for (int i = 0; i < users.size();i++)
			users.get(i).removeQuote(message);
	}
}
