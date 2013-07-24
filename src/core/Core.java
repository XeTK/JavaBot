package core;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
/**
 * This is the core execution point of the program, this handles all messages &
 * passing them to the plugins, it also handles the connection process of getting,
 * an socket opened and interacting with the IRC server and sending the appropriate 
 * data for the bot to function.
 * @author Tom Rosier(XeTK)
 */
public class Core
{
	// Keep a list of all the channels  we are currently connected to.
	private ArrayList<Channel> channels = new ArrayList<Channel>();
	
	/**
	 * This is the start point for the application after it has been launched, 
	 * this is where we end up after we have gone past the static context.
	 * @throws Exception if there is an error we throw it up to the JVM.
	 */
	public Core() throws Exception
	{
		// When the application first loads it needs to connect and open the connection to the server
        connect();
        // We then load into the main loop that keeps the program running.
        mainLoop();
	}
	
	/**
	 * This is the method called to connect the IRC bot to the Server, it also
	 * executes the commands that are needed to make the bot interact correctly
	 * with the IRC server, it sets the values needed for the bot to connect.
	 * @throws Exception throws an exception up to the main if there is an issue.
	 */
	private void connect() throws Exception
	{
		// Get an instance of the IRC class to send the messages on.
		IRC irc = IRC.getInstance();
		// Also get an instance of the details needed to connect to the server 
		Details details = Details.getInstance();
		
		// Open a connection to the server via the IRC class.
		irc.connectServer(details.getServer(), details.getPort());
	
		// Get the nickname of the bot before we use it to connect, it gets used alot...
		String nick = details.getNickName();
	
		// Send the connection information to the IRC server to get us registered.
        irc.sendServer("NICK " + nick);
		irc.sendServer("USER " + nick + " 8 *" + ": " + nick + " " + nick);

		// Send all our startup commands from the details file.
		for (int i = 0;i < details.getStartup().length;i++)
			irc.sendServer(details.getStartup()[i]);

		// Clear the current channels that we are connected to so we don't get duplicate channels.
		channels = new ArrayList<Channel>();
		
		// Connect to all the channels that are stored within the details file
		for (int i = 0;i < details.getChannels().length;i++)
		{	
			// Get the channels identifier.
			String chan_name = details.getChannels()[i];
			// Send join command to the server.
			irc.sendServer("JOIN " + chan_name);
			// Add the channel to are list of connected channels to make sure its tracked.
			channels.add(new Channel(chan_name));
		}
	}
	
	/**
	 * This is the place where all the magic happens, and the messages are 
	 * received and processed and passed onto the plugins to be handled.
	 * @throws Exception problems are thrown up to the main to be handled by the JVM.
	 */
	private void mainLoop() throws Exception
	{
		// Get an instance of the IRC class so we can later carry out operations on it.
		IRC irc = IRC.getInstance();
		Details details = Details.getInstance();
		
		// Keep a list of private messages.
		ArrayList<PrivMsg> privmsgs = new ArrayList<PrivMsg>();
		
		// Keep a rejoin count so we determine if its worth retrying to connect.
		int rejoins = 0;
		
		// Enter the while loop never to return
		while(true)
		{		
			// Try and carry out the actions given. Else we throw an IRC exception that is sent to one of the admin's.
			try
			{
				// Get the output from the server
				String output = irc.getFromServer();
	
				// If it is null it usually means we have been disconnected from the server.
				if (output == null)
				{
					// If we have greater the number of rejoin attempts that we are aloud then we exit the application.
					if (rejoins > 3)
						System.exit(0);
					// Else we close the connection we already have with the server.
					irc.closeConnection();
					// And try to reconnect to the server.
					connect();
					// Incrementing the number of times we have joined so we can decide if its worth rejoining.
					rejoins++;
					// The output string is null so we don't want to continue parsing it so we loop back to get the new output from the server.
					continue;
				}
				
				// Create are Regex matcher, it gets used alot so seems to make more sense to keep it separate.
				Matcher m;
				
				// Check if the output from the server matches a valid IRC channel action. Either a PART|JOIN|PRIVMSG|KICk
				m = Pattern.compile(":(.*)!(?:~)?([\\w\\d\\.@-]*)\\s(PART|JOIN|PRIVMSG|KICK)\\s(?::)?((?:#)?[\\d\\w]*)(?:.*)?",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				// If the message is a IRC channel message then we can check if it has been registered with the channels.
				if (m.find())
				{
					// Get the channel information from the string, so that we can compare it to the channels we have.
					String user = m.group(1);
					String channel = m.group(4);
					// Double check that it is a channel we are looking at, otherwise we have a private message.
					if (channel.charAt(0) == '#')
					{
						// Need a flag so we know if it has been found.
						boolean found = false;
						// Loop through the already registered channels, to see if we already have it.
						for (int i = 0; i < channels.size();i++)
						{
							// If the channel we are looking for matches the channel the message came from.
							if (channels.get(i).getChannel_name().equalsIgnoreCase(channel))
							{
								// Trip are boolean value so we know we found it.
								found = true;
								// Break from the loop we found what we are looking for get the hell out of here.
								break;
							}
						}
						// And if we didn't find the channel we wanted then we create it so we don't have any nasty exceptions.
						if (!found)
							channels.add(new Channel(channel));
					}
					else
					{
						// Same as with the channels just for private messages.
						boolean found = false;
						
						for (int i = 0; i < privmsgs.size();i++)
						{
							if (privmsgs.get(i).getUser_name().equalsIgnoreCase(user))
							{
								found = true;
								break;
							}
						}
						if (!found)
							privmsgs.add(new PrivMsg(user));
					}
							
				}
					
				// This supplys every plugin to have access to have the raw data from the server.
				for (int i = 0; i < channels.size();i++)
					channels.get(i).onRaw(output);
				
				// Process Invites
				m = Pattern.compile(":([\\w\\d]*)!(?:~)?([\\w\\d@\\-.]*) INVITE ([\\w\\d]*) :(#[\\w\\d]*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					irc.sendServer("JOIN " + m.group(4));
					continue;
				}
				
				/*
				 * For the next few methods, the process is the same, it is triggering the valid
				 * onCommand method for the output given by the IRC server, it loops through and 
				 * deploys the plugins functions for messages, users joined, users quit, and finally
				 * users kicked. There is a regex that matches the string from the server to the 
				 * relevant command before triggering a continue statement as once we found the string 
				 * once its not going to be needed again, so we can get the next output from the server.
				 */
				
				// On Message
				m = Pattern.compile(":(.*)!.*@(.*) PRIVMSG (.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Message message = new Message(m);
					if (message.isPrivMsg())
					{
						for (int i = 0; i < privmsgs.size();i++)
							privmsgs.get(i).onMessage(message);
					}
					else
					{
						for (int i = 0;i< channels.size();i++)
							channels.get(i).onMessage(message);
					}
					continue;
				}
				
				// On Join
				m = Pattern.compile(":(.*)!.*@(.*) JOIN :(#?.*)",
			    		Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Join join = new Join(m);
					// Check if the bot or not if it isn't then we continue
					if (!join.getUser().equalsIgnoreCase(details.getNickName()))
						for (int i = 0;i< channels.size();i++)
							channels.get(i).onJoin(join);
					continue;
				}
				
				// On Quit
				m = Pattern.compile(":(.*)!(.*@.*) PART (#.*)",
			    		Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Quit quit = new Quit(m);
					for (int i = 0;i< channels.size();i++)
						channels.get(i).onQuit(quit);
					continue;
				}
				
				// On Kick
				m = Pattern.compile(":(.*)!(.*@.*) KICK (#.*) (.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Kick kick = new Kick(m);
					for (int i = 0;i< channels.size();i++)
						channels.get(i).onKick(kick);
					continue;
				}
				
				// Respond to the IRC servers pings so the bot does not time out.
				if (output.split(" ")[0].equals("PING"))
					irc.sendServer("PONG " + output.split(" ")[1]);
					
				// If we have one successful run this means that we connected successfully and can reset the rejoin attempts.
				rejoins = 0;
			}
			catch (Exception ex)
			{
				throw new IRCException(ex);
			}
		}
	}
}
