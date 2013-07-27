package plugin.users;
import java.io.File;
import java.io.IOException;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;

public class Users extends Plugin
{
	private final String dbFile_loc = "Users.json";
	
	private final String new_user = "Hello %s and welcome! Don't break things!";
	private final String user_joined = "%s Has joined %s times";
	private final String user_quit = "%s Has quit %s times";
	private final String user_kicked = "%s Has be kicked %s times";
	
	private final IRC irc = IRC.getInstance();
	
	private String dbFile = new String();
		
	public void onCreate(Channel in_channel) throws Exception
	{
		dbFile = in_channel.getPath() + dbFile_loc;
		if (new File(dbFile).exists())
			UserList.setInstance((UserList)JSON.loadGSON(dbFile,UserList.class));
		else
			JSON.saveGSON(dbFile, UserList.getInstance());
	}

	public void onMessage(Message in_message) throws IRCException, IOException
	{
		if (!in_message.isPrivMsg())
		{
			JSON.saveGSON(dbFile, UserList.getInstance());
			UserList.getInstance().msgSent(in_message);
		}
	}

	public void onJoin(Join in_join) throws Exception
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
    	User userOBJ = UserList.getInstance().getUser(in_join.getUser());
    	if (userOBJ != null)
    	{
    		userOBJ.incjoins(in_join.getHost());
    		irc.sendPrivmsg(in_join.getChannel(), String.format(user_joined, 
    				in_join.getUser(), userOBJ.getJoins()));
    	}
    	else
    	{
    		String botname = Details.getInstance().getNickName();
    		if (!botname.equalsIgnoreCase(in_join.getUser()))
    		{
    			irc.sendPrivmsg(in_join.getChannel(), 
    					String.format(new_user, in_join.getUser()));
    		}
    	}
	}

	public void onQuit(Quit in_quit) throws Exception
	{
		JSON.saveGSON(dbFile, UserList.getInstance());

    	User userOBJ = UserList.getInstance().getUser(in_quit.getUser());
    	if (userOBJ != null)
    	{
    		userOBJ.incQuits();

    		irc.sendPrivmsg(in_quit.getChannel(), String.format(user_quit, 
    				in_quit.getUser(), userOBJ.getQuits()));
    	}
	}

    public void onKick(Kick in_kick) throws Exception 
	{
    	IRC irc = IRC.getInstance();

		User userOBJ = UserList.getInstance().getUser(in_kick.getKicked());
    	if (userOBJ != null)
    	{
    		userOBJ.incKicks();

    		irc.sendPrivmsg(in_kick.getChannel(), String.format(user_kicked,
    				in_kick.getKicked(), userOBJ.getKicks()));
    	}
	}
    
	public String getHelpString()
	{
		return "Users: This plugin does not have any commands.";
	}
}
