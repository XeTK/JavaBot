
import java.net.MalformedURLException;

import plugin.PluginTemp;
import program.IRC;
import program.Start;




public class Admin implements PluginTemp
{

	@Override
	public void onCreate(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onTime(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(String in_str)
	{
		//:XeTK!xetk@cpc4-swin16-2-0-cust422.3-1.cable.virginmedia.com PRIVMSG #xetk :asdf
		String temp[] = in_str.split(":"),
				message, host, user, channel;
		message = temp[2];
		temp = temp[1].split("!");
		user = temp[0];
		temp = temp[1].split(" ");
		host = temp[0];
		channel = temp[2];
		if (message.matches("^\\.join [A-Za-z0-9#]+$"))
		{
			String str[] = message.split(" ");
			for (int i = 0;i < Start.getDetails().getAdmins().length;i++)
			{
				if (user.equals(Start.getDetails().getAdmins()[i]))
				{
					IRC.sendServer("JOIN " + str[1]);
					IRC.sendServer("PRIVMSG " + channel + " I Have Joined " + str[1] + ", Master!");
					break;
				}
			}
		}
		else if (message.matches("^\\.quit"))
		{
			for (int i = 0;i < Start.getDetails().getAdmins().length;i++)
			{
				if (user.equals(Start.getDetails().getAdmins()[i]))
				{
					IRC.sendServer("QUIT Leaving Master");
					break;
				}
			}
		}
		else if(message.matches("^\\.nick [A-Za-z0-9#]+$"))
		{
			String str[] = message.split(" ");
			for (int i = 0;i < Start.getDetails().getAdmins().length;i++)
			{
				if (user.equals(Start.getDetails().getAdmins()[i]))
				{
					IRC.sendServer("NICK "+ str[1]);
					break;
				}
			}
		}
		else if(message.matches("^\\.reload"))
		{
			for (int i = 0;i < Start.getDetails().getAdmins().length;i++)
			{
				if (user.equals(Start.getDetails().getAdmins()[i]))
				{
					try
					{
						Start.loadPlugins();
						IRC.sendServer("PRIVMSG " + channel + " Reloaded Plugins Master!");
					} 
					catch (MalformedURLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						IRC.sendServer("PRIVMSG " + channel + " Reloading Plugins Fucked Up Master!");
					}
					break;
				}
			}
		}
		else if(message.matches("^\\.help") || message.matches("^\\."))
		{
			IRC.sendServer("PRIVMSG " + channel + " ADMIN: .join #* - Join Channel : .quit - Kill Bot : .nick ** - Change Bot's Nick : .reload - Reload Bot's Plugin : .help - Show Help Text");
		}
	}

	@Override
	public void onJoin(String in_str)
	{
		String data[] = in_str.split(":"),
				user = data[1].split("!")[0],
				channel = data[2];
		IRC.sendServer("MODE " + channel + " +o " +user);
	}

	@Override
	public void onQuit(String in_str)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
