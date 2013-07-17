import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import core.utils.IRC;

public class Sed implements PluginTemp
{
	private static final int MESSAGE_HISTORY = 10;

	private ArrayList<Message> messages = new ArrayList<Message>();

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

		// get rid of old messages
		if (messages.size() > MESSAGE_HISTORY)
			messages.remove(0);

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

			// search message history
			for (int i = messages.size() - 1; i >= 0; i--)
			{
				Message mmessage = messages.get(i);

				m = Pattern.compile(search,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						mmessage.getMessage());

				if (m.find())
				{
					String reply = new String();
					// self-sedding
					if (!targetUser.equalsIgnoreCase(targetUser))
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

		} else
		// no dice, add message to history queue
		{
			messages.add(in_message);
		}
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
