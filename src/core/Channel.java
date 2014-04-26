package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

	private String       channelName_;
	private String       serverName_;

	private String       path_          = "servers/%s/%s/";
	private final String blackListFile_ = "BlackListed.txt";

	private IRC irc = IRC.getInstance();

	private List<Plugin> plugins_;

	private TimeThread timeThread_;

	private UserLocHandle uLH;

	private void buildMenu() {
		MenuItem root = new MenuItem("ROOT", null, 0);
		int index = 0;
		for (Plugin plugin: plugins_) {
			MenuItem pluginRoot = new MenuItem(plugin.name(), root, index);
			plugin.getMenuItems(pluginRoot);
			if (pluginRoot.getChildren().size() != 0)
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
						List<MenuItem> children = uL.getCurLoc().getChildren();

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
						List<MenuItem> children = uL.getCurLoc().getChildren();
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

						// Strip args away so they can't affect the operations.
						int ind = 0;
						if (menuCMD.contains(" ")) {
							ind = menuCMD.indexOf(' ');
						} else {
							ind = menuCMD.length();
						}

						// Breaking the command down without the args.
						String[] cmds = menuCMD.substring(0,ind).split("" + details.getCmdSeperator() + "");
						char cmdPrefix = m.group(1).charAt(0);

						// Adding the args back on once we have finished splitting the tree.
						cmds[cmds.length -1] += menuCMD.substring(ind);

						// Reset menu location to root before deploying command or we get a collison on names.
						MenuNav.returnToRoot(uL);

						for (String cmd : cmds) {
							List<MenuItem> children = uL.getCurLoc().getChildren();

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
	 * Constructor
	 *
	 * @param  channelName the channel's name
	 * @throws Exception
	 */
	public Channel(String channelName) throws Exception {
		this.channelName_   = channelName;

		// Strip special characters
		String cleanChannel = channelName.replace("#", "");
		String server       = Details.getInstance().getServer();

		Matcher m = Regex.getMatcher(REG_GET_SERVER, server);

		if (m.matches()) {
			this.serverName_ = m.group(1);
		}

		path_ = String.format(path_, serverName_, cleanChannel);

		File pluginDir = new File(path_);

		if (!pluginDir.exists()) {
			pluginDir.mkdirs();
		}

		File blackList = new File(path_ + blackListFile_);
		if (!blackList.exists()) {
			blackList.createNewFile();
		}

		loadPlugins();
	}

	private List<String> blackListedPlugins() {
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path_ + blackListFile_));

			String line = br.readLine();

			while (line != null){
				lines.add(line);
				line = br.readLine();
			}

			br.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.exit(1);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return lines;
	}

	public Plugin getPlugin(Class<?> classdef){
		for (Plugin plugin: plugins_) {
			if (plugin.getClass().equals(classdef)){
				return plugin;
			}
		}
		return null;
	}

	private List<Plugin> vettedList(List<Plugin> plugins) {
		List<Plugin> vetted      = new ArrayList<Plugin>();
		List<String> blackListed = blackListedPlugins();

		for (int i = 0; i < plugins.size();i++){
			boolean found = false;

			for (int j = 0; j < blackListed.size();j++){
				if (plugins.get(i).name().equals(blackListed.get(j))){
					found = true;
					break;
				}
			}

			if (!found) {
				vetted.add(plugins.get(i));
			}
		}
		return vetted;
	}

	public String notLoaded(){
		String temp = new String();

		List<String> blackListed = blackListedPlugins();

		for (int i = 0; i < blackListed.size();i++) {
			temp += blackListed.get(i) + ", ";
		}

		if (!temp.isEmpty()) {
			return "[" + temp.substring(0, temp.length() -2) + "]";
		}
		return "[]";
	}

	/**
	 * Loads plugins
	 *
	 * @throws Exception
	 */
	public void loadPlugins() throws Exception {
		this.plugins_ = vettedList(PluginCore.loadPlugins());

		System.out.println(String.format(TXT_LOADED, PluginCore.loadedPlugins(plugins_)));
		System.out.println(String.format(TXT_NOT_LOADED, notLoaded()));
		buildMenu();

		// Init plugins
		for (int i = 0; i < plugins_.size(); i++) {
			plugins_.get(i).onCreate(this);
		}

		// Init timer
		if (timeThread_ != null) {
			this.timeThread_.interrupt();
		}

		this.timeThread_ = new TimeThread(plugins_);
		this.timeThread_.start();
	}

	/**
	 * Handle the onMessage actions for each plugin under this method.
	 * 
	 * @param inMessage the message object
	 */
	public void onMessage(Message inMessage) throws IRCException {
		// Double check that the message is actually for this class.
		if (inMessage.getChannel().equalsIgnoreCase(channelName_)) {
			if (inMessage.getMessage().matches(TXT_MENU_CMD)) {
				handleMenu(inMessage);
			} else {
				for (int i = 0; i < plugins_.size(); i++) {
					try {
						plugins_.get(i).onMessage(inMessage);
					} catch (Exception ex) {
						throw new IRCException(ex);
					}
				}
			}
		}
	}

	/**
	 * Handle all the onJoin commands for each plugin that is loaded.
	 * 
	 * @param in_join the join event
	 */
	public void onJoin(Join inJoin) throws IRCException {
		if (inJoin.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onJoin(inJoin);
				} catch (Exception ex) {
					throw new IRCException(ex);
				}
			}
		}
	}

	/**
	 * Handle onQuit
	 *
	 * @param inQuit the quit event
	 */
	public void onQuit(Quit inQuit) throws IRCException {
		if (inQuit.getChannel().equalsIgnoreCase(channelName_)) {
			for (int i = 0; i < plugins_.size(); i++) {
				try {
					plugins_.get(i).onQuit(inQuit);
				} catch (Exception ex) {
					throw new IRCException(ex);
				}
			}
		}
	}

	/**
	 * Handle onKick
	 *
	 * @param inKick the kick event
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
	 * Handle onRaw
	 *
	 * @param inStr raw server response
	 */
	public void onRaw(String inStr) throws IRCException {
		for (int i = 0; i < plugins_.size(); i++) {
			try {
				plugins_.get(i).rawInput(inStr);
			} catch (Exception ex) {
				throw new IRCException(ex);
			}
		}
	}

	public String getChannelName() {
		return channelName_;
	}

	public String getPath() {
		return path_;
	}

	public String getServer_name() {
		return serverName_;
	}

	public List<Plugin> getPlugins() {
		return plugins_;
	}

	public List<Plugin> getPlugins_() {
		return plugins_;
	}

	public void setPlugins_(List<Plugin> plugins_) {
		this.plugins_ = plugins_;
	}

	public TimeThread getTimeThread() {
		return timeThread_;
	}

}
