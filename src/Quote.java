import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.UserList;

import plugin.PluginTemp;
import program.IRC;
import program.IRCException;


public class Quote implements PluginTemp
{
	@Override
	public void onCreate(String in_str) throws IOException {System.out.println("\u001B[37mQuote Plugin Loaded");}
	@Override
	public void onTime(String in_str) {}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		UserList luq = UserList.getInstance();
		Matcher m = 
				Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
		if (m.find())
		{
			String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
			
			if (message.charAt(message.length() - 1 ) == ' ')
				message = message.substring(0, message.length() -1);

			IRC irc = IRC.getInstance();
			if (message.matches("(\\.quoteadd)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)"))
			{
				Matcher r = Pattern.compile("(\\.quoteadd)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				if (r.find())
				{
					luq.addQuote(r.group(2),r.group(3));
					irc.sendServer("PRIVMSG " + channel + " " + user + ": Quote Added.");
				}
			}
			else if (message.matches("\\.quotes [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length > 0||t[1] != null)
				{
					if (luq.getUser(t[1]) != null)
					{
						String[] quotes = luq.getQuotes(t[1]);
						if (quotes.length > 0)
							for (int i = 0; i < quotes.length;i++)
								irc.sendServer("PRIVMSG " + channel + " " + quotes[i]);
					}
				}
 			}
			else if (message.matches("\\.quote [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length > 0||t[1] != null)
				{
					if (luq.getUser(t[1]) != null)
					{
						String[] quotes = luq.getQuotes(t[1]);
						if (quotes.length > 0)
							irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": "+ quotes[new Random().nextInt(quotes.length)]);
					}
				}
 			}
			/*else if (message.matches("(\\.quotedel)\\s([a-zA-Z\\w\\d\\s]*)"))
			{
				Matcher r = Pattern.compile("(\\.quotedel)\\s([a-zA-Z\\w\\d\\s]*)",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				if (r.find())
				{
					db.delQuote(r.group(2));
					irc.sendServer("PRIVMSG " + channel + " " + user + ": Quote Deleted.");
					JSON.saveGSON(cfgFile, luq);
				}
			}*/
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
	}

	@Override
	public void onJoin(String in_str)
	{
	    Matcher m = 
	    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) JOIN :(#?\\w+)",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
	    if (m.find())
	    {
	    	String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3);
	    	System.out.println(user + " Has joined");
			try
			{
				IRC irc = IRC.getInstance();
				UserList luq = UserList.getInstance();
				
				if (luq.getUser(user) != null)
				{
					String[] quotes = luq.getQuotes(user);
					if (quotes.length > 0)
						irc.sendServer("PRIVMSG " + channel + " " + user + ": "+ quotes[new Random().nextInt(quotes.length)]);
				}
			}
			catch (IRCException e){e.printStackTrace();} 
			catch (IOException e){e.printStackTrace();}
	    }
	}

	@Override
	public void onQuit(String in_str) {}

}
