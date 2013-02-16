package program;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Database
{
	private static Connection handle;
	
	
	private static void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");
		handle = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/JaBot","root","");
	}
	private static void disconnect() throws SQLException
	{
		handle.close();
	}
	public static void updateUser(String username) throws SQLException, ClassNotFoundException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT COUNT(*) FROM User WHERE NickName =\"" + username + "\""));
		int temp = 0;
		while(exist.next())
			temp = exist.getInt(1);
		if (temp != 0)
		{
			String sql = "UPDATE User SET SentMsg = SentMsg + 1, LastOnline = NOW() WHERE NickName = \"" + username + "\"";
			//System.out.println(sql);
			handle.createStatement().executeUpdate(sql);
		}
		else
		{
			handle.createStatement().executeUpdate("INSERT INTO User(NickName, Rep, SentMsg, LastOnline) VALUES(\"" + username + "\", \"0\", \"0\", NOW())");
		}
		System.out.println("Updated Database");
		disconnect();
	}
	public static void updateRep(String username,int rep) throws ClassNotFoundException, SQLException
	{	
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT COUNT(*) FROM User WHERE NickName =\"" + username + "\""));
		int temp = 0;
		while(exist.next())
			temp = exist.getInt(1);
		if (temp != 0)
		{
			String sql = "UPDATE User SET Rep = Rep + " + rep + " WHERE NickName = \"" + username + "\"";
			//System.out.println(sql);
			handle.createStatement().executeUpdate(sql);
		}
		disconnect();
	}
	public static int getUserRep(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM User WHERE NickName =\"" + username + "\" LIMIT 1"));
		int temp = 0;
		while(exist.next())
			temp = exist.getInt("Rep");
		disconnect();
		return temp;
	}
	public static int getMessagesSent(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM User WHERE NickName =\"" + username + "\" LIMIT 1"));
		int temp = 0;
		while(exist.next())
			temp = exist.getInt("SentMsg");
		disconnect();
		return temp;
	}
	public static String getLastOnline(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM User WHERE NickName =\"" + username + "\" LIMIT 1"));
		String temp = "";
		while(exist.next())
			temp = exist.getString("LastOnline");
		disconnect();
		return temp;
	}
	public static void addReminder(String usernameSender,String usernameRecipicant, String message) throws SQLException, ClassNotFoundException
	{
		connect();
		handle.createStatement().executeUpdate("INSERT INTO remind(Sender,Recipient,Message) VALUES(\"" + usernameSender + "\", \"" + usernameRecipicant +"\", \""+ message +"\")");
		disconnect();
	}
	public static String[] getReminders(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM remind WHERE Recipient =\"" + username + "\""));
		ArrayList<String> temp = new ArrayList<String>();
		while(exist.next())
			temp.add(exist.getString("Recipient") + ": " +exist.getString("Message"));
		disconnect();
		return temp.toArray(new String[temp.size()]);
	}
	public static void delReminder(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		handle.createStatement().executeUpdate("DELETE FROM remind WHERE Recipient =\"" + username + "\"");
		disconnect();
	}
}
