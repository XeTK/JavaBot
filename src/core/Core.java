package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.Regex;

/**
 * This is the core execution point of the program, this handles all messages &
 * passing them to the plugins, it also handles the connection process of
 * getting an open socket, interacting with the IRC server and sending the
 * appropriate data for the bot to function.
 *
 * @author Tom Rosier(XeTK)
 */
public class Core {

	/**
	 * The number of acceptable rejoins before we give up
	 */
	private final int MAX_REJOINS = 3;

	/**
	 * Matches valid IRC actions (PART, JOIN etc)
	 */
	private final String REG_ALL_CMD = ":(.*)!(?:~)?([\\w\\d\\.@-]*)\\s(PART|QUIT|JOIN|PRIVMSG|KICK)\\s(?::)?((?:#)?[\\d\\w]*)(?:.*)?";
	/**
	 * Matches INVITE actions
	 */
	private final String REG_INVITE  = ":([\\w\\d]*)!(?:~)?([\\w\\d@\\-.]*) INVITE ([\\w\\d]*) :(#[\\w\\d]*)";
	/**
	 * Matches PRIVMSG
	 */
	private final String REG_MESSAGE = ":(.*)!.*@(.*) PRIVMSG (.*) :(.*)";
	/**
	 * Matches JOIN
	 */
	private final String REG_JOIN    = ":(.*)!.*@(.*) JOIN :(#?.*)";
	/**
	 * Matches QUIT/PART
	 */
	private final String REG_PART    = ":(.*)!(.*@.*)\\s(QUIT|PART)(?:\\s(#[\\w\\d]*))?\\s:(.*)";
	/**
	 * Matches KICK
	 */
	private final String REG_KICK    = ":(.*)!(.*@.*) KICK (#.*) (.*) :(.*)";

	private List<Channel> channels_ = new ArrayList<Channel>();

	public void killBot(){
		IRC irc = IRC.getInstance();

		try {

			irc.sendServer("QUIT");

			for (Channel channel: channels_) {
				channel.getTimeThread().interrupt();
			}

			irc.closeConnection();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (IRCException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * This is the start point for the application after it has been launched,
	 * this is where we end up after we have gone past the static context.
	 *
	 * @throws Exception if there is an error we throw it up to the JVM.
	 */
	public Core() throws Exception {
		connect();
		mainLoop();
	}

	/**
	 * This is the method called to connect the IRC bot to the Server, it also
	 * executes the commands that are needed to make the bot interact correctly
	 * with the IRC server, it sets the values needed for the bot to connect.
	 *
	 * @throws Exception throws an exception up to the main if there is an issue.
	 */
	private void connect() throws Exception {
		IRC irc = IRC.getInstance();
		Details details = Details.getInstance();

		irc.connectServer(details.getServer(), details.getPort());

		String nick = details.getNickName();

		// Send the connection information to the IRC server to get us registered.
		irc.sendServer("NICK " + nick);
		irc.sendServer("USER " + nick + " 8 *" + ": " + nick + " " + nick);

		// Send all our startup commands from the details file.
		for (int i = 0; i < details.getStartup().length; i++) {
			irc.sendServer(details.getStartup()[i]);
		}

		channels_ = new ArrayList<Channel>();

		// Connect to all the channels that are stored within the details file
		for (int i = 0; i < details.getChannels().length; i++) {
			String chanName = details.getChannels()[i];
			irc.sendServer("JOIN " + chanName);
			channels_.add(new Channel(chanName));
		}
	}

	/**
	 * This is the place where all the magic happens, and the messages are
	 * received and processed and passed onto the plugins to be handled.
	 *
	 * @throws Exception problems are thrown up to the main to be handled by the JVM.
	 */
	private void mainLoop() throws Exception {
		IRC irc = IRC.getInstance();
		Details details = Details.getInstance();

		List<PrivMsg> privMsgs = new ArrayList<PrivMsg>();

		// Keep a rejoin count so we determine if its worth retrying to connect.
		int rejoins = 0;

		// Enter the while loop never to return
		while (true) {
			try {
				// grab whatever's waiting for us
				String output = irc.getFromServer();

				// If it is null it usually means we have been disconnected from the server.
				if (output == null) {
					/*
					 *  If we have greater the number of rejoin attempts that we
					 *  are aloud then we exit the application.
					 */
					if (rejoins > MAX_REJOINS)
						System.exit(0);

					// do the reconnection dance
					irc.closeConnection();
					connect();
					rejoins++;

					/*
					 * The output string is null so we don't want to continue
					 * parsing it so we loop back to get the new output from the server.
					 */
					continue;
				}

				Matcher m;

				m = Regex.getMatcher(REG_ALL_CMD, output);

				if (m.find()) {
					String user    = m.group(1);
					String channel = m.group(4);

					if (channel.charAt(0) == '#') {
						// channel message
						boolean found = false;

						for (int i = 0; i < channels_.size(); i++) {
							if (channels_.get(i).getChannelName().equalsIgnoreCase(channel)) {
								found = true;
								break;
							}
						}

						if (!found) {
							channels_.add(new Channel(channel));
						}
					} else {
						// private message (query)
						boolean found = false;

						for (int i = 0; i < privMsgs.size(); i++) {
							if (privMsgs.get(i).getUserName().equalsIgnoreCase(user)) {
								found = true;
								break;
							}
						}

						if (!found) {
							privMsgs.add(new PrivMsg(user));
						}
					}
				}

				// Give Channel objects an opportunity to respond to the server
				for (int i = 0; i < channels_.size(); i++)
					channels_.get(i).onRaw(output);

				// Process Invites
				m = Regex.getMatcher(REG_INVITE, output);
				if (m.find()) {
					String chanName = m.group(4);
					boolean memberOfChannel = false;

					for (Channel channel: channels_) {
						if (channel.getChannelName().equals(chanName)) {
							memberOfChannel = true;
							break;
						}
					}

					if (!memberOfChannel) {
						irc.sendServer("JOIN " + chanName);
						channels_.add(new Channel(chanName));
					}

					continue;
				}

				/*
				 * For the next few methods, the process is the same, it is
				 * triggering the valid onCommand method for the output given by
				 * the IRC server, it loops through and deploys the plugins
				 * functions for messages, users joined, users quit, and finally
				 * users kicked. There is a regex that matches the string from
				 * the server to the relevant command before triggering a
				 * continue statement as once we found the string once its not
				 * going to be needed again, so we can get the next output from
				 * the server.
				 */

				// On Message
				m = Regex.getMatcher(REG_MESSAGE, output);
				if (m.find()) {
					Message message = new Message(m);
					if (message.isPrivMsg()) {
						for (int i = 0; i < privMsgs.size(); i++)
							privMsgs.get(i).onMessage(message);
					} else {
						for (int i = 0; i < channels_.size(); i++)
							channels_.get(i).onMessage(message);
					}
					continue;
				}

				// On Join
				m = Regex.getMatcher(REG_JOIN, output);
				if (m.find()) {
					Join join = new Join(m);
					// make sure we're not talking to ourselves
					if (!join.getUser().equalsIgnoreCase(details.getNickName()))
						for (int i = 0; i < channels_.size(); i++)
							channels_.get(i).onJoin(join);
					continue;
				}

				// On Quit
				m = Regex.getMatcher(REG_PART, output);
				if (m.find()) {
					Quit quit = new Quit(m);
					for (int i = 0; i < channels_.size(); i++)
						channels_.get(i).onQuit(quit);
					continue;
				}

				// On Kick
				m = Regex.getMatcher(REG_KICK, output);
				if (m.find()) {
					Kick kick = new Kick(m);
					for (int i = 0; i < channels_.size(); i++)
						channels_.get(i).onKick(kick);
					continue;
				}

				// Ping Pong
				if (output.split(" ")[0].equals("PING")) {
					irc.sendServer("PONG " + output.split(" ")[1]);
				}

				/*
				 *  If we have one successful run this means that we connected
				 *  successfully and can reset the rejoin attempts.
				 */
				rejoins = 0;
			} catch (Exception ex) {
				throw new IRCException(ex);
			}
		}
	}
}
