import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import plugin.PluginTemp;


public class RSS implements PluginTemp {

	@Override
	public String name() 
	{
		return "RSS";
	}

	@Override
	public void onTime() throws Exception 
	{
		
	}

	@Override
	public void onMessage(Message in_message) throws Exception 
	{
		
	}
	
	@Override
	public void onOther(String in_str) throws Exception 
	{
		
	}
	
	@Override
	public void onCreate() throws Exception {}
	@Override
	public void onJoin(Join in_join) throws Exception {}
	@Override
	public void onQuit(Quit in_quit) throws Exception {}
	@Override
	public void onKick(Kick in_kick) throws Exception {}
}

