package plugin.web.imgur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import core.event.Message;
import core.menu.MenuItem;
import core.plugin.Plugin;
/**
 * Imgur plugin.
 *
 * Reports info about Imgur links posted to a channel.
 *
 * @author Tom (bugsduggan) Leaman (tom@tomleaman.co.uk)
 */
public class Imgur extends Plugin {
		
	private static final String TXT_SPOILER = "I ain't spoiling nawthing! Dawgh!";

	private static final String IMGUR_CREDS_FILE = "imgur_creds";
	private static final String API_ENDPOINT = "https://api.imgur.com/3/gallery/image/";

	private String clientId = new String();

	public Imgur() {
		try {
			clientId = Imgur.getClientId(IMGUR_CREDS_FILE);
		} catch (FileNotFoundException e) {
			System.err.println("No Imgur creds file found - failed to load");
			return;
		}
		// rewrite onMessage
	}

	public void onMessage(Message messageObj) throws Exception {
		try {
			_onMessage(messageObj);
		} catch (Exception e) {
			irc.sendPrivmsg(messageObj.getChannel(), e.toString());
		}
	}

	public void _onMessage(Message messageObj) throws Exception {
		String message = messageObj.getMessage();
		String channel = messageObj.getChannel();

		/*
		 * I'm aiming to match against 3 different URL forms:
		 * http://imgur.com/Tg4pZBe
		 * http://i.imgur.com/Tg4pZBe.jpg
		 * http://imgur.com/r/programmerhumor/Tg4pZBe
		 *
		 * The last 2 forms will require a little cleanup if matched.
		 */
		Pattern imgurRegex = Pattern.compile("http://(?:i\\.)?imgur.com/(.*)\\b");
		Matcher m = imgurRegex.matcher(message);

		if (m.find()) {
			String imageId = m.group(1);
			// handle path/to/id (case 3)
			String[] tokens = imageId.split("/");
			imageId = tokens[tokens.length - 1];
			// strip file extension (case 2)
			if (imageId.contains(".")) {
				tokens = imageId.split("\\.");
				imageId = tokens[0];
			}

			try {
				URL url = new URL(API_ENDPOINT + imageId);
				HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestProperty("Authorization", "Client-ID " + clientId);
				int statusCode = conn.getResponseCode();
				if (statusCode != 200) {
					// oh well, sucks to be you.
					// Actually, it might just be that the image is not in
					// the 'gallery'. So you might be able to get to it at
					// the URL without the '/gallery' bit.
					// I do not have the patience to handle that correctly
					// today. Deal with it.
					return;
				}

				StringBuffer response = new StringBuffer();
				String line = null;

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = in.readLine()) != null) {
					response.append(line);
				}
				in.close();

				Gson parser = new Gson();
				ImgurResponse imgurResponse = (ImgurResponse) parser.fromJson(response.toString(), ImgurResponse.class);
				String imageString = new String();
				if (imgurResponse.isNsfw()) {
					imageString = imageString + "[NSFW] ";
				}
				imageString = imageString + "[" + imgurResponse.getType() + "] ";
				imageString = imageString + "'" + imgurResponse.getTitle() + "'";
				imageString = imageString + " - " + imgurResponse.getViews() + " views";
				imageString = imageString + " (" + imgurResponse.getLikes() + "/" + imgurResponse.getDislikes() + ")";
				
				if (message.startsWith("-s ") || message.endsWith(" -s"))
					imageString = TXT_SPOILER;
				
				irc.sendPrivmsg(channel, imageString);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getClientId(String filename) throws FileNotFoundException {
		File f = new File(filename);
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line = new String();
		try {
			line = in.readLine();
			in.close();
		} catch (IOException e) {
			// TODO something more proactive
			e.printStackTrace();
		}
		return line;
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		// Does not need a menu...
	}

}
