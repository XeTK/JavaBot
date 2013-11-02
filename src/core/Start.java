package core;

/**
 * This is the Main entry point for the program and is thus its start point.
 * 
 * @author Tom Rosier(XeTK)
 */
public class Start {
	/**
	 * We need a Static context for it to be recognized by the JVM, which will
	 * then launch into the code.
	 * 
	 * @param args
	 *            This is any extra information passed in at runtime by the
	 *            user.
	 * @throws Exception
	 *             if we have an early exception it is thrown up to the JVM.
	 */
	private static Core core_;
	
	public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(
            	new Thread(){
    				            @Override
    				            public void run(){
    				                System.out.println("JavaBot is shutting down.");
    				                core_.killBot();
    				                this.interrupt();
    				            }
    				        });
        core_ = new Core();
	}

}
