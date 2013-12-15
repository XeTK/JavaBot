package plugin.tools.stringresponse;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.Random;

import core.Channel;
import core.event.Message;
import core.menu.MenuItem;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;
import core.utils.JSON;
import core.utils.Regex;
import core.utils.RegexFormatter;

public class StringResponse extends Plugin {
	private final String RESPONSE_PATH_LOCATION = "Response.json";

	private final String NEW_RESPONSE = "Added new Response";

	private final String RGX_RESPONSE = RegexFormatter.format("response\\s(.*)//(.*)");

	private String responsesPath_ = new String();

	// Keep a farm of possible responses along with there regex's
	private ResponseList responseList_ = new ResponseList();
	private Random random_ = new Random();

	/**
	 * On create we want to load any previously saved Responses back into the
	 * system for them to be used again.
	 */
	public void onCreate(Channel inChannel) throws Exception {
		responsesPath_ = inChannel.getPath() + RESPONSE_PATH_LOCATION;
		/*
		 * If the file already exists then convert it back into a class Else we
		 * create a new file to stop us having a null pointer.
		 */
		if (new File(responsesPath_).exists())
			responseList_ = (ResponseList) JSON.load(responsesPath_, ResponseList.class);
		else
			JSON.save(responsesPath_, new ResponseList());

		// Double check if the object exists if it dosn't then we instantiate it
		// to stop null pointers.
		if (responseList_ == null)
			responseList_ = new ResponseList();
	}

	public void onMessage(Message inMessage) throws Exception {
		if (!inMessage.isPrivMsg()) {
			IRC irc = IRC.getInstance();
			ArrayList<Response> responces = responseList_.getResponses();

			// Get the information for the bot, so we can use the username.
			Details details = Details.getInstance();
			// Get the botname so we can replace it later on.
			String botName = details.getNickName();

			// Check if there is more than 0 responses, there's no point
			// continuing otherwise.
			if (responces.size() > 0) {
				for (Response resp : responces) {
					String responseRegex = resp.getRegex();

					// set botname at start of regex
					responseRegex = responseRegex.replace("{0}", botName);

					if (inMessage.getMessage().matches(responseRegex)) {
						// grab array of actual valid responses for this input
						String[] replies = resp.getResponses();

						int inx = random_.nextInt(replies.length);
						String reply = replies[inx];

						// fix up reply strings to include target usernames
						reply = reply.replace("{1}", inMessage.getUser());
						// Checks if it is a /me message or not
						if (reply.startsWith("{0}")) {
							reply = reply.replace("{0}", "");
							irc.sendActionMsg(inMessage.getChannel(), reply);
						} else {
							reply = reply.replace("{0}", botName);
							irc.sendPrivmsg(inMessage.getChannel(), reply);
						}
					}
				}
			}

			// Adding a new response.

			// If the user is a member of the admin.
			if (details.isAdmin(inMessage.getUser())) {
				/*
				 * Regex to check if the string is attached to this class and if
				 * we want to add a new response, .response (Regex Here),
				 * [Responses]... .response .* ,helloworld, blah
				 */
				Matcher m = Regex.getMatcher(RGX_RESPONSE, inMessage.getMessage());

				// If the message matches the regex.
				if (m.find()) {
					// Instantiate a new response object.
					Response response = new Response();

					// Set the regex for the response to the first group in the
					// add regex.
					response.setRegex(m.group(1));

					System.out.println(m.group(2));
					// Split the Responses on a ,
					String[] replies = m.group(2).split("[/]");

					// For each new response add it to the array of responses.
					for (int j = 0; j < replies.length; j++)
						response.addResponce(replies[j]);

					// Add the new response to the farm of responses so that is
					// kept.
					responseList_.addResponse(response);

					// Re save the farm so we don't loose are new response.
					JSON.save(responsesPath_, responseList_);

					// Finally Prompt the user that the response has been added.
					irc.sendPrivmsg(inMessage.getChannel(), NEW_RESPONSE);
				}
			}
		}
	}

	public String getHelpString() {
		return "RESPONSE: \n"
				+ "\t.response <regex here>//<text here>/{0} <bot name>/{1} <channel user>\n";
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
	}
}
