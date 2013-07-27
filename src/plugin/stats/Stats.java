package plugin.stats;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.users.UserList;
import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;

public class Stats extends Plugin
{
	private final String popt_path = "options/Stat_options.json";
	private final String log_path = "logs/%s.json";
	
	private final String stat_msg = "Handled %s Messages, %s users joined, "
			+ "%s users quit, %s users kicked in the last %s!";
	
	private final String stat_hour = "%s in last hour: %s";
	private final String stat_day = "%s today: %s";
	private final String msg_lastonline = "%s was last online %s";
	private final String msg_msgsent = "%s has sent %s messages";
	
	// Regex's
	private final String rgx_stat = "\\.stats\\s(hour|day)\\s(msgsent|joins|quits|kicks)";
	private final String rgx_time = "([0-2][0-9]):([0-5][0-9]):([0-5][0-9])";
	private final String rgx_msgsent = "\\.msgsent\\s([\\w\\d]*)";
	private final String rgx_lastonline = "\\.lastonline\\s([\\w\\d]*)";
	
	private final Pattern dot_stat = Pattern.compile(rgx_stat, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final Pattern ptn_stat = Pattern.compile(rgx_time, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final Pattern dot_msgsent = Pattern.compile(rgx_msgsent, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final Pattern dot_lastonline = Pattern.compile(rgx_lastonline, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final IRC irc = IRC.getInstance();
	
	private String save_path = new String();
	private String opt_path = new String();
	private String channel_name = new String();
	
	private StatDay today;
	private StatOption options;
	
	public void onCreate(Channel in_channel) throws Exception 
	{
		channel_name = in_channel.getChannel_name();
		save_path = in_channel.getPath() + log_path;
		
		String ti = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		String path = String.format(save_path, ti);

		if (new File(path).exists())
			today = (StatDay) JSON.loadGSON(path, StatDay.class);

		opt_path = in_channel.getPath() + popt_path;
		
		if (new File(opt_path).exists())
			options = (StatOption) JSON.loadGSON(opt_path, StatOption.class);
		else
			JSON.saveGSON(opt_path, new StatOption());
	}

	public void onTime() throws Exception
	{
		String ti = new SimpleDateFormat("HH:mm:ss").format(new Date());
		
		Matcher m = ptn_stat.matcher(ti);
		if (m.find())
		{
			String hour = m.group(1);
			String min = m.group(2);
			String sec = m.group(3);			
			
			if (options.isHour_Stats()&&
					min.equals("59")&&sec.equals("59")&&
					today.getHour().getMsgSent() != 0)
			{
				StatHour thour = today.getHour();
				
				String msg = String.format(stat_msg, 
						thour.getMsgSent(),
						thour.getJoins(),
						thour.getQuits(),
						thour.getKicks(),
						"hour"
						);
				
				irc.sendActionMsg(channel_name, msg);
			}
			
			if (hour.equals("00")&&min.equals("00")&&sec.equals("00"))
			{
				if (options.isDay_Stats()&&today.msgsSent() != 0)
				{
					String msg = String.format(stat_msg,
							today.msgsSent(),
							today.joins(),
							today.quits(),
							today.kicks(),
							"day"
							);
					
					irc.sendActionMsg(channel_name, msg);
				}
				today = new StatDay();
			}
			
			ti = new SimpleDateFormat("yyyyMMdd").format(new Date());
			
			String path = String.format(save_path, ti);
			
			if (sec.equals("00"))
					JSON.saveGSON(path, today);
			
		}
	}

	public void onMessage(Message in_message) throws IRCException, IOException
	{		
		if (!in_message.isPrivMsg())
		{
			String message = in_message.getMessage(); 
			String channel = in_message.getChannel(); 
			String user = in_message.getUser();
			
			//Message.Trim
			
			if (message.charAt(message.length() - 1 ) == ' ')
				message = message.substring(0, message.length() -1);
			
			UserList ul = UserList.getInstance();
			
			if (today == null)
				today = new StatDay();
			
			today.incMsgSent(user);
			
			Matcher m;
			
			m = dot_msgsent.matcher(message);
			if (m.matches())
			{
				String tuser = m.group(1);
				long msgsent = ul.getUser(tuser).getMsgSent();
				String msg = String.format(msg_msgsent, tuser, msgsent);
				irc.sendPrivmsg(channel, msg);
			}

			m = dot_lastonline.matcher(message);
			if (m.matches())
			{
				String tuser = m.group(1);
				Date lastonline = ul.getUser(tuser).getLastOnline();
				String format = "HH:mm dd:MM:yyyy";
				String date = new SimpleDateFormat(format).format(lastonline);
				String msg = String.format(msg_lastonline, tuser, date);
				irc.sendPrivmsg(channel, msg);
			}

			m = dot_stat.matcher(message);
			if (m.find())
			{
				String t1 = m.group(1);
				String cm = m.group(2);
				
				String msg = new String();
				String type = new String();
				int count = 0;
				
				if (t1.equals("hour"))
				{
					StatHour hour = today.getHour();
					
					if (cm.equals("msgsent"))
					{
						type = "Messages";
						count = hour.getMsgSent();
					}
					else if (cm.equals("joins"))
					{
						type = "Joins";
						count = hour.getJoins();
					}
					else if (cm.equals("quits"))
					{
						type = "Quits";
						count = hour.getQuits();
					}
					else if (cm.equals("kicks"))
					{
						type = "Kicks";
						count = hour.getKicks();
					}
					msg = String.format(stat_hour, type,count);
				}
				else if (t1.equals("day"))
				{
					if (cm.equals("msgsent"))
					{
						type = "Messages";
						count = today.msgsSent();
					}
					else if (cm.equals("joins"))
					{
						type = "Joins";
						count = today.joins();
					}
					else if (cm.equals("quits"))
					{
						type = "Quits";
						count = today.quits();
					}
					else if (cm.equals("kicks"))
					{
						type = "Kicks";
						count = today.kicks();
					}
					msg = String.format(stat_day, type, count);
				}
				irc.sendPrivmsg(channel, msg);
			}
		}
	}

	public void onJoin(Join in_join) throws IRCException, IOException
	{
		if (today == null)
			today = new StatDay();
		
		today.incJoins();
	}
	
	public void onQuit(Quit in_quit) throws IRCException, IOException
	{
		if (today == null)
			today = new StatDay();
		
		today.incQuits();
	}
	
    public void onKick(Kick in_kick) throws IRCException, IOException 
	{
		if (today == null)
			today = new StatDay();
		
		today.incKicks();
	}

	public String getHelpString()
	{
		return "STATS: \n" +
				".lastonline <username> - check when a member was last active : \n" +
				".msgsent <username> - check how many messages a user has sent globaly within the channel : \n" +
				".stats (hour|day) (msgsent|joins|quits|kicks) - get stats for that given time frame : ";
	}
}
