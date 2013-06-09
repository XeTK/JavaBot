import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.Message;

import plugin.PluginTemp;
import program.IRC;
import program.IRCException;


public class Sed implements PluginTemp
{
	private ArrayList<Message> messages = new ArrayList<Message>();

	@Override
	public void onCreate(String in_str) throws IRCException, IOException{System.out.println("\u001B[37mSed Plugin Loaded");}
	@Override
	public void onTime(String in_str) throws IRCException, IOException{}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException
	{
		IRC irc = IRC.getInstance();
		
		Matcher m = 
		    		Pattern.compile(":([\\w_\\-]+)!\\w+@([\\w\\d\\.-]+) PRIVMSG (#?\\w+) :(.*)$",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
		 
	    if (m.find())
	    {
		    String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
		    
		    if(message.matches("^\\.help") || message.matches("^\\."))
				irc.sendServer("PRIVMSG " + channel + " SED: " +
								"*Username*: s/*Source*/*Replacement*/ - e.g XeTK: s/.*/hello/ is used to replace the previous statement with hello :"
								);
		    
			if (messages.size() > 10)
				messages.remove(0);

		    m = Pattern.compile("(^[a-zA-Z0-9]*): s/([^/]*)/([^/]*)/",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
		    if (m.find())
		    {

		    	String tuser = m.group(1),
		    			replacement = m.group(3),
		    			source = m.group(2);

		    	for (int i = 0; i < messages.size();i++)
		    		if (messages.get(i).getUser().equals(tuser))
		    			if ((Pattern.compile(source,
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(messages.get(i).getMessage())).find())
		    				irc.sendServer("PRIVMSG " + channel + " " + user + " thought " + tuser + " meant at " + new SimpleDateFormat("HH:mm:ss").format(messages.get(i).getDate()) + ": " + messages.get(i).getMessage().replaceFirst(source, replacement));

		    }
		    else
		    	messages.add(new Message(user,message));
	    }
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException{}
	@Override
	public void onQuit(String in_str) throws IRCException, IOException{}

}
