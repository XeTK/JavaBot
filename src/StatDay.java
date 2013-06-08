
public class StatDay
{
	private StatHour[] hours = new StatHour[24];
	
	public void incMsgSent(String user)
	{
		int i = 0;
		hours[i].incMsgSent(user);
	}
	
	public void incJoins()
	{
		int i = 0;
		hours[i].incJoins();
	}
	
	public void incQuits()
	{
		int i = 0;
		hours[i].incQuits();
	}
	
	public StatHour[] getHours()
	{
		return hours;
	}
	
}
