import java.util.ArrayList;


public class StatHour
{
	private ArrayList<StatUser> users = new ArrayList<StatUser>();
	private int joins, quits;
	
	public void incJoins()
	{
		joins++;
	}
	public void incQuits()
	{
		quits++;
	}
	public void incMsgSent(String user)
	{
		for (int i = 0; i < users.size();i++)
			if (users.get(i).getUsername().equals(user))
				users.get(i).incMsgSent();
	}
	
	public int getJoins()
	{
		return joins;
	}
	public int getQuits()
	{
		return quits;
	}
	public int getMsgSent()
	{
		int msgs = 0;
		for (int i = 0; i < users.size();i++)
			msgs += users.get(i).getMsgSent();
		return msgs;
	}
	
}
