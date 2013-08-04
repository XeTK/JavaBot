package plugin.utils.authenticate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import plugin.stats.user.User;
import plugin.stats.user.UserList;
import core.Channel;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.EMail;
import core.utils.IRC;

public class Authenticate extends Plugin {
	private final String keyPath_ = "key.txt";

	private AuthenticatedUsers authUsers_ = AuthenticatedUsers.getInstance();

	private Channel channel_;

	public void onCreate(Channel inChannel) throws Exception {
		this.channel_ = inChannel;
	}
	public void onMessage(Message inMessage) throws Exception {
		IRC irc = IRC.getInstance();

		if (inMessage.isPrivMsg()) {
			User user = ((UserList) channel_.getPlugin(UserList.class)).getUser(inMessage.getUser());
			if (user != null) {
				if (inMessage.getMessage().matches("LOGIN .*")) {
					if (!authUsers_.contains(user)) {
						if (user.getEmail() == null||user.getEmail().isEmpty()){
							irc.sendPrivmsg(inMessage.getChannel(),
									"Please Register");
						} else {
							Matcher m = Pattern.compile(
									"LOGIN (.*@.*\\..*) (.*)",
									Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
									.matcher(inMessage.getMessage());
							if (m.find()) {
								String email = m.group(1);
								String password = m.group(2);

								byte[] rawHaPwd = hashPassword(password);

								byte[] enHaPsw = user
										.getEncryptedPasswordHash();

								if (Arrays.equals(rawHaPwd,
										decrpytPasswordHash(enHaPsw))
										&& user.getEmail().equalsIgnoreCase(
												email)) {
									irc.sendPrivmsg(inMessage.getChannel(),
											"Authenticated");
									authUsers_.add(user);
								} else {
									irc.sendPrivmsg(inMessage.getChannel(),
											"Incorrect login details");
								}
							} else {
								irc.sendPrivmsg(inMessage.getChannel(),
										"LOGIN (EMAIL) (PASSWORD)");
							}
						}
					} else {
						irc.sendPrivmsg(inMessage.getChannel(),
								"Already Logged in");
					}
				} else if (inMessage.getMessage().matches("REGISTER .*")) {
					Matcher m = Pattern.compile("REGISTER (.*@.*\\..*) (.*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
							inMessage.getMessage());
					if (m.find()) {
						if (user.getEmail() == null||user.getEmail().isEmpty()){
							String email = m.group(1);
							String password = m.group(2);
							byte[] hashedPassword = hashPassword(password);
							byte[] enPassword = encryptPasswordHash(hashedPassword);
							user.setEncyptedPasswordHash(enPassword);
							user.setEmail(email);
							irc.sendPrivmsg(inMessage.getChannel(),
									"You are now Registered");
							EMail.sendEmail(user.getEmail(),
									"Hello\n\nYou have Registered with Spunky using the email "
											+ email,
									"You are now registered with Spunky!");
						} else {
							irc.sendPrivmsg(inMessage.getChannel(),
									"You are already registered");
						}
					} else {
						irc.sendPrivmsg(inMessage.getChannel(),
								"REGISTER (EMAIL) (PASSWORD)");
					}
				} else if (inMessage.getMessage().matches("LOGOUT")) {
					if (authUsers_.contains(user)) {
						authUsers_.remove(user);
						irc.sendPrivmsg(inMessage.getChannel(),
								"You have been logged out!");
					} else {
						irc.sendPrivmsg(inMessage.getChannel(),
								"You were not logged in");
					}
				} else if (inMessage.getMessage().matches("RECOVER .*")) {
					Matcher m = Pattern.compile("RECOVER (.*@.*\\..*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(
							inMessage.getMessage());
					if (m.find()) {
						EMail.sendEmail(user.getEmail(), "Test Lemons",
								"I like to lemons");
						irc.sendPrivmsg(inMessage.getUser(), "MsgSent");
					} else {
						// I really need more lemons
						// This really needs implementing
					}
				}
			}
		}
	}

	public void onJoin(Join inJoin) throws Exception {
		IRC irc = IRC.getInstance();
		User user = ((UserList) channel_.getPlugin(UserList.class)).getUser(inJoin.getUser());
		if (user != null)
			if (!authUsers_.contains(user))
				if (user.getEmail() != null && !user.getEmail().isEmpty())
					irc.sendPrivmsg(user.getUser(), "Please Login!");
	}

	public void onQuit(Quit inQuit) throws Exception {
		User user = ((UserList) channel_.getPlugin(UserList.class)).getUser(inQuit.getUser());
		authUsers_.remove(user);
	}

	@Override
	public void onKick(Kick inKick) throws Exception {
		User user = ((UserList) channel_.getPlugin(UserList.class)).getUser(inKick.getKicked());
		authUsers_.remove(user);
	}

	private byte[] hashPassword(String password) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(password.getBytes());
		return new String(messageDigest.digest()).getBytes();
	}
	// Merge Decription and encryption
	private byte[] encryptPasswordHash(byte[] paswordHash) throws Exception {
		byte[] encryptkey = getEncryptionKey();
		SecretKey key = new SecretKeySpec(encryptkey,0,encryptkey.length,"AES");

		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		desCipher.init(Cipher.ENCRYPT_MODE, key);

		return desCipher.doFinal(paswordHash);
	}

	private byte[] decrpytPasswordHash(byte[] enPasswordHash) throws Exception {
		byte[] decryptkey = getEncryptionKey();
		SecretKey key = new SecretKeySpec(decryptkey,0,decryptkey.length,"AES");

		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

		desCipher.init(Cipher.DECRYPT_MODE, key);
		return desCipher.doFinal(enPasswordHash);
	}

	private byte[] getEncryptionKey() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(keyPath_));

		String line = reader.readLine();

		// Close the file read to prevent us problems late.
		reader.close();

		return line.getBytes();
	}

	public String getHelpString() {
		return "AUTHENICATIONL:\n"
				+ "\tLOGIN <EMAIL> <PASSWORD> - This is to authenticate with the bot\n"
				+ "\tREGISTER <EMAIL> <PASSWORD> - Use this to register with the bot\n"
				+ "\tLOGOUT - This will unauthenticate you with the bot\n"
				+ "\tRECOVER - TO BE COMPLETED\n";
	}
}
