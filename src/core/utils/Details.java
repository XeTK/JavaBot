package core.utils;

import java.io.File;
import java.io.IOException;

/**
 * This holds all the data needed to carry out the connection to the IRC server,
 * and any other information that is needed
 * 
 * @author Tom Rosier(XeTK)
 */
public class Details {
	
	private static Details details;
	
	private static final String CONFIG_FILE_     = "Details.json";
	
	private static final String TXT_FAILED_START = "Populate %s before reexecuting the application!";

	private int      port         = 6667;

	private char     cmdPrefix    = '.';
	
	private String   server       = "127.0.0.1";
	private String   botNickName  = "JavaBot";
	private String   smtpEmail    = "Spunky@Spunkybot.co.uk";
	private String   smtpHost     = "smtp.gmail.com";
	private String   smtpUser     = "spunky@gmail.com";
	private String   smtpPassword = "Helloworld";

	private String[] channels     = { "#xetk" };
	private String[] admins       = { "xetk" };
	private String[] startup      = { "PRIVMSG zippy identify helloworld" };

	/**
	 * Get our instance of the details class back for us to use.
	 * 
	 * @return we get the original instance of the class back
	 * @throws IOException this is if we have to load the JSON object
	 */
	public static Details getInstance() {
		
		try {
			
			if (new File(CONFIG_FILE_).exists()) {
				details = (Details) JSON.load(CONFIG_FILE_, Details.class);
			} 
			
			if (details == null) {
				
				details = new Details();
				
				JSON.save(CONFIG_FILE_, details);
				
				// We don't want the program to load with a unpopulated JSON file.
				System.out.println(String.format(TXT_FAILED_START, CONFIG_FILE_));
				System.exit(1);
			}
		} catch (IOException e) {
			// If there was an problem while processing the JSON file.
			e.printStackTrace();
			System.exit(1);
		}
		return details;
	}

	/**
	 * If we get a instance back from JSON we want to set this instance of the
	 * class to this original instance
	 * 
	 * @param instance take in the instance we want to set the class to.
	 */
	public static void setInstance(Details instance) {
		details = instance;
	}

	/**
	 * Check if a user is an admin without reusing the same code over and over.
	 * 
	 * @param name The user we want to check if is a admin.
	 * @return true or false to if the user is a admin.
	 */
	public boolean isAdmin(String name) {
		for (int i = 0; i < admins.length; i++)
			if (name.equalsIgnoreCase(admins[i]))
				return true;
		return false;
	}

	/**
	 * Getters
	 */
	public static Details getDetails() {
		return details;
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}
	public char getCMDPrefix() {
		return cmdPrefix;
	}

	public String[] getChannels() {
		return channels;
	}

	public String[] getAdmins() {
		return admins;
	}

	public String[] getStartup() {
		return startup;
	}

	public String getNickName() {
		return botNickName;
	}

	public String getSmtpEmail() {
		return smtpEmail;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	// Setters
	public void setNickName(String nickname) {
		this.botNickName = nickname;
	}
}
