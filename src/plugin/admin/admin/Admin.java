package plugin.admin.admin;

import java.lang.management.ManagementFactory;

import core.Channel;
import core.event.Join;
import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.Colour;
import core.utils.IRC;

/**
 * Handles admin tasks - joining channels - bot nick change - start/stop bot -
 * send raw data to server - reload plugins - mode changes
 * 
 * @author Tom Rosier(XeTK)
 */
public class Admin extends Plugin {
	private Channel channel_;
	
	private final String CMD_JOIN       = "join";
	private final String CMD_PART       = "part";
	private final String CMD_NICK       = "nick";
	private final String CMD_QUIT       = "quit";
	private final String CMD_CMD        = "cmd";
	private final String CMD_EXCEPTION  = "exception";
	private final String CMD_GIT_PULL   = "gitpull";
	private final String CMD_RELOAD     = "reload";
	private final String CMD_LOADED     = "loaded";
	private final String CMD_NOT_LOADED = "notloaded";
	
	private final String TXT_NOT_LOADED = "plugins not loaded: %s";
	private final String TXT_LOADED     = "plugins loaded: %s";
	private final String TXT_RELOAD     = "reloading plugins!";
	private final String TXT_GIT_PULL   = "reloading from git!";
	private final String TXT_EXCEPTION  = "Exception Thrown.";
	private final String TXT_QUIT       = "GoodBye";
	private final String TXT_JOIN       = "joined %s";
	private final String TXT_PART       = "left %s";

	private IRC irc = IRC.getInstance();

	public void onCreate(Channel inChannel) throws Exception {
		this.channel_ = inChannel;
	}

	public void onJoin(Join inJoin) throws Exception {
		IRC irc = IRC.getInstance();
		irc.sendServer("MODE " + inJoin.getChannel() + " +v " + inJoin.getUser());
	}

	public String getHelpString() {
		return "ADMIN: \n" 
				+ "\t.join <channel> - Join Channel\n"
				+ "\t.part <channel> - Part Channel\n"
				+ "\t.quit - Kill Bot : \n"
				+ "\t.nick <nickname> - Change bot's Nick\n"
				+ "\t.loaded - Returns list of loaded plugins\n"
				+ "\t.notloaded - Returns list of plugins not loaded\n"
				+ "\t.reload - Reloads plugins from local directory without restarting the bot\n"
				+ "\t.gitpull - Pulls from git and reloads the bot\n"
				+ "\t.exception - This tests that admins can get exception notifications\n"
				+ "\t.cmd <raw irc message> - Admin command to execute commands directly on the irc server\n";
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		MenuItem pluginRoot = rootItem;
		
		MenuItem adminJoin = new MenuItem(CMD_JOIN, rootItem, 1, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					irc.sendServer("JOIN " + args);
					String msg = String.format(TXT_JOIN, args);
					irc.sendActionMsg(channel_.getChannelName(), msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminJoin);
		
		MenuItem adminPart = new MenuItem(CMD_PART, rootItem, 2, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					irc.sendServer("PART " + args);
					String msg = String.format(TXT_PART, args);
					irc.sendActionMsg(channel_.getChannelName(), msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminPart);
		
		MenuItem adminQuit = new MenuItem(CMD_QUIT, rootItem, 3, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					String msg = String.format(TXT_QUIT, args);
					irc.sendActionMsg(channel_.getChannelName(), msg);
					irc.sendServer("QUIT " + args);
					irc.closeConnection();
					System.exit(0);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminQuit);
		
		MenuItem adminNick = new MenuItem(CMD_NICK, rootItem, 4, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					irc.sendServer("NICK " + args);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminNick);
		
		MenuItem adminCMD = new MenuItem(CMD_CMD, rootItem, 5, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					irc.sendServer(args);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminCMD);
		
		MenuItem adminException = new MenuItem(CMD_EXCEPTION, rootItem, 6, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					irc.sendPrivmsg(channel_.getChannelName(), TXT_EXCEPTION); 
					throw new Exception(); 
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminException);
		
		MenuItem adminGIT = new MenuItem(CMD_GIT_PULL, rootItem, 7, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					String msg = Colour.colour(TXT_GIT_PULL, Colour.RED, Colour.WHITE);
					irc.sendActionMsg(channel_.getChannelName(), msg);
					String pid = ManagementFactory.getRuntimeMXBean().getName();
					String[] ids = pid.split("@");
					Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", "./git.sh " + ids[0] });
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminGIT);
		
		MenuItem adminReload = new MenuItem(CMD_RELOAD, rootItem, 8, AuthGroup.ADMIN){
			@Override
			public void onExecution(String args) { 
				try {
					String msg = Colour.colour(TXT_RELOAD, Colour.RED);
					irc.sendActionMsg(channel_.getChannelName(), msg);
					channel_.loadPlugins();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminReload);
		
		MenuItem adminLoaded = new MenuItem(CMD_LOADED, rootItem, 9, AuthGroup.NONE){
			@Override
			public void onExecution(String args) { 
				try {
					String loaded = PluginCore.loadedPlugins(channel_.getPlugins());
					loaded = Colour.colour(loaded, Colour.GREEN, Colour.BLACK);

					String msg = String.format(TXT_LOADED, loaded);
					irc.sendActionMsg(channel_.getChannelName(), msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminLoaded);
		
		MenuItem adminNotLoaded = new MenuItem(CMD_NOT_LOADED, rootItem, 10, AuthGroup.NONE){
			@Override
			public void onExecution(String args) { 
				try {
					String notLoaded = channel_.notLoaded();
					notLoaded = Colour.colour(notLoaded, Colour.RED, Colour.WHITE);
					
					String msg = String.format(TXT_NOT_LOADED, notLoaded);
					irc.sendActionMsg(channel_.getChannelName(), msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		pluginRoot.addChild(adminNotLoaded);
		
		rootItem = pluginRoot;
	}
}
