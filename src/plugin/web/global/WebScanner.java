package plugin.web.global;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Message;
import core.plugin.Plugin;
import core.utils.Colour;
import core.utils.IRC;

public class WebScanner extends Plugin{

	public void onMessage(Message inMessage) throws Exception {
		Matcher m = Pattern
				.compile(
						"(http(?:s)?://(?:www.)?[\\w\\d]*.[\\w]*[./][\\w\\d//?/=-]*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						inMessage.getMessage());
		Matcher yt = Pattern
				.compile(
						"(https?://(?:www\\.)?youtu.?be(?:.com)?/(?:v/)?(?:watch\\?v=)?-?.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						inMessage.getMessage());
		Matcher img = Pattern
				.compile(
					"(http://(?:www.)?imgur.com/((?:gallery/)?).*)",
					Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
					inMessage.getMessage());
		
		if (m.find()&&!yt.find()&&!img.find()) {
			URL myUrl = new URL(m.group(1));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					myUrl.openStream()));

			String line, title = "";

			while ((line = in.readLine()) != null) {

				m = Pattern.compile("<title>(.*)</title>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
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

	public String getHelpString(){
		return null;
	}
}
