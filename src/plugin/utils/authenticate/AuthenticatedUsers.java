package plugin.utils.authenticate;

import java.util.ArrayList;

import plugin.stats.user.User;

/**
 * This holds a list of the currently authenticated users active to the bot, it
 * uses the Singleton pattern to insure its accessible throughout the
 * application. It has the functionality to manipulate the data held within the
 * list of users.
 * 
 * @author Tom Rosier(XeTK)
 */
public class AuthenticatedUsers {
	// Hold the instance of the class so its accessible for the singleton
	// pattern.
	private static AuthenticatedUsers ausers_;

	// Hold a list of the authenticated user so they can be worked with later.
	private ArrayList<User> authenticatedUsers = new ArrayList<User>();

	/**
	 * Get the instance of the object so it can be manipulated from another
	 * place, if there is not already an instance of the class then we create
	 * one.
	 */
	public static AuthenticatedUsers getInstance() {
		if (ausers_ == null)
			ausers_ = new AuthenticatedUsers();
		return ausers_;
	}

	/**
	 * This adds a new authenticated user to the list for later use.
	 * 
	 * @param user
	 *            this is the user object of the user that we want to add.
	 */
	public void add(User user) {
		authenticatedUsers.add(user);
	}

	/**
	 * This removes an authenticated user when they either quit or logout.
	 * 
	 * @param user
	 *            this is the user object of the user we want to remove.
	 */
	public void remove(User user) {
		authenticatedUsers.remove(user);
	}

	/**
	 * If we want to check if an user is already authenticated, we call this
	 * method and it returns a true or false saying if the user is
	 * authenticated.
	 * 
	 * @param user
	 *            this is the object of the user we want to check the status of.
	 * @return's the status of the user, if they are already authenticated or
	 *           not.
	 */
	public boolean contains(User user) {
		return authenticatedUsers.contains(user);
	}
}
