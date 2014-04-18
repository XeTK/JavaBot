package plugin.stats.channel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import plugin.stats.channel.data.Day;
import plugin.stats.channel.data.Hour;
import plugin.stats.user.UserList;
import plugin.stats.user.UserListLoader;
import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;
import core.utils.Regex;
import core.utils.RegexFormatter;

public class ChannelStatistics extends Plugin {
	private final String OPTION_PATH    = "options/Stat_options.json";
	private final String LOG_PATH       = "logs/%s.json";

	private final String STAT_MSG       = "Handled %s Messages, %s users joined, %s users quit, %s users kicked in the last %s!";

	private final String STAT_HOUR      = "%s in last hour: %s";
	private final String STAT_DAY       = "%s today: %s";
	private final String MSG_LASTONLINE = "%s was last online %s";
	private final String MSG_MSGSSENT   = "%s has sent %s messages";

	// Regex's
	private final String RGX_STAT       = ".stats\\s(hour|day)\\s(msgsent|joins|quits|kicks)";
	private final String RGX_TIME       = "([0-2][0-9]):([0-5][0-9]):([0-5][0-9])";
	private final String RGX_MSGSENT    = ".msgsent " + RegexFormatter.REG_NICK;
	private final String RGX_LASTONLINE = ".lastonline " + RegexFormatter.REG_NICK;
	
	private final IRC irc = IRC.getInstance();

	private String savePath_    = new String();
	private String optPath_     = new String();
	private String channelName_ = new String();

	private Day     today_;
	private Options options_;
	private Channel channel_;

	public void onCreate(Channel inChannel) throws Exception {
		this.channel_     = inChannel;
		this.channelName_ = inChannel.getChannelName();
		this.savePath_    = inChannel.getPath() + LOG_PATH;

		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());

		String path = String.format(savePath_, time);

		if (new File(path).exists())
			today_ = (Day) JSON.load(path, Day.class);

		optPath_ = inChannel.getPath() + OPTION_PATH;

		if (new File(optPath_).exists())
			options_ = (Options) JSON.load(optPath_, Options.class);
		
		if (options_ == null) {
			options_ = new Options();
			JSON.save(optPath_, options_);
		}
	}

	public void onTime() throws Exception {
		String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		Matcher m = Regex.getMatcher(RGX_TIME,time);
		if (m.find()) {
			String hour = m.group(1);
			String min  = m.group(2);
			String sec  = m.group(3);

			if (options_.isHourStats() && min.equals("59") && sec.equals("59")
					&& today_.getHour().getMsgSent() != 0
					&&!today_.hourIsViewed()) {
				Hour tempHour = today_.getHour();

				String msg = String.format(STAT_MSG, 
						tempHour.getMsgSent(),
						tempHour.getJoins(), 
						tempHour.getQuits(), 
						tempHour.getKicks(),
						"hour");
				
				today_.hasViewed();
				
				irc.sendActionMsg(channelName_, msg);
			}

			if (hour.equals("00") && min.equals("00") && sec.equals("00")) {
				if (options_.isDayStats() && today_.msgsSent() != 0&&
						!today_.isDisplayedDayStats()) {
					String msg = String.format(STAT_MSG, 
							today_.msgsSent(),
							today_.joins(),
							today_.quits(), 
							today_.kicks(), 
							"day");
					
					irc.sendActionMsg(channelName_, msg);
					
					today_.setDisplayedDayStats();
				}
				today_ = new Day();
			}

			time = new SimpleDateFormat("yyyyMMdd").format(new Date());

			String path = String.format(savePath_, time);

			if (sec.equals("00"))
				JSON.save(path, today_);

		}
	}

	public void onMessage(Message inMessage) throws IRCException, IOException {
		if (!inMessage.isPrivMsg()) {
			String message = inMessage.getMessage();
			String channel = inMessage.getChannel();
			String user    = inMessage.getUser();

			// Message.Trim

			if (message.charAt(message.length() - 1) == ' ')
				message = message.substring(0, message.length() - 1);

			UserList uuserList = ((UserListLoader) channel_.getPlugin(UserListLoader.class)).getUserList();

			if (today_ == null)
				today_ = new Day();

			today_.incMsgSent(user);

			Matcher m;

			m = Regex.getMatcher(RGX_MSGSENT, message);
			if (m.matches()) {
				String tempUser = m.group(1);
				long msgSent = uuserList.getUser(tempUser).getMsgSent();
				String msg = String.format(MSG_MSGSSENT, tempUser, msgSent);
				irc.sendPrivmsg(channel, msg);
			}

			Regex.getMatcher(RGX_LASTONLINE, message);
			if (m.matches()) {
				String tempUser = m.group(1);
				Date lastOnline = uuserList.getUser(tempUser).getLastOnline();
				String format = "HH:mm dd:MM:yyyy";
				String date = new SimpleDateFormat(format).format(lastOnline);
				String msg = String.format(MSG_LASTONLINE, tempUser, date);
				irc.sendPrivmsg(channel, msg);
			}

			m = Regex.getMatcher(RGX_STAT, message);
			if (m.find()) {
				String duration = m.group(1);
				String cmd = m.group(2);

				String msg = new String();
				String type = new String();
				int count = 0;

				if (duration.equals("hour")) {
					Hour hour = today_.getHour();

					if (cmd.equals("msgsent")) {
						type = "Messages";
						count = hour.getMsgSent();
					} else if (cmd.equals("joins")) {
						type = "Joins";
						count = hour.getJoins();
					} else if (cmd.equals("quits")) {
						type = "Quits";
						count = hour.getQuits();
					} else if (cmd.equals("kicks")) {
						type = "Kicks";
						count = hour.getKicks();
					}
					msg = String.format(STAT_HOUR, type, count);
				} else if (duration.equals("day")) {
					if (cmd.equals("msgsent")) {
						type = "Messages";
						count = today_.msgsSent();
					} else if (cmd.equals("joins")) {
						type = "Joins";
						count = today_.joins();
					} else if (cmd.equals("quits")) {
						type = "Quits";
						count = today_.quits();
					} else if (cmd.equals("kicks")) {
						type = "Kicks";
						count = today_.kicks();
					}
					msg = String.format(STAT_DAY, type, count);
				}
				irc.sendPrivmsg(channel, msg);
			}
		}
	}

	public void onJoin(Join inJoin) throws IRCException, IOException {
		if (today_ == null)
			today_ = new Day();

		today_.incJoins();
	}

	public void onQuit(Quit inQuit) throws IRCException, IOException {
		if (today_ == null)
			today_ = new Day();

		today_.incQuits();
	}

	public void onKick(Kick inKick) throws IRCException, IOException {
		if (today_ == null)
			today_ = new Day();

		today_.incKicks();
	}

	public String getHelpString() {
		return "STATS: \n"
				+ "\t.lastonline <username> - check when a member was last active\n"
				+ "\t.msgsent <username> - check how many messages a user has sent globaly within the channel\n"
				+ "\t.stats (hour|day) (msgsent|joins|quits|kicks) - get stats for that given time frame\n";
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
	}
}
