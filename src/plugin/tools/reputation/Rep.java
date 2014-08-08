package plugin.tools.reputation;

import java.io.File;
import java.util.regex.Matcher;
import java.util.ArrayList;

import core.Channel;
import core.event.Message;
import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.JSON;
import core.utils.Regex;

public class Rep extends Plugin {
	
	private final String CMD_REP  = "rep";
	private final String CMD_INFO = "info";
	
	private final String TXT_REP    = "%s has a reputation of %s.";
	private final String TXT_EX_REP = "You can't do that, it's too much rep...";
	private final String TXT_INFO   = "Please view the help on how to use this plugin";
	
	private final String HLP_REP  = String.format("%s <Item> - view the reputation of a item\n", CMD_REP);
	private final String HLP_INFO = "<Item>--/++ - increment or decrement the rep of a item /\n" + 
			  						"<Item> +/- <Ammount> - increment or decrement the rep of a set item by a set amount";
	
	private final String RGX_GET_REP = "^(\\w+)\\s?([+-])(?:\\2|=\\s?(\\d+))";
	
	private final String CONFIG_FILE_LOCATION = "Rep.json";
	
	private String cfgFile_ = new String();

	private RepList repList_ = new RepList();

	private String[] dependencies_ = {};

	public String[] getDependencies() {
		return dependencies_;
	}

	public boolean hasDependencies() {
		return (dependencies_.length > 0);
	}

	public void onCreate(Channel inChannel) throws Exception {
		super.onCreate(inChannel);
		
		cfgFile_ = channel_.getPath() + CONFIG_FILE_LOCATION;
		
		if (new File(cfgFile_).exists())
			repList_ = (RepList) JSON.load(cfgFile_, RepList.class);
		else
			JSON.save(cfgFile_, repList_);

	}

	public void onMessage(Message inMessage) throws Exception {
		if (!inMessage.isPrivMsg()) {
			String channel = inMessage.getChannel();
			String message = inMessage.getMessage();


			if (message.charAt(message.length() - 1) == ' ')
				message = message.substring(0, message.length() - 1);
			
			Matcher r = Regex.getMatcher(RGX_GET_REP, message);
			
			if (r.find()) {

				String item = r.group(1);
				String type = r.group(2);
				String ammount = r.group(3);
				
				if (!inMessage.getUser().equalsIgnoreCase(item))
				{
					int iAmmount = 0;

					if (type.equals("+") && r.group(3) == null) {
						iAmmount = 1;
					} else if (type.equals("-") && r.group(3) == null) {
						iAmmount = -1;
					} else {
						type = type.trim();
						ammount = ammount.trim();
						if (type.equals("+"))
							iAmmount = Integer.valueOf(ammount);
						else
							iAmmount = Integer.valueOf(type + ammount);
					}
					if (iAmmount > 100 || iAmmount < -100) {
						irc.sendPrivmsg(channel,TXT_EX_REP);
					} else {
						Reputation tempRep = repList_.getRep(item);
						tempRep.modRep(iAmmount);
						
						String msg = String.format(TXT_REP, item, tempRep.getRep());
						
						irc.sendPrivmsg(channel, msg);
					}
				}
			}
			JSON.save(cfgFile_, repList_);
		}
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		MenuItem pluginRoot = rootItem;
		
		MenuItem repGetRep = new MenuItem(CMD_REP, rootItem, 1, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 
				if (args != null) {
					String item = args.trim();
					
					int rep = repList_.getRep(item).getRep();
					
					String msg = String.format(TXT_REP, item, rep);
					
					try {
						irc.sendPrivmsg(channel_.getChannelName(), msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public String onHelp() {
				return HLP_REP;
			}
		};

		pluginRoot.addChild(repGetRep);
		
		MenuItem repGetInfo = new MenuItem(CMD_INFO, rootItem, 2, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 
				try {
					irc.sendPrivmsg(channel_.getChannelName(), TXT_INFO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public String onHelp() {
				return HLP_INFO;
			}
		};

		pluginRoot.addChild(repGetInfo);
		
		rootItem = pluginRoot;
	}

}
