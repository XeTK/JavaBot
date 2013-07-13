
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import core.utils.IRC;


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
		  
		String message = in_message.getMessage(), 
				channel = in_message.getChannel(), 
				user = in_message.getUser();
		
	    if(message.matches("^\\.help") || message.matches("^\\."))
	    	irc.sendPrivmsg(channel, "SED: " +
							"*Username*: s/*Source*/*Replacement*/ - e.g XeTK: s/.*/hello/ is used to replace the previous statement with hello :"
							);
	    
		if (messages.size() > 10)
			messages.remove(0);

	    Matcher m = Pattern.compile("(?:([\\s\\w]*):\\s)?s/([\\s\\w\\d\\$\\*\\.]*)/([\\s\\w\\d]*)(?:/)?",
	    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
	    				.matcher(message);
	    
	    if (m.find())
	    {
	    	String tuser = "", replacement = "", source = "";
	    	if (m.group(1) != null)
	    		tuser = m.group(1);

    		replacement = m.group(3);
    		source = m.group(2);
             
	    	for (int i = messages.size() -1; i >= 0; i--)
	    	{
	    		Message mmessage = messages.get(i);
	    		
    			m = Pattern.compile(source, 
    					Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
    					.matcher(mmessage.getMessage());
    			
    			if (m.find())
    			{
    				String reply = "";
    				if (!tuser.equalsIgnoreCase(tuser))
    					 reply = user + " thought ";
    				
    				reply += "%s meant : %s"; 
    				irc.sendPrivmsg(channel, String.format(reply, 
    						mmessage.getUser(), 
    						mmessage.getMessage().replaceAll(source, replacement)));
    				break;
    			}
	    	}

	    }
	    else 
	    {
		    	messages.add(in_message);
	    }
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
	@Override
	public void onOther(String in_str) throws Exception {}
}
