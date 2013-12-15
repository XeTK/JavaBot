package plugin.web.global;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;

import core.event.Message;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.Colour;
import core.utils.IRC;
import core.utils.Regex;

public class WebScanner extends Plugin{

	public void onMessage(Message inMessage) throws Exception {
		Matcher m = Regex.getMatcher("(http(?:s)?://(?:www.)?[\\w\\d]*.[\\w]*[./][\\.\\w\\d//?/=-]*)", inMessage.getMessage());
		Matcher yt = Regex.getMatcher("(https?://(?:www\\.)?youtu.?be(?:.com)?/(?:v/)?(?:watch\\?v=)?-?.*)",inMessage.getMessage());
		Matcher img = Regex.getMatcher("(http://(?:www.)?imgur.com/((?:gallery/)?).*)", inMessage.getMessage());
		
		if (m.find()&&!yt.find()&&!img.find()) {
			System.out.println(m.group());
			URL myUrl = new URL(m.group(1));
			if (myUrl != null){
				InputStreamReader isr = new InputStreamReader(myUrl.openStream());
				BufferedReader in = new BufferedReader(isr);
	
				String line, title = "";
	
				while ((line = in.readLine()) != null) {
	
					m = Regex.getMatcher("<title>(.*)</title>", line);
					if (m.find())
						title = m.group(1);
				}
				if (!title.isEmpty()) {
					IRC irc = IRC.getInstance();
					String link = Colour.colour("[LINK]", Colour.YELLOW, Colour.BLUE);
					irc.sendPrivmsg(inMessage.getChannel(), link + " '" + title + "'");	
				}
				
				in.close();
			}
		}
	}

	public String getHelpString(){
		return null;
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
	}
}
