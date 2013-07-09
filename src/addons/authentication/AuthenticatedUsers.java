package addons.authentication;

import java.util.ArrayList;

import addons.users.User;

public class AuthenticatedUsers 
{
	private static AuthenticatedUsers ausers;
	
	private ArrayList<User> authenticated_users = new ArrayList<User>();
	
	public static AuthenticatedUsers getInstance()
	{
		if (ausers == null)
			ausers = new AuthenticatedUsers();
		return ausers;
	}
	
	public void add(User user)
	{
		authenticated_users.add(user);
	}
	
	public void remove(User user)
	{
		authenticated_users.remove(user);
	}
	
	public boolean contains(User user)
	{
		return authenticated_users.contains(user);
	}
}
