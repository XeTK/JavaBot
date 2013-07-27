package core.plugin;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;

/**
 * This is the abstract class that all the plugins extend from.
 * @author Tom Rosier (XeTK)
 */
public abstract class Plugin
{
	public String name()
	{
		return this.getClass().getSimpleName();
	}
	
	public void onCreate(Channel in_channel) throws Exception{}
	public void onTime() throws Exception {}

	/**
	 * Called in response to incoming PRIVMSG
	 */
	public void onMessage(Message in_message) throws Exception {}

	public void onJoin(Join in_join) throws Exception {}
	public void onQuit(Quit in_quit) throws Exception {}
	public void onKick(Kick in_kick) throws Exception {}
	public void rawInput(String in_str) throws Exception{}

	/**
	 * The string displayed by .help message
	 */
	public abstract String getHelpString();

}
