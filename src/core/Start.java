package core;
/**
 * This is the Main entry point for the program and is thus its start point.
 * @author Tom Rosier(XeTK)
 */
public class Start 
{
	/**
	 * We need a Static context for it to be recognized by the JVM, which will
	 * then launch into the code.
	 * @param args This is any extra information passed in at runtime by the user.
	 * @throws Exception if we have an early exception it is thrown up to the JVM.
	 */
	public static void main(String[] args) throws Exception 
	{
		// We don't need to keep track of the object we just let it run.
		new Core();
	}

}
