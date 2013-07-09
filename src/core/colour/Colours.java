package core.colour;

public enum Colours 
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
	
	private Colours(String ident)
	{
		this.ident = ident;
	}
	
	public String getIdent()
	{
		return ident;
	}
}
