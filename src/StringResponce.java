import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;

import addons.Responce;
import addons.ResponceList;

import plugin.PluginTemp;
import program.Details;
import program.IRC;
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
	public void onCreate() throws Exception 
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
	public void onMessage(Message in_message) throws Exception 
	{
		IRC irc = IRC.getInstance();
		
	    ArrayList<Responce> responces = rl.getResponces();
	    
	    if (responces.size() > 0)
	    {
		    for (int i = 0; i < responces.size();i++)
		    {
		    	System.out.println(responces.get(i).getRegex().replace("{0}", Details.getIntance().getNickName()));
		    	Matcher m = Pattern.compile(responces.get(i).getRegex().replace("{0}", Details.getIntance().getNickName()),Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_message.getMessage());
		    	if (m.find())
		    	{
		    		String[] replies = responces.get(i).getResponces();

	    			int inx = 1 + (int)(Math.random() * ((replies.length - 1) + 1));
	    			if (inx > replies.length -1)
	    				inx--;
	    			irc.sendServer("PRIVMSG " + in_message.getChannel() + " " +replies[inx].replace("{0}", Details.getIntance().getNickName()).replace("{1}", in_message.getUser()));
		    	}
		    }
	    }
	    
	    Details details = Details.getIntance();
        for (int i = 0;i < details.getAdmins().length;i++)
		{
			if (in_message.getUser().equals(details.getAdmins()[i]))
			{
			    Matcher m = Pattern.compile("^\\.responce ([a-zA-Z0-9\\.\\*\\\\\\^]*) ,([a-zA-Z0-9 ,]*)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(in_message.getMessage());
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

	@Override
	public void onTime() throws Exception {}
	@Override
	public void onJoin(Join in_join) throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
	@Override
	public void onKick(Kick in_kick) throws Exception {}

}
