package plugin.quote;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.users.UserList;
import core.event.Join;
import core.event.Message;
import core.plugin.Plugin;
import core.utils.IRC;

public class Quote extends Plugin
{	
	public void onMessage(Message in_message) throws Exception
	{
		if (!in_message.isPrivMsg())
		{
			UserList luq = UserList.getInstance();
			
			String message = in_message.getMessage(); 
			String channel = in_message.getChannel(); 
			String user = in_message.getUser();
			
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
					irc.sendPrivmsg(channel, user + ": Quote Added.");
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
								irc.sendPrivmsg(channel, quotes[i]);
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
							irc.sendPrivmsg(channel, 
									t[1] + ": "+ quotes[new Random().nextInt(quotes.length)]);
					}
				}
			}
			else if (message.matches("\\.quotedel ([a-zA-Z\\w\\d\\s]*)"))
			{
				Matcher r = Pattern.compile("\\.quotedel ([a-zA-Z\\w\\d\\s]*)",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				if (r.find())
				{
					luq.removeQuote(r.group(1));
					irc.sendPrivmsg(channel, user + ": Quote Deleted.");
				}
			}
		}
	}

	public void onJoin(Join in_join) throws Exception
	{
		IRC irc = IRC.getInstance();
		UserList luq = UserList.getInstance();
		
		if (luq.getUser(in_join.getUser()) != null)
		{
			String[] quotes = luq.getQuotes(in_join.getUser());
			if (quotes.length > 0)
				irc.sendPrivmsg(in_join.getChannel(), 
						in_join.getUser() + ": "+ quotes[new Random().nextInt(quotes.length)]);
		}
	}

	public String getHelpString()
	{
		return "QUOTE: " +
				".quotes *item* - returns all the quotes tied to this item : " +
				".quote *item* - returns a random quote for that item : " +
				".quoteadd *item* *message* - will add a new quote to the appropriate item : " +
				".quotedel *message* - will remove the message from the libary of quotes : ";
	}	
}
