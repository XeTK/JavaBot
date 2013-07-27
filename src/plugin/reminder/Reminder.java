package plugin.reminder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.users.User;
import plugin.users.UserList;
import core.Channel;
import core.event.Message;
import core.plugin.Plugin;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;

public class Reminder extends Plugin {
	private final String CONFIG_FILE_LOCATION = "Reminders.json";
	private String cfgFile_ = new String();

	private IRC irc_ = IRC.getInstance();

	private Channel channel_;
	private RemindersList remindersList_ = new RemindersList();

	public void onCreate(Channel inChannel) throws IRCException, IOException {
		this.channel_ = inChannel;

		cfgFile_ = channel_.getPath() + CONFIG_FILE_LOCATION;

		if (new File(cfgFile_).exists())
			remindersList_ = (RemindersList) JSON.load(cfgFile_, RemindersList.class);
		else
			JSON.save(cfgFile_, remindersList_);
	}

	public void onTime() throws Exception {
		Reminders[] reminders = remindersList_.getReminders(new Date());

		for (int i = 0; i < reminders.length; i++)
			irc_.sendPrivmsg(channel_.getChannelName(),
					reminders[i].getReminder());
	}

	public void onMessage(Message inMessage) throws Exception {
		if (!inMessage.isPrivMsg()) {
			IRC irc = IRC.getInstance();
			UserList userList = UserList.getInstance();

			String user = inMessage.getUser();
			String channel = inMessage.getChannel();
			String message = inMessage.getMessage();

			Matcher m;

			m = Pattern.compile(
					"\\.remind\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
			if (m.find()) {
				userList.addReminder(m.group(1), m.group(1) + ": " + user
						+ " Said to you earlier " + m.group(2));

				irc.sendPrivmsg(channel, user + ": I will remind " + m.group(1)
						+ " next time they are here.");
			}

			m = Pattern.compile("^\\.reminder ([\\d//:]*) ([\\d:]*)(.*)",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);

			if (m.find()) {
				String date = m.group(1);
				String reminder = m.group(3);

				Date eventTime;

				if (date.matches("([0-3][0-9]/[0-1][0-9]/20[1-9][0-9])"))
					date += " " + m.group(2);
				else
					date = new SimpleDateFormat("dd/MM/yyyy")
							.format(new Date()) + " " + m.group(1);

				eventTime = new SimpleDateFormat("dd/MM/yyyy HH:mm",
						Locale.ENGLISH).parse(date);
				remindersList_.addReminder(reminder, eventTime);
				irc.sendPrivmsg(channel, user + ": Reminder Added.");
			}

			User tempUser = userList.getUser(user);
			if (tempUser != null) {
				if (tempUser.isDirty()) {
					String[] reminders = tempUser.getReminders();
					if (reminders.length > 0) {
						for (int i = 0; i < reminders.length; i++) {
							irc.sendPrivmsg(channel, reminders[i]);
						}
					} else {
						irc.sendPrivmsg(channel, user
								+ ": Your host has changed...");
					}
				}
			}
			JSON.save(cfgFile_, remindersList_);
		}
	}

	public String getHelpString() {
		return "REMINDER: \n"
				+ ".reminder <username> <Message> - leave a message for another member : \n"
				+ ".reminder 00:00 <Message> - Leave reminder for the channel to view later today : \n"
				+ ".reminder 01/01/1970 00:00 <Message> Leave a reminder for the future on a different date : ";
	}

}
