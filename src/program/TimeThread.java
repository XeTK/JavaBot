package program;

import java.io.IOException;
import java.util.ArrayList;

import plugin.PluginTemp;

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
			catch (IRCException e) {} 
			catch (IOException e) {} 
			catch (InterruptedException e) {}	
		}
	}
	
	/**
	 * When we deploy the class we want to get the plugins from Start.java
	 * so that we can reference them later, when this class is deployed we
	 * get an instance of start and pull the plugins into this one.
	 */
	public TimeThread()
	{
		plugins = Start.getInstance().getPluginsglob();
	}
}
