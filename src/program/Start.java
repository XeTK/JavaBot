package program;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import plugin.PluginLoader;
import plugin.PluginTemp;

public class Start
{
	private static Start startInstance;
	
	private static final String cfgFile = "Details.json";
	private static final String version = "Java Bot v1.11";
	private static String pluginPath = System.getProperty("user.dir");
        
	private ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
	
	public static void main(String[] args) throws Exception
	{
            for (int i = 0; i < args.length;i++)
                if (args[i].equals("-p"))
                    pluginPath = args[++i];
            
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
	
	private void connect() throws Exception
	{
		IRC irc = IRC.getInstance();
		Details details = Details.getIntance();
		
		irc.connectServer(details.getServer(), details.getPort());
/*		for (int i = 0; i < 4;i++)
			irc.getFromServer();
*/		
		String nick = details.getNickName();
	
                irc.sendServer("Nick " + nick);
		irc.sendServer("USER " + nick + " 8 *" + ": " + nick + " " + nick);

		for (int i = 0;i < details.getStartup().length;i++)
			irc.sendServer(details.getStartup()[i]);

		for (int i = 0;i < details.getChannels().length;i++)
			irc.sendServer("JOIN " + details.getChannels()[i]);
	}
	
	private void mainLoop() throws Exception
	{
		IRC irc = IRC.getInstance();
		
		//On Create
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate("");
		
		new TimeThread().start();
		
		int rejoins = 0;
		
		while(true)
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

			if (output.split(" ")[1].equals("KICK"))
				for (int i = 0;i< pluginsglob.size();i++)
					pluginsglob.get(i).onKick(output);
			//Respond to pings
			if (output.split(" ")[0].equals("PING"))
				irc.sendServer("PONG " + output.split(" ")[1]);
				
			if (output.split(":")[1].equals("VERSION"))
				irc.sendServer("PRIVMSG " + output.split("!")[0].substring(1) + " " + version);
				

		}
	}
	
	private void loadPlugins() throws Exception
	{
		pluginsglob = new ArrayList<PluginTemp>();
		File dir = new File(pluginPath);
		
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
		System.out.println("\nNumber of plugins loaded : " + pluginsglob.size());
	}

	public ArrayList<PluginTemp> getPluginsglob()
	{
		return pluginsglob;
	}
	
}
