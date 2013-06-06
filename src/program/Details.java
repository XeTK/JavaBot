package program;

/**
 * This holds all the data needed to carry out the connection to the IRC server, and any other information that is needed
 * @author Tom Rosier(XeTK)
 *
 */

public class Details
{
	private static Details details;
	private String nickName = "JaBot", host = "JaBot", server = "127.0.0.1", name = "Java Bot", dbServer = "127.0.0.1", dbPort = "3306", dbTable = "jabot", dbUser = "root", dbpasswd = "password";
	private int port = 6667;
	private String[] channels = {"#69"}, admins = {"XeTK"}, startup = {"PRIVMSG zippy identify helloworld"};
	
	/**
	 * Get our instance of the details class back for us to use.
	 * @return we get the origernal instance of the class back
	 */
	public static Details getIntance()
	{
		if (details == null)
			details = new Details();
		return details;
	}
	
	/**
	 * If we get a instance back from JSON we want to set this instance of the class to this origernal instance
	 * @param instance take in the instance we want to set the class to.
	 */
	public static void setInstance(Details instance)
	{
		details = instance;
	}
	
	/**
	 * Getters & setters
	 */
	public String getNickName()
	{
		return nickName;
	}
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
	public String getHost()
	{
		return host;
	}
	public void setHost(String host)
	{
		this.host = host;
	}
	public String getServer()
	{
		return server;
	}
	public void setServer(String server)
	{
		this.server = server;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public String[] getChannels()
	{
		return channels;
	}
	public void setChannels(String[] channels)
	{
		this.channels = channels;
	}
	public String[] getAdmins()
	{
		return admins;
	}
	public void setAdmins(String[] admins)
	{
		this.admins = admins;
	}
	public String[] getStartup()
	{
		return startup;
	}
	public void setStartup(String[] startup)
	{
		this.startup = startup;
	}
	public String getDbServer()
	{
		return dbServer;
	}
	public void setDbServer(String dbServer)
	{
		this.dbServer = dbServer;
	}
	public String getDbPort()
	{
		return dbPort;
	}
	public void setDbPort(String dbPort)
	{
		this.dbPort = dbPort;
	}
	public String getDbTable()
	{
		return dbTable;
	}
	public void setDbTable(String dbTable)
	{
		this.dbTable = dbTable;
	}
	public String getDbUser()
	{
		return dbUser;
	}
	public void setDbUser(String dbUser)
	{
		this.dbUser = dbUser;
	}
	public String getDbpasswd()
	{
		return dbpasswd;
	}
	public void setDbpasswd(String dbpasswd)
	{
		this.dbpasswd = dbpasswd;
	}
	
	
}
