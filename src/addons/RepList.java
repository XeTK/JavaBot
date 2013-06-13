package addons;

import java.util.ArrayList;

/**
 * This holds the list of all the reputations that are tied to all the items
 * within the IRC Bot, it also deals with getting and adding reputation.
 * @author Tom Rosier (XeTK)
 */
public class RepList
{
	private ArrayList<Reputation> repList = new ArrayList<Reputation>();
	public Reputation getRep(String item)
	{
		for (int i = 0; i < repList.size();i++)
			if (repList.get(i).getItem().equals(item))
				return repList.get(i);
		
		Reputation tRep = new Reputation(item, 0);
		repList.add(tRep);
		return tRep;
	}
}
