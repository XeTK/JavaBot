package core;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.TimeThread;
/**
 * This holds all the plugins tied to a specific channel, along with the methods
 * to run the plugins, this is to encapsulate channels and ensure that data cannot
 * leak between the channels.
 * @author Tom Rosier(XeTK)
 */
public class Channel
{
	
	private String path = "servers/%s/%s/";
	
	// Keep the channel name & plugins save so they can be accessed later.
	private String channel_name;
	private String server_name;
	private ArrayList<Plugin> plugins;
	private TimeThread time_thread;
	
	/**
	 * Set the class up on creation, as we don't want to change theses details later.
	 * @param channelName this is the channels name, it uniquely identifies the channel.
	 * @throws Exception if there was an error then we need to throw an exception.
	 */
	public Channel(String channelName) throws Exception
	{		
		// Set our channel unique identifier.
		this.channel_name = channelName;
		
		String server = Details.getInstance().getServer();
	
		Matcher m = Pattern.compile("(?:[\\w\\d]*\\.)?([\\w\\d]*)\\..*",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(server);
		
		if (m.matches())
			this.server_name = m.group(1);
		
		// Strip special charectors 
		String cl_channel = channel_name.replace("#", "");
		
		path = String.format(path, server_name, cl_channel);
		
		File plugin_dir = new File(path);
		
		if (!plugin_dir.exists())
		{
			plugin_dir.mkdirs();
		}
		// Load the plugins
		loadPlugins();
	}
	
	/**
	 * Have this in a seperate method so that we can quickly reload the plugins.
	 * @throws Exception this is if we have any issues loading the plugins
	 */
	public void loadPlugins() throws Exception
	{
		// Assign this channel with a fresh list of plugins that we can now manipulate.
		this.plugins = PluginCore.loadPlugins();
		
		String loadedMsg = "\u001B[33mPlugins Loaded: %s";
		
		System.out.println(String.format(loadedMsg, PluginCore.loadedPlugins(plugins)));
		
		// Call onCreate for each plugin to set them up ready for use.
		for (int i = 0;i < plugins.size();i++)
			plugins.get(i).onCreate(this);
		
		// Create a new TimeThread for our class, this will carry out actions on set times.
		if (time_thread != null)
			this.time_thread.interrupt();
		this.time_thread = new TimeThread(plugins);
		
		this.time_thread.start();
	}
	
	/**
	 * Handle the onMessage actions for each plugins under this method.
	 * @param in_message this is the message object passed in from the core of the program.
	 */
	public void onMessage(Message in_message)
	{
		IRC irc = IRC.getInstance();
		// Double check that the message is actually for this class.
		if (in_message.getChannel().equalsIgnoreCase(channel_name))
		{
			for (int i = 0;i< plugins.size();i++)
			{
				try
				{
					// If we are not asking for the help string then continue as per normal
					if (!in_message.getMessage().matches("^\\.help"))
					{
						plugins.get(i).onMessage(in_message);
					}
					else
					{
						// Get the help string for the plugin we are working on
						String helpString = plugins.get(i).getHelpString();
						// Send the help string to the user that asled for it.
						irc.sendPrivmsg(in_message.getUser(), helpString);
					}
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
	
	/**
	 * Handle all the onJoin commands for each plugin that is loaded.
	 * @param in_join this is the information object with the information about the user that has joined.
	 */
	public void onJoin(Join in_join)
	{
		if (in_join.getChannel().equalsIgnoreCase(channel_name))
		{
			for (int i = 0;i< plugins.size();i++)
			{
				try
				{
					plugins.get(i).onJoin(in_join);
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
	
	/**
	 * For ever user that quits we need to call the onQuit method for all the plugins.
	 * @param in_quit this is the information about the user that has quit.
	 */
	public void onQuit(Quit in_quit)
	{
		if (in_quit.getChannel().equalsIgnoreCase(channel_name))
		{
			for (int i = 0;i< plugins.size();i++)
			{
				try
				{
					plugins.get(i).onQuit(in_quit);
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
	
	/**
	 * If a user is kicked then we call the onKick method within each plugin.
	 * @param in_kick this is the information about the user that has been kicked.
	 */
	public void onKick(Kick in_kick)
	{
		if (in_kick.getChannel().equalsIgnoreCase(channel_name))
		{
			for (int i = 0;i< plugins.size();i++)
			{
				try
				{
					plugins.get(i).onKick(in_kick);
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
	/**
	 * This handles any other possible outcomes a user wants to deal with with 
	 * plugins, this sends the raw server strings to the plugin to be manipulated
	 * if need be.
	 * @param in_str this is the raw input data from the server.
	 */
	public void onRaw(String in_str)
	{
		for (int i = 0;i< plugins.size();i++)
		{
			try
			{
				plugins.get(i).rawInput(in_str);
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
	
	// Getters
	public String getChannel_name()
	{
		return channel_name;
	}

	public String getPath() {
		return path;
	}

	public String getServer_name() {
		return server_name;
	}

	public ArrayList<Plugin> getPlugins() {
		return plugins;
	}
	
}
