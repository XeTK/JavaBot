import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.PluginTemp;
import program.Database;
import program.IRC;
import program.IRCException;


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
	public void onMessage(String in_str) throws IRCException, IOException
	{
	    Matcher m = 
	    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	        String user = m.group(1), host = m.group(2), channel = m.group(3), message = m.group(4);
			
	        if (message.charAt(message.length() - 1 ) == ' ')
	        	message = message.substring(0, message.length() -1);
	        
			try
			{
				IRC irc = IRC.getInstance();
				Database db = Database.getInstance();
				if (message.matches("^[A-Za-z0-9]+([\\+]){2,2}"))
				{
					db.updateRep(message.substring(0, message.length()-2), +1);
					irc.sendServer("PRIVMSG " + channel + " " + message.substring(0, message.length()-2) + ": Rep = " + db.getUserRep(message.substring(0, message.length()-2)) + "!");
				}
				else if (message.matches("^[A-Za-z0-9]+([\\-]){2,2}"))
				{
					db.updateRep(message.substring(0, message.length()-2), -1);
					irc.sendServer("PRIVMSG " + channel + " " + message.substring(0, message.length()-2) + ": Rep = " + db.getUserRep(message.substring(0, message.length()-2)) + "!");
				}
				else if (message.matches("\\.rep [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t[1] == null)
						irc.sendServer("PRIVMSG " + channel + " " + user + ": Rep = " + db.getUserRep(user) + "!");
					else
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Rep = " + db.getUserRep(t[1]) + "!");
	 			}
				else if (message.matches("\\.msgsent [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length <= 0||t[1] == null)
						irc.sendServer("PRIVMSG " + channel + " " + user + ": Messages Sent = " + db.getMessagesSent(user) + "!");
					else
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Messages Sent = " + db.getMessagesSent(t[1]) + "!");
	 			}
				else if (message.matches("\\.lastonline [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length > 0||t[1] != null)
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Last Online = " + db.getLastOnline(t[1]) + "!");
	 			}
				else if (message.matches("\\.remind .* \".*\"")||message.matches("\\.remind .* '.*'"))
				{
					String[] t = message.split("'");
					if (message.matches("\\.remind .* \".*\""))				
						t = message.split("\"");
					String[] tt = t[0].split(" ");
					db.addReminder(user, tt[1], t[1]);
					irc.sendServer("PRIVMSG " + channel + " " + user + ": I will remind them next time they are round master!");
				}
				else
				{
					String[] reminders = db.getReminders(user);
					if (reminders.length > 0)
					{
						for (int i = 0; i < reminders.length;i++)
						{
							irc.sendServer("PRIVMSG " + channel + " " + reminders[i]);
							db.delReminder(user);
						}
					}
					db.updateUser(user);
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
