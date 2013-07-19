import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.IRCException;
import core.plugin.PluginTemp;
import core.utils.Details;
import core.utils.IRC;

public class Admin implements PluginTemp
{
	@Override
	public String name() 
	{
		return "Adminstration";
	}
	
	@Override
	public void onMessage(Message in_message) throws Exception
	{
		IRC irc = IRC.getInstance();
		Details details = Details.getInstance();
		
		String message = in_message.getMessage(), 
				user = in_message.getUser(), 
				channel = in_message.getChannel();
		
		if (details.isAdmin(user))
		{
	        if (message.charAt(message.length() - 1 ) == ' ')
	        	message = message.substring(0, message.length() -1);
			
			if (message.matches("^\\.join [A-Za-z0-9#]+$"))
			{
				String str[] = message.split(" ");
				irc.sendServer("JOIN " + str[1]);
				irc.sendPrivmsg(channel, "I Have Joined " + str[1]);
			}
			else if (message.matches("^\\.quit"))
			{
				irc.sendServer("QUIT Goodbye All!");
			}
			else if(message.matches("^\\.nick [A-Za-z0-9#]+$"))
			{
				String str[] = message.split(" ");
				irc.sendServer("NICK "+ str[1]);
			}
			else if(message.matches("^\\.cmd .*"))
			{
				Matcher p = Pattern.compile("^\\.cmd (.*)", 
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(message);
				
				if (p.find())
					irc.sendServer(p.group(1));
			}
			/*else if (message.matches("^\\.loaded"))
			{
				irc.sendPrivmsg(channel,
						"Plugins Loaded : " + pc.loadedPlugins());
			}
			else if(message.matches("^\\.reload"))
			{
				irc.sendPrivmsg(channel, "Reloading plugins");
				
				pc.reloadPlugins();
				
				irc.sendPrivmsg(channel, 
						"Plugins Loaded : " + pc.loadedPlugins());
			}*/
			else if(message.matches("^.exception"))
			{
				irc.sendPrivmsg(channel, "I like throwing exceptions");
				throw new IRCException("Called by user");
			}
			else if(message.matches("^\\.help") || message.matches("^\\."))
			{
				irc.sendPrivmsg(channel, "ADMIN: " +
						".join #* - Join Channel : " +
						".quit - Kill Bot : " +
						".nick ** - Change Bot's Nick : " +
						".help - Show Help Text : " +
						".loaded - Returns list of loaded plugins : " +
						".reload - Reloads plugins from directory :"
						);
			}
		}
	}
	

	@Override
	public void onJoin(Join in_join) throws Exception
	{
		IRC irc = IRC.getInstance();
	    irc.sendServer("MODE " + in_join.getChannel() + " +v " + in_join.getUser());
	}

	@Override
	public void onKick(Kick in_kick) throws Exception 
	{
		IRC irc = IRC.getInstance();

		if (in_kick.getKicked().equals(Details.getInstance().getNickName()))
		{
			irc.sendServer("JOIN " + in_kick.getChannel()); 
			irc.sendPrivmsg(in_kick.getChannel(), 
					"Dont kick me!! " + in_kick.getKicked() + "... bad person!");
		}
	}
	
	
	@Override
	public void onCreate() throws Exception {}
	@Override
	public void onTime() throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
}
