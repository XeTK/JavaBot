package program;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;

import plugin.PluginLoader;
import plugin.PluginTemp;

public class Start
{
	private static Start startInstance;
	
	private static final String cfgFile = "Details.json";
	private static String pluginPath = System.getProperty("user.dir");
        
	private ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
	
	public static void main(String[] args) throws Exception
	{
            for (int i = 0; i < args.length;i++)
                if (args[i].equals("-p"))
                    pluginPath = args[++i];
            
            if (new File(cfgFile).exists())
                    Details.setInstance((Details)JSON.loadGSON(cfgFile, Details.class));
            else
                    JSON.saveGSON(cfgFile, Details.getIntance());

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
				m = Pattern.compile(":(.*)!.*@(.*) PRIVMSG (#.*) :(.*)",
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
				m = Pattern.compile(":([a-zA-Z0-9]*)!([a-zA-Z0-9@\\-\\.]*) KICK (#[a-zA-Z0-9]*) ([a-zA-Z0-9]*) :(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(output);
				
				if (m.find())
					for (int i = 0;i< pluginsglob.size();i++)
						pluginsglob.get(i).onKick(new Kick(m));
				
				//Respond to pings
				if (output.split(" ")[0].equals("PING"))
					irc.sendServer("PONG " + output.split(" ")[1]);
					
				rejoins = 0;
			}
			catch (Exception ex)
			{
				String[] admins = Details.getIntance().getAdmins();
				for (int i = 0; i < admins.length;i++)
					irc.sendServer("PRIVMSG " + admins[i] + " " + ex.toString());
				System.err.println(ex.toString());
			}
		}
			
	}
	
	private void loadPlugins() throws Exception
	{
		pluginsglob = new ArrayList<PluginTemp>();
		File dir = new File(pluginPath);
		
		System.out.println("\u001B[33mPlugins Dir: " + dir.toString());
		
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
		System.out.println("Plugins Loaded : " + loadedPlugins() +
							"\nNumber of Plugins Loaded : " + pluginsglob.size());
	}
	
	public String loadedPlugins()
	{
		String lp = "";
		for (int i = 0; i < pluginsglob.size();i++)
			lp += pluginsglob.get(i).name() + ", ";
		return lp;
	}
	
	public void reloadPlugins() throws Exception
	{
		pluginsglob = new ArrayList<PluginTemp>();
		loadPlugins();
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate();
	}

	public ArrayList<PluginTemp> getPluginsglob()
	{
		return pluginsglob;
	}
	
}
