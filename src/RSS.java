import org.xml.sax.SAXException;

import plugin.PluginTemp;
import uk.org.catnip.eddie.FeedData;
import uk.org.catnip.eddie.parser.Parser;

public class RSS implements PluginTemp
{

	@Override
	public void onCreate(String in_str)
	{
		System.out.println("RSS LOADED");
		try
		{
			FeedData feed = new Parser().parse("http://feeds.bbci.co.uk/news/rss.xml?edition=int");
			System.out.println(feed);
		} 
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTime(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoin(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onQuit(String in_str)
	{
		// TODO Auto-generated method stub

	}

}
