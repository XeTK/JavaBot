package addons.responce;

import java.util.ArrayList;
/**
 * This a farm for all the responses,
 * it encapsulates them so they can be later saved as a JSON object.
 * @author Tom Rosier(XeTK)
 */
public class ResponceList 
{
	// Keep a list of responses
	private ArrayList<Responce> responces = new ArrayList<Responce>();
	
	// Add a Response
	public void addResponce(Responce replies)
	{
		responces.add(replies);
	}
	
	// Return the list of Responses
	public ArrayList<Responce> getResponces()
	{
		return responces;
	}
}
