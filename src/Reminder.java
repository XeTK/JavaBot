import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plugin.PluginTemp;
import program.Database;
import program.Details;
import program.IRC;
import program.IRCException;


public class Reminder implements PluginTemp
{

	@Override
	public void onCreate(String in_str) throws IRCException, IOException {System.out.println("\u001B[37mReminder Plugin Loaded");}

	@Override
	public void onTime(String in_str) throws IRCException, IOException
	{	
		try
		{
			IRC irc = IRC.getInstance();
			Database db = Database.getInstance();
			
			String[] channels = Details.getIntance().getChannels();
			
			String sampleTime = new SimpleDateFormat("yyyy MM dd HH:mm").format(new Date());

			String[] reminders = db.getReminderEvents(sampleTime);
			
			for (int i = 0; i < reminders.length; i++)
				for (int j = 0; j < channels.length;j++)
					irc.sendServer("PRIVMSG " + channels[j] + " " + reminders[i]);
			
			if (reminders.length > 0)
				db.delReminderEvent(sampleTime);
		} 
		catch (SQLException e) {} catch (ClassNotFoundException e) {}
	}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		try
		{
			Database db = Database.getInstance();
			IRC irc = IRC.getInstance();
			
			Matcher m = 
			    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
			    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
			 
		    if (m.find())
		    {
			    String user = m.group(1), host = m.group(2), channel = m.group(3), message = m.group(4);
			    
				if (message.matches("(\\.remind)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)"))
				{
				    Matcher r = Pattern.compile("(\\.remind)\\s([a-zA-Z0-9]*)\\s([a-zA-Z\\w\\d\\s]*)",
				    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				    if (r.find())
				    {
				    	db.addReminder(user,r.group(2),r.group(3));
						irc.sendServer("PRIVMSG " + channel + " " + user + ": I will remind " + r.group(2) + " next time they are here.");
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
				    		date = new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + " " + m.group(1);
				    		reminder = m.group(2);
				    	}
				    	eventtime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).parse(date);
			    		db.addReminderEvent(new SimpleDateFormat("yyyy MM dd HH:mm").format(eventtime), reminder);
			    		irc.sendServer("PRIVMSG " + channel + " " + user + ": Reminder Added.");
				    }
				}
				else if(message.matches("^\\.help") || message.matches("^\\."))
			    {
					irc.sendServer("PRIVMSG " + channel + " REMINDER: " +
									".reminder 00:00 *Message* - Leave reminder for the channel to view later today : " +
									".reminder 01/01/1970 00:00 *Message* Leave a reminder for the future on a different date : "
									);
			    }
		    }
		} 
    	catch (ParseException e){} catch (SQLException e){} catch (ClassNotFoundException e){}

	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException{}
	@Override
	public void onQuit(String in_str) throws IRCException, IOException{}

}
