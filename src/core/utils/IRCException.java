package core.utils;

import java.io.IOException;
import java.util.Arrays;

/**
 * Special exception class for when there is a issue within IRC related classes
 * 
 * @author Tom Rosier (XeTK)
 */
public class IRCException extends Exception {
	public IRCException() {
		super();
	}

	public IRCException(String message) {
		super(message);
		sendMsg(this);
	}

	public IRCException(Exception ex) {
		super(ex);
		sendMsg(ex);
	}
	
	private void sendMsg(Exception ex)
	{
		try {
			IRC irc = IRC.getInstance();
			// This sends any exceptions to the admins of the bot so they are 
			//aware if there is a problem.
	
			String[] admins = Details.getInstance().getAdmins();
			// Send the exception to all the admins that are registered within 
			// the details file.
			
			String stack = Arrays.toString(super.getStackTrace());
			String msg = super.toString() + ", " + stack;
			
			for (int i = 0; i < admins.length; i++) {
				irc.sendPrivmsg(admins[i], msg);
			}
			ex.printStackTrace();
		} catch (IRCException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
