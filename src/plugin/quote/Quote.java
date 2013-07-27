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
	private final String rgx_add = "\\.quoteadd\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)";
	private final String rgx_remove = "\\.quotedel\\s([a-zA-Z\\w\\d\\s]*)";
	private final String rgx_quotes = "\\.quotes\\s([\\w\\d]*)";
	private final String rgx_quote = "\\.quote\\s([\\w\\d]*)";

	private final String msg_add = "%s: quote added";
	private final String msg_remove = "%s: quote removed";
	
	private final Pattern dot_add = Pattern.compile(rgx_add, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final Pattern dot_remove = Pattern.compile(rgx_remove, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final Pattern dot_quotes = Pattern.compile(rgx_quotes, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final Pattern dot_quote = Pattern.compile(rgx_quote, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	
	private final IRC irc = IRC.getInstance();
	private final UserList luq = UserList.getInstance();
	
	public void onMessage(Message in_message) throws Exception
	{
		if (!in_message.isPrivMsg())
		{		
			String message = in_message.getMessage(); 
			String channel = in_message.getChannel(); 
			String user = in_message.getUser();	
			
			Matcher m; 
			
			m = dot_add.matcher(message);
			
			if (m.find())
			{
				luq.addQuote(m.group(1),m.group(2));
				String msg = String.format(msg_add, user);
				irc.sendPrivmsg(channel, msg);
			}
			
			m = dot_remove.matcher(message);
			
			if (m.find())
			{
				luq.removeQuote(m.group(1));
				String msg = String.format(msg_remove, user);
				irc.sendPrivmsg(channel, msg);
			}
				
			m = dot_quotes.matcher(message);
			if (m.find())
			{
				String[] quotes = luq.getQuotes(m.group(1));
				for (int i = 0; i < quotes.length;i++)
					irc.sendPrivmsg(channel, quotes[i]);
			}

			m = dot_quote.matcher(message);
			if (m.find())
			{
				String[] quotes = luq.getQuotes(m.group(1));
				if (quotes.length > 0)
				{
					int ran_ind = new Random().nextInt(quotes.length);
					irc.sendPrivmsg(channel, quotes[ran_ind]);
				}
			}
			
		}
	}

	public void onJoin(Join in_join) throws Exception
	{		
		if (luq.getUser(in_join.getUser()) != null)
		{
			String[] quotes = luq.getQuotes(in_join.getUser());
			if (quotes.length > 0)
			{
				int ran_ind = new Random().nextInt(quotes.length);
				
				String quote = quotes[ran_ind];
				String user = in_join.getUser();
				String msg = String.format("%s: %s", user, quote);
				
				irc.sendPrivmsg(in_join.getChannel(), msg);
			}
		}
	}

	public String getHelpString()
	{
		return "QUOTE: " +
				".quotes <item> - returns all the quotes tied to this item : \n" +
				".quote <item> - returns a random quote for that item : \n" +
				".quoteadd <item> <message> - will add a new quote to the appropriate item : \n" +
				".quotedel <message> - will remove the message from the libary of quotes : ";
	}	
}
