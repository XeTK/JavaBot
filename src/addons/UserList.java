package addons;
import java.util.ArrayList;


public class UserList
{
	private static UserList userList;
	
	private ArrayList<User> users = new ArrayList<User>();
	
	public static UserList getInstance()
	{
		if (userList == null)
			userList = new UserList();
		return userList;
	}
	public static void setInstance(UserList instance)
	{
		userList = instance;
	}
	
	public User getUser(String user)
	{
		for (int i = 0; i < users.size();i++)
			if (users.get(i).getUser().equals(user))
				return users.get(i);
		return null;
	}
	
	public void msgSent(String user, String host)
	{
		boolean exists = false;
		for (int i = 0; i < users.size(); i++)
		{
			if (users.get(i).getUser().equals(user))
			{
				users.get(i).incMsgSent(host);
				exists = true;
			}
		}
		if (!exists)
			users.add(new User(user,host));
	}
	
	public boolean isUserDirty(String user)
	{
		for (int i = 0; i < users.size();i++)
			if (users.get(i).getUser().equals(user))
				return users.get(i).isDirty();
		return false;
	}
	
	public boolean isNewUser(String user)
	{
		for (int i = 0; i < users.size(); i++)
			if (users.get(i).getUser().equals(i))
				return false;
		return true;
	}
	
	public void addReminder(String user, String message)
	{
		boolean exists = false;
		for (int i = 0; i < users.size();i++)
		{
			if (users.get(i).getUser().equals(user))
			{
				users.get(i).addReminder(message);
				exists = true;
				break;
			}
		}
		if (exists == false)
		{
			User uR = new User(user, "");
			uR.addReminder(message);
			users.add(uR);
		}
	}
	
	public String[] getQuotes(String user)
	{
		for (int i = 0; i < users.size();i++)
			if (users.get(i).getUser().equals(user))
				return users.get(i).getQuotes();
		return null;
	}
	
	public void addQuote(String user, String message)
	{
		boolean exists = false;
		for (int i = 0; i < users.size();i++)
		{
			if (users.get(i).getUser().equals(user))
			{
				users.get(i).addQuote(message);
				exists = true;
				break;
			}
		}
		if (exists == false)
		{
			User uQ = new User(user, "");
			uQ.addQuote(message);
			users.add(uQ);
		}
	}
	public void removeQuote(String message)
	{
		for (int i = 0; i < users.size();i++)
			users.get(i).removeQuote(message);
	}
}
