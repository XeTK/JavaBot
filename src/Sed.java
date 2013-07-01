
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import plugin.PluginTemp;
import program.IRC;


public class Sed implements PluginTemp
{
	private ArrayList<Message> messages = new ArrayList<Message>();

	public String name() 
	{
		return "Sed";
	}
	
	@Override
	public void onMessage(Message in_message) throws Exception
	{
		IRC irc = IRC.getInstance();
		  
		String message = in_message.getMessage(), channel = in_message.getChannel(), user = in_message.getUser();
		
	    if(message.matches("^\\.help") || message.matches("^\\."))
			irc.sendServer("PRIVMSG " + channel + " SED: " +
							"*Username*: s/*Source*/*Replacement*/ - e.g XeTK: s/.*/hello/ is used to replace the previous statement with hello :"
							);
	    
		if (messages.size() > 10)
			messages.remove(0);

	    Matcher m = Pattern.compile("(^[a-zA-Z0-9]*): s/([^/]*)/([^/]*)/",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
	    if (m.find())
	    {
	    	String tuser = m.group(1).toLowerCase(),
	    			replacement = m.group(3),
	    			source = m.group(2);
               
	    	for (int i = 0; i < messages.size();i++)
	    	{
	    		if (messages.get(i).getUser().equals(tuser))
	    		{
	    			if ((Pattern.compile(source, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(messages.get(i).getMessage())).find())
	    			{
	    				irc.sendServer("PRIVMSG " + channel + " " + user + " thought " + tuser + " meant at " + new SimpleDateFormat("HH:mm:ss").format(messages.get(i).getDate()) + ": " + messages.get(i).getMessage().replaceAll(source, replacement));
	    				if (source.contains(".")||source.contains("$"))
	    					break;
	    			}
	    		}
	    	}

	    }
	    else
	    	messages.add(in_message);
	}

	@Override
	public void onCreate() throws Exception {}
	@Override
	public void onTime() throws Exception {}
	@Override
	public void onJoin(Join in_join) throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
	@Override
	public void onKick(Kick in_kick) throws Exception {}
	
	
}
