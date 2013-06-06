
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
		
		//:XeTK!xetk@cpc4-swin16-2-0-cust422.3-1.cable.virginmedia.com PRIVMSG #xetk :asdf
		
	    Matcher m = 
	    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	        String user = m.group(1), host = m.group(2), channel = m.group(3), message = m.group(4);
	        
	        if (message.charAt(message.length() - 1 ) == ' ')
	        	message = message.substring(0, message.length() -1);
			
			if (message.matches("^\\.join [A-Za-z0-9#]+$"))
			{
				String str[] = message.split(" ");
				for (int i = 0;i < details.getAdmins().length;i++)
				{
					if (user.equals(details.getAdmins()[i]))
					{
						irc.sendServer("JOIN " + str[1]);
						irc.sendServer("PRIVMSG " + channel + " I Have Joined " + str[1] + ", Master!");
						break;
					}
				}
			}
			else if (message.matches("^\\.quit"))
			{
				for (int i = 0;i < details.getAdmins().length;i++)
				{
					if (user.equals(details.getAdmins()[i]))
					{
						irc.sendServer("QUIT Leaving Master");
						break;
					}
				}
			}
			else if(message.matches("^\\.nick [A-Za-z0-9#]+$"))
			{
				String str[] = message.split(" ");
				for (int i = 0;i < details.getAdmins().length;i++)
				{
					if (user.equals(details.getAdmins()[i]))
					{
						irc.sendServer("NICK "+ str[1]);
						break;
					}
				}
			}
			else if(message.matches("^\\.help") || message.matches("^\\."))
			{
				irc.sendServer("PRIVMSG " + channel + " ADMIN: .join #* - Join Channel : .quit - Kill Bot : .nick ** - Change Bot's Nick : .help - Show Help Text");
			}
	    }
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException
	{
		IRC irc = IRC.getInstance();
		String data[] = in_str.split(":"),
				user = data[1].split("!")[0],
				channel = data[2];
		irc.sendServer("MODE " + channel + " +v " +user);
	}

	@Override
	public void onQuit(String in_str) throws IRCException, IOException
	{
		// TODO Auto-generated method stub

	}


}
