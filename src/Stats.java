import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.helpers.IRCException;
import core.plugin.PluginTemp;
import core.utils.Details;
import core.utils.IRC;
import core.utils.JSON;
import addons.stats.StatDay;
import addons.stats.StatOption;
import addons.users.UserList;


public class Stats implements PluginTemp
{
	private final String opt_path = "stat_options.json";
	private final String log_path = "logs/%s.json";
	
	private String save_path = new String();
	
	private StatDay today;
	private StatOption options;
	
	
	@Override
	public String name() 
	{
		return "Statistics";
	}
	
	@Override
	public void onCreate(String savePath) throws Exception 
	{
		save_path = savePath + "/" + log_path;
		
		String ti = new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		String path = String.format(save_path, ti);
		
		System.out.println(path);
		
		if (new File(path).exists())
			today = (StatDay) JSON.loadGSON(path, StatDay.class);

		
		if (new File(opt_path).exists())
			options = (StatOption) JSON.loadGSON(opt_path, StatOption.class);
		else
			JSON.saveGSON(opt_path, new StatOption());
	}

	@Override
	public void onTime() throws Exception
	{
		IRC irc = IRC.getInstance();
		String ti = new SimpleDateFormat("HH:mm:ss").format(new Date());
		
		Matcher m = 
				Pattern.compile("([0-2][0-9]):([0-5][0-9]):([0-5][0-9])",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(ti);
		if (m.find())
		{
			String hour = m.group(1);
			String min = m.group(2);
			String sec = m.group(3);			
			
			String[] channels = Details.getInstance().getChannels();
			
			if (options.isHour_Stats())
				if (min.equals("59")&&sec.equals("59"))
					for (int i = 0; i < channels.length;i++)
						if (today.getHour().getMsgSent() != 0)
							irc.sendPrivmsg(channels[i], "Hourly Stats: Messages Sent : " + today.getHour().getMsgSent() + "| Users joined :" + today.getHour().getJoins() +"| Users left : " + today.getHour().getQuits() + "| Users kicked" + today.getHour().getKicks());
			
			if (hour.equals("00")&&min.equals("00")&&sec.equals("00"))
			{
				if (options.isDay_Stats())
					if (today.msgsSent() != 0)
						for (int i = 0; i < channels.length; i++)
							irc.sendPrivmsg(channels[i], "I has handled, " + today.msgsSent() + " Messages, " + today.joins() + "  Users Join, " + today.quits() + " User left and " + today.kicks() + " users kicked!");

				today = new StatDay();
			}
			
			ti = new SimpleDateFormat("yyyyMMdd").format(new Date());
			
			String path = String.format(save_path, ti);
			
			if (sec.equals("00"))
					JSON.saveGSON(path, today);
			
		}
	}

	@Override
	public void onMessage(Message in_message) throws IRCException, IOException
	{		
		String message = in_message.getMessage(); 
		String channel = in_message.getChannel(); 
		String user = in_message.getUser();
		
		//Message.Trim
		
		if (message.charAt(message.length() - 1 ) == ' ')
			message = message.substring(0, message.length() -1);

		IRC irc = IRC.getInstance();
		
		UserList ul = UserList.getInstance();
		
		if (today == null)
			today = new StatDay();
		
		today.incMsgSent(user);
		
		if (message.matches("\\.msgsent [A-Za-z0-9#]+$"))
		{
			String[] t = message.split(" ");
			if ((t.length > 0||t[1] != null)&&ul.getUser(t[1]) != null)
			{
				irc.sendPrivmsg(channel, t[1] + ": Messages Sent = " + ul.getUser(t[1]).getMsgSent() + "!");
			}
		}
		else if (message.matches("\\.lastonline [A-Za-z0-9#]+$"))
		{
			String[] t = message.split(" ");
			if ((t.length > 0||t[1] != null)&&ul.getUser(t[1]) != null)
			{
				irc.sendPrivmsg(channel, t[1] + ": Last Online = " + new SimpleDateFormat("yyyy/MM/dd HH:mm").format(ul.getUser(t[1]).getLastOnline()) + "!");
			}
		}
		else if (message.matches("\\.stats (hour|day) (msgsent|joins|quits|kicks)"))
		{
			Matcher m =	Pattern.compile("\\.stats (hour|day) (msgsent|joins|quits|kicks)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
			if (m.find())
			{
				String t1 = m.group(1);
				String cm = m.group(2);
				
				if (t1.equals("hour"))
				{
					if (cm.equals("msgsent"))
					{
						irc.sendPrivmsg(channel, "Messages sent in the last hour : " + today.getHour().getMsgSent());
					}
					else if (cm.equals("joins"))
					{
						irc.sendPrivmsg(channel, "Users joined in the last hour : " + today.getHour().getJoins());
					}
					else if (cm.equals("quits"))
					{
						irc.sendPrivmsg(channel, "Users quit in the last hour : " + today.getHour().getQuits());
					}
					else if (cm.equals("kicks"))
					{
						irc.sendPrivmsg(channel, "Users kicked in the last hour : " + today.getHour().getKicks());
					}
				}
				else if (t1.equals("day"))
				{
					if (cm.equals("msgsent"))
					{
						irc.sendPrivmsg(channel, "Messages sent today : " + today.msgsSent());
					}
					else if (cm.equals("joins"))
					{
						irc.sendPrivmsg(channel, "Users joined today : " + today.joins());
					}
					else if (cm.equals("quits"))
					{
						irc.sendPrivmsg(channel, "Users quit today : " + today.quits());
					}
					else if (cm.equals("kicks"))
					{
						irc.sendPrivmsg(channel, "Users kicked today : " + today.kicks());
					}
				}
			}
		}
	}

	@Override
	public void onJoin(Join in_join) throws IRCException, IOException
	{
		if (today == null)
			today = new StatDay();
		
		today.incJoins();
	}
	
	@Override
	public void onQuit(Quit in_quit) throws IRCException, IOException
	{
		if (today == null)
			today = new StatDay();
		
		today.incQuits();
	}
	
    @Override
    public void onKick(Kick in_kick) throws IRCException, IOException 
	{
		if (today == null)
			today = new StatDay();
		
		today.incKicks();
	}
	@Override
	public String getHelpString()
	{
		return "STATS: " +
				".lastonline *username* - check when a member was last active : " +
				".msgsent *username* - check how many messages a user has sent globaly within the channel : " +
				".stats (hour|day) (msgsent|joins|quits|kicks) - get stats for that given time frame : ";
	}
    // Unused
	@Override
	public void rawInput(String in_str) throws Exception{}


}
