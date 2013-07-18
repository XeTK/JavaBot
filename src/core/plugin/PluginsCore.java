package core.plugin;

import java.io.File;
import java.util.ArrayList;

public class PluginsCore 
{
	
	public static ArrayList<PluginTemp> loadPlugins() throws Exception
	{
		final String plugin_dir = PluginsCore.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		ArrayList<PluginTemp> pluginsglob = new ArrayList<PluginTemp>();
		File dir = new File(plugin_dir);//System.getProperty("user.dir"));
		
		if (dir.exists() && dir.isDirectory()) 
		{
			String[] fi = dir.list();
			for (int i=0; i<fi.length; i++) 
			{
				File file = new File(plugin_dir + fi[i]);
				PluginTemp pf = (PluginTemp) new PluginLoader().loadClassOBJ(file);
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
		for (int i = 0;i < pluginsglob.size();i++)
			pluginsglob.get(i).onCreate();
	}

}
