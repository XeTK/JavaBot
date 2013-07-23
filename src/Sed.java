import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.Colour;
import core.plugin.PluginTemp;
import core.utils.Details;
import core.utils.IRC;

public class Sed implements PluginTemp
{
	private static final int CACHE_SIZE = 10; // per user
	private static final char ASCII_SOH = (char)1;

	private Details details = Details.getInstance();
	private IRC irc = IRC.getInstance();

	private Map<String, Stack<Message>> cache = new HashMap<String, Stack<Message>>();

	public String name()
	{
		return "Sed";
	}

	@Override
	public void onMessage(Message in_message) throws Exception
	{
		String message = in_message.getMessage();
		String channel = in_message.getChannel();
		String user = in_message.getUser();

		// help string
		if (message.matches("^\\.help") || message.matches("^\\.")) {
			irc.sendPrivmsg(channel,"SED: "
							+ "[<username>: ]s/<search>/<replacement>/ - e.g XeTK: s/.*/hello/ is used to replace the previous statement with hello :");
			return;
		}

		// debug commands
		if (message.matches("^\\.seddumpcache") && details.isAdmin(user)) {
			dumpCache(user, channel);
			return;
		}

		if (message.matches("^\\.seddropcache") && details.isAdmin(user)) {
			dropCache(user, channel);
			return;
		}

		// set up sed finding regex
		// (?:      starts a non-capture group
		// ([\\w]+) captures the username
		// )?       makes the group optional
		// ([^/]+)  captures the search string
		// ([^/]*)  captures the replacement
		Pattern sedFinder = Pattern.compile("^(?:([\\w]+): )?s/([^/]+)/([^/]*)/?");
		Matcher m = sedFinder.matcher(message);

		// it's sed time, baby
		if (m.find())
		{
			String targetUser = new String();
			if (m.group(1) != null)
				targetUser = m.group(1);
			String search = m.group(2);
			String replacement = m.group(3);

			Stack<Message> userCache = new Stack<Message>();
			boolean hasTarget = (!targetUser.equals(new String()) && !targetUser.equals(user));
			if (!hasTarget)
			{
				// no target, use last message from sourceUser's cache
				// (or do nothing)
				userCache = getUserCache(user);
			}
			else
			{
				// target specified, run through the cache (most recent first)
				// and either match and replace or do nothing
				userCache = getUserCache(targetUser);
			}

			while (!userCache.empty())
			{
				Message mmessage = userCache.pop();
				String text = mmessage.getMessage();

				if (isActionMessage(text))
					text = processActionMessage(text, mmessage.getUser());

				m = Pattern.compile(search,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);

				if (m.find())
				{
					String reply = new String();
					if (hasTarget)
						reply = user + " thought ";

					reply += "%s meant : %s";
					irc.sendPrivmsg(channel, String.format(
							reply,
							mmessage.getUser(),
							text.replaceAll(search,
									replacement)));
					break;
				}
			}

		}
		else // no dice, add message to history queue
		{
			addToCache(in_message);
		}
	}

	private boolean isActionMessage(String msg) {
		return msg.startsWith(ASCII_SOH + "ACTION") &&
					 msg.endsWith(Character.toString(ASCII_SOH));
	}

	private String processActionMessage(String msg, String user) {
		if (!isActionMessage(msg))
			return msg;

		// remove special chars
		msg = msg.substring(1);
		msg = msg.substring(0, msg.length() - 1);

		// format for output
		msg = msg.replace("ACTION", Colour.colour("* " + user, Colour.MAGENTA));
		return msg;
	}

	private void addToCache(Message msg) {
		String username = msg.getUser();
		if (!cache.containsKey(username))
			cache.put(username, new Stack<Message>());
		cache.get(username).push(msg);
		if (cache.get(username).size() > CACHE_SIZE)
			cache.get(username).remove(0);
	}

	private Stack<Message> getUserCache(String username) {
		Stack<Message> userCache = new Stack<Message>();
		if (cache.containsKey(username))
			userCache.addAll(cache.get(username));
		return userCache;
	}

	private void dumpCache(String target, String channel) throws Exception {
		irc.sendPrivmsg(target, "sed cache for " + channel);
		for (String user : cache.keySet()) {
			Stack<Message> userCache = getUserCache(user);

			irc.sendPrivmsg(target, " ");
			irc.sendPrivmsg(target, user + ": " + userCache.size() + "/" + CACHE_SIZE);
			for (Message msg : userCache) {
				irc.sendPrivmsg(target, "\"" + msg.getMessage() + "\"");
			}
		}
	}

	private void dropCache(String target, String channel) throws Exception {
		cache = new HashMap<String, Stack<Message>>();
		irc.sendPrivmsg(target, "dropped sed cache for " + channel);
	}

	@Override
	public void onCreate() throws Exception
	{
	}

	@Override
	public void onTime() throws Exception
	{
	}

	@Override
	public void onJoin(Join in_join) throws Exception
	{
	}

	@Override
	public void onQuit(Quit in_quit) throws Exception
	{
	}

	@Override
	public void onKick(Kick in_kick) throws Exception
	{
	}
}
