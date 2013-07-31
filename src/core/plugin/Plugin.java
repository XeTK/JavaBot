package core.plugin;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.plugin;

/**
 * This is the abstract class that all the plugins extend from.
 * 
 * @author Tom Rosier (XeTK)
 */
@plugin
public abstract class Plugin {
	public String name() {
		return this.getClass().getSimpleName();
	}

	public void onCreate(Channel inChannel) throws Exception {}

	public void onTime() throws Exception {}

	/**
	 * Called in response to incoming PRIVMSG
	 */
	public void onMessage(Message inMessage) throws Exception {}

	public void onJoin(Join inJoin) throws Exception {}

	public void onQuit(Quit inQuit) throws Exception {}

	public void onKick(Kick inKick) throws Exception {}

	public void rawInput(String inStr) throws Exception {}

	/**
	 * The string displayed by .help message
	 */
	public abstract String getHelpString();

}
