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
import core.utils.RegexFormatter;

/**
 * Handles admin tasks - joining channels - bot nick change - start/stop bot -
 * send raw data to server - reload plugins - mode changes
 * 
 * @author Tom Rosier(XeTK)
 */
public class Admin extends Plugin {
	private Channel uchannel_;
	
	private final String REG_JOIN = RegexFormatter.format("join", 
			RegexFormatter.REG_CHAN);
	private final String REG_PART = RegexFormatter.format("part", 
			RegexFormatter.REG_CHAN);
	private final String REG_NICK = RegexFormatter.format("nick", 
			RegexFormatter.REG_NICK);
	
	private final String REG_QUIT = RegexFormatter.format("quit");
	private final String REG_CMD = RegexFormatter.format("cmd (.*)");
	private final String REG_EXCEPTION = RegexFormatter.format("exception");
	private final String REG_GIT_PULL = RegexFormatter.format("gitpull");
	private final String REG_RELOAD = RegexFormatter.format("reload");
	private final String REG_LOADED = RegexFormatter.format("loaded");
	private final String REG_NOT_LOADED = RegexFormatter.format("notloaded");
	
	private final String TXT_NOT_LOADED = "plugins not loaded: %s";
	private final String TXT_LOADED = "plugins loaded: %s";
	private final String TXT_RELOAD = "reloading plugins!";
	private final String TXT_GIT_PULL = "reloading from git!";
	private final String TXT_EXCEPTION = "Exception Thrown.";
	private final String TXT_QUIT = "GoodBye";
	private final String TXT_JOIN = "joined %s";
	private final String TXT_PART = "left %s";

	private IRC irc = IRC.getInstance();

	public void onCreate(Channel inChannel) throws Exception {
		this.uchannel_ = inChannel;
	}

	/**
	 * Process the new message and check if it is bound to a specific admin
	 * command.
	 */
	public void onMessage(Message inMessage) throws Exception {

		// Get the details for the bot so we can check if the user issuing the
		// command is a admin.
		Details details = Details.getInstance();

		// Information about the message that is sent.
		String message = inMessage.getMessage();
		String user = inMessage.getUser();
		String channel = inMessage.getChannel();
		
		// Check if the user is an admin and is aloud to issues theses commands.
		if (details.isAdmin(user)) {
			// Remove padding at the end of message. To stop any issues.
			if (message.charAt(message.length() - 1) == ' ')
				message = message.substring(0, message.length() - 1);

			// If we want to join a channel then we access this command.
			if (message.matches(REG_JOIN)) {
				String str[] = message.split(" ");
				irc.sendServer("JOIN " + str[1]);
				String msg = String.format(TXT_JOIN, str[1]);
				irc.sendActionMsg(channel, msg);
			} else if (message.matches(REG_PART)) {
				String str[] = message.split(" ");
				irc.sendServer("PART " + str[1]);
				String msg = String.format(TXT_PART, str[1]);
				irc.sendActionMsg(channel, msg);
			} else if (message.matches(REG_QUIT)) {
				irc.sendServer("QUIT " + TXT_QUIT);
				System.exit(0);
			} else if (message.matches(REG_NICK)) {
				String str[] = message.split(" ");
				irc.sendServer("NICK " + str[1]);
			} else if (message.matches(REG_CMD)) {
				Matcher p = Pattern.compile(REG_CMD,
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						message);

				if (p.find())
					irc.sendServer(p.group(1));
			} else if (message.matches(REG_EXCEPTION)) {
				irc.sendPrivmsg(channel, TXT_EXCEPTION); 
				throw new Exception(); 
			} else if (message.matches(REG_GIT_PULL)) {
				String msg = Colour.colour(TXT_GIT_PULL, Colour.RED, Colour.WHITE);
				irc.sendActionMsg(channel, msg);
				String pid = ManagementFactory.getRuntimeMXBean().getName();
				String[] ids = pid.split("@");
				Runtime.getRuntime()
						.exec(new String[] { "/bin/bash", "-c",
								"./git.sh " + ids[0] });
				
			} else if (message.matches(REG_RELOAD)) {
				String msg = Colour.colour(TXT_RELOAD, Colour.RED);
				irc.sendActionMsg(channel, msg);
				uchannel_.loadPlugins();
			}
		}
		if (!inMessage.isPrivMsg()){
			if (message.matches(REG_LOADED)) {
				String loaded = PluginCore.loadedPlugins(uchannel_.getPlugins());
				loaded = Colour.colour(loaded, Colour.GREEN, Colour.BLACK);

				String msg = String.format(TXT_LOADED, loaded);
				irc.sendActionMsg(channel, msg);
			} else if (message.matches(REG_NOT_LOADED)) {
				String notLoaded = uchannel_.notLoaded();
				notLoaded = Colour.colour(notLoaded, Colour.RED, Colour.WHITE);
				
				String msg = String.format(TXT_NOT_LOADED, notLoaded);
				irc.sendActionMsg(channel, msg);
			}
		}
	}

	public void onJoin(Join inJoin) throws Exception {
		IRC irc = IRC.getInstance();
		irc.sendServer("MODE " + inJoin.getChannel() + " +v "
				+ inJoin.getUser());
	}

	public String getHelpString() {
		return "ADMIN: \n" 
				+ "\t.join <channel> - Join Channel\n"
				+ "\t.part <channel> - Part Channel\n"
				+ "\t.quit - Kill Bot : \n"
				+ "\t.nick <nickname> - Change bot's Nick\n"
				+ "\t.loaded - Returns list of loaded plugins\n"
				+ "\t.notloaded - Returns list of plugins not loaded\n"
				+ "\t.reload - Reloads plugins from local directory without restarting the bot\n"
				+ "\t.gitpull - Pulls from git and reloads the bot\n"
				+ "\t.exception - This tests that admins can get exception notifications\n"
				+ "\t.cmd <raw irc message> - Admin command to execute commands directly on the irc server\n";
	}
}
