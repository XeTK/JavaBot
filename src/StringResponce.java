import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import addons.Responce;
import addons.ResponceList;

import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.IRCException;
import program.JSON;


public class StringResponce implements PluginTemp
{
	private ResponceList rl = new ResponceList();
	@Override
	public String name()
	{
		return "StringResponce";
	}

	@Override
	public void onCreate(String in_str) throws IRCException, IOException 
	{
		String path = "responces.json";
		
		if (new File(path).exists())
			rl = (ResponceList) JSON.loadGSON(path, ResponceList.class);
		else
			JSON.saveGSON(path, new ResponceList());
		
		if (rl == null)
			rl = new ResponceList();
	}
	
	@Override
	public void onTime(String in_str) throws IRCException, IOException {}

	@Override
	public void onMessage(String in_str) throws IRCException, IOException 
	{
		IRC irc = IRC.getInstance();
		
		Matcher m = 
		    		Pattern.compile(":(.*)!.*@(.*) PRIVMSG (#.*) :(.*)",
		    				Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_str);
		 
	    if (m.find())
	    {
		    String user = m.group(1).toLowerCase(), host = m.group(2), channel = m.group(3), message = m.group(4);
		    
		    ArrayList<Responce> responces = rl.getResponces();
		    System.out.println(responces.size());
		    if (responces.size() > 0)
		    {
			    for (int i = 0; i < responces.size();i++)
			    {
			    	m = Pattern.compile(responces.get(i).getRegex(),Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
			    	if (m.find())
			    	{
			    		String[] replies = responces.get(i).getResponces();
	
		    			int inx = 1 + (int)(Math.random() * ((replies.length - 1) + 1));
		    			if (inx > replies.length -1)
		    				inx--;
		    			irc.sendServer("PRIVMSG " + channel + " " + user + ": " + replies[inx]);
			    	}
			    }
		    }
		    Details details = Details.getIntance();
	        for (int i = 0;i < details.getAdmins().length;i++)
			{
				if (user.equals(details.getAdmins()[i]))
				{
				    m = Pattern.compile("^\\.responce ([a-zA-Z0-9\\.\\*\\\\\\^]*) ,([a-zA-Z0-9 ,]*)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
				    if (m.find())
				    {
				    	System.out.println("Adding new Responce");
				    	Responce re = new Responce();
				    	re.setRegex(m.group(1));
				    	String[] replies = m.group(2).split(",");
				    	for (int j = 0; j < replies.length;j++)
				    		re.addResponce(replies[j]);
				    	rl.addResponce(re);
				    	JSON.saveGSON("responces.json", rl);
				    }
				}
			}
	    }
	}

	@Override
	public void onJoin(String in_str) throws IRCException, IOException {}
	@Override
	public void onQuit(String in_str) throws IRCException, IOException {}
	@Override
	public void onKick(String in_str) throws IRCException, IOException {}

}
