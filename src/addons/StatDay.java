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
	
	public void incKicks()
	{
		getHour().incKicks();
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
	
	public int msgsSent()
	{
		int t = 0;
		for (int i = 0; i < hours.length; i++)
			if (hours[i] != null)
				t += hours[i].getMsgSent();
		return t;
	}
	
	public int joins()
	{
		int t = 0;
		for (int i = 0; i < hours.length; i++)
			if (hours[i] != null)
				t += hours[i].getJoins();
		return t;
	}
	
	public int quits()
	{
		int t = 0;
		for (int i = 0; i < hours.length; i++)
			if (hours[i] != null)
				t += hours[i].getQuits();
		return t;
	}
	
	public int kicks()
	{
		int t = 0;
		for (int i = 0; i < hours.length; i++)
			if (hours[i] != null)
				t += hours[i].getKicks();
		return t;
	}
}
