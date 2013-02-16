package plugin;




public interface PluginTemp
{
	public void onCreate(String in_str);
	public void onTime(String in_str);
	public void onMessage(String in_str);
	public void onJoin(String in_str);
	public void onQuit(String in_str);

}
