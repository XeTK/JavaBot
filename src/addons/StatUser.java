package addons;

public class StatUser
{
	private String username;
	private int msgSent = 1;
	
	public StatUser(String username)
	{
		this.username = username;
	}
	
	public void incMsgSent()
	{
		msgSent++;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public int getMsgSent()
	{
		return msgSent;
	}
	
}
