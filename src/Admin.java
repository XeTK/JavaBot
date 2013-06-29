import java.util.regex.Matcher;
import java.util.regex.Pattern;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;

import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.Start;

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
		Details details = Details.getIntance();
		
		String message = in_message.getMessage(), user = in_message.getUser(), channel = in_message.getChannel();
        for (int i = 0;i < details.getAdmins().length;i++)
		{
			if (user.equals(details.getAdmins()[i]))
			{
		        if (message.charAt(message.length() - 1 ) == ' ')
		        	message = message.substring(0, message.length() -1);
				
				if (message.matches("^\\.join [A-Za-z0-9#]+$"))
				{
					String str[] = message.split(" ");
					irc.sendServer("JOIN " + str[1]);
					irc.sendServer("PRIVMSG " + channel + " I Have Joined " + str[1]);
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
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
					if (p.find())
						irc.sendServer(p.group(1));
				}
				else if (message.matches("^\\.loaded"))
				{
					irc.sendServer("PRIVMSG " + channel + " Plugins Loaded : " + Start.getInstance().loadedPlugins());
				}
				else if(message.matches("^\\.reload"))
				{
					irc.sendServer("PRIVMSG " + channel + " Reloading plugins");
					try
					{
						Start.getInstance().reloadPlugins();
					}
					catch (Exception ex){}
					irc.sendServer("PRIVMSG " + channel + " Plugins Loaded : " + Start.getInstance().loadedPlugins());
				}
				else if(message.matches("^\\.help") || message.matches("^\\."))
				{
					irc.sendServer("PRIVMSG " + channel + " ADMIN: " +
							".join #* - Join Channel : " +
							".quit - Kill Bot : " +
							".nick ** - Change Bot's Nick : " +
							".help - Show Help Text : " +
							".loaded - Returns list of loaded plugins : " +
							".reload - Reloads plugins from directory :"
							);
				}
				break;
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

		if (in_kick.getKicked().equals(Details.getIntance().getNickName()))
		{
			irc.sendServer("JOIN " + in_kick.getChannel()); 
			irc.sendServer("PRIVMSG " + in_kick.getChannel() + " Dont kick me!! " + in_kick.getKicked() + "... bad bad bad person!");
		}
	}
	
	@Override
	public void onCreate() throws Exception {}
	@Override
	public void onTime() throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
}
