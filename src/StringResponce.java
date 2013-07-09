import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Random;

import core.Details;
import core.IRC;
import core.JSON;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.PluginTemp;
import addons.responce.Responce;
import addons.responce.ResponceList;

public class StringResponce implements PluginTemp 
{
	// Keep a farm of possible responses along with there regex's
	private ResponceList rl = new ResponceList();
	private Random random = new Random();

	/**
	 * Returns the Name of the class for the plugin loader.
	 */
	@Override
	public String name() 
	{
		return "StringResponce";
	}

	/**
	 * On create we want to load any previously saved Responces back into the
	 * system for them to be used again.
	 */
	@Override
	public void onCreate() throws Exception 
	{
		String path = "responces.json";

		/* 
		 * If the file already exists then convert it back into a class 
		 * Else we create a new file to stop us having a null pointer.
		 */
		if (new File(path).exists())
			rl = (ResponceList) JSON.loadGSON(path, ResponceList.class);
		else
			JSON.saveGSON(path, new ResponceList());

		if (rl == null)
			rl = new ResponceList();
	}

	@Override
	public void onMessage(Message in_message) throws Exception
	{
		IRC irc = IRC.getInstance();
		ArrayList<Responce> responces = rl.getResponces();
		Details details = Details.getIntance();
		String botname = details.getNickName();

		if (responces.size() > 0)
		{
			for (Responce resp : responces)
			{
				String resp_regex = resp.getRegex();

				// set botname at start of regex
				resp_regex = resp_regex.replace("{0}", botname);

				if (in_message.getMessage().matches(resp_regex))
				{
					// grab array of actual valid responses for this input
					String[] replies = resp.getResponces();

					int inx = random.nextInt(replies.length);
					String reply = replies[inx];

					// fix up reply strings to include target usernames
					reply = reply.replace("{0}", botname);
					reply = reply.replace("{1}", in_message.getUser());

					irc.sendPrivmsg(in_message.getChannel(), reply);
				}
			}
		}

		// Adding a new response.
		
		// If the user is a member of the admin.
		if (details.isAdmin(in_message.getUser()))
		{
			/* 
			 * Regex to check if the string is attached to this class and 
			 * if we want to add a new responce,
			 * .responce (Regex Here), [Responces]...
			 * .responce .* ,helloworld, blah
			 */
			Matcher m = Pattern
					.compile("^\\.responce (.*),([a-zA-Z0-9 ,]*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
					.matcher(in_message.getMessage());

			// If the message matches the regex.
			if (m.find()) 
			{
				// Instantiate a new response object.
				Responce re = new Responce();

				// Set the regex for the response to the first group in the add regex.
				re.setRegex(m.group(1));

				// Split the Responses on a ,
				String[] replies = m.group(2).split(",");

				// For each new response add it to the array of responses.
				for (int j = 0; j < replies.length; j++)
					re.addResponce(replies[j]);

				// Add the new response to the farm of responses so that is kept.
				rl.addResponce(re);

				// Resave the farm so we don't loose are new response/
				JSON.saveGSON("responces.json", rl);
				
				// Finaly Prompt the user that the response has been added.
				irc.sendPrivmsg(in_message.getChannel(),
						"Added new Responce");
			}
		}
	}

	// Unused.
	@Override
	public void onTime() throws Exception {}

	@Override
	public void onJoin(Join in_join) throws Exception {}

	@Override
	public void onQuit(Quit in_quit) throws Exception {}

	@Override
	public void onKick(Kick in_kick) throws Exception {}

	@Override
	public void onOther(String in_str) throws Exception {}
}
