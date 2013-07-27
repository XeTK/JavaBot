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
	private static final String CONFIG_FILE_ = "Details.json";

	private static Details details_ = null;

	private int port_ = 6667;

	private String server_ = "127.0.0.1";
	private String botNickName_ = "JavaBot";
	private String smtpEmail_ = "Spunky@Spunkybot.co.uk";
	private String smtpHost_ = "smtp.gmail.com";
	private String smtpUser_ = "spunky@gmail.com";
	private String smtpPassword_ = "Helloworld";

	private String[] channels_ = { "#xetk" };
	private String[] admins_ = { "xetk" };
	private String[] startup_ = { "PRIVMSG zippy identify helloworld" };

	/**
	 * Get our instance of the details class back for us to use.
	 * 
	 * @return we get the original instance of the class back
	 * @throws IOException
	 *             this is if we have to load the JSON object
	 */
	public static Details getInstance() {
		try {
			if (new File(CONFIG_FILE_).exists()) {
				details_ = (Details) JSON.load(CONFIG_FILE_, Details.class);
			} else {
				details_ = new Details();
				JSON.save(CONFIG_FILE_, details_);
				// We don't want the program to load with a unpopulated json
				// file.
				System.out.println("Populate Details.json"
						+ " before reexecuting the application!");
				System.exit(1);
			}
		} catch (IOException e) {
			// If there was an problem while processing the JSON file.
			e.printStackTrace();
			System.exit(1);
		}
		return details_;
	}

	/**
	 * If we get a instance back from JSON we want to set this instance of the
	 * class to this original instance
	 * 
	 * @param instance
	 *            take in the instance we want to set the class to.
	 */
	public static void setInstance(Details instance) {
		details_ = instance;
	}

	/**
	 * Check if a user is an admin without reusing the same code over and over.
	 * 
	 * @param name
	 *            The user we want to check if is a admin.
	 * @return true or false to if the user is a admin.
	 */
	public boolean isAdmin(String name) {
		for (int i = 0; i < admins_.length; i++)
			if (name.equalsIgnoreCase(admins_[i]))
				return true;
		return false;
	}

	/**
	 * Getters
	 */
	public static Details getDetails() {
		return details_;
	}

	public String getServer() {
		return server_;
	}

	public int getPort() {
		return port_;
	}

	public String[] getChannels() {
		return channels_;
	}

	public String[] getAdmins() {
		return admins_;
	}

	public String[] getStartup() {
		return startup_;
	}

	public String getNickName() {
		return botNickName_;
	}

	public String getSmtpEmail() {
		return smtpEmail_;
	}

	public String getSmtpHost() {
		return smtpHost_;
	}

	public String getSmtpUser() {
		return smtpUser_;
	}

	public String getSmtpPassword() {
		return smtpPassword_;
	}

	// Setters
	public void setNickName(String nickname) {
		this.botNickName_ = nickname;
	}
}
