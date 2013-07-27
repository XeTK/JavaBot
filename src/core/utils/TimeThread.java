package core.utils;

import java.util.ArrayList;

import core.plugin.Plugin;

/**
 * Thread to run the timed events as we can't just leave it in the main loop as
 * we wont loop till we have received input from the Server/Clients.
 * 
 * @author Tom Rosier(XeTK)
 */
public class TimeThread extends Thread {
	// Keep a link to the plugins stored within Start.java
	private ArrayList<Plugin> plugins;

	/**
	 * Deploy are timed events on are separate thread. Keep looping till the
	 * application terminates, we sleep for a second before we loop back round
	 * again to run all the timed events again
	 */
	public void run() {
		while (true) {
			try {
				for (int i = 0; i < plugins.size(); i++)
					plugins.get(i).onTime();

				super.sleep(1000);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * This takes in the plugins for a specific channel and adds them to its own
	 * separate time thread, this is to help with having channel isolation.
	 * @param plugins
	 *            this is the list of plugins tied to a specific channel.
	 */
	public TimeThread(ArrayList<Plugin> plugins) {
		this.plugins = plugins;
	}
}
