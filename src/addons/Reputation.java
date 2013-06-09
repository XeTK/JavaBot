package addons;

public class Reputation
{
	private int rep;
	private String item;
	public Reputation(String item, int startRep)
	{
		this.rep = startRep;
		this.item = item;
	}
	public void modRep(int ammount)
	{
		rep += ammount;
	}
	public int getRep()
	{
		return rep;
	}
	public String getItem()
	{
		return item;
	}
	
}
