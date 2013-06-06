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
		Details details = Details.getIntance();
		handle = DriverManager.getConnection("jdbc:mysql://" + details.getDbServer() + ":" + details.getDbPort() + "/" + details.getDbTable(),details.getDbUser(), details.getDbpasswd());
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
			handle.createStatement().executeUpdate("INSERT INTO User(NickName, Rep, SentMsg, LastOnline) VALUES(\"" + username + "\", \"1\", \"0\", NOW())");
		}
		System.out.println("Updated Database");
		disconnect();
	}
	public static void updateRep(String item,int rep) throws ClassNotFoundException, SQLException
	{	
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT COUNT(*) FROM Rep WHERE item =\"" + item + "\""));
		int temp = 0;
		while(exist.next())
			temp = exist.getInt(1);
		if (temp != 0)
		{
			String sql = "UPDATE Rep SET rep = rep + " + rep + " WHERE item = \"" + item + "\"";
			//System.out.println(sql);
			handle.createStatement().executeUpdate(sql);
		}
		else
		{
			handle.createStatement().executeUpdate("INSERT INTO Rep(rep,item) VALUES(\"" + rep + "\", \"" + item + "\")");
		}
		disconnect();
	}
	public static int getUserRep(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM Rep WHERE item =\"" + username + "\" LIMIT 1"));
		int temp = 0;
		while(exist.next())
			temp = exist.getInt("rep");
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
			temp.add(exist.getString("Recipient") + ": " + exist.getString("Sender") + " Said " +exist.getString("Message"));
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
