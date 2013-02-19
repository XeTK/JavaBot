import java.sql.Date;
import java.sql.SQLException;

import plugin.PluginTemp;
import program.Database;
import program.IRC;


public class Quote implements PluginTemp
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
		String temp[] = in_str.split(":"),
				message, host, user, channel;
		message = temp[2];
		temp = temp[1].split("!");
		user = temp[0];
		temp = temp[1].split(" ");
		host = temp[0];
		channel = temp[2];
		try
		{
			if (message.matches("^[A-Za-z0-9]+([\\+]){2,2}"))
			{
				Database.updateRep(message.substring(0, message.length()-2), +1);
				IRC.sendServer("PRIVMSG " + channel + " " + message.substring(0, message.length()-2) + ": Rep = " + Database.getUserRep(message.substring(0, message.length()-2)) + "!");
			}
			else if (message.matches("^[A-Za-z0-9]+([\\-]){2,2}"))
			{
				Database.updateRep(message.substring(0, message.length()-2), -1);
				IRC.sendServer("PRIVMSG " + channel + " " + message.substring(0, message.length()-2) + ": Rep = " + Database.getUserRep(message.substring(0, message.length()-2)) + "!");
			}
			else if (message.matches("^.rep")||message.matches("^.rep [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length <= 0||t[1] == null)
					IRC.sendServer("PRIVMSG " + channel + " " + user + ": Rep = " + Database.getUserRep(user) + "!");
				else
					IRC.sendServer("PRIVMSG " + channel + " " + t[1] + ": Rep = " + Database.getUserRep(t[1]) + "!");
 			}
			else if (message.matches("^.msgsent [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length <= 0||t[1] == null)
					IRC.sendServer("PRIVMSG " + channel + " " + user + ": Messages Sent = " + Database.getMessagesSent(user) + "!");
				else
					IRC.sendServer("PRIVMSG " + channel + " " + t[1] + ": Messages Sent = " + Database.getMessagesSent(t[1]) + "!");
 			}
			else if (message.matches("^.lastonline [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length > 0||t[1] != null)
					IRC.sendServer("PRIVMSG " + channel + " " + t[1] + ": Last Online = " + Database.getLastOnline(t[1]) + "!");
 			}
			else if (message.matches("\\.remind .* \".*\"")||message.matches("\\.remind .* '.*'"))
			{
				String[] t = message.split("'");
				if (message.matches("\\.remind .* \".*\""))				
					t = message.split("\"");
				String[] tt = t[0].split(" ");
				Database.addReminder(user, tt[1], t[1]);
				IRC.sendServer("PRIVMSG " + channel + " " + user + ": I will remind them next time they are round master!");
			}
			else
			{
				String[] reminders = Database.getReminders(user);
				if (reminders.length > 0)
				{
					for (int i = 0; i < reminders.length;i++)
					{
						IRC.sendServer("PRIVMSG " + channel + " " + reminders[i]);
						Database.delReminder(user);
					}
				}
				Database.updateUser(user);
			}
		} 
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onJoin(String in_str)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQuit(String in_str)
	{
		// TODO Auto-generated method stub
		
	}


}
