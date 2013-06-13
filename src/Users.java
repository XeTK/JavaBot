import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.User;
import addons.UserList;

import plugin.PluginTemp;
import program.IRC;
import program.IRCException;
import program.JSON;


public class Users implements PluginTemp
{

	private final String dbFile = "Users.json";
	@Override
	public void onCreate(String in_str) throws IRCException, IOException
	{
		System.out.println("\u001B[37mUsers Plugin Loaded");
		if (new File(dbFile).exists())
			UserList.setInstance((UserList)JSON.loadGSON(dbFile, UserList.class));
		else
			JSON.saveGSON(dbFile, UserList.getInstance());
	}

	@Override
	public void onTime(String in_str) throws IRCException, IOException {}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		Matcher m = 
				Pattern.compile(":(.*)!.*@(.*) PRIVMSG (#.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
		if (m.find())
		{
			String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
			UserList.getInstance().msgSent(user, host);
		}
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		Matcher m = 
		    		Pattern.compile(":(.*)!.*@(.*) JOIN :(#?.*)",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	    	String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3);
	    	User userOBJ = UserList.getInstance().getUser(user);
	    	if (userOBJ != null)
	    		userOBJ.incjoins(host);
	    	else
	    		IRC.getInstance().sendServer("PRIVMSG " + channel + " Hello " + user + ", Nice to see a new user arround here. Welcome and dont break things!");
	    }
	}

	@Override
	public void onQuit(String in_str) throws IRCException, IOException
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		
		Matcher m = 
		    		Pattern.compile(":(.*)!\\(.*@.*) PART (#.*)",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	    	String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3);
	    	User userOBJ = UserList.getInstance().getUser(user);
	    	if (userOBJ != null)
	    		userOBJ.incQuits();
	    }
	}

}
