package core.utils;

import java.util.List;

import core.plugin.Plugin;

/**
 * Thread to run the timed events.
 *
 * @author Tom Rosier(XeTK)
 */
public class TimeThread extends Thread {
	private static int SLEEP_MILLIS = 1000;

	private List<Plugin> plugins;

	/**
	 * Deploy timed events on a separate thread, keep looping until the
	 * application terminates
	 */
	public void run() {
		while (true) {
			try {
				for (int i = 0; i < plugins.size(); i++) {
					plugins.get(i).onTime();
				}
				super.sleep(SLEEP_MILLIS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This takes in the plugins for a specific channel and adds them to its own
	 * separate time thread
	 *
	 * @param plugins the list of plugins for a specific channel.
	 */
	public TimeThread(List<Plugin> plugins) {
		this.plugins = plugins;
	}
}
