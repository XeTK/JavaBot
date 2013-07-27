package core;

import java.util.ArrayList;

import core.event.Message;
import core.plugin.Plugin;
import core.plugin.PluginCore;
import core.utils.IRCException;

public class PrivMsg {
	private String userName_;

	private ArrayList<Plugin> plugins_;

	public PrivMsg(String userName) throws Exception {
		this.userName_ = userName;

		// Assign this channel with a fresh list of plugins that we can now
		// manipulate.
		this.plugins_ = PluginCore.loadPlugins();
	}

	/**
	 * Handle the onMessage actions for each plugins under this method.
	 * 
	 * @param inMessage
	 *            this is the message object passed in from the core of the
	 *            program.
	 */
	public void onMessage(Message inMessage) {
		if (inMessage.isPrivMsg()) {
			// Double check that the message is actually for this class.
			if (inMessage.getChannel().equalsIgnoreCase(userName_)) {
				for (int i = 0; i < plugins_.size(); i++) {
					try {
						plugins_.get(i).onMessage(inMessage);
					} catch (Exception ex) {
						try {
							throw new IRCException(ex);
						} catch (IRCException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public String getUserName() {
		return userName_;
	}
}
