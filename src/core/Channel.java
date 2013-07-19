package core;

import java.util.ArrayList;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.TimeThread;
import core.plugin.PluginTemp;
import core.plugin.PluginsCore;
/**
 * This holds all the plugins tied to a specific channel, along with the methods
 * to run the plugins, this is to encapsulate channels and ensure that data cannot
 * leak between the channels.
 * @author Tom Rosier(XeTK)
 */
public class Channel
{
	// Keep the channel name & plugins save so they can be accessed later.
	private String channel_name;
	private ArrayList<PluginTemp> plugins;
	
	/**
	 * Set the class up on creation, as we don't want to change theses details later.
	 * @param channelName this is the channels name, it uniquely identifies the channel.
	 * @throws Exception if there was an error then we need to throw an exception.
	 */
	public Channel(String channelName) throws Exception
	{
		// Set our channel unique identifier.
		this.channel_name = channelName;
		
		// Assign this channel with a fresh list of plugins that we can now manipulate.
		this.plugins = PluginsCore.loadPlugins();
		
		// Call are on create methods for each plugin to set them up ready for use.
		for (int i = 0;i < plugins.size();i++)
			plugins.get(i).onCreate();
		
		// Create a new timed thread for are class, this will carry out actions on set times.
		new TimeThread(plugins).start();
	}
	
	/**
	 * Handle the onMessage actions for each plugins under this method.
	 * @param in_message this is the message object passed in from the core of the program.
	 * @throws Exception if there was an error during the duration of the execution of a plugin.
	 */
	public void onMessage(Message in_message) throws Exception
	{
		// Double check that the message is actually for this class.
		if (in_message.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onMessage(in_message);
	}
	
	/**
	 * Handle all the onJoin commands for each plugin that is loaded.
	 * @param in_join this is the information object with the information about the user that has joined.
	 * @throws Exception if there was a problem during execution of the plugin.
	 */
	public void onJoin(Join in_join) throws Exception
	{
		if (in_join.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onJoin(in_join);
	}
	
	/**
	 * For ever user that quits we need to call the onQuit method for all the plugins.
	 * @param in_quit this is the information about the user that has quit.
	 * @throws Exception if there was an error while the user was quitting.
	 */
	public void onQuit(Quit in_quit) throws Exception
	{
		if (in_quit.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onQuit(in_quit);
	}
	
	/**
	 * If a user is kicked then we call the onKick method within each plugin.
	 * @param in_kick this is the information about the user that has been kicked.
	 * @throws Exception if there was an error while dealing with kicking a user.
	 */
	public void onKick(Kick in_kick) throws Exception
	{
		if (in_kick.getChannel().equals(channel_name))
			for (int i = 0;i< plugins.size();i++)
				plugins.get(i).onKick(in_kick);
	}
	
	// Getters
	public String getChannel_name()
	{
		return channel_name;
	}
}
