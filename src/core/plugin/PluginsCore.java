package core.plugin;

import java.io.File;
import java.util.ArrayList;

public class PluginsCore 
{
	private ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
	
	private static PluginsCore pcInstance;
	
	private PluginsCore() throws Exception
	{
		loadPlugins();
	}
	
	public static PluginsCore getInstance() throws Exception
	{
		if (pcInstance == null)
			pcInstance = new PluginsCore();
		return pcInstance;
	}
	
	private void loadPlugins() throws Exception
	{
		pluginsglob = new ArrayList<PluginTemp>();
		File dir = new File(System.getProperty("user.dir"));
		
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
