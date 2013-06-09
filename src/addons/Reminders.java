package addons;

import java.util.Date;

public class Reminders
{
	private Date timeOfEvent;
	private String reminder;
	
	public Reminders(String reminder, Date timeOfEvent)
	{
		this.reminder = reminder;
		this.timeOfEvent = timeOfEvent;
	}

	public Date getTimeOfEvent()
	{
		return timeOfEvent;
	}

	public String getReminder()
	{
		return reminder;
	}
	
	
}
