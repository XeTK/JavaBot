package core;

import java.util.ArrayList;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.TimeThread;
import core.plugin.PluginTemp;
import core.plugin.PluginsCore;

public class Channel
{
	private String channel_name;
	private ArrayList<PluginTemp> plugins;
	
	public Channel(String channelName) throws Exception
	{
		this.channel_name = channelName;
		this.plugins = PluginsCore.loadPlugins();
		
		for (int i = 0;i < plugins.size();i++)
			plugins.get(i).onCreate();
		
		new TimeThread(plugins).start();
	}
	public void onMessage(Message in_message) throws Exception
	{
		if (in_message.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onMessage(in_message);
	}
	public void onJoin(Join in_join) throws Exception
	{
		if (in_join.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onJoin(in_join);
	}
	public void onQuit(Quit in_quit) throws Exception
	{
		if (in_quit.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onQuit(in_quit);
	}
	public void onKick(Kick in_kick) throws Exception
	{
		if (in_kick.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onKick(in_kick);
	}
}
