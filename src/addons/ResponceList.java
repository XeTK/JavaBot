package addons;

import java.util.ArrayList;

public class ResponceList 
{
	private ArrayList<Responce> responces = new ArrayList<Responce>();
	
	public void addResponce(Responce replies)
	{
		responces.add(replies);
	}
	
	public ArrayList<Responce> getResponces()
	{
		return responces;
	}
}
