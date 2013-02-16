package plugin;
import program.IRC;
import program.Start;




public class Admin implements PluginTemp
{

	@Override
	public void onCreate(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onTime(String in_str)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(String in_str)
	{
		//:XeTK!xetk@cpc4-swin16-2-0-cust422.3-1.cable.virginmedia.com PRIVMSG #xetk :asdf
		String temp[] = in_str.split(":"),
				message, host, user, channel;
		message = temp[2];
		temp = temp[1].split("!");
		user = temp[0];
		temp = temp[1].split(" ");
		host = temp[0];
		channel = temp[2];
		if (message.matches("^.join"))
		{
			String str[] = message.split(" ");
			for (int i = 0;i < Start.getDetails().getAdmins().length;i++)
			{
				if (user.equals(Start.getDetails().getAdmins()[i]))
				{
					IRC.sendServer("JOIN " + str[1]);
					IRC.sendServer("PRIVMSG " + channel + " I Have Joined " + str[1] + ", Master!");
					break;
				}
			}
		}
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

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
