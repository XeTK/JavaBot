package plugin.stats.channel;

import java.util.TimerTask;

import core.utils.IRC;

import plugin.stats.channel.data.Day;
import plugin.stats.channel.ChannelStatistics;

public class MidnightThread extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	/*private final String STAT_MSG = "Handled %s Messages, %s users joined, "
			+ "%s users quit, %s users kicked in the last %s!";
	
	private final IRC irc = IRC.getInstance();
	
	private ChannelStatistics channelStats;
	
	public MidnightThread(ChannelStatistics channelStats){
		this.channelStats = channelStats;
		System.out.println("Thread Started");
	}
	@Override
	public void run() {
		try {
			Day today = channelStats.getToday();
			String msg = String.format(STAT_MSG, 
					today.msgsSent(),
					today.joins(),
					today.quits(), 
					today.kicks(), 
					"day");
			
			irc.sendActionMsg(channelStats.getChannel().getChannelName(), msg);
			
			today.setDisplayedDayStats();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}*/
	
}
