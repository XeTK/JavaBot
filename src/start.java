import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.google.gson.Gson;

import run.IRC;
import run.PluginClassLoader;


public class start
{
	private static ArrayList<Plugin> pluginsglob = new ArrayList<Plugin>();
	private static Details details = new Details();
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		//saveGSON("details.json");
		loadGSON("details.json");
		loadPlugins();
		connect();
		mainLoop();
	}
	private static void connect() throws UnknownHostException, IOException
	{
		IRC.connectServer(details.getServer(), details.getPort());
		IRC.sendServer("User " + details.getNickName() + " " + details.getName() + " " + details.getHost() + " :" + details.getName());
		IRC.sendServer("NICK " + details.getNickName());
		for (int i = 0;i < details.getChannels().length;i++)
			IRC.sendServer("JOIN " + details.getChannels()[i]);
	}
	private static void mainLoop() throws IOException
	{
		//On Create
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate("");
		
		while(true)
		{
			//:XeTK!xetk@cpc4-swin16-2-0-cust422.3-1.cable.virginmedia.com PRIVMSG #xetk :asdf
			
			String output = IRC.getFromServer();
			
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
				IRC.sendServer("PONG " + output.split(" ")[1]);

		}
		IRC.closeConnection();
	}
	private static void loadGSON(String in_Path) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(in_Path));
		String tempWord = "", json = "";
		while ((tempWord = reader.readLine()) != null)
			json += tempWord + "\n";
		reader.close();
		details = new Gson().fromJson(json, Details.class);
	}
	private static void saveGSON(String in_Path) throws IOException
	{
			File filePath = new File(in_Path);
			if (!filePath.exists())
				filePath.createNewFile();
			else
				filePath.delete();
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(new Gson().toJson(details));
			writer.close();
	}
	
	//http://www.javaranch.com/journal/200607/Plugins.html
	public static void loadPlugins() throws MalformedURLException
	{
		pluginsglob = new ArrayList<Plugin>();
		File dir = new File(System.getProperty("user.dir") + File.separator + "plugins");
		System.out.println(dir.toString());
		ClassLoader cl = new PluginClassLoader(dir);
		if (dir.exists() && dir.isDirectory()) 
		{
			// we'll only load classes directly in this directory -
			// no subdirectories, and no classes in packages are recognized
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) 
			{
				try 
				{
					// only consider files ending in ".class"
					if (! files[i].endsWith(".class"))
						continue;
					Class c = cl.loadClass(files[i].substring(0, files[i].indexOf(".")));
					Class[] intf = c.getInterfaces();
					for (int j=0; j<intf.length; j++) 
					{
						if (intf[j].getName().equals("Plugin")) 
						{
							// the following line assumes that PluginFunction has a no-argument constructor
							Plugin pf = (Plugin) c.newInstance();
							pluginsglob.add(pf);
							continue;
						}
					}
				} 
				catch (Exception ex) 
				{
					System.err.println("File " + files[i] + " does not contain a valid PluginFunction class.");
				}
			}
		}

	}
}
