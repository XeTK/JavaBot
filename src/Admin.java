
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.IRCException;
import program.Start;

public class Admin implements PluginTemp
{
	@Override
	public void onCreate(String in_str) throws IRCException, IOException {}
	
	@Override
	public void onTime(String in_str) throws IRCException, IOException {}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		IRC irc = IRC.getInstance();
		Details details = Details.getIntance();
		
	    Matcher m = 
	    		Pattern.compile(":(.*)!.*@(.*) PRIVMSG (#.*) :(.*)",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	        String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
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
	}
	

	@Override
	public void onJoin(String in_str) throws IRCException, IOException
	{
		IRC irc = IRC.getInstance();
	    Matcher m = 
	    		Pattern.compile(":(.*)!.*@(.*) JOIN :(#.*)",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	    	String user = m.group(1), host = m.group(2), channel = m.group(3);
	    	irc.sendServer("MODE " + channel + " +v " +user);
	    }
	}

	@Override
	public void onQuit(String in_str) throws IRCException, IOException {}

	@Override
	public void onKick(String in_str) throws IRCException, IOException 
	{
		IRC irc = IRC.getInstance();
		Matcher m = Pattern.compile(":([a-zA-Z0-9]*)!([a-zA-Z0-9@\\-\\.]*) KICK (#[a-zA-Z0-9]*) ([a-zA-Z0-9]*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);

		if (m.find())
		{
			String kicker = m.group(1), host = m.group(2), channel = m.group(3), kicked = m.group(4), message = m.group(5);
			if (kicked.equals(Details.getIntance().getNickName()))
			{
				irc.sendServer("JOIN " + channel); 
				irc.sendServer("PRIVMSG " + channel + " Dont kick me!! " + kicker + "... bad bad bad person!");
			}
		}
	}

	@Override
	public String name() 
	{
		return "Adminstration";
	}
}
