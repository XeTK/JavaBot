package program;

/**
 * This holds all the data needed to carry out the connection to the IRC server,
 * and any other information that is needed
 * @author Tom Rosier(XeTK)
 */
public class Details
{
	private static Details details;
	
	private String server = "127.0.0.1";;
	
	private int port = 6667;
	
	private boolean dayStats = true , hourStats = false;
	
	private String botNickName = "JavaBot";
	
	private String[] channels = {"#xetk"}, admins = {"xetk"}, 
			startup = {"PRIVMSG zippy identify helloworld"};
	
	private String encryptionKey = "helloworldthomas";
	
	/**
	 * Get our instance of the details class back for us to use.
	 * @return we get the original instance of the class back
	 */
	public static Details getIntance()
	{
		if (details == null)
			details = new Details();
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

	public boolean isDayStats()
	{
		return dayStats;
	}

	public boolean isHourStats()
	{
		return hourStats;
	}
	public String getNickName()
	{
		return botNickName;
	}
	public byte[] getEncryptionKey()
	{
		return encryptionKey.getBytes();
	}
	//Setters
	public void setNickName(String nickname)
	{
		this.botNickName = nickname;
	}
}
