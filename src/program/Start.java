package program;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import plugin.PluginLoader;
import plugin.PluginTemp;

import com.google.gson.Gson;


public class Start
{
	private static Start startInstance;
	
	private ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
	
	public static void main(String[] args) throws Exception
	{
		if (new File("details.json").exists())
			loadGSON("details.json");
		else
			saveGSON("details.json");
		
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

		}
		irc.closeConnection();
	}
	
	private static void loadGSON(String in_Path) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(in_Path));
		
		String tempWord = "", json = "";
		
		while ((tempWord = reader.readLine()) != null)
			json += tempWord + "\n";
		
		reader.close();
		
		Details.setInstance(new Gson().fromJson(json, Details.class));
	}
	
	private static void saveGSON(String in_Path) throws IOException
	{
		File filePath = new File(in_Path);
		
		if (!filePath.exists())
			filePath.createNewFile();
		else
			filePath.delete();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(new Gson().toJson(Details.getIntance()));
		writer.close();
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
