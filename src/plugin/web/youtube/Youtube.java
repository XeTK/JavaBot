package plugin.web.youtube;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Message;
import core.plugin.Plugin;
import core.utils.Colour;
import core.utils.IRC;

public class Youtube extends Plugin {

	// This needs rewritting again from scratch

	public void onMessage(Message in_message) throws Exception {
		Matcher m = Pattern
				.compile(
						"(https?://(?:www\\.)?youtu.?be(?:.com)?/(?:v/)?(?:watch\\?v=)?-?.*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						in_message.getMessage());
		if (m.find()) {
			URL myUrl = new URL(m.group(1));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					myUrl.openStream()));

			String line, dislikes = "", likes = "", viewcount = "", title = "";

			boolean views = false;
			while ((line = in.readLine()) != null) {
				if (views) {
					viewcount = line.replaceAll("\\s", "");
					views = false;
				}

				// Dislikes
				m = Pattern.compile(
						"<span class=\"dislikes-count\">([\\d,]*)</span>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
				if (m.find())
					dislikes = m.group(1);

				// Likes
				m = Pattern.compile(
						"<span class=\"likes-count\">([\\d,]*)</span>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
				if (m.find())
					likes = m.group(1);

				// Views
				m = Pattern
						.compile(
								"</span></span><div id=\"watch7-views-info\">      <span class=\"watch-view-count \" >",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
				if (m.find())
					views = true;

				m = Pattern.compile("<title>(.*)</title>",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(line);
				if (m.find())
					title = m.group(1);

				// ADD UPLOADED TIME
			}
			IRC irc = IRC.getInstance();

			String yt = Colour.colour("You", Colour.BLACK, Colour.WHITE);
			yt += Colour.colour("Tube", Colour.WHITE, Colour.RED);
			title = title.replace("YouTube", yt);
			irc.sendPrivmsg(in_message.getChannel(), "'" + title + "', " + viewcount
					+ " Views, " + likes + "|" + dislikes + " Likes|Dislikes");
			in.close();
		}
	}

	public String getHelpString() {
		return "YOUTUBE:\n"
				+ "\t<URL> - This will pharse YouTube links for there stats\n";
	}
}
