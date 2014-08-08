package plugin.tools.quote;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.ArrayList;

import plugin.stats.user.UserList;
import plugin.stats.user.UserListLoader;
import core.Channel;
import core.event.Join;
import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.Regex;
import core.utils.RegexFormatter;

public class Quote extends Plugin {
	
	private final String CMD_ADD    = "add";
	private final String CMD_REMOVE = "delete";
	private final String CMD_QUOTES = "quotes";
	private final String CMD_QUOTE  = "quote";
	
	private final String HLP_ADD    = String.format("%s <item> <message> - will add a new quote to the appropriate item",CMD_ADD);
	private final String HLP_REMOVE = String.format("%s <message> - will remove the message from the libary of quotes", CMD_REMOVE);
	private final String HLP_QUOTES = String.format("%s <item> - returns all the quotes tied to this item", CMD_QUOTES);
	private final String HLP_QUOTE  = String.format("%s <item> - returns a random quote for that item", CMD_QUOTE);
	
	private final String RGX_ADD    = String.format("(%s)\\s(.*)", RegexFormatter.REG_NICK.getRegex());
	private final String RGX_REMOVE = "(.*)";
	private final String RGX_QUOTES = String.format("(%s)", RegexFormatter.REG_NICK.getRegex());
	private final String RGX_QUOTE  = String.format("(%s)", RegexFormatter.REG_NICK.getRegex());

	private final String MSG_ADD    = "%s: quote added";
	private final String MSG_REMOVE = "%s: quote removed";

	private UserList userList_;

	private String[] dependencies_ = {"UserListLoader"};

	public String[] getDependencies() {
		return dependencies_;
	}

	public boolean hasDependencies() {
		return (dependencies_.length > 0);
	}

	public void onCreate(Channel inChannel) throws Exception {
		super.onCreate(inChannel);
		this.userList_ = ((UserListLoader) inChannel.getPlugin(UserListLoader.class)).getUserList();
	}

	public void onJoin(Join inJoin) throws Exception {
		if (userList_.getUser(inJoin.getUser()) != null) {
			String[] quotes = userList_.getQuotes(inJoin.getUser());
			if (quotes.length > 0) {
				int ranInd = new Random().nextInt(quotes.length);

				String quote = quotes[ranInd];
				String user  = inJoin.getUser();
				String msg   = String.format("%s: %s", user, quote);

				irc.sendPrivmsg(inJoin.getChannel(), msg);
			}
		}
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		MenuItem pluginRoot = rootItem;
		
		MenuItem quoteAdd = new MenuItem(CMD_ADD, rootItem, 1, AuthGroup.REGISTERD){
			@Override
			public void onExecution(String args, String username) { 
				Matcher m = Regex.getMatcher(RGX_ADD, args);
				
				if (m.find()) {
					userList_.addQuote(m.group(0), m.group(1));
					
					String msg = String.format(MSG_ADD, username);
					
					try {
						irc.sendPrivmsg(channel_.getChannelName(), msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public String onHelp() {
				return HLP_ADD;
			}
		};
		
		pluginRoot.addChild(quoteAdd);
		
		MenuItem quoteRemove = new MenuItem(CMD_REMOVE, rootItem, 2, AuthGroup.REGISTERD){
			@Override
			public void onExecution(String args, String username) { 
				Matcher m = Regex.getMatcher(RGX_REMOVE, args);

				if (m.find()) {
					userList_.removeQuote(m.group(1));
					
					String msg = String.format(MSG_REMOVE, username);
					
					try {
						irc.sendPrivmsg(channel_.getChannelName(), msg);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}
			@Override
			public String onHelp() {
				return HLP_REMOVE;
			}
		};

		pluginRoot.addChild(quoteRemove);
		
		MenuItem quoteQuotes = new MenuItem(CMD_QUOTES, rootItem, 3, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) {
				
				Matcher m = Regex.getMatcher(RGX_QUOTES, args);
				
				if (m.find()) {
					
					String[] quotes = userList_.getQuotes(m.group(0));
					for (int i = 0; i < quotes.length; i++) {
						try {
							irc.sendPrivmsg(channel_.getChannelName(), quotes[i]);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
			@Override
			public String onHelp() {
				return HLP_QUOTES;
			}
		};

		pluginRoot.addChild(quoteQuotes);
		
		MenuItem quoteQuote = new MenuItem(CMD_QUOTE, rootItem, 4, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 
				
				Matcher m = Regex.getMatcher(RGX_QUOTE, args);
				
				if (m.find()) {
					
					String[] quotes = userList_.getQuotes(m.group(0));
					
					if (quotes.length > 0) {
						int ran_ind = new Random().nextInt(quotes.length);
						
						try {
							irc.sendPrivmsg(channel_.getChannelName(), quotes[ran_ind]);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
			@Override
			public String onHelp() {
				return HLP_QUOTE;
			}
		};

		pluginRoot.addChild(quoteQuote);
		
		rootItem = pluginRoot;
	}
}
