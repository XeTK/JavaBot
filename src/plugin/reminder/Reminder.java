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
import core.utils.Details;
import core.utils.IRC;
import core.utils.IRCException;
import core.utils.JSON;

public class Reminder extends Plugin
{
	private final String cfgFileLoc = "Reminders.json";
	private String cfgFile = new String(); 
	
	private RemindersList rl = new RemindersList();
	
	public void onCreate(Channel in_channel) throws IRCException, IOException 
	{
		cfgFile = in_channel.getPath() + cfgFileLoc;
		
		if (new File(cfgFile).exists())
			rl = (RemindersList)JSON.loadGSON(cfgFile, RemindersList.class);
		else
			JSON.saveGSON(cfgFile, rl);
	}

	public void onTime() throws Exception
	{	
		IRC irc = IRC.getInstance();
		
		String[] channels = Details.getInstance().getChannels();
		
		Reminders[] reminders = rl.getReminders(new Date());
		
		for (int i = 0; i < reminders.length; i++)
			for (int j = 0; j < channels.length;j++)
				irc.sendPrivmsg(channels[j], reminders[i].getReminder());
	}

	public void onMessage(Message in_message) throws Exception
	{
		if (!in_message.isPrivMsg())
		{
			IRC irc = IRC.getInstance();
			UserList ul = UserList.getInstance();
			
			String user = in_message.getUser(); 
			String channel = in_message.getChannel(); 
			String message = in_message.getMessage();
			    
			if (message.matches("(\\.remind)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)"))
			{
			    Matcher r = Pattern.compile("\\.remind\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
			    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
			    if (r.find())
			    {
			    	ul.addReminder(r.group(1),  
			    			r.group(1) + ": " + user + " Said to you earlier " + r.group(2));
			    	
			    	irc.sendPrivmsg(channel, 
			    			user + ": I will remind " + r.group(1) + " next time they are here.");
			    }
			}
			else if(message.matches("^\\.reminder ([\\d//:]*) ([\\d:]*).*"))
			{
			    Matcher m = Pattern.compile("^\\.reminder ([\\d//:]*) ([\\d:]*)(.*)",
			    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
			    
			    if (m.find())
			    {	
			    	String date = m.group(1);
			    	String reminder = m.group(3);
			    	
			    	Date eventtime;
			    	
			    	if (date.matches("([0-3][0-9]/[0-1][0-9]/20[1-9][0-9])"))
			    		date += " " + m.group(2);	
			    	else
			    		date = new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " " + m.group(1);
			    	
			    	eventtime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(date);
		    		rl.addReminder(reminder, eventtime);
		    		irc.sendPrivmsg(channel, user + ": Reminder Added.");
			    }
			}
			else
			{
				User userOBJ = ul.getUser(user);
				if (userOBJ != null)
				{
					if (userOBJ.isDirty())
					{
						String[] reminders = userOBJ.getReminders();
						if (reminders.length > 0)
						{
							for (int i = 0; i < reminders.length;i++)
							{
								irc.sendPrivmsg(channel, reminders[i]);
							}
						}
						else
						{
							irc.sendPrivmsg(channel, user + ": Your host has changed...");
						}
					}
				}
			}
			JSON.saveGSON(cfgFile, rl);
		}
	}
	
	public String getHelpString()
	{
		return "REMINDER: \n" +
				".reminder <username> <Message> - leave a message for another member : \n" +
				".reminder 00:00 <Message> - Leave reminder for the channel to view later today : \n" +
				".reminder 01/01/1970 00:00 <Message> Leave a reminder for the future on a different date : ";
	}

}
