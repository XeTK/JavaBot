package core;

import java.util.ArrayList;

import core.event.Message;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.IRCException;

public class PrivMsg 
{
	private String user_name;

	private ArrayList<Plugin> plugins;
	
	public PrivMsg(String user_name) throws Exception
	{
		this.user_name = user_name;
		
		// Assign this channel with a fresh list of plugins that we can now manipulate.
		this.plugins = PluginCore.loadPlugins();
	}
	
	/**
	 * Handle the onMessage actions for each plugins under this method.
	 * @param in_message this is the message object passed in from the core of the program.
	 */
	public void onMessage(Message in_message)
	{
		if (in_message.isPrivMsg())
		{
			// Double check that the message is actually for this class.
			if (in_message.getChannel().equalsIgnoreCase(user_name))
			{
				for (int i = 0;i< plugins.size();i++)
				{
					try
					{
						plugins.get(i).onMessage(in_message);
					}
					catch (Exception ex)
					{
						try 
						{
							throw new IRCException(ex);
						} 
						catch (IRCException e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public String getUser_name() 
	{
		return user_name;
	}
}
