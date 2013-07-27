package core.utils;

/**
 * This enum exists to give IRC messages colour before they are sent. It also
 * adds the additional formating to the line to stop the colour 'leaking'.
 * 
 * @author Tom Rosier(XeTK)
 */
public enum Colour {
	// All the IRC colours that exist.
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

	// Define the second value died to the names.
	private String ident_;

	// Create the getter for the colour.
	private Colour(String ident) {
		this.ident_ = ident;
	}

	/**
	 * This method returns a coloured string that can be sent via IRC.
	 * 
	 * @param inText
	 *            This is the text that we want to colour.
	 * @param foreground
	 *            this is the foreground colour we want the text to be.
	 * @param background
	 *            this is the background colour we want the text to be.
	 * @return
	 */
	public static String colour(String inText, Colour foreground,
			Colour background) {
		// Set up the string with the foreground colour along with the extra
		// data needed.
		String patch = "\u0003" + foreground.ident_;

		// If we are having a background colour then we patch that information
		// on to.
		if (background != null)
			patch += "," + background.ident_;

		// Finally we return the string with the colouring and the reset code at
		// the end.
		return (patch + inText + "\u000f");
	}

	// This is the same constructor without the background colour which makes
	// choosing easier.
	public static String colour(String inText, Colour forground) {
		return colour(inText, forground, null);
	}
}
