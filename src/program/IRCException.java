package program;
/**
 * Special exception class for when there is a issue within IRC related classes
 * @author Tom Rosier (XeTK)
 *
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
	}
}
