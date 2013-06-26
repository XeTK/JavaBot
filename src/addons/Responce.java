package addons;

import java.util.ArrayList;

public class Responce 
{
	private String regex = "";
	private ArrayList<String> responces = new ArrayList<String>();
	
	public String getRegex() 
	{
		return regex;
	}
	
	public String[] getResponces() 
	{
		return responces.toArray(new String[0]);
	}
	
	public void setRegex(String regex) 
	{
		this.regex = regex;
	}
	
	public void addResponce(String responce) 
	{
		this.responces.add(responce);
	}
	
}
