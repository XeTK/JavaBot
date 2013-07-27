package plugin.authenticate;
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

import plugin.users.User;
import plugin.users.UserList;
import core.event.Join;
import core.event.Kick;
import core.event.Message;
import core.event.Quit;
import core.plugin.Plugin;
import core.utils.EMail;
import core.utils.IRC;

public class Authenticate extends Plugin
{
	private final String key_Path = "key.txt";
	
	private AuthenticatedUsers auth_Users = AuthenticatedUsers.getInstance();

	public void onMessage(Message in_message) throws Exception 
	{
		IRC irc = IRC.getInstance();
		
		if (in_message.isPrivMsg())
		{
			User user = UserList.getInstance().getUser(in_message.getUser());
			if (user != null)
			{
				if (in_message.getMessage().matches("LOGIN .*"))
				{
					if (!auth_Users.contains(user))
					{
						if (user.getEmail() == null||user.getEmail().isEmpty())
						{
							irc.sendPrivmsg(in_message.getChannel(), "Please Register");
						}
						else
						{
							Matcher m = Pattern.compile("LOGIN (.*@.*\\..*) (.*)",
									Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
									.matcher(in_message.getMessage());
							if (m.find())
							{
								String email = m.group(1), password = m.group(2);
								
								byte[] rawHaPwd = hashPassword(password);
								
								byte[] enHaPsw = user.getEncryptedPasswordHash();
								
								if (Arrays.equals(rawHaPwd, decrpytPasswordHash(enHaPsw))
										&& user.getEmail().equalsIgnoreCase(email))
								{
									irc.sendPrivmsg(in_message.getChannel(), "Authenticated");
									auth_Users.add(user);
								}
								else
								{
									irc.sendPrivmsg(in_message.getChannel(), "Incorrect login details");
								}
							}
							else
							{
								irc.sendPrivmsg(in_message.getChannel(), "LOGIN (EMAIL) (PASSWORD)");
							}
						}
					}
					else
					{
						irc.sendPrivmsg(in_message.getChannel(), "Already Logged in");
					}
				}
				else if (in_message.getMessage().matches("REGISTER .*"))
				{
					Matcher m = Pattern.compile("REGISTER (.*@.*\\..*) (.*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
							.matcher(in_message.getMessage());
					if (m.find())
					{
						if (user.getEmail() == null||user.getEmail().isEmpty())
						{
							String email = m.group(1), password = m.group(2);
							byte[] hashedPassword = hashPassword(password);
							byte[] enPassword = encryptPasswordHash(hashedPassword);
							user.setEncyptedPasswordHash(enPassword);
							user.setEmail(email);
							irc.sendPrivmsg(in_message.getChannel(), "You are now Registered");
							EMail.sendEmail(user.getEmail(),
								"Hello\n\nYou have Registered with Spunky using the email " + email
								, "You are now registered with Spunky!");
						}
						else
						{
							irc.sendPrivmsg(in_message.getChannel(), "You are already registered");
						}
					}
					else
					{
						irc.sendPrivmsg(in_message.getChannel(), "REGISTER (EMAIL) (PASSWORD)");
					}
				}
				else if (in_message.getMessage().matches("LOGOUT"))
				{
					if (auth_Users.contains(user))
					{
						auth_Users.remove(user);
						irc.sendPrivmsg(in_message.getChannel(), "You have been logged out!");
					}
					else
					{
						irc.sendPrivmsg(in_message.getChannel(), "You were not logged in");
					}
				}
				else if (in_message.getMessage().matches("RECOVER .*"))
				{
					Matcher m = Pattern.compile("RECOVER (.*@.*\\..*)",
							Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
							.matcher(in_message.getMessage());
					if (m.find())
					{
						EMail.sendEmail(user.getEmail(), "Test Lemons", "I like to lemons");
						irc.sendPrivmsg(in_message.getUser(), "MsgSent");
					}
					else
					{
						// I really need more lemons
						// This really needs implementing
					}	
				}
			}
		}
	}

	public void onJoin(Join in_join) throws Exception 
	{
		IRC irc = IRC.getInstance();
		User user = UserList.getInstance().getUser(in_join.getUser());
		if (user != null)
			if (!auth_Users.contains(user))
				if (user.getEmail() != null&&!user.getEmail().isEmpty())
					irc.sendPrivmsg(user.getUser(), "Please Login!");
	}

	public void onQuit(Quit in_quit) throws Exception
	{
		User user = UserList.getInstance().getUser(in_quit.getUser());
		auth_Users.remove(user);
	}

	@Override
	public void onKick(Kick in_kick) throws Exception
	{
		User user = UserList.getInstance().getUser(in_kick.getKicked());
		auth_Users.remove(user);
	}
	
	
	private byte[] hashPassword(String password) throws Exception
	{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(password.getBytes());
		return new String(messageDigest.digest()).getBytes();
	}
	
	private byte[] encryptPasswordHash(byte[] paswordHash) throws Exception
	{
		byte[] encryptkey = getEncryptionKey();
		SecretKey sKey = new SecretKeySpec(encryptkey,0,encryptkey.length, "AES");
		
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    
	    	desCipher.init(Cipher.ENCRYPT_MODE, sKey);
	    
	   	return desCipher.doFinal(paswordHash);
	}

	private byte[] decrpytPasswordHash(byte[] enPasswordHash) throws Exception
	{
		byte[] decryptkey = getEncryptionKey();
		SecretKey sKey = new SecretKeySpec(decryptkey,0,decryptkey.length, "AES");
		
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    
		desCipher.init(Cipher.DECRYPT_MODE, sKey);
		return desCipher.doFinal(enPasswordHash);
	}
	
	private byte[] getEncryptionKey() throws IOException
	{ 
		BufferedReader reader = new BufferedReader(new FileReader(key_Path));
		
		String line = reader.readLine();
		
		// Close the file read to prevent us problems late.
		reader.close();
		
		return line.getBytes();
	}

	public String getHelpString()
	{
		// TODO Auto-generated method stub
		return "Authentication help string";
	}
}
