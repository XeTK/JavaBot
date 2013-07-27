package plugin.reminder;

import java.util.Date;

/**
 * Data class for Reminders Holds the message and the time of the event.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Reminders {
	// Globals holding the data for the reminder
	private Date timeOfEvent_ = new Date();
	private String reminder_ = new String();

	/**
	 * Take in the information for a reminder and set on creation
	 * 
	 * @param reminder
	 *            is the message that is given when reminder is triggered
	 * @param timeOfEvent
	 *            this is the time of the reminder should be given
	 */
	public Reminders(String reminder, Date timeOfEvent) {
		this.reminder_ = reminder;
		this.timeOfEvent_ = timeOfEvent;
	}

	// Getters
	public Date getTimeOfEvent() {
		return timeOfEvent_;
	}

	public String getReminder() {
		return reminder_;
	}
}
