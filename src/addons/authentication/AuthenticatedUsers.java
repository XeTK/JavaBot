package addons.authentication;

import java.util.ArrayList;

import addons.users.User;
/**
 * This holds a list of the currently authenticated users active to the bot,
 * it uses the Singleton pattern to insure its accessible throughout the application.
 * It has the functionality to manipulate the data held within the list of users.
 * @author Tom Rosier(XeTK)
 */
public class AuthenticatedUsers 
{
	// Hold the instance of the class so its accessible for the singleton pattern.
	private static AuthenticatedUsers ausers;
	
	// Hold a list of the authenticated user so they can be worked with later.
	private ArrayList<User> authenticated_users = new ArrayList<User>();
	
	/**
	 *  Get the instance of the object so it can be manipulated from another 
	 *  place, if there is not already an instance of the class then we create one.
	 */
	public static AuthenticatedUsers getInstance()
	{
		if (ausers == null)
			ausers = new AuthenticatedUsers();
		return ausers;
	}
	
	/**
	 * This adds a new authenticated user to the list for later use.
	 * @param user this is the user object of the user that we want to add.
	 */
	public void add(User user)
	{
		authenticated_users.add(user);
	}
	
	/**
	 * This removes an authenticated user when they either quit or logout.
	 * @param user this is the user object of the user we want to remove.
	 */
	public void remove(User user)
	{
		authenticated_users.remove(user);
	}
	
	/**
	 * If we want to check if an user is already authenticated, we call this method
	 * and it returns a true or false saying if the user is authenticated.
	 * @param user this is the object of the user we want to check the status of.
	 * @return's the status of the user, if they are already authenticated or not.
	 */
	public boolean contains(User user)
	{
		return authenticated_users.contains(user);
	}
}
