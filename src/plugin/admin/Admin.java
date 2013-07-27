package plugin.admin;
import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Channel;
import core.event.Join;
import core.event.Message;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.Colour;
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
public class Admin extends Plugin
{
	private Channel uchannel;
	/**
	 * Get the name of the plugin so it can be presented to the end user.
	 */
	
	public void onCreate(Channel in_channel) throws Exception 
	{
		this.uchannel = in_channel;
	}
	
	/**
	 * Process the new message and check if it is bound to a specific admin command.
	 */
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
		if (in_message.isPrivMsg())
		{	
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
					String Msg = Colour.colour("Reloading from git!", 
							Colour.RED, Colour.WHITE);
					
					irc.sendPrivmsg(channel, Msg);
				    String pid = ManagementFactory.getRuntimeMXBean().getName();  
				    String[]Ids = pid.split("@"); 
				    Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", "./git.sh " + Ids[0]});
				}
			}
		}
		else
		{
			if (message.matches("^\\.loaded"))
			{
				String loaded = PluginCore.loadedPlugins(uchannel.getPlugins());
				loaded = Colour.colour(loaded, Colour.BLUE,Colour.WHITE);
				String loadedString = "Plugins Loaded: %s";
				loadedString = String.format(loadedString, loaded);
				irc.sendActionMsg(channel, loadedString);
			}
		}
	}

	public void onJoin(Join in_join) throws Exception
	{
		IRC irc = IRC.getInstance();
	    irc.sendServer("MODE " + in_join.getChannel() + " +v " + in_join.getUser());
	}

	public String getHelpString()
	{
		// TODO Auto-generated method stub
		return "ADMIN: \n" +
						".join #* - Join Channel : \n" +
						".quit - Kill Bot : \n" +
						".nick ** - Change Bot's Nick : \n" +
						".help - Show Help Text : \n" +
						".loaded - Returns list of loaded plugins : \n" +
						".reload - Reloads plugins from directory :";
	} 
}
