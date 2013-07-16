import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import core.utils.IRC;

//Refactor
public class Imgur implements PluginTemp
{

	@Override
	public String name()
	{
		return "Imgur";
	}

	@Override
	public void onMessage(Message in_message) throws Exception
	{
		Matcher m = Pattern.compile("(http://(?:www.)?imgur.com/((?:gallery/)?).*)",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
				.matcher(in_message.getMessage());
		if (m.find())
		{
			URL myUrl = new URL(m.group(1));
		    BufferedReader in = new BufferedReader(new InputStreamReader(
		                        myUrl.openStream()));
		
		    String line, dislikes = "", likes = "" , viewcount = "",
		    		title = "", time = "", bandwidth = "";
		    
		    boolean date = false;
		    
		    int dateind = 0;
		    
		    while ((line = in.readLine()) != null)
		    {
		    	if (time.isEmpty())
		    	{
		    		if (dateind == 2)
		    		{
				    	m = Pattern.compile("([\\d][\\w\\s]*)",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
								.matcher(line);
				    	if (m.find())
				    		time = m.group(1);
		    		}
			    	
			    	if (line.contains("<div id=\"stats-submit-date\" style=\"float:left\">"))
			    		date = true;
			    	
			    	if (date)
			    		dateind++;
		    	}
		    	if (time.isEmpty())
		    	{
			    	m = Pattern.compile("<span id=\"nicetime\" title=\"(.*)\">(.*)</span>",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
							.matcher(line);
			    	if (m.find())
			    		time = m.group(2);
		    	}
			    	
		    	
		    	m = Pattern.compile("<div class=\"title negative \" title=\"([\\d,]*) dislikes\" style=\"width: [\\d.]*%\"></div>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
		    	if (m.find())
		    		dislikes = m.group(1);
		    	
		    	m = Pattern.compile("<div class=\"title positive \" title=\"([\\d,]*) likes\" style=\"width: [\\d.]*%\"></div>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
		    	if (m.find())
		    		likes = m.group(1);
		    	
		    	m = Pattern.compile("<span id=\"stats-bandwidth\" class=\"stat\">([\\d\\w\\s.]*)</span> bandwidth</span>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
		    	if (m.find())
		    		bandwidth = m.group(1);
		    	
		    	m = Pattern.compile("<span id=\"stats-views\" class=\"stat\">([\\d,]*)</span> views",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
		    	if (m.find())
		    		viewcount = m.group(1);
		    	
		    	m = Pattern.compile("<title>\\s*(.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
		    	if (m.find())
		    		title = m.group(1);
		    	
		    }
		    title = title.trim();
		    IRC irc =IRC.getInstance();
		    irc.sendPrivmsg(in_message.getChannel(), title + ", Views : " + viewcount + ", Bandwidth used : " + bandwidth + ", Likes/Dislikes : " +  likes + "/" + dislikes);
		}
	}
	
	@Override
	public void onCreate() throws Exception{}
	@Override
	public void onTime() throws Exception{}
	@Override
	public void onJoin(Join in_join) throws Exception{}
	@Override
	public void onQuit(Quit in_quit) throws Exception{}
	@Override
	public void onKick(Kick in_kick) throws Exception{}
}
