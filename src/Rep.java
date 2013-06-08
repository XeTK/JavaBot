import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.PluginTemp;
import program.Database;
import program.IRC;
import program.IRCException;


public class Rep implements PluginTemp
{

	@Override
	public void onCreate(String in_str) throws IRCException, IOException {}
	@Override
	public void onTime(String in_str) throws IRCException, IOException {}

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
				if (message.matches("(^[a-zA-Z0-9]*)([\\s+-]*)([\\s\\d]*)"))
				{
					Matcher r = Pattern.compile("(^[a-zA-Z0-9]*)([\\s+-]*)([\\s\\d]*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
					if (r.find())
					{
						String item = r.group(1),
								type = r.group(2),
								ammount = r.group(3);
						if (!type.equals(""))
						{
							int iAmmount = 0;
							
							if (type.equals("++"))
							{
								iAmmount = 1;
							}
							else if(type.equals("--"))
							{
								iAmmount = -1;
							}
							else
							{
								type = type.trim();
								ammount = ammount.trim();
								if (type.equals("+"))
									iAmmount = Integer.valueOf(ammount);
								else	
									iAmmount = Integer.valueOf(type + ammount);
							}
							if (iAmmount > 100||iAmmount < -100)
							{
								irc.sendServer("PRIVMSG " + channel + " You cant do that its to much rep...");
							}
							else
							{
								db.updateRep(item, iAmmount);
								irc.sendServer("PRIVMSG " + channel + " " + item + ": Rep = " + db.getUserRep(item) + "!");
							}
						}
					}
				}
				else if (message.matches("\\.rep [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length > 0||t[1] != null)
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Rep = " + db.getUserRep(t[1]) + "!");
	 			}
				else if(message.matches("^\\.help") || message.matches("^\\."))
				{
					irc.sendServer("PRIVMSG " + channel + " REP: " +
							".rep *Item* - view the reputation of a item : " +
							"*Item*--/++ - increment or decrement the rep of a desired item / : " +
							"*Item* +/- *Ammount - increment or decrement the rep of a set item by a set amount : "
							);
				}
			}
			catch (SQLException e){e.printStackTrace();} 
			catch (ClassNotFoundException e){e.printStackTrace();}	
		}
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException {}
	@Override
	public void onQuit(String in_str) throws IRCException, IOException {}

}
