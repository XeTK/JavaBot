package plugin.admin;

import java.lang.management.ManagementFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Channel;
import core.event.Join;
import core.event.Message;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.Colour;
import core.utils.Details;
import core.utils.IRC;

/**
 * Handles admin tasks - joining channels - bot nick change - start/stop bot -
 * send raw data to server - reload plugins - mode changes
 * 
 * @author Tom Rosier(XeTK)
 */
public class Admin extends Plugin {
	private static final String RE_JOIN = String.format("^\\.%s %s$", "join", IRC.RE_CHAN);
	private static final String RE_PART = String.format("^\\.%s %s$", "part", IRC.RE_CHAN);
	private static final String RE_NICK = String.format("^\\.%s %s$", "nick", IRC.RE_NICK);

	private Channel uchannel_;
	private final Details details_ = Details.getInstance();
	private final IRC irc_ = IRC.instance;

	/**
	 * Get the name of the plugin so it can be presented to the end user.
	 */

	public void onCreate(Channel inChannel) throws Exception {
		this.uchannel_ = inChannel;
	}

	/**
	 * Process the new message and check if it is bound to a specific admin
	 * command.
	 */
	public void onMessage(Message inMessage) throws Exception {
		// Information about the message that is sent.
		String message = inMessage.getMessage();
		String user = inMessage.getUser();
		String channel = inMessage.getChannel();

		// Check if the user is an admin and is aloud to issues theses commands.
		if (details_.isAdmin(user)) {
			// Remove padding at the end of message. To stop any issues.
			if (message.charAt(message.length() - 1) == ' ')
				message = message.substring(0, message.length() - 1);

			// If we want to join a channel then we access this command.
			if (message.matches(RE_JOIN)) {
				String str[] = message.split(" ");
				irc_.sendServer("JOIN " + str[1]);
				irc_.sendPrivmsg(channel, "I Have Joined " + str[1]);
			} else if (message.matches(RE_PART)) {
				String str[] = message.split(" ");
				irc_.sendServer("PART " + str[1]);
				irc_.sendPrivmsg(channel, "I Have Parted " + str[1]);
			} else if (message.matches("^\\.quit")) {
				irc_.sendServer("QUIT Goodbye All!");
				System.exit(0);
			} else if (message.matches(RE_NICK)) {
				String str[] = message.split(" ");
				irc_.sendServer("NICK " + str[1]);
			} else if (message.matches("^\\.cmd .*")) {
				Matcher p = Pattern.compile("^\\.cmd (.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						message);

				if (p.find())
					irc_.sendServer(p.group(1));
			}
			/*
			 * else if (message.matches("^\\.exception")) {
			 * irc_.sendPrivmsg(channel, "Exception Thrown."); throw new
			 * Exception(); }
			 */
			else if (message.matches("^\\.gitpull")) {
				String msg = Colour.colour("Reloading from git!", Colour.RED,
						Colour.WHITE);

				irc_.sendPrivmsg(channel, msg);
				String pid = ManagementFactory.getRuntimeMXBean().getName();
				String[] ids = pid.split("@");
				Runtime.getRuntime()
						.exec(new String[] { "/bin/bash", "-c",
								"./git.sh " + ids[0] });
			} else if (message.matches("^\\.reload")) {
				String msg = Colour.colour("Reloading Plugins!", Colour.RED);
				irc_.sendActionMsg(channel, msg);
				uchannel_.loadPlugins();
			} else if (message.matches("^\\.loaded")) {
				String loaded = PluginCore.loadedPlugins(uchannel_.getPlugins());
				loaded = Colour.colour(loaded, Colour.BLUE_DARK, Colour.WHITE);
				String loadedString = "Plugins Loaded: %s";
				loadedString = String.format(loadedString, loaded);
				irc_.sendActionMsg(channel, loadedString);
			}
		}
	}

	public void onJoin(Join inJoin) throws Exception {
		irc_.sendServer("MODE " + inJoin.getChannel() + " +v "
				+ inJoin.getUser());
	}

	public String getHelpString() {
		return "ADMIN: \n" + ".join <channel> - Join Channel : \n"
				+ ".part <channel> - Part Channel : \n"
				+ ".quit - Kill Bot : \n"
				+ ".nick <nickname> - Change Bot's Nick : \n"
				+ ".help - Show Help Text : \n"
				+ ".loaded - Returns list of loaded plugins : \n"
				+ ".reload - Pulls from git and reloads the bot :";
	}
}
