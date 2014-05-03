package plugin.tools.reminder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.ArrayList;

import plugin.stats.user.User;
import plugin.stats.user.UserList;
import plugin.stats.user.UserListLoader;
import core.Channel;
import core.event.Message;
import core.menu.AuthGroup;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.JSON;
import core.utils.Regex;

public class Reminder extends Plugin {
	
	
	private static final String CMD_REMIND   = "remind";
	private static final String CMD_REMINDER = "reminder";
	
	private static final String RGX_REMIND   = "([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)";
	private static final String RGX_REMINDER = "([0-3][0-9]\\/[0-1][0-9]\\/20[1-9][0-9])\\s([0-2][0-9]\\:[0-5][0-9])\\s([\\w\\s]*)";
	
	private static final String TXT_REMIND   = "%s: I will remind %s next time they are here.";
	private static final String TXT_REMINDER = "%s: Reminder Added.";
	
	private static final String MSG_REMIND   = "%s: %s Said to you earlier %s"; 
			
	private static final String HLP_REMIND   = String.format("%s <username> <Message> - leave a message for another member", CMD_REMIND);
	private static final String HLP_REMINDER = String.format("%s 00:00 <Message> - Leave reminder for the channel to view later today\n"
														   + "%s 01/01/1970 00:00 <Message> Leave a reminder for the future on a different date", CMD_REMINDER, CMD_REMINDER);
	
	private final String CONFIG_FILE_LOCATION = "Reminders.json";
	
	private String cfgFile_ = "";

	private RemindersList remindersList_ = new RemindersList();
	
	private UserList userList_;

	private String[] dependencies_ = {"UserListLoader"};

	public void onCreate(Channel inChannel) throws Exception {
		super.onCreate(inChannel);
		
		cfgFile_ = channel_.getPath() + CONFIG_FILE_LOCATION;
		
		userList_ = ((UserListLoader) channel_.getPlugin(UserListLoader.class)).getUserList();
		
		if (new File(cfgFile_).exists())
			remindersList_ = (RemindersList) JSON.load(cfgFile_, RemindersList.class);
		else
			JSON.save(cfgFile_, remindersList_);
	}

	public String[] getDependencies() {
		return dependencies_;
        }

        public boolean hasDependencies() {
		return (dependencies_.length > 0);
        }

	public void onTime() throws Exception {
		Reminders[] reminders = remindersList_.getReminders(new Date());

		for (int i = 0; i < reminders.length; i++)
			irc.sendPrivmsg(channel_.getChannelName(),
					reminders[i].getReminder());
	}

	public void onMessage(Message inMessage) throws Exception {
		if (!inMessage.isPrivMsg()) {
			
			String user    = inMessage.getUser();
			String channel = inMessage.getChannel();
			User tempUser = userList_.getUser(user);
			
			if (tempUser != null) {
				if (tempUser.isDirty()) {
					String[] reminders = tempUser.getReminders();
					if (reminders.length > 0) {
						for (int i = 0; i < reminders.length; i++) {
							irc.sendPrivmsg(channel, reminders[i]);
						}
					} 
				}
			}
			JSON.save(cfgFile_, remindersList_);
		}
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		
		MenuItem pluginRoot = rootItem;
		
		MenuItem reminderRemind = new MenuItem(CMD_REMIND, rootItem, 1, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 
				Matcher m = Regex.getMatcher(RGX_REMIND, args);
				if (m.find()) {
					userList_.addReminder(m.group(1), String.format(MSG_REMIND, m.group(1), username, m.group(2)));
					
					try {
						JSON.save(cfgFile_, remindersList_);
						
						irc.sendPrivmsg(channel_.getChannelName(), String.format(TXT_REMIND, username, m.group(1)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public String onHelp() {
				return HLP_REMIND;
			}
		};
		
		pluginRoot.addChild(reminderRemind);
		
		MenuItem reminderReminder = new MenuItem(CMD_REMINDER, rootItem, 2, AuthGroup.NONE){
			@Override
			public void onExecution(String args, String username) { 
				System.out.println(RGX_REMINDER + " " + args);
				Matcher m = Regex.getMatcher(RGX_REMINDER, args);

				if (m.find()) {
					try {
						String date = m.group(1);
						String reminder = m.group(3);
	
						Date eventTime;
	
						if (date.matches("([0-3][0-9]/[0-1][0-9]/20[1-9][0-9])"))
							date += " " + m.group(2);
						else
							date = new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " " + m.group(1);
	
						eventTime = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.ENGLISH).parse(date);
						
						remindersList_.addReminder(reminder, eventTime);
					
						JSON.save(cfgFile_, remindersList_);
						
						irc.sendPrivmsg(channel_.getChannelName(), String.format(TXT_REMINDER, username));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public String onHelp() {
				return HLP_REMINDER;
			}
		};
		
		pluginRoot.addChild(reminderReminder);
	
		rootItem = pluginRoot;
	}

}
