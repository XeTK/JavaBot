package plugin.tools.sed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

import core.event.Message;
import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.Colour;
import core.utils.Details;

/**
 * Sed plugin.
 * 
 * Handles messages of the form: s/<search>/<replacement>/
 * s/<search>/<replacement> <username>: s/<search>/<replacement>/ <username>:
 * s/<search>/<replacement>
 */
public class Sed extends Plugin {
	
	private static final String CMD_INFO     = "info";
	private static final String CMD_SED_DUMP = "seddumpcache";
	private static final String CMD_SED_DROP = "seddropcache";
	
	private static final String HLP_INFO     = "[<username>: ]s/<search>/<replacement>/ - e.g XeTK: s/.*/hello/ is used to replace the previous statement with hello";
	private static final String HLP_SED_DUMP = String.format("seddumpcache - dumps sed cache", CMD_SED_DUMP);
	private static final String HLP_SED_DROP = String.format("seddropcache - drops sed cache", CMD_SED_DROP);
	
//	private static final String RGX_SED      = "^(?:([\\w]+): )?s/((?:(?<=\\\\)/|[^/])+)/((?:(?<=\\\\)/|[^/])*)/?";
	private static final String RGX_SED      = "^(?:([\\w\\[\\]\\{\\}`\\|\\^\\\\-]+): )?(?:\\[(\\d+)\\])?s/((?:(?<=\\\\)/|[^/])+)/((?:(?<=\\\\)/|[^/])*)/?";

	/**
	 * The max number of Message objects stored per user.
	 */
	private static final int CACHE_SIZE = 10;
	/**
	 * The ASCII SOH character.
	 */
	private static final char ASCII_SOH = '\001';

	/**
	 * The first group of the sed regex.
	 */
	private static final int SED_USERNAME_GROUP = 1;
	/**
	 * The second group of the sed regex.
	 */
	private static final int SED_MSG_TARGET_GROUP = 2;
	/**
	 * The third group of the sed regex.
	 */
	private static final int SED_SEARCH_GROUP = 3;
	/**
	 * The fourth group of the sed regex.
	 */
	private static final int SED_REPLACEMENT_GROUP = 4;

	/**
	 * Details instance.
	 */
	private Details details_ = Details.getInstance();

	/**
	 * Stores Messages objects per-user.
	 */
	private Map<String, Stack<Message>> cache_ = new HashMap<String, Stack<Message>>();

	private String[] dependencies_ = {};

	public String[] getDependencies() {
		return dependencies_;
	}

	public boolean hasDependencies() {
		return (dependencies_.length > 0);
	}
	
	public final void onMessage(final Message messageObj) throws Exception {
		String message = messageObj.getMessage();
		String channel = messageObj.getChannel();
		String user = messageObj.getUser();

		// set up sed finding regex
		// (?: starts a non-capture group
		// ([\\w]+) captures the username
		// )? makes the group optional
		// ([^/]+) captures the search string
		// ([^/]*) captures the replacement
		Pattern sedFinder = Pattern.compile(RGX_SED);
		Matcher m = sedFinder.matcher(message);

		// it's sed time, baby
		if (m.find()) {
			String targetUser = new String();
			if (m.group(SED_USERNAME_GROUP) != null) {
				targetUser = m.group(SED_USERNAME_GROUP);
			}
			String search = m.group(SED_SEARCH_GROUP);
			String replacement = m.group(SED_REPLACEMENT_GROUP);

			Stack<Message> userCache = new Stack<Message>();
			boolean hasTarget = (!targetUser.equals(new String()) && !targetUser
					.equals(user));
			if (!hasTarget) {
				// no target, use last message from
				// sourceUser's cache (or do nothing)
				userCache = getUserCache(user);
			} else {
				// target specified, run through the cache
				// (most recent first) and either match and
				// replace or do nothing
				userCache = getUserCache(targetUser);
			}

                        boolean isStackSize = false;
                        int stacksize = -1;
			if(m.group(SED_MSG_TARGET_GROUP) != null) {
				stacksize = Integer.parseInt(m.group(SED_MSG_TARGET_GROUP));
                                isStackSize = true;
			}

			while (!userCache.empty()) {
                                stacksize--;
                                if(isStackSize && stacksize > 0) {
				// We don't want to continue if a match is not found at stacksize
                                        Message discard = userCache.pop();
					continue;
				}
				Message tempMessage = userCache.pop();
				String text = tempMessage.getMessage();

				if (isActionMessage(text)) {
					text = processActionMessage(text, tempMessage.getUser());
				}

				m = Pattern.compile(search, Pattern.CASE_INSENSITIVE).matcher(text);
					
				// Use StringBuffer to hold message String for replacement
				StringBuffer sb = new StringBuffer();

				if (m.find()) {
					String reply = new String();
					if (hasTarget) {
						reply = user + " thought ";
					}

					reply += "%s meant: %s";
					try {
						// Use Matcher class to make replacement
						// This preserves modifiers used in Pattern compilation
						m.appendReplacement(sb, replacement);
						
					} catch (StringIndexOutOfBoundsException e) {
						throw new SedException(e.getMessage());
					}
					// Append remainder of StringBuffer to the match.
					m.appendTail(sb);
					text = sb.toString();
					
					irc.sendPrivmsg(channel,
						String.format(reply, tempMessage.getUser(), text));

					break;
				}

			}

		} else {
			// no dice, add message to history queue
			addToCache(messageObj);
		}
	}

	/**
	 * Identifies ACTION messages.
	 * 
	 * @param msg The message string
	 * 
	 * @return true if msg is an ACTION message
	 */
	private boolean isActionMessage(final String msg) {
		return msg.startsWith(ASCII_SOH + "ACTION") && msg.endsWith(Character.toString(ASCII_SOH));
	}

	/**
	 * Converts an ACTION message into a friendlier form.
	 * 
	 * @param  msg  The message string
	 * @param  user The speaker
	 * @return The modified message string
	 */
	private String processActionMessage(final String msg, final String user) {
		if (!isActionMessage(msg)) {
			return msg;
		}

		// remove special chars
		String newMsg = msg.substring(1);
		newMsg = newMsg.substring(0, newMsg.length() - 1);

		// format for output
		newMsg = newMsg.replace("ACTION", Colour.colour("* " + user, Colour.MAGENTA));
		return newMsg;
	}

	/**
	 * Adds a message object to the cache.
	 * 
	 * @param msg The message object
	 */
	private void addToCache(final Message msg) {
		String userName = msg.getUser();
		if (!cache_.containsKey(userName)) {
			cache_.put(userName, new Stack<Message>());
		}
		cache_.get(userName).push(msg);
		if (cache_.get(userName).size() > CACHE_SIZE) {
			cache_.get(userName).remove(0);
		}
	}
	
	public void addToCache(final String msg)  {
		Message tMsg = new Message(details_.getNickName(), channel_.getChannelName(), msg);
		addToCache(tMsg);
	}

	/**
	 * Stack of messages from a single user.
	 * 
	 * @param userName The name of the user
	 * 
	 * @return The user's cache
	 */
	private Stack<Message> getUserCache(final String userName) {
		Stack<Message> userCache = new Stack<Message>();
		if (cache_.containsKey(userName)) {
			userCache.addAll(cache_.get(userName));
		}
		return userCache;
	}

	/**
	 * Dumps entire cache into query window.
	 * 
	 * @param target  The username of the requester
	 * @param channel The channel originating the request
	 * @throws Exception Inherited badness from core
	 */
	private void dumpCache(final String target, final String channel) throws Exception {
		irc.sendPrivmsg(target, "sed cache for " + channel);
		for (String user : cache_.keySet()) {
			Stack<Message> userCache = getUserCache(user);

			irc.sendPrivmsg(target, " ");
			irc.sendPrivmsg(target, user + ": " + userCache.size() + "/" + CACHE_SIZE);
			for (Message msg : userCache) {
				irc.sendPrivmsg(target, "\"" + msg.getMessage() + "\"");
			}
		}
	}

	/**
	 * Drops the entire sed cache and reinstantiates cache.
	 * 
	 * @param target The username of the requester
	 * 
	 * @param channel The channel originating the request
	 * 
	 * @throws Exception Inherited badness from core
	 */
	private void dropCache(final String target, final String channel) throws Exception {
		cache_ = new HashMap<String, Stack<Message>>();
		irc.sendPrivmsg(target, "dropped sed cache for " + channel);
	}

  @SuppressWarnings("serial")
  public class SedException extends Exception {

    public SedException(String message) {
      super(message);
    }

  }

  
	@Override
	public void getMenuItems(MenuItem rootItem) {

		MenuItem pluginRoot = rootItem;
		
		MenuItem sedInfo = new MenuItem(CMD_INFO, rootItem, 1, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 
				try {
					irc.sendPrivmsg(channel_.getChannelName(), HLP_INFO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public String onHelp() {
				return HLP_INFO;
			}
		};
		
		pluginRoot.addChild(sedInfo);
		
		MenuItem sedDumpCache = new MenuItem(CMD_SED_DUMP, rootItem, 2, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args, String username) { 
				try {
					dumpCache(username, channel_.getChannelName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public String onHelp() {
				return HLP_SED_DUMP;
			}
		};

		pluginRoot.addChild(sedDumpCache);
		
		MenuItem sedDropCache = new MenuItem(CMD_SED_DROP, rootItem, 3, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args, String username) { 
				try {
					dropCache(username, channel_.getChannelName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public String onHelp() {
				return HLP_SED_DROP;
			}
		};

		pluginRoot.addChild(sedDropCache);
		rootItem = pluginRoot;
	}

}
