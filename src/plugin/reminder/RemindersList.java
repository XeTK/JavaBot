package plugin.reminder;

import java.util.ArrayList;
import java.util.Date;

/**
 * This is a holder for all the reminder events as they need to be encapsulated
 * for the use with JSON, it also deals with adding and getting reminders.
 * 
 * @author Tom Rosier(XeTK)
 */
public class RemindersList {
	// Keep a global list of the reminders for the system.
	private ArrayList<Reminders> reminders_ = new ArrayList<Reminders>();

	/**
	 * Simple adds a new reminder to the list of reminders.
	 * 
	 * @param message
	 *            is the message that we want to hold with are reminder.
	 * @param timeOfEvent
	 *            this is the time that the event is scheduled for.
	 */
	public void addReminder(String message, Date timeOfEvent) {
		reminders_.add(new Reminders(message, timeOfEvent));
	}

	/**
	 * This gets all the events at the current time, it also removes the events
	 * from the list so that they do not repeat.
	 * 
	 * @param timeOfEvent
	 *            this is the time we want to get the reminders for
	 * @return's a list of reminders that then can be printed out to the server
	 */
	public Reminders[] getReminders(Date timeOfEvent) {
		// List of reminders to be be returned
		ArrayList<Reminders> eventsAtThisTime = new ArrayList<Reminders>();

		// Loop through all the elements within the list
		for (int i = 0; i < reminders_.size();) {
			/*
			 * If the time of the event has passed or is current then we add it
			 * to the list to be returned, we also delete it from are reminders
			 * list, if we delete a element we don't want to increment the loop
			 * counter as it will skip the next element of the list, so we check
			 * the current index again and if that isn't a reminder for the
			 * current time then we increment the loop counter.
			 */
			if (reminders_.get(i).getTimeOfEvent().compareTo(timeOfEvent) <= 0) {
				eventsAtThisTime.add(reminders_.get(i));
				reminders_.remove(i);
			} else
				i++;
		}

		/*
		 * Finally convert are ArrayList to an array so it can't be manipulated
		 * outside this class.
		 */
		return eventsAtThisTime.toArray(new Reminders[0]);
	}
}
