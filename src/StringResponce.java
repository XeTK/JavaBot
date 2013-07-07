import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import addons.Responce;
import addons.ResponceList;
import plugin.PluginTemp;
import program.Details;
import program.IRC;
import program.JSON;

public class StringResponce implements PluginTemp 
{
	// Keep a farm of possible responses along with there regex's
	private ResponceList rl = new ResponceList();

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

		// Pull all the responses out of the farm to make them easier to access.
		ArrayList<Responce> responces = rl.getResponces();

		Details details = Details.getIntance();

		// Get the bot's nickname to make life easier later.
		String botname = details.getNickName();

		// Detecting responables
		if (responces.size() > 0) 
		{
			// Loop through all the responses so we can check if the strings match.
			for (int i = 0; i < responces.size(); i++) 
			{
				// Get the regex ready for us to check against.
				String regex = responces.get(i).getRegex();

				// Replace the preset value with the bot's name.
				regex = regex.replace("{0}", botname);

				// Match the current message that has been sent to the regex
				if (in_message.getMessage().matches(regex)) 
				{
					// If the string matches return all the possible responses to an array
					String[] replies = responces.get(i).getResponces();

					// Get a random index for are response
					int inx = 1 + (int) (Math.random() * ((replies.length - 1) + 1));

					// If we are outside the array size then we decrement by 1;
					if (inx > replies.length - 1)
						inx--;

					// Select the final final reply and assign it to a string for easy manipulation.
					String reply = replies[inx];

					// Again replace are pre set strings with either botname or username.
					reply = reply.replace("{0}", botname);
					reply = reply.replace("{1}", in_message.getUser());

					// Finally Send the message back to the channel
					irc.sendPrivmsg(in_message.getChannel(), reply);
				}
			}
		}

		// Adding a new response.
		
		// Loop through all the admins for the bot to check if we are aloud to add a response.
		for (int i = 0; i < details.getAdmins().length; i++) 
		{
			// If the user is a member of the admin.
			if (in_message.getUser().equals(details.getAdmins()[i])) 
			{
				/* 
				 * Regex to check if the string is attached to this class and 
				 * if we want to add a new responce,
				 * .responce (Regex Here), [Responces]...
				 * .responce .* ,helloworld, blah
				 */
				Matcher m = Pattern
						.compile("^\\.responce ([a-zA-Z0-9\\.\\*\\\\\\^]*) ,([a-zA-Z0-9 ,]*)",
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
