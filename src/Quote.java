import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.PluginTemp;
import program.Database;
import program.IRC;
import program.IRCException;


public class Quote implements PluginTemp
{

	@Override
	public void onCreate(String in_str) {System.out.println("\u001B[37mQuote Plugin Loaded");}

	@Override
	public void onTime(String in_str) {}

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
				if (message.matches("(\\.quoteadd)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)"))
				{
					Matcher r = Pattern.compile("(\\.quoteadd)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
									Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
					if (r.find())
					{
						db.addQuote(r.group(2),r.group(3));
						irc.sendServer("PRIVMSG " + channel + " " + user + ": Quote Added.");
					}
				}
				else if (message.matches("\\.quotes [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length > 0||t[1] != null)
					{
						String[] quotes = db.getQuotes(t[1]);
						if (quotes.length > 0)
							for (int i = 0; i < quotes.length;i++)
								irc.sendServer("PRIVMSG " + channel + " " + quotes[i]);
					}
	 			}
				else if (message.matches("\\.quote [A-Za-z0-9#]+$"))
				{
					String[] t = message.split(" ");
					if (t.length > 0||t[1] != null)
					{
						String[] quotes = db.getQuotes(t[1]);
						if (quotes.length > 0)
							irc.sendServer("PRIVMSG " + channel + " " + quotes[new Random().nextInt(quotes.length)]);
					}
	 			}
				else if (message.matches("(\\.quotedel)\\s([a-zA-Z\\w\\d\\s]*)"))
				{
					Matcher r = Pattern.compile("(\\.quotedel)\\s([a-zA-Z\\w\\d\\s]*)",
									Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
					if (r.find())
					{
						db.delQuote(r.group(2));
						irc.sendServer("PRIVMSG " + channel + " " + user + ": Quote Deleted.");
					}
				}
				else if(message.matches("^\\.help") || message.matches("^\\."))
				{
					irc.sendServer("PRIVMSG " + channel + " QUOTE: " +
							".quotes *item* - returns all the quotes tied to this item : " +
							".quote *item* - returns a random quote for that item : " +
							".quoteadd *item* *message* - will add a new quote to the appropriate item : " +
							".quotedel *message* - will remove the message from the libary of quotes : "
							);
				}
			} 
			catch (SQLException e){e.printStackTrace();} 
			catch (ClassNotFoundException e){e.printStackTrace();}	
		}
	}

	@Override
	public void onJoin(String in_str)
	{
	    Matcher m = 
	    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) JOIN :(#?\\w+)",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	    	String user = m.group(1), host = m.group(2), channel = m.group(3);
			try
			{
				Database db = Database.getInstance();
				IRC irc = IRC.getInstance();
				
				String[] quotes = db.getQuotes(user);
				if (quotes.length > 0)
				{
					irc.sendServer("PRIVMSG " + channel + " " + quotes[new Random().nextInt(quotes.length)]);
				}
			}
			catch (SQLException e){e.printStackTrace();} 
			catch (ClassNotFoundException e){e.printStackTrace();} 
			catch (IRCException e){e.printStackTrace();} 
			catch (IOException e){e.printStackTrace();}
	    }
	}

	@Override
	public void onQuit(String in_str) {}

}
