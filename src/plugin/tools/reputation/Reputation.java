package plugin.tools.reputation;

/**
 * This is a data class for the reputation of items within the IRC Bot.
 * 
 * @author Tom Rosier (XeTK)
 */
public class Reputation {
	private int    rep_  = 0;
	private String item_ = new String();

	/**
	 * Define are object and starting reputation on creation of the object.
	 * 
	 * @param item this is the real life object we want to model
	 * 
	 * @param startRep this is are start number of the reputation.
	 */
	public Reputation(String item, int startRep) {
		this.rep_ = startRep;
		this.item_ = item;
	}

	/**
	 * Directly edit the reputation by a +/- and an amount.
	 * 
	 * @param ammount the amount that should be decremented or incremented.
	 */
	public void modRep(int ammount) {
		rep_ += ammount;
	}

	// Getters for the data
	public int getRep() {
		return rep_;
	}

	public String getItem() {
		return item_;
	}

}
