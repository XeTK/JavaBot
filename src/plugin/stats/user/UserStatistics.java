package plugin.stats.user;

import java.io.IOException;

import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.menu.MenuItem;
import core.plugin.Plugin;
import plugin.stats.user.UserListLoader;
import core.utils.Details;
import core.utils.IRCException;

public class UserStatistics extends Plugin {

	private final String NEW_USER    = "Hello %s and welcome! Don't break things!";
	private final String USER_JOINED = "%s has joined %s times";
	private final String USER_QUIT   = "%s has quit %s times";
	private final String USER_KICKED = "%s has been kicked %s times";
	
	private UserList userList;

	public void onCreate(Channel inChannel) throws Exception {
		super.onCreate(inChannel);
		this.userList = ((UserListLoader) inChannel.getPlugin(UserListLoader.class)).getUserList();
	}

	public void onMessage(Message inMessage) throws IRCException, IOException {
		if (!inMessage.isPrivMsg()) {
			userList.msgSent(inMessage);
		}
	}

	public void onJoin(Join inJoin) throws Exception {
		User user = userList.getUser(inJoin.getUser());
		if (user != null) {
			user.incjoins(inJoin.getHost());
			String msg = String.format(USER_JOINED, inJoin.getUser(),user.getJoins());
			
			irc.sendPrivmsg(inJoin.getChannel(), msg);
		} else {
			String botName = Details.getInstance().getNickName();
			if (!botName.equalsIgnoreCase(inJoin.getUser())) {
				String msg = String.format(NEW_USER, inJoin.getUser());
				irc.sendPrivmsg(inJoin.getChannel(), msg);
			}
		}
	}

	public void onQuit(Quit in_quit) throws Exception {

		User userOBJ = userList.getUser(in_quit.getUser());
		if (userOBJ != null) {
			userOBJ.incQuits();
			String msg = String.format(USER_QUIT, in_quit.getUser(), userOBJ.getQuits());
			
			irc.sendPrivmsg(in_quit.getChannel(), msg);		
		}
	}

	public void onKick(Kick in_kick) throws Exception {

		User userOBJ = userList.getUser(in_kick.getKicked());
		if (userOBJ != null) {
			userOBJ.incKicks();

			String msg = String.format(USER_KICKED, in_kick.getKicked(), userOBJ.getKicks());
					
			irc.sendPrivmsg(in_kick.getChannel(), msg);
		}
	}

	@Override
	public void getMenuItems(MenuItem rootItem) {
		//No Menu Needed.
	}
}
