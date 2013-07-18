package addons.stats;
/**
 * This is the savable class containing the options for the Statistics plugin,
 * this is saved out to a JSON file where the user can select if the channel 
 * receives hourly Statistics updates or only daily.
 * @author Tom Rosier(XeTK)
 */
public class StatOption
{
	// Global variables with a default value of fause to disable the options.
	private boolean day_Stats = false, hour_Stats = false;

	// Getters
	public boolean isDay_Stats()
	{
		return day_Stats;
	}

	public boolean isHour_Stats()
	{
		return hour_Stats;
	}
	
	
}
