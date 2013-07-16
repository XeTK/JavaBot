package core.plugin;

import java.io.File;
import java.util.ArrayList;

public class PluginsCore 
{
	
	public static ArrayList<PluginTemp> loadPlugins() throws Exception
	{
		ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
		File dir = new File(System.getProperty("user.dir"));
		
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

		return pluginsglob;
	}
	
	public static String loadedPlugins(ArrayList<PluginTemp> pluginsglob)
	{
		String lp = "";
		for (int i = 0; i < pluginsglob.size();i++)
			lp += pluginsglob.get(i).name() + ", ";
		return lp;
	}
	
	public static void reloadPlugins(ArrayList<PluginTemp> pluginsglob) throws Exception
	{
		pluginsglob = new ArrayList<PluginTemp>();
		loadPlugins();
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate();
	}

}
