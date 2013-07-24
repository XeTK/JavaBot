package core.plugin;

import java.io.File;
import java.util.ArrayList;
/**
 * This separates the plugin loading away from the core of the program.
 * It returns an ArrayList of the plugins that can be loaded and used by each channel.
 * @author Tom Rosier(XeTK)
 */
public class PluginCore
{
	/**
	 * This returns a ArrayList of plugins that can then be used by channels. 
	 * It loads them from the class directory of the program.
	 * @return's a list of plugins that then can be used by the each IRC channel.
	 * @throws Exception if there is a problem loading plugins then we throw an exception.
	 */
	public static ArrayList<Plugin> loadPlugins() throws Exception
	{
		// This is the location of the plugins. This is where the classes are deployed from usualy.
		final String plugin_dir = PluginCore.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		
		// We keep a list of the plugins that we have loaded.
		ArrayList<Plugin> pluginsglob = new ArrayList<Plugin>();
		
		// Create a file from the path of the directory where we keep the plugins
		File dir = new File(plugin_dir);//System.getProperty("user.dir"));
		System.out.println(plugin_dir);	
		// If the directory exists then it is infact a Directory then we can start loading plugins.
		if (dir.exists() && dir.isDirectory()) 
		{
			// List all the files in the directory for us to find classes to load.
			String[] fi = dir.list();
			for (int i=0; i<fi.length; i++) 
			{
				// Create a file of the file we have found in the Directory, so we can more easily load it if it is a class.
				File file = new File(plugin_dir + fi[i]);
				
				/* Pass the file to the Plugin loader which checks if the file is acceptable, 
				 * then passes it back to us if it has loaded correctly.
				 */
				Plugin pf = (Plugin) new PluginLoader().loadClassOBJ(file);
				// If the plugin was loaded correctly then it is finaly added to the list and is returned to the channel.
				if (pf != null)
					pluginsglob.add(pf);
			}
		}
		// Finally we return the list of plugins to the class that can then use them.
		return pluginsglob;
	}
	
	/**
	 * This returns a list of the plugins that are loaded and there names, which
	 * can then be printed to the screen.
	 * @param pluginsglob this is the list of plugins we want to get the names for.
	 * @return's a string containing the plugins that have been loaded.
	 */
	public static String loadedPlugins(ArrayList<Plugin> pluginsglob)
	{
		String lp = new String();
		// Loops through the plugins and gets the name of the plugins. and add it to the string.
		for (int i = 0; i < pluginsglob.size();i++)
			lp += pluginsglob.get(i).name() + ", ";
		return lp;
	}
}
