import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.Details;
import core.utils.IRC;

/**
 * Handles admin tasks
 * - joining channels
 * - bot nick change
 * - start/stop bot
 * - send raw data to server
 * - reload plugins
 * - mode changes
 * @author Tom Rosier(XeTK)
 */
public class Admin implements Plugin
{
	private Channel uchannel;
	/**
	 * Get the name of the plugin so it can be presented to the end user.
	 */
	@Override
	public String name() 
	{
		return "Admin";
	}
	
	@Override
	public void onCreate(Channel in_channel) throws Exception 
	{
		this.uchannel = in_channel;
	}
	/**
	 * Process the new message and check if it is bound to a specific admin command.
	 */
	@Override
	public void onMessage(Message in_message) throws Exception
	{
		if (in_message.isPrivMsg())
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
				else if (message.matches("^\\.part [A-Za-z0-9#]+$"))
				{
					String str[] = message.split(" ");
					irc.sendServer("PART " + str[1]);
					irc.sendPrivmsg(channel, "I Have Parted " + str[1]);
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
				else if (message.matches("^\\.exception"))
				{
					irc.sendPrivmsg(channel, "Exception Thrown.");
					throw new Exception();
				}
				else if(message.matches("^\\.reload"))
				{
					irc.sendPrivmsg(channel, "Reloading from git");
				    String pid = ManagementFactory.getRuntimeMXBean().getName();  
				    String[]Ids = pid.split("@"); 
				    Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", "./git.sh " + Ids[0]});
				}
				else if (message.matches("^\\.loaded"))
				{
					String loaded = PluginCore.loadedPlugins(uchannel.getPlugins());
					String loadedString = "Plugins Loaded, %s";
					loadedString = String.format(loadedString, loaded);
					irc.sendPrivmsg(channel, loadedString);
				}
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
	public String getHelpString()
	{
		// TODO Auto-generated method stub
		return "ADMIN: " +
						".join #* - Join Channel : " +
						".quit - Kill Bot : " +
						".nick ** - Change Bot's Nick : " +
						".help - Show Help Text : " +
						".loaded - Returns list of loaded plugins : " +
						".reload - Reloads plugins from directory :";
	} 
	@Override
	public void onKick(Kick in_kick) throws Exception {}
	@Override
	public void onTime() throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
	@Override
	public void rawInput(String in_str) throws Exception{}	
	
}
