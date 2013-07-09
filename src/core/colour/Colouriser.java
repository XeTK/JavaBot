package core.colour;

public class Colouriser 
{
	public static String colour(String in_Text, Colours forground, Colours background)
	{
		String patch = "\u0003" + forground.getIdent();
		if (background != null)
			patch += "," + background.getIdent();
		return (patch + in_Text + "\u000f");
	}
	public static String Colour(String in_Text, Colours forground)
	{
		return colour(in_Text,forground,null);
	}
}
