package core.helpers;

import java.util.ArrayList;

import core.plugin.PluginTemp;

/**
 * Thread to run the timed events as we can't just leave it in the main loop
 * as we wont loop till we have received input from the Server/Clients.
 * @author Tom Rosier(XeTK)
 */
public class TimeThread extends Thread
{
	//Keep a link to the plugins stored within Start.java
	private ArrayList<PluginTemp> plugins;
	
	/**
	 * Deploy are timed events on are separate thread.
	 * Keep looping till the application terminates, we sleep for a second
	 * before we loop back round again to run all the timed events again
	 */
	public void run()
	{
		while (true)
		{
			try
			{
				for (int i = 0; i < plugins.size();i++)
					plugins.get(i).onTime();
				
				super.sleep(1000);
			} 
			catch (Exception e) {} 
		}
	}
	

	public TimeThread(ArrayList<PluginTemp> plugins) throws Exception
	{
		this.plugins = plugins;
	}
}
