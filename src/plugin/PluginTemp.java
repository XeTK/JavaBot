package plugin;

import event.*;

/**
 * This is the interface that all the plugins inherit from it makes sure 
 * that we have a set standard for all the classes to work from.
 * @author Tom Rosier (XeTK)
 */
public interface PluginTemp
{
	public String name();
	public void onCreate() throws Exception;
	public void onTime() throws Exception;
	public void onMessage(Message in_message) throws Exception;
	public void onJoin(Join in_join) throws Exception;
	public void onQuit(Quit in_quit) throws Exception;
	public void onKick(Kick in_kick) throws Exception;
}
