package plugin.stringresponse;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

import core.Channel;
import core.event.Message;
import core.plugin.Plugin;
import core.utils.Details;
import core.utils.IRC;
import core.utils.JSON;

public class StringResponse extends Plugin
{
	private final String responses_path_loc = "Response.json";
	private String responses_path = new String();
	
	// Keep a farm of possible responses along with there regex's
	private ResponseList rl = new ResponseList();
	private Random random = new Random();

	/**
	 * On create we want to load any previously saved Responses back into the
	 * system for them to be used again.
	 */
	public void onCreate(Channel in_channel) throws Exception 
	{
		responses_path = in_channel.getPath() + responses_path_loc;
		/* 
		 * If the file already exists then convert it back into a class 
		 * Else we create a new file to stop us having a null pointer.
		 */
		if (new File(responses_path).exists())
			rl = (ResponseList) JSON.loadGSON(responses_path, ResponseList.class);
		else
			JSON.saveGSON(responses_path, new ResponseList());

		// Double check if the object exists if it dosn't then we instantiate it to stop null pointers.
		if (rl == null)
			rl = new ResponseList();
	}

	public void onMessage(Message in_message) throws Exception
	{
		if (!in_message.isPrivMsg())
		{
			IRC irc = IRC.getInstance();
			ArrayList<Response> responces = rl.getResponses();
			
			// Get the information for the bot, so we can use the username.
			Details details = Details.getInstance();
			// Get the botname so we can replace it later on.
			String botname = details.getNickName();
	
			// Check if there is more than 0 responses, there's no point continuing otherwise.
			if (responces.size() > 0)
			{
				for (Response resp : responces)
				{
					String resp_regex = resp.getRegex();
	
					// set botname at start of regex
					resp_regex = resp_regex.replace("{0}", botname);
	
					if (in_message.getMessage().matches(resp_regex))
					{
						// grab array of actual valid responses for this input
						String[] replies = resp.getResponses();
	
						int inx = random.nextInt(replies.length);
						String reply = replies[inx];
	
						// fix up reply strings to include target usernames
						reply = reply.replace("{1}", in_message.getUser());
						// Checks if it is a /me message or not
						if (reply.startsWith("{0}"))
						{
							reply = reply.replace("{0}", "");
							irc.sendActionMsg(in_message.getChannel(), reply);
						}
						else
						{
							reply = reply.replace("{0}", botname);
							irc.sendPrivmsg(in_message.getChannel(), reply);
						}
					}
				}
			}
	
			// Adding a new response.
			
			// If the user is a member of the admin.
			if (details.isAdmin(in_message.getUser()))
			{
				/* 
				 * Regex to check if the string is attached to this class and 
				 * if we want to add a new response,
				 * .response (Regex Here), [Responses]...
				 * .response .* ,helloworld, blah
				 */
				Matcher m = Pattern
						.compile(".response\\s(.*)//(.*)",
								Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
						.matcher(in_message.getMessage());
	
				// If the message matches the regex.
				if (m.find()) 
				{
					// Instantiate a new response object.
					Response re = new Response();
	
					// Set the regex for the response to the first group in the add regex.
					re.setRegex(m.group(1));
	
					System.out.println(m.group(2));
					// Split the Responses on a ,
					String[] replies = m.group(2).split("[/]");
	
					// For each new response add it to the array of responses.
					for (int j = 0; j < replies.length; j++)
						re.addResponce(replies[j]);
	
					// Add the new response to the farm of responses so that is kept.
					rl.addResponse(re);
	
					// Re save the farm so we don't loose are new response.
					JSON.saveGSON(responses_path, rl);
					
					// Finally Prompt the user that the response has been added.
					irc.sendPrivmsg(in_message.getChannel(),
							"Added new Response");
				}
			}
		}
	}
	
	public String getHelpString()
	{
		// TODO Auto-generated method stub
		return ".response <regex here>//<text here>/{0} <bot name>/{1} <channel user>";
	}
}
