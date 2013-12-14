package plugin.stats.channel;

/**
 * This is the savable class containing the options for the Statistics plugin,
 * this is saved out to a JSON file where the user can select if the channel
 * receives hourly Statistics updates or only daily.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Options {
	// Global variables with a default value of false to disable the options.
	private boolean dayStats_  = false;
	private boolean hourStats_ = false;

	// Getters
	public boolean isDayStats() {
		return dayStats_;
	}

	public boolean isHourStats() {
		return hourStats_;
	}

}
