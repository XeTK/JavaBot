import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import addons.User;
import addons.UserList;
import event.Join;
import event.Kick;
import event.Message;
import event.Quit;
import plugin.PluginTemp;
import program.Details;
import program.IRC;


public class Authenticate implements PluginTemp
{

	private ArrayList<User> authenticated_users = new ArrayList<User>();
	@Override
	public String name() 
	{
		return "Authentication";
	}

	@Override
	public void onTime() throws Exception 
	{
		
	}

	@Override
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
					if (!authenticated_users.contains(user))
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
										&& user.getEmail().equals(email))
								{
									irc.sendPrivmsg(in_message.getChannel(), "Authenticated");
									authenticated_users.add(user);
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
						}
						else
						{
							//Already registered
						}
					}
					else
					{
						//Incorrect CMD
					}
				}
				else if (in_message.getMessage().matches("LOGOUT"))
				{
					if (authenticated_users.contains(user))
					{
						authenticated_users.remove(user);
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
						
					}
					else
					{
						
					}	
				}
			}
		}
	}

	@Override
	public void onJoin(Join in_join) throws Exception 
	{
		
	}

	@Override
	public void onQuit(Quit in_quit) throws Exception
	{
		
	}

	@Override
	public void onKick(Kick in_kick) throws Exception
	{
		
	}
	
	private byte[] hashPassword(String password) throws NoSuchAlgorithmException
	{
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(password.getBytes());
		return new String(messageDigest.digest()).getBytes();
	}
	
	private byte[] encryptPasswordHash(byte[] paswordHash) throws 
	NoSuchAlgorithmException, NoSuchPaddingException, 
	IllegalBlockSizeException, BadPaddingException, InvalidKeyException
	{
		byte[] encryptkey = Details.getIntance().getEncryptionKey();
		SecretKey sKey = new SecretKeySpec(encryptkey,0,encryptkey.length, "AES");
		
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    
	    	desCipher.init(Cipher.ENCRYPT_MODE, sKey);
	    
	   	return desCipher.doFinal(paswordHash);
	}

	private byte[] decrpytPasswordHash(byte[] enPasswordHash) throws 
	NoSuchAlgorithmException, NoSuchPaddingException, 
	InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] decryptkey = Details.getIntance().getEncryptionKey();
		SecretKey sKey = new SecretKeySpec(decryptkey,0,decryptkey.length, "AES");
		
		Cipher desCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    
		desCipher.init(Cipher.DECRYPT_MODE, sKey);
		return desCipher.doFinal(enPasswordHash);
	}
	
	@Override
	public void onCreate() throws Exception {}
	@Override
	public void onOther(String in_str) throws Exception {}

}
