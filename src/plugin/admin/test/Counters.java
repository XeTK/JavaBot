package plugin.admin.test;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;

public class Counters extends Plugin {

	private static final String CMD_GET_STATS = "globstats";
	
	private static final String HLP_GET_STATS = String.format("%s - Gets raw stats about the bot since it started.", CMD_GET_STATS);
	
	private static final String TXT_GET_STATS = "| %s Creates | %s Times | %s Joins | %s Quits | %s Messages | %s Kicks | %s Raws |";
	
	private static int creates_  = 0;
	private static int times_    = 0;
	private static int messages_ = 0;
	private static int joins_    = 0;
	private static int quits_    = 0;
	private static int kicks_    = 0;
	private static int raws_     = 0;
	
	public void onCreate(Channel inChannel) throws Exception {
		super.onCreate(inChannel);
		creates_++;
	}

	public void onTime() throws Exception {
		times_++;
	}

	public void onMessage(Message inMessage) throws Exception {
		messages_++;
	}

	public void onJoin(Join inJoin) throws Exception {
		joins_++;
	}

	public void onQuit(Quit inQuit) throws Exception {
		quits_++;
	}

	public void onKick(Kick inKick) throws Exception {
		raws_++;
	}

	public void rawInput(String inStr) throws Exception {
		raws_++;
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		MenuItem pluginRoot = rootItem;
		
		MenuItem repGetStats = new MenuItem(CMD_GET_STATS, rootItem, 1, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 				
				try {
					irc.sendPrivmsg(channel_.getChannelName(), String.format(TXT_GET_STATS, creates_,times_,joins_,quits_,messages_,kicks_,raws_));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public String onHelp() {
				return HLP_GET_STATS;
			}
		};

		pluginRoot.addChild(repGetStats);
		
		rootItem = pluginRoot;
	}

}
