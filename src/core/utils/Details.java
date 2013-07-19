package core.utils;

import java.io.File;
import java.io.IOException;

/**
 * This holds all the data needed to carry out the connection to the IRC server,
 * and any other information that is needed
 * @author Tom Rosier(XeTK)
 */
public class Details
{
	private static final String cfgFile = "Details.json";
	
	private static Details details;
	
	private String server = "127.0.0.1";;
	
	private int port = 6667;
	
	private String botNickName = "JavaBot", smtpEmail = "Spunky@Spunkybot.co.uk",
			smtpHost = "smtp.gmail.com", smtpUser = "spunky@gmail.com", smtpPassword = "Helloworld";
	
	private String[] channels = {"#xetk"}, admins = {"xetk"}, 
			startup = {"PRIVMSG zippy identify helloworld"};
	
	/**
	 * Get our instance of the details class back for us to use.
	 * @return we get the original instance of the class back
	 * @throws IOException this is if we have to load the JSON object
	 */
	public static Details getInstance() throws IOException
	{
		if (new File(cfgFile).exists())
        {
            details = (Details)JSON.loadGSON(cfgFile, Details.class);
        }
        else
        {
        	details = new Details();
            JSON.saveGSON(cfgFile, details);
        }
		return details;
	}
	
	/**
	 * If we get a instance back from JSON we want to set 
	 * this instance of the class to this original instance
	 * @param instance take in the instance we want to set the class to.
	 */
	public static void setInstance(Details instance)
	{
		details = instance;
	}
	
	/**
	 * Check if a user is an admin without reusing the same code over and over.
	 * @param name The user we want to check if is a admin.
	 * @return true or false to if the user is a admin.
	 */
	public boolean isAdmin(String name)
	{
		for (int i = 0; i < admins.length;i++)
			if (name.equalsIgnoreCase(admins[i]))
				return true;
		return false;
	}
	
	/**
	 * Getters
	 */
	public static Details getDetails()
	{
		return details;
	}

	public String getServer()
	{
		return server;
	}

	public int getPort()
	{
		return port;
	}

	public String[] getChannels()
	{
		return channels;
	}

	public String[] getAdmins()
	{
		return admins;
	}

	public String[] getStartup()
	{
		return startup;
	}
	public String getNickName() 
	{
		return botNickName;
	}

	public String getSmtpEmail() 
	{
		return smtpEmail;
	}

	public String getSmtpHost() 
	{
		return smtpHost;
	}

	public String getSmtpUser() 
	{
		return smtpUser;
	}

	public String getSmtpPassword() 
	{
		return smtpPassword;
	}

	// Setters
	public void setNickName(String nickname)
	{
		this.botNickName = nickname;
	}
}
