package plugin.reputation;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Channel;
import core.event.Message;
import core.plugin.Plugin;
import core.utils.IRC;
import core.utils.JSON;

public class Rep extends Plugin {
	private final String CONFIG_FILE_LOCATION = "Rep.json";
	
	private String cfgFile_ = new String();

	private RepList repList_ = new RepList();

	public void onCreate(Channel inChannel) throws Exception {
		cfgFile_ = inChannel.getPath() + CONFIG_FILE_LOCATION;
		if (new File(cfgFile_).exists())
			repList_ = (RepList) JSON.load(cfgFile_, RepList.class);
		else
			JSON.save(cfgFile_, repList_);

	}

	public void onMessage(Message inMessage) throws Exception {
		if (!inMessage.isPrivMsg()) {
			String channel = inMessage.getChannel();
			String message = inMessage.getMessage();

			// Message .Trim

			if (message.charAt(message.length() - 1) == ' ')
				message = message.substring(0, message.length() - 1);

			IRC irc = IRC.getInstance();
			if (message
					.matches("(^[a-zA-Z0-9]*)[\\s\\+-]([-\\+])[\\=\\s]*([\\d]*)")) {
				Matcher r = Pattern.compile(
						"(^[a-zA-Z0-9]*)[\\s\\+-]([-\\+])[\\=\\s]*([\\d]*)",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
						message);
				if (r.find()) {
					String item = r.group(1);
					String type = r.group(2);
					String ammount = r.group(3);

					if (!inMessage.getUser().equalsIgnoreCase(item))
					{
						if (!type.equals("")) {
							int iAmmount = 0;

							if (type.equals("++")) {
								iAmmount = 1;
							} else if (type.equals("--")) {
								iAmmount = -1;
							} else {
								type = type.trim();
								ammount = ammount.trim();
								if (type.equals("+"))
									iAmmount = Integer.valueOf(ammount);
								else
									iAmmount = Integer.valueOf(type + ammount);
							}
							if (iAmmount > 100 || iAmmount < -100) {
								irc.sendPrivmsg(channel,
										"You cant do that its to much rep...");
							} else {
								Reputation tempRep = repList_.getRep(item);
								tempRep.modRep(iAmmount);
								irc.sendPrivmsg(channel, item + ": Rep = "
										+ tempRep.getRep() + "!");
							}
						}
					}
				}
			} else if (message.matches("\\.rep [A-Za-z0-9#]+$")) {
				String[] t = message.split(" ");
				if (t.length > 0 || t[1] != null)
					irc.sendPrivmsg(channel, t[1] + ": Rep = "
							+ repList_.getRep(t[1]).getRep() + "!");
			}
			JSON.save(cfgFile_, repList_);
		}
	}

	public String getHelpString() {
		return "REP: \n"
				+ "\t.rep <Item> - view the reputation of a item\n"
				+ "\t<Item>--/++ - increment or decrement the rep of a item /\n"
				+ "\t<Item> +/- <Ammount> - increment or decrement the rep of a set item by a set amount\n";
	}

}
