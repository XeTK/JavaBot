package program;

/**
 * This holds all the data needed to carry out the connection to the IRC server,
 * and any other information that is needed
 * @author Tom Rosier(XeTK)
 */
public class Details
{
	private static Details details;
	
	private String server = "127.0.0.1", dbServer = "127.0.0.1", dbPort = "3306", 
			dbTable = "jabot", dbUser = "root", dbpasswd = "password";
	
	private int port = 6667;
	
	private String[] channels = {"#xetk"}, admins = {"XeTK"}, 
			startup = {"User JaBotTest Java Bot JaBot :Java Bot", 
			"Nick JabotTest","PRIVMSG zippy identify helloworld"};
	
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

	public String getDbServer()
	{
		return dbServer;
	}

	public String getDbPort()
	{
		return dbPort;
	}

	public String getDbTable()
	{
		return dbTable;
	}

	public String getDbUser()
	{
		return dbUser;
	}

	public String getDbpasswd()
	{
		return dbpasswd;
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
}
