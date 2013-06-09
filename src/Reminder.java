import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.Reminders;
import addons.RemindersList;
import addons.RepList;
import addons.User;
import addons.UserList;

import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.IRCException;
import program.JSON;


public class Reminder implements PluginTemp
{
	private RemindersList rl = new RemindersList();
	private static final String cfgFile = "Reminders.json";
	
	@Override
	public void onCreate(String in_str) throws IRCException, IOException 
	{
		System.out.println("\u001B[37mReminder Plugin Loaded");
		if (new File(cfgFile).exists())
			rl = (RemindersList)JSON.loadGSON(cfgFile, RemindersList.class);
		else
			JSON.saveGSON(cfgFile, rl);
	}

	@Override
	public void onTime(String in_str) throws IRCException, IOException
	{	
		IRC irc = IRC.getInstance();
		
		String[] channels = Details.getIntance().getChannels();
		
		Reminders[] reminders = rl.getReminders(new Date());
		
		for (int i = 0; i < reminders.length; i++)
			for (int j = 0; j < channels.length;j++)
				irc.sendServer("PRIVMSG " + channels[j] + " " + reminders[i].getReminder());
	}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		try
		{
			IRC irc = IRC.getInstance();
			UserList ul = UserList.getInstance();
			
			Matcher m = 
			    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
			    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
			 
		    if (m.find())
		    {
			    String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
			    
				if (message.matches("(\\.remind)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)"))
				{
				    Matcher r = Pattern.compile("\\.remind\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
				    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				    if (r.find())
				    {
				    	ul.addReminder(r.group(1),  r.group(1) + ": " + user + " Said to you earlier " + r.group(2));
						irc.sendServer("PRIVMSG " + channel + " " + user + ": I will remind " + r.group(1) + " next time they are here.");
				    }
				}
				else if(message.matches("^\\.reminder ([\\d//:]*) ([\\d:]*).*"))
				{
				    m = Pattern.compile("^\\.reminder ([\\d//:]*) ([\\d:]*)(.*)",
				    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				    
				    if (m.find())
				    {
				    	
				    	String date = m.group(1), reminder = m.group(2);
				    	Date eventtime;
				    	if (date.matches("([0-3][0-9]/[0-1][0-9]/20[1-9][0-9])"))
				    	{
				    		date += " " + m.group(2);
				    		reminder = m.group(3);	
				    	}
				    	else
				    	{
				    		date = new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + " " + m.group(1);
				    		reminder = m.group(2);
				    	}
				    	eventtime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(date);
			    		rl.addReminder(reminder, eventtime);
			    		irc.sendServer("PRIVMSG " + channel + " " + user + ": Reminder Added.");
				    }
				}
				else if(message.matches("^\\.help") || message.matches("^\\."))
			    {
					irc.sendServer("PRIVMSG " + channel + " REMINDER: " +
									".remind *username* *Message* - leave a message for another member : " +
									".reminder 00:00 *Message* - Leave reminder for the channel to view later today : " +
									".reminder 01/01/1970 00:00 *Message* Leave a reminder for the future on a different date : "
									);
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
									irc.sendServer("PRIVMSG " + channel + " " + reminders[i]);
								}
							}
							else
							{
								irc.sendServer("PRIVMSG " + channel + " " + user + ": Your host has changed...");
							}
						}
					}
				}
				JSON.saveGSON(cfgFile, rl);
		    }
		} 
    	catch (ParseException e){}

	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException{}
	@Override
	public void onQuit(String in_str) throws IRCException, IOException{}

}
