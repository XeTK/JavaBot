package addons;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StatDay
{
	private StatHour[] hours = new StatHour[24];
	
	public void incMsgSent(String user)
	{
		getHour().incMsgSent(user);
	}
	
	public void incJoins()
	{
		getHour().incJoins();
	}
	
	public void incQuits()
	{
		getHour().incQuits();
	}
	
	public StatHour[] getHours()
	{
		return hours;
	}

	public StatHour getHour()
	{
		int i = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
		if (hours[i] == null)
			hours[i] = new StatHour();
		return hours[i];
	}
}
