package plugin;

import java.io.IOException;

import program.IRCException;

/**
 * This is the interface that all the plugins inherit from it makes sure that we have a set standard for all the classes to work from
 * @author Tom Rosier (XeTK)
 *
 */

public interface PluginTemp
{
	public void onCreate(String in_str) throws IRCException, IOException;
	public void onTime(String in_str) throws IRCException, IOException;
	public void onMessage(String in_str) throws IRCException, IOException;
	public void onJoin(String in_str) throws IRCException, IOException;
	public void onQuit(String in_str) throws IRCException, IOException;

}
