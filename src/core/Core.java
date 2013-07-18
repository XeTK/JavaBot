package core;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.utils.Details;
import core.utils.IRC;

public class Core
{
	
	private ArrayList<Channel> channels = new ArrayList<Channel>();
	public Core() throws Exception
	{
        connect();
        mainLoop();
	}
	
	private void connect() throws Exception
	{
		IRC irc = IRC.getInstance();
		Details details = Details.getInstance();
		
		irc.connectServer(details.getServer(), details.getPort());
	
		String nick = details.getNickName();
	
        irc.sendServer("NICK " + nick);
		irc.sendServer("USER " + nick + " 8 *" + ": " + nick + " " + nick);

		for (int i = 0;i < details.getStartup().length;i++)
			irc.sendServer(details.getStartup()[i]);

		for (int i = 0;i < details.getChannels().length;i++)
		{	
			String chan_name = details.getChannels()[i];
			irc.sendServer("JOIN " + chan_name);
			channels.add(new Channel(chan_name));
		}
	}
	
	private void mainLoop() throws Exception
	{
		IRC irc = IRC.getInstance();
		
		int rejoins = 0;
		
		while(true)
		{		
			try
			{
				String output = irc.getFromServer();
	
				if (output == null)
				{
					if (rejoins > 3)
						System.exit(0);
					irc.closeConnection();
					connect();
					rejoins++;
					continue;
				}
				
				Matcher m;
				
				m = Pattern.compile(":(.*)!(?:~)?([\\w\\d\\.@-]*)\\s(PART|JOIN|PRIVMSG|KICK)\\s(?::)?((?:#)?[\\d\\w]*)(?:.*)?",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					String channel = m.group(4);
					if (channel.charAt(0) == '#')
					{
						boolean found = false;
						for (int i = 0; i < channels.size();i++)
						{
							if (channels.get(i).getChannel_name().equals(channel))
							{
								found = true;
								break;
							}
						}
						if (!found)
							channels.add(new Channel(channel));
					}
					else
					{
						System.out.println("Hello Privmsg");
					}
							
				}
						
				//On Message
				m = Pattern.compile(":(.*)!.*@(.*) PRIVMSG (.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Message message = new Message(m);
					for (int i = 0;i< channels.size();i++)
						channels.get(i).onMessage(message);
					continue;
				}
				
				//On Join
				m = Pattern.compile(":(.*)!.*@(.*) JOIN :(#?.*)",
			    		Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Join join = new Join(m);
					for (int i = 0;i< channels.size();i++)
						channels.get(i).onJoin(join);
					continue;
				}
				
				//On Quit
				m = Pattern.compile(":(.*)!(.*@.*) PART (#.*)",
			    		Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Quit quit = new Quit(m);
					for (int i = 0;i< channels.size();i++)
						channels.get(i).onQuit(quit);
					continue;
				}
				
				//On Kick
				m = Pattern.compile(":(.*)!(.*@.*) KICK (#.*) (.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
				{
					Kick kick = new Kick(m);
					for (int i = 0;i< channels.size();i++)
						channels.get(i).onKick(kick);
					continue;
				}
				
				//Respond to pings
				if (output.split(" ")[0].equals("PING"))
					irc.sendServer("PONG " + output.split(" ")[1]);
					
				rejoins = 0;
			}
			catch (Exception ex)
			{
				String[] admins = Details.getInstance().getAdmins();
				for (int i = 0; i < admins.length;i++)
				{
					irc.sendPrivmsg(admins[i],
							ex.toString() + ", " + Arrays.toString(ex.getStackTrace()));
				}
			}
		}
	}
}
