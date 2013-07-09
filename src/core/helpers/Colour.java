package core.helpers;

public enum Colour 
{
	WHITE("00"),
	BLACK("01"),
	BLUE_DARK("02"),
	GREEN_DARK("03"),
	RED("04"),
	BROWN("05"),
	PURPLE("06"),
	OLIVE("07"),
	YELLOW("08"),
	GREEN("09"),
	TEAL("10"),
	CYAN("11"),
	BLUE("12"),
	MAGENTA("13"),
	GRAY_DARK("14"),
	GRAY_LIGHT("15");
	
	private String ident;
	
	private Colour(String ident)
	{
		this.ident = ident;
	}
	
	public static String colour(String in_Text, Colour forground, Colour background)
	{
		String patch = "\u0003" + forground.ident;
		
		if (background != null)
			patch += "," + background.ident;
		
		return (patch + in_Text + "\u000f");
	}
	
	public static String colour(String in_Text, Colour forground)
	{
		return colour(in_Text,forground,null);
	}
}
