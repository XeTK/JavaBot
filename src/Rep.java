import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import core.utils.IRC;
import core.utils.JSON;
import addons.reputation.RepList;
import addons.reputation.Reputation;


public class Rep implements PluginTemp
{
	private final String cfgFile = "Rep.json";
	
	private RepList repList = new RepList();
	
	@Override
	public String name() 
	{
		return "Reputation";
	}
	
	@Override
	public void onCreate(String savePath) throws Exception 
	{
		if (new File(cfgFile).exists())
			repList = (RepList)JSON.loadGSON(cfgFile, RepList.class);
		else
			JSON.saveGSON(cfgFile, repList);
			
	}

	@Override
	public void onMessage(Message in_message) throws Exception
	{
		String channel = in_message.getChannel();
		String message = in_message.getMessage();
		
		//Message .Trim
		
		if (message.charAt(message.length() - 1 ) == ' ')
			message = message.substring(0, message.length() -1);
		
		IRC irc = IRC.getInstance();
		if (message.matches("(^[a-zA-Z0-9]*)[\\s\\+-]([-\\+])[\\=\\s]*([\\d]*)"))
		{
			Matcher r = Pattern.compile("(^[a-zA-Z0-9]*)[\\s\\+-]([-\\+])[\\=\\s]*([\\d]*)",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(message);
			if (r.find())
			{
				String item = r.group(1);
				String type = r.group(2);
				String ammount = r.group(3);
				
				if (!in_message.getUser().equalsIgnoreCase(item));
				{
					if (!type.equals(""))
					{
						int iAmmount = 0;
					
						if (type.equals("++"))
						{
							iAmmount = 1;
						}
						else if(type.equals("--"))
						{
							iAmmount = -1;
						}
						else
						{
							type = type.trim();
							ammount = ammount.trim();
							if (type.equals("+"))
								iAmmount = Integer.valueOf(ammount);
							else	
								iAmmount = Integer.valueOf(type + ammount);
						}
						if (iAmmount > 100||iAmmount < -100)
						{
							irc.sendPrivmsg(channel, "You cant do that its to much rep...");
						}
						else
						{
							Reputation tRep = repList.getRep(item);
							tRep.modRep(iAmmount);
							irc.sendPrivmsg(channel, item + ": Rep = " + tRep.getRep() + "!");
						}
					}
				}
			}
		}
		else if (message.matches("\\.rep [A-Za-z0-9#]+$"))
		{
			String[] t = message.split(" ");
			if (t.length > 0||t[1] != null)
				irc.sendPrivmsg(channel, t[1] + ": Rep = " + repList.getRep(t[1]).getRep() + "!");
		}
		JSON.saveGSON(cfgFile, repList);
	}
	
	@Override
	public String getHelpString()
	{
		return "REP: " +
				".rep *Item* - view the reputation of a item : " +
				"*Item*--/++ - increment or decrement the rep of a desired item / : " +
				"*Item* +/- *Ammount - increment or decrement the rep of a set item by a set amount : ";
	}
	
	@Override
	public void onTime() throws Exception {}
	@Override
	public void onJoin(Join in_join) throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
	@Override
	public void onKick(Kick in_kick) throws Exception {}
	@Override
	public void rawInput(String in_str) throws Exception{}


}

