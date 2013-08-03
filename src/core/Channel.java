package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
 * to run the plugins, this is to encapsulate channels and ensure that data
 * cannot leak between the channels.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Channel {

	private String path_ = "servers/%s/%s/";
	private final String blackListFile_ = "BlackListed.txt";
	
	// Keep the channel name & plugins save so they can be accessed later.
	private String channelName_;
	private String serverName_;
	private ArrayList<Plugin> plugins_;
	private TimeThread timeThread_;

	/**
	 * Set the class up on creation, as we don't want to change theses details
	 * later.
	 * 
	 * @param channelName
	 *            this is the channels name, it uniquely identifies the channel.
	 * @throws Exception
	 *             if there was an error then we need to throw an exception.
	 */
	public Channel(String channelName) throws Exception {
		// Set our channel unique identifier.
		this.channelName_ = channelName;

		String server = Details.getInstance().getServer();

		Matcher m = Pattern.compile("(?:[\\w\\d]*\\.)?([\\w\\d]*)\\..*",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(server);

		if (m.matches())
			this.serverName_ = m.group(1);

		// Strip special charectors
		String cleanChannel = channelName.replace("#", "");

		path_ = String.format(path_, serverName_, cleanChannel);

		File pluginDir = new File(path_);

		if (!pluginDir.exists()) {
			pluginDir.mkdirs();
		}
		
		File blackList = new File(path_ + blackListFile_);
		if (!blackList.exists())
			blackList.createNewFile();
		
		// Load the plugins
		loadPlugins();
	}
	private ArrayList<String> blackListedPlugins(){
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(path_ + blackListFile_
							));
			
			String line = br.readLine();
			while (line != null){
			    lines.add(line);
			    line = br.readLine();
			}
			br.close();
		} catch (Exception ex) {
			try {
				throw new IRCException(ex);
			} catch (IRCException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}
	private ArrayList<Plugin> vettedList(ArrayList<Plugin> plugins){
		ArrayList<Plugin> vetted = new ArrayList<Plugin>();
		ArrayList<String> blackListed = blackListedPlugins();
		
		for (int i = 0; i < plugins.size();i++){
			
			boolean found = false;
			
			for (int j = 0; j < blackListed.size();j++){
				if (plugins.get(i).name().equals(blackListed.get(j))){
					found = true;
					break;
				}
			}
			
			if (!found)
				vetted.add(plugins.get(i));
		}
		return vetted;
	}
	
	public String notLoaded(){
		String temp = new String();
		ArrayList<String> blackListed = blackListedPlugins();
		for (int i = 0; i < blackListed.size();i++)
			temp += blackListed.get(i) + ", ";
		if (!temp.isEmpty())
			return "[" + temp.substring(0, temp.length() -2) + "]";
		else
			return "[]";
	}
	
	/**
	 * Have this in a seperate method so that we can quickly reload the plugins.
	 * 
	 * @throws Exception
	 *             this is if we have any issues loading the plugins
	 */
	public void loadPlugins() throws Exception {
		// Assign this channel with a fresh list of plugins that we can now
		// manipulate.
		this.plugins_ = vettedList(PluginCore.loadPlugins());

		String loadedMsg = "\u001B[33mPlugins Loaded: %s";

		System.out.println(String.format(loadedMsg,
				PluginCore.loadedPlugins(plugins_)));
		System.out.println("\u001B[33mPlugins Not Loaded: " + notLoaded());

		// Call onCreate for each plugin to set them up ready for use.
		for (int i = 0; i < plugins_.size(); i++)
			plugins_.get(i).onCreate(this);

		// Create a new TimeThread for our class, this will carry out actions on
		// set times.
		if (timeThread_ != null)
			this.timeThread_.interrupt();
		this.timeThread_ = new TimeThread(plugins_);

		this.timeThread_.start();
	}

	/**
	 * Handle the onMessage actions for each plugins under this method.
	 * 
	 * @param inMessage
	 *            this is the message object passed in from the core of the
	 *            program.
	 */
	public void onMessage(Message inMessage) {
		IRC irc = IRC.getInstance();
		// Double check that the message is actually for this class.
		if (inMessage.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					// If we are not asking for the help string then continue as
					// per normal
					if (!inMessage.getMessage().matches("^\\.help")) {
						plugins_.get(i).onMessage(inMessage);
					} else {
						// Get the help string for the plugin we are working on
						String helpString = plugins_.get(i).getHelpString();
						// Send the help string to the user that asled for it.
						irc.sendPrivmsg(inMessage.getUser(), helpString);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					try {
						throw new IRCException(ex);
					} catch (IRCException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Handle all the onJoin commands for each plugin that is loaded.
	 * 
	 * @param in_join
	 *            this is the information object with the information about the
	 *            user that has joined.
	 */
	public void onJoin(Join inJoin) {
		if (inJoin.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onJoin(inJoin);
				} catch (Exception ex) {
					try {
						throw new IRCException(ex);
					} catch (IRCException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * For ever user that quits we need to call the onQuit method for all the
	 * plugins.
	 * 
	 * @param inQuit
	 *            this is the information about the user that has quit.
	 */
	public void onQuit(Quit inQuit) {
		if (inQuit.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onQuit(inQuit);
				} catch (Exception ex) {
					try {
						throw new IRCException(ex);
					} catch (IRCException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * If a user is kicked then we call the onKick method within each plugin.
	 * 
	 * @param inKick
	 *            this is the information about the user that has been kicked.
	 */
	public void onKick(Kick inKick) {
		if (inKick.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onKick(inKick);
				} catch (Exception ex) {
					try {
						throw new IRCException(ex);
					} catch (IRCException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * This handles any other possible outcomes a user wants to deal with with
	 * plugins, this sends the raw server strings to the plugin to be
	 * manipulated if need be.
	 * 
	 * @param inStr
	 *            this is the raw input data from the server.
	 */
	public void onRaw(String inStr) {
		for (int i = 0; i < plugins_.size(); i++) {
			try {
				plugins_.get(i).rawInput(inStr);
			} catch (Exception ex) {
				try {
					throw new IRCException(ex);
				} catch (IRCException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Getters
	public String getChannelName() {
		return channelName_;
	}

	public String getPath() {
		return path_;
	}

	public String getServer_name() {
		return serverName_;
	}

	public ArrayList<Plugin> getPlugins() {
		return plugins_;
	}

}
