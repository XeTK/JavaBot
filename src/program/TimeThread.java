package program;

import java.io.IOException;
import java.util.ArrayList;

import plugin.PluginTemp;

public class TimeThread extends Thread
{
	private ArrayList<PluginTemp> plugins;
	public void run()
	{
		while (true)
		{
			try
			{
				for (int i = 0; i < plugins.size();i++)
					plugins.get(i).onTime("");
				super.sleep(1000);
			} 
			catch (IRCException e) {} catch (IOException e) {} catch (InterruptedException e) {}	
		}
	}
	public TimeThread()
	{
		plugins = Start.getInstance().getPluginsglob();
	}
}
