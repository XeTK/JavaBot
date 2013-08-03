package plugin.test;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.IRC;

public class Counters extends Plugin {

	private static int creates_ = 0;
	private static int times_ = 0;
	private static int messages_ = 0;
	private static int joins_ = 0;
	private static int quits_ = 0;
	private static int kicks_ = 0;
	private static int raws_ = 0;
	
	public void onCreate(Channel inChannel) throws Exception {
		creates_++;
	}

	public void onTime() throws Exception {
		times_++;
	}

	public void onMessage(Message inMessage) throws Exception {
		if (inMessage.getMessage().startsWith(".globstats"))
			IRC.getInstance().sendActionMsg(inMessage.getChannel(), printDebug());
		messages_++;
	}

	public void onJoin(Join inJoin) throws Exception {
		joins_++;
	}

	public void onQuit(Quit inQuit) throws Exception {
		quits_++;
	}

	public void onKick(Kick inKick) throws Exception {
		raws_++;
	}

	public void rawInput(String inStr) throws Exception {
		raws_++;
	}
	@Override
	public String getHelpString() {
		//printDebug();
		return "COUNTERS: \n"
				+ "\t.globstats - Gets raw stats about the bot since it started.";
	}
	private String printDebug()
	{
		String text = "| %s Creates | %s Times | %s Joins | %s Quits | " +
				" %s Messages | %s Kicks | %s Raws |";
		text = String.format(text, 
				creates_,times_,joins_,quits_,messages_,kicks_,raws_);
		return text;
	}

}
