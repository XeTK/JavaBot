import java.io.File;
import java.io.IOException;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import addons.User;
import addons.UserList;
import plugin.PluginTemp;
import program.IRC;
import program.IRCException;
import program.JSON;
import program.Details;


public class Users implements PluginTemp
{

	private final String dbFile = "Users.json";
	
	@Override
	public String name() 
	{
		return "Users";
	}
	
	@Override
	public void onCreate() throws Exception
	{
		if (new File(dbFile).exists())
			UserList.setInstance((UserList)JSON.loadGSON(dbFile, UserList.class));
		else
			JSON.saveGSON(dbFile, UserList.getInstance());
	}

	@Override
	public void onMessage(Message in_message) throws IRCException, IOException
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		UserList.getInstance().msgSent(in_message.getUser(), in_message.getHost());
	}

	@Override
	public void onJoin(Join in_join) throws Exception
	{
		JSON.saveGSON(dbFile, UserList.getInstance());
		IRC irc = IRC.getInstance();
    	User userOBJ = UserList.getInstance().getUser(in_join.getUser());
    	if (userOBJ != null)
    	{
    		userOBJ.incjoins(in_join.getHost());
    		irc.sendPrivmsg(in_join.getChannel(), in_join.getUser() + " Has joined " + userOBJ.getJoins() + " times.");
    	}
    	else
			if (!Details.getIntance().getNickName().toLowerCase().equals(in_join.getUser()))
				irc.sendPrivmsg(in_join.getChannel(), "Hello " + in_join.getUser() + ", Nice to see a new user arround here. Welcome and dont break things!");
	}

	@Override
	public void onQuit(Quit in_quit) throws Exception
	{
		IRC irc = IRC.getInstance();
		JSON.saveGSON(dbFile, UserList.getInstance());

    	User userOBJ = UserList.getInstance().getUser(in_quit.getUser());
    	if (userOBJ != null)
    	{
    		userOBJ.incQuits();
    		irc.sendPrivmsg(in_quit.getChannel(), in_quit.getUser() + " Has Left " + userOBJ.getQuits() + " times.");
    	}
	}

    @Override
    public void onKick(Kick in_kick) throws Exception 
	{
    	IRC irc = IRC.getInstance();

		User userOBJ = UserList.getInstance().getUser(in_kick.getKicked());
    	if (userOBJ != null)
    	{
    		userOBJ.incKicks();
    		irc.sendPrivmsg(in_kick.getChannel(),in_kick.getKicked() + " Has been kicked " + userOBJ.getKicks() + " times.");
    	}
	}

	@Override
	public void onTime() throws Exception {}
	@Override
	public void onOther(String in_str) throws Exception {}
}
