import java.util.ArrayList;


public class StatHour
{
	private ArrayList<StatUser> users = new ArrayList<StatUser>();
	private int joins = 0, quits = 0;
	
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
		boolean exists = false;
		for (int i = 0; i < users.size();i++)
		{
			if (users.get(i).getUsername().equals(user))
			{
				users.get(i).incMsgSent();
				exists = true;
				break;
			}
		}
		if (!exists)
			users.add(new StatUser(user));
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
