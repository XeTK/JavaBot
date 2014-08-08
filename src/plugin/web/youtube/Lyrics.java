package plugin.web.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.event.Message;
import core.plugin.Plugin;
import core.utils.IRC;


public class Lyrics extends Plugin {
// ([\d\w\s&]*)(?:\s)?[:-](?:\s)?([\w\s\d&]*)
	private ArrayList<String> lastSong = null;
	private int lastIndex = 0;
	
	private IRC irc = IRC.getInstance();
	public void onMessage(Message inMessage) throws Exception {
		if (inMessage.getMessage().startsWith(".sing")){
			if (lastSong != null) {
				if (lastSong.size() != lastIndex) {
					for (int i = lastIndex; i < lastSong.size(); i++){
						String cur = lastSong.get(i);
						lastIndex = i + 1;
						if (cur.isEmpty()) {
							break;
						} else {
							irc.sendPrivmsg(inMessage.getChannel(), cur);
						}
					}
				} else {
					irc.sendPrivmsg(inMessage.getChannel(), "But " + inMessage.getUser() + " I've already sung!");
				}
			} else {
				irc.sendActionMsg(inMessage.getChannel(), "Cant remember any songs right now :(");
			}
		} else {
			Matcher m = Pattern.compile(
							"(https?://(?:www\\.)?youtu.?be(?:.com)?/(?:v/)?(?:watch\\?v=)?-?.*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
							inMessage.getMessage());
			if (m.find()) {
				URL myUrl = new URL(m.group(1));
				BufferedReader in = new BufferedReader(new InputStreamReader(
						myUrl.openStream()));
	
				String line = new String();
				String title = new String();
				
				while ((line = in.readLine()) != null) {
	
					m = Pattern.compile("<title>(.*)</title>",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
							.matcher(line);
					if (m.find())
						title = m.group(1);
	
				}
				in.close();
				
				if (!title.isEmpty()){
					WebUtils w = new WebUtils();
					title = w.unescapeHTML(title);
					title = title.replace(" - YouTube", "");
					title = title.toLowerCase();
					System.out.println(title);
					m = Pattern.compile(
								"([\\d\\w\\s&]*)(?:\\s)?[:-](?:\\s)?([\\w\\s\\d&]*)",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
								title);
					if (m.find()) {
						String url = "http://www.lyrics.com/";
						url += m.group(2).trim().replaceAll("\\s", "-");
						url += "-lyrics-";
						url += m.group(1).trim().replaceAll("\\s", "-");
						url += ".html";
						System.out.println("'" + url + "'" );
						Process child = Runtime.getRuntime().exec("curl " + url);
						InputStream bin = child.getInputStream();
						String msg = fromStream(bin);
						String[] doc = msg.split("\n");
						ArrayList<String> lyrics = new ArrayList<String>();
						boolean reading = false;
						for (int i = 0; i < doc.length;i++){
							String cur = doc[i];
							cur = trim(cur);
							if (cur.contains("<div id=\"lyric_space\">")) {
								reading = true;
								continue;
							} else if (cur.contains("<br />---<br />")) {
								reading = false;
								break;
							}
							if (reading)
								lyrics.add(cur.replace("<br />", ""));
							
						}
						lyrics = w.unescapeHTML(lyrics);
						lastSong = lyrics;
						lastIndex = 0;
					}
				}
			}
		}
	}

	public String getHelpString() {
		return "YOUTUBE:\n"
				+ "\t<URL> - This will pharse YouTube links for there stats\n";
	}
	public static String fromStream(InputStream in) throws IOException
	{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    StringBuilder out = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null) {
	        out.append(line + "\n");
	    }
	    return out.toString();
	}

	public String trim(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != ' ') {
				return str.substring(i);
			}
		}
		return str.trim();
	}
}
