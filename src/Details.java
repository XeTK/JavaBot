
public class Details
{
	private String nickName = "JaBot", host = "JaBot", server = "127.0.0.1", name = "Java Bot";
	private int port = 6667;
	private String[] channels = {"#42"};
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
	
	
}
