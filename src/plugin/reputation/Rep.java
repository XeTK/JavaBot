package plugin.reputation;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.IRC;
import core.utils.JSON;

public class Rep extends Plugin
{
	private final String cfgFile_loc = "Rep.json";
	private String cfgFile = new String();
	
	private RepList repList = new RepList();
	
	public Rep()
	{
		
	}
	
	public String name() 
	{
		return "Reputation";
	}
	
	public void onCreate(Channel in_channel) throws Exception 
	{
		cfgFile = in_channel.getPath() + cfgFile_loc;
		if (new File(cfgFile).exists())
			repList = (RepList)JSON.loadGSON(cfgFile, RepList.class);
		else
			JSON.saveGSON(cfgFile, repList);
			
	}

	public void onMessage(Message in_message) throws Exception
	{
		if (!in_message.isPrivMsg())
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
	}
	
	public String getHelpString()
	{
		return "REP: " +
				".rep *Item* - view the reputation of a item : " +
				"*Item*--/++ - increment or decrement the rep of a desired item / : " +
				"*Item* +/- *Ammount - increment or decrement the rep of a set item by a set amount : ";
	}

}

