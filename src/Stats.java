import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.PluginTemp;
import program.Database;
import program.IRC;
import program.IRCException;


public class Stats implements PluginTemp
{

	@Override
	public void onCreate(String in_str) throws IRCException, IOException {}

	@Override
	public void onTime(String in_str) throws IRCException, IOException
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
				db.updateUser(user);
				if (message.matches("\\.msgsent [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length > 0||t[1] != null)
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Messages Sent = " + db.getMessagesSent(t[1]) + "!");
	 			}
				else if (message.matches("\\.lastonline [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length > 0||t[1] != null)
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Last Online = " + db.getLastOnline(t[1]) + "!");
	 			}
				else if(message.matches("^\\.help") || message.matches("^\\."))
				{
					irc.sendServer("PRIVMSG " + channel + " STATS: " +
							".lastonline *username* - check when a member was last active : " +
							".msgsent *username* - check how many messages a user has sent globaly within the channel : "
							);
				}
			}
			catch (SQLException e){e.printStackTrace();} 
			catch (ClassNotFoundException e){e.printStackTrace();}	
	    }

	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException
	{
		
	}
	
	@Override
	public void onQuit(String in_str) throws IRCException, IOException
	{
		
	}

}
