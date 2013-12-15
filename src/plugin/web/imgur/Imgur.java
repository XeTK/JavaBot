package plugin.web.imgur;

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

public class Imgur extends Plugin {

	public void onMessage(Message in_message) throws Exception {
		Matcher m = Regex.getMatcher("(http://(?:www.)?imgur.com/((?:gallery/)?).*)", in_message.getMessage());
		if (m.find()) {
			URL myUrl = new URL(m.group(1));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					myUrl.openStream()));

			String line, dislikes = "", likes = "", viewcount = "", title = "", time = "", bandwidth = "";

			boolean date = false;

			int dateind = 0;

			while ((line = in.readLine()) != null) {
				if (time.isEmpty()) {
					if (dateind == 2) {
						m = Regex.getMatcher("([\\d][\\w\\s]*)", line);
						if (m.find())
							time = m.group(1);
					}

					if (line.contains("<div id=\"stats-submit-date\" style=\"float:left\">"))
						date = true;

					if (date)
						dateind++;
				}
				if (time.isEmpty()) {
					m = Regex.getMatcher("<span id=\"nicetime\" title=\"(.*)\">(.*)</span>", line);
					if (m.find())
						time = m.group(2);
				}

				m = Regex.getMatcher("<div class=\"title negative \" title=\"([\\d,]*) dislikes\" style=\"width: [\\d.]*%\"></div>", line);
				if (m.find())
					dislikes = m.group(1);

				m = Regex.getMatcher("<div class=\"title positive \" title=\"([\\d,]*) likes\" style=\"width: [\\d.]*%\"></div>", line);
				if (m.find())
					likes = m.group(1);

				m = Regex.getMatcher("<span id=\"stats-bandwidth\" class=\"stat\">([\\d\\w\\s.]*)</span> bandwidth</span>", line);
				if (m.find())
					bandwidth = m.group(1);

				m = Regex.getMatcher("<span id=\"stats-views\" class=\"stat\">([\\d,]*)</span> views", line);
				if (m.find())
					viewcount = m.group(1);

				m =Regex.getMatcher("<title>\\s*(.*)", line);
				if (m.find())
					title = m.group(1);

			}
			title = title.trim();
			IRC irc = IRC.getInstance();
			String coloured = Colour.colour(" I", Colour.GREEN,Colour.BLACK);
			coloured += Colour.colour("mgur ", Colour.WHITE, Colour.BLACK);
			title = title.replace("Imgur",coloured);
			irc.sendPrivmsg(in_message.getChannel(), "'" + title + "', Views : " + viewcount + ", Bandwidth used : " + bandwidth + ", Likes/Dislikes : " + likes + "/" + dislikes);
		}
	}

	public String getHelpString() {
		return "IMGUR:\n"
				+ "\t<URL> - This will pharse Imgur links\n";
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
	}
}
