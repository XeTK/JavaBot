package plugin.tools.quote;

import java.util.Random;
import java.util.regex.Matcher;

import plugin.stats.user.UserList;
import plugin.stats.user.UserListLoader;
import core.Channel;
import core.event.Join;
import core.event.Message;
import core.plugin.Plugin;
import core.utils.IRC;
import core.utils.Regex;
import core.utils.RegexFormatter;

public class Quote extends Plugin {
	private final String RGX_ADD    = RegexFormatter.format("quoteadd\\s([\\w\\d]*)\\s(.*)");
	private final String RGX_REMOVE = RegexFormatter.format("quotedel\\s(.*)");
	private final String RGX_QUOTES = RegexFormatter.format("quotes", RegexFormatter.REG_NICK);
	private final String RGX_QUOTE  = RegexFormatter.format("quote", RegexFormatter.REG_NICK);

	private final String MSG_ADD    = "%s: quote added";
	private final String MSG_REMOVE = "%s: quote removed";

	private final IRC irc_ = IRC.getInstance();
	private UserList userList_;

	public void onCreate(Channel inChannel) throws Exception {
		this.userList_ = ((UserListLoader) inChannel.getPlugin(UserListLoader.class)).getUserList();
	}

	public void onMessage(Message inMessage) throws Exception {
		if (!inMessage.isPrivMsg()) {
			
			String message = inMessage.getMessage();
			String channel = inMessage.getChannel();
			String user    = inMessage.getUser();

			Matcher m;

			m = Regex.getMatcher(RGX_ADD, message);

			if (m.find()) {
				userList_.addQuote(m.group(1), m.group(2));
				String msg = String.format(MSG_ADD, user);
				irc_.sendPrivmsg(channel, msg);
			}

			m = Regex.getMatcher(RGX_REMOVE, message);

			if (m.find()) {
				userList_.removeQuote(m.group(1));
				String msg = String.format(MSG_REMOVE, user);
				irc_.sendPrivmsg(channel, msg);
			}

			m = Regex.getMatcher(RGX_QUOTES, message);
			if (m.find()) {
				String[] quotes = userList_.getQuotes(m.group(1));
				for (int i = 0; i < quotes.length; i++)
					irc_.sendPrivmsg(channel, quotes[i]);
			}

			m = Regex.getMatcher(RGX_QUOTE, message);
			if (m.find()) {
				String[] quotes = userList_.getQuotes(m.group(1));
				if (quotes.length > 0) {
					int ran_ind = new Random().nextInt(quotes.length);
					irc_.sendPrivmsg(channel, quotes[ran_ind]);
				}
			}

		}
	}

	public void onJoin(Join inJoin) throws Exception {
		if (userList_.getUser(inJoin.getUser()) != null) {
			String[] quotes = userList_.getQuotes(inJoin.getUser());
			if (quotes.length > 0) {
				int ranInd = new Random().nextInt(quotes.length);

				String quote = quotes[ranInd];
				String user  = inJoin.getUser();
				String msg   = String.format("%s: %s", user, quote);

				irc_.sendPrivmsg(inJoin.getChannel(), msg);
			}
		}
	}

	public String getHelpString() {
		return "QUOTE: \n"
				+ "\t.quotes <item> - returns all the quotes tied to this item\n"
				+ "\t.quote <item> - returns a random quote for that item\n"
				+ "\t.quoteadd <item> <message> - will add a new quote to the appropriate item\n"
				+ "\t.quotedel <message> - will remove the message from the libary of quotes\n";
	}
}
