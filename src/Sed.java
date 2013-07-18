import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import core.utils.IRC;

public class Sed implements PluginTemp
{
	private static final int CACHE_SIZE = 10; // per user

	private Map<String, Stack<Message>> cache = new HashMap<String, Stack<Message>>();

	public String name()
	{
		return "Sed";
	}

	@Override
	public void onMessage(Message in_message) throws Exception
	{
		IRC irc = IRC.getInstance();

		String message = in_message.getMessage();
		String channel = in_message.getChannel();
		String user = in_message.getUser();

		// help string
		if (message.matches("^\\.help") || message.matches("^\\."))
			irc.sendPrivmsg(
					channel,
					"SED: "
							+ "[<username>: ]s/<search>/<replacement>/ - e.g XeTK: s/.*/hello/ is used to replace the previous statement with hello :");

		// set up sed finding regex
		Matcher m = Pattern
				.compile(
						"(?:([\\s\\w]*):\\s)?s/([\\s\\w\\d\\$\\*\\.]*)/([\\s\\w\\d]*)(?:/)?",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						message);

		// it's sed time, baby
		if (m.find())
		{
			String targetUser = new String();
			String replacement = new String();
			String search = new String();

			if (m.group(1) != null)
				targetUser = m.group(1);
			search = m.group(2);
			replacement = m.group(3);

			Stack<Message> userCache = new Stack<Message>();
			boolean hasTarget = !targetUser.equals(new String());
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

				m = Pattern.compile(search,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						mmessage.getMessage());

				if (m.find())
				{
					String reply = new String();
					if (hasTarget)
						reply = user + " thought ";

					reply += "%s meant : %s";
					irc.sendPrivmsg(channel, String.format(
							reply,
							mmessage.getUser(),
							mmessage.getMessage().replaceAll(search,
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

	public void emptyCache() {
		cache = new HashMap<String, Stack<Message>>();
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
