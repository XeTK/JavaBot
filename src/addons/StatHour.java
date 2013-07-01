package addons;
import java.util.ArrayList;

/**
 * This holds the stats for the hour in time. This is encapsulated by StatDay.
 * @author Tom Rosier (XeTK)
 */
public class StatHour
{
	/* 
	 * Keep a list of the users from the last hour and how many messages they 
	 * have sent, this is useful for leader board stats.
	 */
	private ArrayList<StatUser> users = new ArrayList<StatUser>();
	
	// Hold the other stats for the hour.
	private int joins = 0, quits = 0, kicks = 0;
	
	/**
	 * Quick method to increment the number of users that have joined.
	 */
	public void incJoins()
	{
		joins++;
	}
	
	/**
	 * Quick method to increment the number of users that have left the channel.
	 */
	public void incQuits()
	{
		quits++;
	}
	
	/**
	 * Quick method to increment the number of users that have been kicked.
	 */
	public void incKicks()
	{
		kicks++;
	}
	
	/**
	 * This increment the number of messages a user has sent in the last hour.
	 * @param user this is the user that needs to there total incrementing.
	 */
	public void incMsgSent(String user)
	{
		// Keep a flag to see if the user has been found.
		boolean exists = false;
		// Loop through all the users that we already have.
		for (int i = 0; i < users.size();i++)
		{
			// If the user we are incrementing over equals the user we want.
			if (users.get(i).getUsername().equals(user))
			{
				// Increment the messages that they have sent.
				users.get(i).incMsgSent();
				// Set are flag to know that we have found the user.
				exists = true;
				// Found the user no need to loop through the rest of the loop.
				break;
			}
		}
		// If the user has not been found then we create a new instance of a user
		if (!exists)
			users.add(new StatUser(user));
	}
	
	// Getters
	public int getJoins()
	{
		return joins;
	}
	public int getQuits()
	{
		return quits;
	}
	public int getKicks()
	{
		return kicks;
	}
	
	// This totals the number of messages sent in the last hour.
	public int getMsgSent()
	{
		int msgs = 0;
		for (int i = 0; i < users.size();i++)
			msgs += users.get(i).getMsgSent();
		return msgs;
	}
}
