import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.StatDay;
import addons.UserList;

import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.IRCException;
import program.JSON;


public class Stats implements PluginTemp
{

	private StatDay today;
	@Override
	public void onCreate(String in_str) throws IRCException, IOException {System.out.println("\u001B[37mStats Plugin Loaded");}

	@Override
	public void onTime(String in_str) throws IRCException, IOException
	{
		IRC irc = IRC.getInstance();
		String ti = new SimpleDateFormat("HH:mm:ss").format(new Date());
		
		Matcher m = 
				Pattern.compile("([0-2][0-9]):([0-5][0-9]):([0-5][0-9])",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(ti);
		if (m.find())
		{
			String hour = m.group(1), min = m.group(2), sec = m.group(3);
			
			String[] channels = Details.getIntance().getChannels();
			if (min.equals("59")&&sec.equals("59"))
				for (int i = 0; i < channels.length;i++)
					irc.sendServer("PRIVMSG " + channels[i] + " Hourly Stats: Messages Sent : " + today.getHour().getMsgSent() + "| Users joined :" + today.getHour().getJoins() +"| Users left : " + today.getHour().getQuits());;
			
			if (hour.equals("00")&&min.equals("00")&&sec.equals("00"))
			{
				JSON.saveGSON("TestDay.json", today);
				today = new StatDay();
			}
			
			if (sec.equals("00"))
			{
				ti = new SimpleDateFormat("yyyyMMdd").format(new Date());
				
				String path = "logs/" + ti + ".json";
				
				File fi = new File(path);
				
				if (fi.exists())
					fi.delete();
				
				if (sec.equals("00"))
					JSON.saveGSON(path, today);
			}
		}
	}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		Matcher m = 
				Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
		if (m.find())
		{
			String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
			
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
				if (t.length > 0||t[1] != null)
					if (ul.getUser(t[1]) != null)
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Messages Sent = " + ul.getUser(t[1]).getMsgSent() + "!");
 			}
			else if (message.matches("\\.lastonline [A-Za-z0-9#]+$"))
			{
				String[] t = message.split(" ");
				if (t.length > 0||t[1] != null)
					if (ul.getUser(t[1]) != null)
						irc.sendServer("PRIVMSG " + channel + " " + t[1] + ": Last Online = " + new SimpleDateFormat("yyyy/MM/dd HH:mm").format(ul.getUser(t[1]).getLastOnline()) + "!");
 			}
			else if(message.matches("^\\.help") || message.matches("^\\."))
			{
				irc.sendServer("PRIVMSG " + channel + " STATS: " +
						".lastonline *username* - check when a member was last active : " +
						".msgsent *username* - check how many messages a user has sent globaly within the channel : "
						);
			}
	    }
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException
	{
		if (today == null)
			today = new StatDay();
		
		today.incJoins();
	}
	
	@Override
	public void onQuit(String in_str) throws IRCException, IOException
	{
		if (today == null)
			today = new StatDay();
		
		today.incQuits();
	}

}
