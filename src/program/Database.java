package program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Database connection for various sections of the IRC bot
 * @author Tom Rosier (XeTK)
 *
 */
public class Database
{
	//Singleton for this class is stored here
	private static Database db;
	
	//keep are connection handler easy to get to, we will need to interact with it a lot...
	private Connection handle;
	
	/**
	 * This returns an instance of this Database class for other objects to interact with
	 * @return we return a reference to this class else we create a new one for us to work with
	 */
	public static Database getInstance()
	{
		if (db == null)
			db = new Database();
		
		return db;
	}
	
	/**
	 * Connect to the database of choice, simple really
	 */
	private void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");	
		Details details = Details.getIntance();	
		handle = DriverManager.getConnection(
				"jdbc:mysql://" + details.getDbServer() + ":"+ details.getDbPort() + "/" + details.getDbTable()
				,details.getDbUser()
				,details.getDbpasswd()
				);
	}
	
	/**
	 * Disconnect from the database once we are done working on it
	 */
	private void disconnect() throws SQLException
	{
		handle.close();
	}
	
	private void executeUpdate(String sql) throws SQLException, ClassNotFoundException
	{
		connect();
		handle.createStatement().executeUpdate(sql);
		System.out.println("\u001B[33mUpdated Database");
		disconnect();
	}
	
	private int executeQueryInt(String sql, String row) throws SQLException, ClassNotFoundException
	{
		connect();
		
		ResultSet exist = handle.createStatement().executeQuery(sql);
		int temp = 0;
		
		if (!row.equals(""))
			while(exist.next())
				temp = exist.getInt(row);
		else
			while(exist.next())
				temp = exist.getInt(1);
		
		disconnect();
		
		return temp;
	}
	
	private String executeQueryString(String sql, String row) throws SQLException, ClassNotFoundException
	{
		connect();
		
		ResultSet exist = handle.createStatement().executeQuery(sql);
		String temp = "";
		
		while(exist.next())
			temp = exist.getString(row);
		
		disconnect();
		
		return temp;
	}
	
	public void updateUser(String username) throws SQLException, ClassNotFoundException
	{	
		if (executeQueryInt("SELECT COUNT(*) FROM User WHERE NickName =\"" + username + "\"", "") != 0)
			executeUpdate("UPDATE User SET SentMsg = SentMsg + 1, LastOnline = NOW() WHERE NickName = \"" + username + "\"");
		else
			executeUpdate("INSERT INTO User(NickName, Rep, SentMsg, LastOnline) VALUES(\"" + username + "\", \"1\", \"0\", NOW())");
	}
	
	public void updateRep(String item,int rep) throws ClassNotFoundException, SQLException
	{	
		if (executeQueryInt("SELECT COUNT(*) FROM Rep WHERE item =\"" + item + "\"","") != 0)
			executeUpdate("UPDATE Rep SET rep = rep + " + rep + " WHERE item = \"" + item + "\"");
		else
			executeUpdate("INSERT INTO Rep(rep,item) VALUES(\"" + rep + "\", \"" + item + "\")");
	}
	
	public int getUserRep(String username) throws ClassNotFoundException, SQLException
	{
		return executeQueryInt("SELECT * FROM Rep WHERE item =\"" + username + "\" LIMIT 1","rep");
	}
	
	public int getMessagesSent(String username) throws ClassNotFoundException, SQLException
	{
		return executeQueryInt("SELECT * FROM User WHERE NickName =\"" + username + "\" LIMIT 1","SentMsg");
	}
	
	public String getLastOnline(String username) throws ClassNotFoundException, SQLException
	{
		return executeQueryString("SELECT * FROM User WHERE NickName =\"" + username + "\" LIMIT 1","LastOnline");
	}
	
	public void addReminder(String usernameSender,String usernameRecipicant, String message) throws SQLException, ClassNotFoundException
	{
		executeUpdate("INSERT INTO remind(Sender,Recipient,Message) VALUES(\"" + usernameSender + "\", \"" + usernameRecipicant +"\", \""+ message +"\")");
	}
	
	public String[] getReminders(String username) throws ClassNotFoundException, SQLException
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM remind WHERE Recipient =\"" + username + "\""));
		ArrayList<String> temp = new ArrayList<String>();
		while(exist.next())
			temp.add(exist.getString("Recipient") + ": " + exist.getString("Sender") + " Said " +exist.getString("Message"));
		disconnect();
		return temp.toArray(new String[temp.size()]);
	}
	
	public void delReminder(String username) throws ClassNotFoundException, SQLException
	{
		executeUpdate("DELETE FROM remind WHERE Recipient =\"" + username + "\"");
	}
	
	public void addQuote(String username, String quote) throws SQLException, ClassNotFoundException
	{
		executeUpdate("INSERT INTO Quote(User,Message) VALUES(\"" + username + "\", \"" + quote + "\")");
	}
	
	public String[] getQuotes(String username) throws SQLException, ClassNotFoundException 
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM Quote WHERE User =\"" + username + "\""));
		ArrayList<String> temp = new ArrayList<String>();
		while(exist.next())
			temp.add(exist.getString("User") + ": " + exist.getString("Message"));
		disconnect();
		return temp.toArray(new String[temp.size()]);
	}
	
	public void delQuote(String message) throws ClassNotFoundException, SQLException
	{
		executeUpdate("DELETE FROM Quote WHERE Message =\"" + message + "\"");
	}
	
	public void addReminderEvent(String time, String reminder) throws SQLException, ClassNotFoundException
	{
		executeUpdate("INSERT INTO Reminder(time,event) VALUES(\"" + time + "\", \"" + reminder + "\")");
	}
	
	public String[] getReminderEvents(String time) throws SQLException, ClassNotFoundException 
	{
		connect();
		ResultSet exist = (handle.createStatement().executeQuery("SELECT * FROM Reminder WHERE time =\"" + time + "\""));
		ArrayList<String> temp = new ArrayList<String>();
		while(exist.next())
			temp.add("Reminder: " + exist.getString("event"));
		disconnect();
		return temp.toArray(new String[temp.size()]);
	}
	
	public void delReminderEvent(String time) throws ClassNotFoundException, SQLException
	{
		executeUpdate("DELETE FROM Reminder WHERE time =\"" + time + "\"");
	}
}
