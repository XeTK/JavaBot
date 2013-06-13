package program;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import addons.UserList;

import plugin.PluginLoader;
import plugin.PluginTemp;


public class Start
{
	private static Start startInstance;
	
	private static final String cfgFile = "Details.json";
	private static final String version = "Java Bot v2.11";
			
	private ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
	
	public static void main(String[] args) throws Exception
	{
		if (new File(cfgFile).exists())
			Details.setInstance((Details)JSON.loadGSON(cfgFile,Details.class));
		else
			JSON.saveGSON(cfgFile,Details.getIntance());
		
		Start init = getInstance();
		
		init.loadPlugins();
		init.connect();
		init.mainLoop();
	}
	
	public static Start getInstance()
	{
		if (startInstance == null)
			startInstance = new Start();
		
		return startInstance;
	}
	
	private void connect() throws UnknownHostException, IOException, IRCException
	{
		IRC irc = IRC.getInstance();
		Details details = Details.getIntance();
		
		irc.connectServer(details.getServer(), details.getPort());
		
		for (int i = 0;i < details.getStartup().length;i++)
			irc.sendServer(details.getStartup()[i]);
		
		for (int i = 0;i < details.getChannels().length;i++)
			irc.sendServer("JOIN " + details.getChannels()[i]);
	}
	
	private void mainLoop() throws IOException, IRCException
	{
		IRC irc = IRC.getInstance();
		
		//On Create
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate("");
		
		new TimeThread().start();
		
		while(true)
		{			
			String output = irc.getFromServer();

			if (output == null)
				break;
			
			//On Message
			if (output.split(" ")[1].equals("PRIVMSG"))
				for (int i = 0;i< pluginsglob.size();i++)
					pluginsglob.get(i).onMessage(output);
			//On Join
			if (output.split(" ")[1].equals("JOIN"))
				for (int i = 0;i< pluginsglob.size();i++)
					pluginsglob.get(i).onJoin(output);
			//On Quit
			if (output.split(" ")[1].equals("PART"))
				for (int i = 0;i< pluginsglob.size();i++)
					pluginsglob.get(i).onQuit(output);
			//Respond to pings
			if (output.split(" ")[0].equals("PING"))
				irc.sendServer("PONG " + output.split(" ")[1]);
			
			if (output.split(":")[1].equals("VERSION"))
				irc.sendServer("PRIVMSG " + output.split("!")[0].substring(1) + " " + version);
				

		}
		irc.closeConnection();
	}
	
	private void loadPlugins() throws Exception
	{
		pluginsglob = new ArrayList<PluginTemp>();
		File dir = new File(System.getProperty("user.dir"));
		
		System.out.println("\u001B[33mPlugins Dir: " + dir.toString());
		System.out.print("Plugins Found : ");
		
		if (dir.exists() && dir.isDirectory()) 
		{
			String[] fi = dir.list();
			for (int i=0; i<fi.length; i++) 
			{
				PluginTemp pf = (PluginTemp) new PluginLoader().loadClassOBJ(fi[i]);
				if (pf != null)
					pluginsglob.add(pf);
			}
		}
		System.out.println();
	}

	public ArrayList<PluginTemp> getPluginsglob()
	{
		return pluginsglob;
	}
	
}
