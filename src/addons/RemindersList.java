package addons;

import java.util.ArrayList;
import java.util.Date;

public class RemindersList
{
	private ArrayList<Reminders> reminders = new ArrayList<Reminders>();
	public void addReminder(String message,Date timeOfEvent)
	{
		reminders.add(new Reminders(message,timeOfEvent));
	}
	public Reminders[] getReminders(Date timeOfEvent)
	{
		ArrayList<Reminders> eventsAtThisTime = new ArrayList<Reminders>();
		for (int i = 0; i < reminders.size();)
		{
			if (reminders.get(i).getTimeOfEvent().compareTo(timeOfEvent) <= 0)
			{
				eventsAtThisTime.add(reminders.get(i));
				reminders.remove(i);
			}
			else
				i++;
		}
		return eventsAtThisTime.toArray(new Reminders[0]);
	}
}
