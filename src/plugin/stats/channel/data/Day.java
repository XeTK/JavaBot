package plugin.stats.channel.data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This encapsulation of a Days worth of Stat hour's, it also tallies and
 * updates the various data for the day and current hour.
 * 
 * @author Tom Rosier (XeTK)
 */
public class Day {
	// Hold a days worth of hours
	private Hour[] hours_ = new Hour[24];
	private boolean displayedDayStats_ = false;

	/**
	 * Increment the message sent for the current hour
	 * 
	 * @param user
	 *            this is the user that incremented the counter
	 */
	public void incMsgSent(String user) {
		// Get the current hour object then increment the counter for messages
		// sent
		getHour().incMsgSent(user);
	}

	/**
	 * When a new user joins this counter for the current hour is incremented
	 */
	public void incJoins() {
		getHour().incJoins();
	}

	/**
	 * Same as above just for quits
	 */
	public void incQuits() {
		getHour().incQuits();
	}

	/**
	 * Same again just for kicked users.
	 */
	public void incKicks() {
		getHour().incKicks();
	}

	// Getter for the whole day
	public Hour[] getHours() {
		return hours_;
	}

	/**
	 * This method returns the current hour object for the hour that is
	 * currently taking place.
	 * 
	 * @return's the current hour object to be manipulated.
	 */
	public Hour getHour() {
		int i = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
		if (hours_[i] == null)
			hours_[i] = new Hour();
		return hours_[i];
	}

	/**
	 * Totals the messages sent for the day then returns a number.
	 * 
	 * @return's the number of messages sent today.
	 */
	public int msgsSent() {
		int t = 0;
		for (int i = 0; i < hours_.length; i++)
			if (hours_[i] != null)
				t += hours_[i].getMsgSent();
		return t;
	}

	/**
	 * Returns number of users joined throughout the day.
	 * 
	 * @return's the number of users joined throughout the day.
	 */
	public int joins() {
		int t = 0;
		for (int i = 0; i < hours_.length; i++)
			if (hours_[i] != null)
				t += hours_[i].getJoins();
		return t;
	}

	/**
	 * Totals the number of users quit throughout the day and returns a int.
	 * 
	 * @return's an int with the number of uses quit.
	 */
	public int quits() {
		int t = 0;
		for (int i = 0; i < hours_.length; i++)
			if (hours_[i] != null)
				t += hours_[i].getQuits();
		return t;
	}

	/**
	 * This totals the number of kicked users throughout the day.
	 * 
	 * @return's an int of the number of users kicked throughtout the day.
	 */
	public int kicks() {
		int t = 0;
		for (int i = 0; i < hours_.length; i++)
			if (hours_[i] != null)
				t += hours_[i].getKicks();
		return t;
	}
	
	public boolean hourIsViewed(){
		return getHour().isDisplayedStats();
	}
	
	public void hasViewed(){
		getHour().setDisplayedStats(true);
	}
	
	public boolean isDisplayedDayStats() {
		return displayedDayStats_;
	}

	public void setDisplayedDayStats() {
		this.displayedDayStats_ = true;
	}
}
