package plugin.reputation;

import java.util.ArrayList;

/**
 * This holds the list of all the reputations that are tied to all the items
 * within the IRC Bot, it also deals with getting and adding reputation.
 * @author Tom Rosier (XeTK)
 */
public class RepList
{
	// Keep a list of the reputation awarded to different items.
	private ArrayList<Reputation> repList = new ArrayList<Reputation>();
	
	/**
	 * This gets and returns the reputation of a specific item, and if the item
	 * does not exists it creates it and adds it to the list.
	 * @param item this is the real life object we are giving reputation to.
	 * @return this is the object storing the reputation and real life object.
	 */
	public Reputation getRep(String item)
	{
		for (int i = 0; i < repList.size();i++)
			if (repList.get(i).getItem().equalsIgnoreCase(item))
				return repList.get(i);
		
		// Couldn't find item so we create it.
		
		Reputation tRep = new Reputation(item, 0);
		repList.add(tRep);
		return tRep;
	}
}
