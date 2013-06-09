package addons;

import java.util.ArrayList;

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
