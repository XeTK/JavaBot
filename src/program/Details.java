package program;

public class Details
{
	private String nickName = "JaBot", host = "JaBot", server = "127.0.0.1", name = "Java Bot";
	private int port = 6667;
	private String[] channels = {"#69"}, admins = {"XeTK"}, startup = {"PRIVMSG zippy identify helloworld"};
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
	
	
}
