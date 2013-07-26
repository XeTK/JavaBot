package plugin.reminder;

import java.util.Date;
/**
 * Data class for Reminders
 * Holds the message and the time of the event.
 * @author Tom Rosier(XeTK)
 */
public class Reminders
{
	// Globals holding the data for the reminder
	private Date timeOfEvent;
	private String reminder;
	
	/**
	 * Take in the information for a reminder and set on creation
	 * @param reminder is the message that is given when reminder is triggered
	 * @param timeOfEvent this is the time of the reminder should be given
	 */
	public Reminders(String reminder, Date timeOfEvent)
	{
		this.reminder = reminder;
		this.timeOfEvent = timeOfEvent;
	}

	// Getters
	public Date getTimeOfEvent()
	{
		return timeOfEvent;
	}

	public String getReminder()
	{
		return reminder;
	}
	
	
}
