package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.menu.MenuItem;
import core.menu.MenuNav;
import core.menu.UserLoc;
import core.menu.UserLocHandle;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.Colour;
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.Regex;
import core.utils.RegexFormatter;
import core.utils.TimeThread;

/**
 * This holds all the plugins tied to a specific channel, along with the methods
 * to run the plugins, this is to encapsulate channels and ensure that data
 * cannot leak between the channels.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Channel {
	
	
	private final String TXT_LOADED     = "\u001B[33mPlugins Loaded: %s";
	private final String TXT_NOT_LOADED = "\u001B[33mPlugins Not Loaded: %s";
	private final String TXT_MENU_CMD   = "(" + Details.getInstance().getNickName() + ":\\s|\\?|" + Details.getInstance().getCMDPrefix() +  ")(.*)";
	
	private final String REG_GET_SERVER = "(?:[\\w\\d]*\\.)?([\\w\\d]*)\\..*";
	private final String REG_MENU_CMD   = "([a-zA-Z]*)(?:\\s(.*))?";

	// Keep the channel name & plugins save so they can be accessed later.
	private String       channelName_;
	private String       serverName_;
	
	private String       path_          = "servers/%s/%s/";
	private final String blackListFile_ = "BlackListed.txt";
	
	private IRC irc = IRC.getInstance();
	
	private ArrayList<Plugin> plugins_;
	
	private TimeThread timeThread_;
	
	private UserLocHandle uLH;
	
	private void buildMenu() {
		MenuItem root = new MenuItem("ROOT", null, 0);
		int index = 0;
		for (Plugin plugin: plugins_) {
			MenuItem pluginRoot = new MenuItem(plugin.name(), root, index);
			plugin.getMenuItems(pluginRoot);
			root.addChild(pluginRoot);
			index++;
		}
		uLH = new UserLocHandle(root);
	}
	
	private void handleMenu(Message inMessage) {
		try {
			Matcher m = Regex.getMatcher(TXT_MENU_CMD, inMessage.getMessage());
			if (m.matches()) {
				Details details = Details.getInstance();
				MenuItem cL = null;
				UserLoc uL = uLH.getUser(inMessage.getUser());
				
				if (m.group(1).startsWith(details.getNickName())) {
					String menuCMD = m.group(2);
					if (menuCMD.endsWith("back") || menuCMD.endsWith("b")){
						cL = MenuNav.preLevel(uL);
					} else if (menuCMD.endsWith("root") || menuCMD.endsWith("r")){
						cL = MenuNav.returnToRoot(uL);
					} else if (menuCMD.endsWith("list") || menuCMD.endsWith("l")) {
						cL = uL.getCurLoc();
						String cmdDir = new String();
						ArrayList<MenuItem> children = uL.getCurLoc().getChildren();
						
						for (MenuItem child : children) {
							cmdDir += "|| " + child.getNodeName() + " ";
						}
						
						cmdDir += "|| ";
						cmdDir = Colour.colour(cmdDir, Colour.GREEN, Colour.BLUE);
						
						irc.sendPrivmsg(inMessage.getChannel(), cmdDir);
					} else if (menuCMD.endsWith("location") || menuCMD.endsWith("p")) {
						cL = uL.getCurLoc();
						irc.sendPrivmsg(inMessage.getChannel(), "Cur Menu : " + cL.getNodeName());
					} else {
						ArrayList<MenuItem> children = uL.getCurLoc().getChildren();
						m = Regex.getMatcher(REG_MENU_CMD, menuCMD);
						
						if (m.find()) {
							String pluginName = m.group(1).toLowerCase();
							String args       = m.group(2);
							
							for (MenuItem child : children) {
								if (child.getNodeName().toLowerCase().equals(pluginName)) {
									MenuItem tNode = MenuNav.selectNode(uL, child.getNodeNumber());
									if (args != null && args.equals("?")) {
										String helpText = MenuNav.helpNode(tNode, uL);
										irc.sendPrivmsg(inMessage.getChannel(),helpText);
									} else {
										MenuNav.executeNode(tNode, uL, args);
									}
									break;
								}
							}
						}
						cL = uL.getCurLoc();
					}
				} else if (m.group(1).startsWith("" + details.getCMDPrefix()) || m.group(1).startsWith("?")) {
					String menuCMD = m.group(2);
					if (menuCMD.contains("" + details.getCmdSeperator())) {
						String[] cmds = menuCMD.split("" + details.getCmdSeperator());
						char cmdPrefix = m.group(1).charAt(0);
						for (String cmd : cmds) {
							ArrayList<MenuItem> children = uL.getCurLoc().getChildren();

							m = Regex.getMatcher(REG_MENU_CMD, cmd);
							
							if (m.find()) {
								String pluginName = m.group(1).toLowerCase();
								String args       = m.group(2);
								
								for (MenuItem child : children) {
									if (child.getNodeName().toLowerCase().equals(pluginName)) {
										MenuItem tNode = MenuNav.selectNode(uL, child.getNodeNumber());
										if (cmdPrefix == '?') {
											String helpText = MenuNav.helpNode(tNode, uL);
											irc.sendPrivmsg(inMessage.getChannel(),helpText);
										} else {
											MenuNav.executeNode(tNode, uL, args);
										}
										break;
									}
								}
							}
						}
					}
				} 
			}
		} catch (IRCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Set the class up on creation, as we don't want to change theses details later.
	 * 
	 * @param  channelName this is the channels name, it uniquely identifies the channel.
	 * @throws Exception if there was an error then we need to throw an exception.
	 */
	public Channel(String channelName) throws Exception {
		// Set our channel unique identifier.
		this.channelName_   = channelName;

		// Strip special characters
		String cleanChannel = channelName.replace("#", "");
		String server       = Details.getInstance().getServer();

		Matcher m = Regex.getMatcher(REG_GET_SERVER, server);

		if (m.matches())
			this.serverName_ = m.group(1);

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
			BufferedReader br = new BufferedReader(new FileReader(path_ + blackListFile_));
			
			String line = br.readLine();
			
			while (line != null){
			    lines.add(line);
			    line = br.readLine();
			}
			
			br.close();
		} catch (Exception ex) {
				new IRCException(ex);
		}
		return lines;
	}
	
	public Plugin getPlugin(Class<?> classdef){
		for (Plugin plugin: plugins_) {
			
			if (plugin.getClass().equals(classdef)){
			//if (plugin.getClass().isAssignableFrom(classdef)) {
			//if (plugin.getClass().getName().equals(classdef.getName())) {
				return plugin;
			}
		}
		return null;
	}
	
	private ArrayList<Plugin> vettedList(ArrayList<Plugin> plugins){
		ArrayList<Plugin> vetted      = new ArrayList<Plugin>();
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
	 * @throws Exception this is if we have any issues loading the plugins
	 */
	public void loadPlugins() throws Exception {
		// Assign this channel with a fresh list of plugins that we can now manipulate.
		this.plugins_ = vettedList(PluginCore.loadPlugins());

		System.out.println(String.format(TXT_LOADED,PluginCore.loadedPlugins(plugins_)));
		
		System.out.println(String.format(TXT_NOT_LOADED,notLoaded()));
		
		buildMenu();

		// Call onCreate for each plugin to set them up ready for use.
		for (int i = 0; i < plugins_.size(); i++)
			plugins_.get(i).onCreate(this);

		// Create a new TimeThread for our class, this will carry out actions on set times.
		if (timeThread_ != null)
			this.timeThread_.interrupt();
		
		this.timeThread_ = new TimeThread(plugins_);

		this.timeThread_.start();
	}

	/**
	 * Handle the onMessage actions for each plugins under this method.
	 * 
	 * @param inMessage this is the message object passed in from the core of the program.
	 */
	public void onMessage(Message inMessage) {
		
		// Double check that the message is actually for this class.
		if (inMessage.getChannel().equalsIgnoreCase(channelName_)) {
			if (inMessage.getMessage().matches(TXT_MENU_CMD)) {
				handleMenu(inMessage);
			} else {
				for (int i = 0; i < plugins_.size(); i++) {
					try {
						plugins_.get(i).onMessage(inMessage);
					} catch (Exception ex) {
						new IRCException(ex);
					}
				}
			}
		}
	}

	/**
	 * Handle all the onJoin commands for each plugin that is loaded.
	 * 
	 * @param in_join this is the information object with the information about the user that has joined.
	 */
	public void onJoin(Join inJoin) {
		if (inJoin.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onJoin(inJoin);
				} catch (Exception ex) {
					new IRCException(ex);
				}
			}
		}
	}

	/**
	 * For ever user that quits we need to call the onQuit method for all the plugins.
	 * 
	 * @param inQuit this is the information about the user that has quit.
	 */
	public void onQuit(Quit inQuit) {
		if (inQuit.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onQuit(inQuit);
				} catch (Exception ex) {
					new IRCException(ex);
				}
			}
		}
	}

	/**
	 * If a user is kicked then we call the onKick method within each plugin.
	 * 
	 * @param inKick this is the information about the user that has been kicked.
	 */
	public void onKick(Kick inKick) {
		if (inKick.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onKick(inKick);
				} catch (Exception ex) {
					new IRCException(ex);
				}
			}
		}
	}

	/**
	 * This handles any other possible outcomes a user wants to deal with with
	 * plugins, this sends the raw server strings to the plugin to be
	 * manipulated if need be.
	 * 
	 * @param inStr this is the raw input data from the server.
	 */
	public void onRaw(String inStr) {
		for (int i = 0; i < plugins_.size(); i++) {
			try {
				plugins_.get(i).rawInput(inStr);
			} catch (Exception ex) {
				new IRCException(ex);
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
	public ArrayList<Plugin> getPlugins_() {
		return plugins_;
	}
	public void setPlugins_(ArrayList<Plugin> plugins_) {
		this.plugins_ = plugins_;
	}
	public TimeThread getTimeThread() {
		return timeThread_;
	}
	
	
	
}
