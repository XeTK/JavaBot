package plugin;

import java.io.IOException;

import program.IRCException;

public interface PluginTemp
{
	public void onCreate(String in_str) throws IRCException, IOException;
	public void onTime(String in_str) throws IRCException, IOException;
	public void onMessage(String in_str) throws IRCException, IOException;
	public void onJoin(String in_str) throws IRCException, IOException;
	public void onQuit(String in_str) throws IRCException, IOException;

}
