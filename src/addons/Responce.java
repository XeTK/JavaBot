package addons;

import java.util.ArrayList;

/**
 * This is data class to handle string responses 
 * it holds the original Regex and the responses
 * that go with that Regex.
 * @author Tom Rosier(XeTK)
 */
public class Responce 
{
	// This is the data that is held for a responce to take place
	private String regex = "";
	private ArrayList<String> responces = new ArrayList<String>();
	
	// Getters
	public String getRegex() 
	{
		return regex;
	}
	
	public String[] getResponces() 
	{
		return responces.toArray(new String[0]);
	}
	
	// Setters
	public void setRegex(String regex) 
	{
		this.regex = regex;
	}
	
	public void addResponce(String responce) 
	{
		this.responces.add(responce);
	}
	
}
