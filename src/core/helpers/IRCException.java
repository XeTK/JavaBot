package core.helpers;

import java.io.IOException;
import java.util.Arrays;

import core.utils.Details;
import core.utils.IRC;

/**
 * Special exception class for when there is a issue within IRC related classes
 * @author Tom Rosier (XeTK)
 */
public class IRCException extends Exception
{
	public IRCException()
	{
		super();
	}
	public IRCException(String message)
	{
		super(message);
		
		IRC irc = IRC.getInstance();
		// This sends any exceptions to the admins of the bot so they are aware if there is a problem.
		
		String[] admins = Details.getInstance().getAdmins();
		// Send the exception to all the admins that are registered within the details file.
		
		for (int i = 0; i < admins.length;i++)
		{
			try 
			{
				irc.sendPrivmsg(admins[i],
						super.toString() + ", " + Arrays.toString(super.getStackTrace()));
			} 
			catch (IRCException | IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public IRCException(Exception ex)
	{
		super(ex);
		
		IRC irc = IRC.getInstance();
		// This sends any exceptions to the admins of the bot so they are aware if there is a problem.
		
		String[] admins = Details.getInstance().getAdmins();
		// Send the exception to all the admins that are registered within the details file.
		
		for (int i = 0; i < admins.length;i++)
		{
			try 
			{
				irc.sendPrivmsg(admins[i],
						ex.toString() + ", " + Arrays.toString(ex.getStackTrace()));
			} 
			catch (IRCException | IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
