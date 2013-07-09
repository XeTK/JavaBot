package core;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.TimeThread;
import core.plugin.PluginTemp;
import core.plugin.PluginsCore;
import core.utils.Details;
import core.utils.IRC;

public class Core
{
	
	public Core() throws Exception
	{
        connect();
        mainLoop();
	}
	
	private void connect() throws Exception
	{
		IRC irc = IRC.getInstance();
		Details details = Details.getIntance();
		
		irc.connectServer(details.getServer(), details.getPort());
	
		String nick = details.getNickName();
	
        irc.sendServer("NICK " + nick);
		irc.sendServer("USER " + nick + " 8 *" + ": " + nick + " " + nick);

		for (int i = 0;i < details.getStartup().length;i++)
			irc.sendServer(details.getStartup()[i]);

		for (int i = 0;i < details.getChannels().length;i++)
			irc.sendServer("JOIN " + details.getChannels()[i]);
	}
	
	private void mainLoop() throws Exception
	{
		IRC irc = IRC.getInstance();
		
		ArrayList<PluginTemp> pluginsglob = PluginsCore.getInstance().getPluginsglob();
		//On Create
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate();
		
		new TimeThread().start();
		
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
				
				//On Message
				m = Pattern.compile(":(.*)!.*@(.*) PRIVMSG (.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
					for (int i = 0;i< pluginsglob.size();i++)
						pluginsglob.get(i).onMessage(new Message(m));
				
				//On Join
				m = Pattern.compile(":(.*)!.*@(.*) JOIN :(#?.*)",
			    		Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
					for (int i = 0;i< pluginsglob.size();i++)
						pluginsglob.get(i).onJoin(new Join(m));
				
				//On Quit
				m = Pattern.compile(":(.*)!(.*@.*) PART (#.*)",
			    		Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
					for (int i = 0;i< pluginsglob.size();i++)
						pluginsglob.get(i).onQuit(new Quit(m));
				
				//On Kick
				m = Pattern.compile(":(.*)!(.*@.*) KICK (#.*) (.*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
					for (int i = 0;i< pluginsglob.size();i++)
						pluginsglob.get(i).onKick(new Kick(m));
				
				for (int i = 0; i < pluginsglob.size();i++)
					pluginsglob.get(i).onOther(output);
					
				rejoins = 0;
			}
			catch (Exception ex)
			{
				String[] admins = Details.getIntance().getAdmins();
				for (int i = 0; i < admins.length;i++)
				{
					irc.sendPrivmsg(admins[i],
							ex.toString() + ", " + Arrays.toString(ex.getStackTrace()));
				}
			}
		}
	}
}
