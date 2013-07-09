package addons.reputation;

/**
 * This is a data class for the reputation of items within the IRC Bot.
 * @author Tom Rosier (XeTK)
 */
public class Reputation
{
	private int rep;
	private String item;
	
	/**
	 * Define are object and starting reputation on creation of the object.
	 * @param item this is the real life object we want to model
	 * @param startRep this is are start number of the reputation.
	 */
	public Reputation(String item, int startRep)
	{
		this.rep = startRep;
		this.item = item;
	}
	
	/**
	 * Directly edit the reputation by a +/- and an amount.
	 * @param ammount the amount that should be decremented or incremented.
	 */
	public void modRep(int ammount)
	{
		rep += ammount;
	}
	
	//Getters for the data
	public int getRep()
	{
		return rep;
	}
	public String getItem()
	{
		return item;
	}
	
}
