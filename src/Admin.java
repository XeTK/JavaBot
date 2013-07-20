import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import core.utils.Details;
import core.utils.IRC;
/**
 * This is a plugin, to help with management of the irc bot its self, it
 * handles tasks like joining channel's, hot nickname change, 
 * killing the execution of the bot, it will also allow direct manipulation by
 * a admin to send messages to the IRC server, it can also reload plugins.
 * It can also deal with voicing members on join along with handling the bot being kicked.
 * @author Tom Rosier(XeTK)
 */
public class Admin implements PluginTemp
{
	/**
	 * Get the name of the plugin so it can be presented to the end user.
	 */
	@Override
	public String name() 
	{
		return "Adminstration";
	}
	
	/**
	 * Process the new message and check if it is bound to a specific admin command.
	 */
	@Override
	public void onMessage(Message in_message) throws Exception
	{
		// Get the IRC instance so that we can send commands to the server.
		IRC irc = IRC.getInstance();
		// Get the details for the bot so we can check if the user issuing the command is a admin.
		Details details = Details.getInstance();
		
		// Information about the message that is sent.
		String message = in_message.getMessage();
		String user = in_message.getUser();
		String channel = in_message.getChannel();
		
		// Check if the user is an admin and is aloud to issues theses commands.
		if (details.isAdmin(user))
		{
			// Remove padding at the end of message. To stop any issues.
	        if (message.charAt(message.length() - 1 ) == ' ')
	        	message = message.substring(0, message.length() -1);
			
	        // If we want to join a channel then we access this command.
			if (message.matches("^\\.join [A-Za-z0-9#]+$"))
			{
				String str[] = message.split(" ");
				irc.sendServer("JOIN " + str[1]);
				irc.sendPrivmsg(channel, "I Have Joined " + str[1]);
			}
			else if (message.matches("^\\.quit"))
			{
				irc.sendServer("QUIT Goodbye All!");
				System.exit(0);
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
