
public class HelloWorldPlugin implements Plugin
{

	@Override
	public void onCreate(String in_str)
	{
		// TODO Auto-generated method stub
		System.out.println("Plugin Created");
	}

	@Override
	public void onTime(String in_str)
	{
		// TODO Auto-generated method stub
		System.out.println("On Time");
	}

	@Override
	public void onMessage(String in_str)
	{
		// TODO Auto-generated method stub
		System.out.println("Plugin on Messaged");
	}

	@Override
	public void onJoin(String in_str)
	{
		// TODO Auto-generated method stub
		System.out.println("Plugin On Join");
	}

	@Override
	public void onQuit(String in_str)
	{
		// TODO Auto-generated method stub
		System.out.println("On Quit");
	}

	
}
