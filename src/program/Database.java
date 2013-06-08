package program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Database connection for various sections of the IRC bot.
 * @author Tom Rosier (XeTK)
 */
public class Database
{
	//Singleton for this class is stored here.
	private static Database db;
	
	/**
	 * Keep are connection handler easy to get to, 
	 * we will need to interact with it a lot... 
	 */
	private Connection handle;
	
	/**
	 * Makes the class comply with the singleton pattern,
	 * by returning an instance of the class if one already exists.
	 * @return's a reference to this class else we create a new one for us.
	 */
	public static Database getInstance()
	{
		if (db == null)
			db = new Database();
		
		return db;
	}
	
	/**
	 * Connect to the database of choice, simple really, this contains 
	 * the class information for the library needed to connect to the database
	 * along with passing the information about the database to the driver class.
	 */
	private void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.jdbc.Driver");	
		Details details = Details.getIntance();	
		handle = DriverManager.getConnection(
				"jdbc:mysql://" + details.getDbServer() + 
				":"+ details.getDbPort() + "/" + details.getDbTable()
				,details.getDbUser()
				,details.getDbpasswd()
				);
	}
	
	/**
	 * Disconnect from the database once we are done working on it, this just
	 * closes the handler for now.
	 */
	private void disconnect() throws SQLException
	{
		handle.close();
	}
	
	/**
	 * This is a simplified method to do a update on the database without 
	 * repeating code in multiple places, it also handles the connecting 
	 * and disconnecting along with giving a console message 
	 * to say that an update has happened.
	 * @param sql this is the SQL statement that we need for the update
	 */
	private void executeUpdate(String sql) 
			throws SQLException, ClassNotFoundException
	{
		connect();
		handle.createStatement().executeUpdate(sql);
		System.out.println("\u001B[33mUpdated Database");
		disconnect();
	}
	
	/**
	 * This is a method to carry out a Query within the database and return an
	 * integer value for the end user to then work on, this is quite common for
	 * finding out statistics.
	 * @param sql this is are SQL query string/command
	 * @param row this is the row that we want to retrieve the value from
	 * @return this return is the value of the row that we have requested
	 */
	private int executeQueryInt(String sql, String row) 
			throws SQLException, ClassNotFoundException
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
	
	/**
	 * Similar to the method above although this time it returns a String 
	 * rather than a Integer, it is useful for querying the database for an
	 * string value, and is used a lot for quote type tasks.
	 * @param sql this is the SQL statement/command needed to carry out the query
	 * @param row this is the row holding the data within the database
	 * @return we return the string for us to later work on
	 */
	private String executeQueryString(String sql, String row) 
			throws SQLException, ClassNotFoundException
	{
		connect();
		
		ResultSet exist = handle.createStatement().executeQuery(sql);
		String temp = "";
		
		while(exist.next())
			temp = exist.getString(row);
		
		disconnect();
		
		return temp;
	}
	/**
	 * This method is used to update the number of messages that a user has sent
	 * this is good for statistics it also at the same time updates the users
	 * last online time so we can then query when a user was last active.
	 * @param user this is the username that we want to update the information for
	 */
	public void updateUser(String user) 
			throws SQLException, ClassNotFoundException
	{	
		String query = "SELECT COUNT(*) FROM User " +
				"WHERE NickName =\"" + user + "\"",
				update = "";
		
		if (executeQueryInt(query, "") != 0)
			update = "UPDATE User SET SentMsg = SentMsg + 1, " +
					"LastOnline = NOW() WHERE NickName = \"" + user + "\"";
		else
			update = "INSERT INTO User(NickName, Rep, SentMsg, LastOnline) " +
					"VALUES(\"" + user + "\", \"1\", \"0\", NOW())";
		
		executeUpdate(update);
	}
	
	/**
	 * This method updates the reputation of a specific item that the user wants
	 * to increment or decrement.
	 * @param item this is the item that the user want to change the reputation for
	 * @param rep this is the amount of reputation that the user wants to + or -
	 */
	public void updateRep(String item,int rep) 
			throws ClassNotFoundException, SQLException
	{	
		String query = "SELECT COUNT(*) FROM Rep WHERE item =\"" + item + "\"",
				update = "";
		
		if (executeQueryInt(query,"") != 0)
			update = "UPDATE Rep SET rep = rep + " + rep + " " +
					"WHERE item = \"" + item + "\"";
		else
			update = "INSERT INTO Rep(rep,item) " +
					"VALUES(\"" + rep + "\", \"" + item + "\")";
		
		executeUpdate(update);
	}
	
	/**
	 * This returns the reputation of a specific item given, it executes and 
	 * returns the value that is stored within the DB.
	 * @param user this is the name of the item we want to get the reputation for.
	 * @return this is the value of the reputation of the passed in item
	 */
	public int getUserRep(String user) 
			throws ClassNotFoundException, SQLException
	{
		return executeQueryInt("SELECT * FROM Rep " +
				"WHERE item =\"" + user + "\" LIMIT 1","rep");
	}
	
	/**
	 * This returns the number of message sent by a specific user, it executes 
	 * a query to return the value of the number of messages that the user has sent.
	 * @param user this is the username we want to return the messages sent for.
	 * @return's the value of the messages sent by the end user.
	 */
	public int getMessagesSent(String user) 
			throws ClassNotFoundException, SQLException
	{
		return executeQueryInt("SELECT * FROM User " +
				"WHERE NickName =\"" + user + "\" LIMIT 1","SentMsg");
	}
	
	public String getLastOnline(String user) 
			throws ClassNotFoundException, SQLException
	{
		return executeQueryString("SELECT * FROM User " +
				"WHERE NickName =\"" + user + "\" LIMIT 1","LastOnline");
	}
	
	/**
	 * This adds a reminder to the DB to be later picked up and used to reminder 
	 * an user of something another user wants to remind them off.
	 * @param sender this is the person that sent the reminder
	 * @param recip this is the recipient of the message and who it is targeted at
	 * @param message this is the actual message that is going to given to the user
	 */
	public void addReminder(String sender,String recip, String message) 
			throws SQLException, ClassNotFoundException
	{
		executeUpdate("INSERT INTO remind(Sender,Recipient,Message) " +
				"VALUES(\"" + sender + "\", \"" + recip +"\", \""+ message +"\")");
	}
	
	/**
	 * This retrieves all the reminders for a given user, so they can be presented
	 * to the end user, it uses a result set and query to get this information
	 * @param username this the user that we want to retrieve the reminders for
	 * @return's a list of reminders for the given user to be handled and printed
	 */
	public String[] getReminders(String username) 
			throws ClassNotFoundException, SQLException
	{
		connect();
		
		ResultSet exist = (handle.createStatement().executeQuery(
				"SELECT * FROM remind WHERE Recipient =\"" + username + "\""));
		
		ArrayList<String> temp = new ArrayList<String>();
		
		while(exist.next())
			temp.add(exist.getString("Recipient") + ": " 
					+ exist.getString("Sender") 
					+ " Said " +exist.getString("Message"));
		
		disconnect();
		
		return temp.toArray(new String[temp.size()]);
	}
	
	/**
	 * This removes any already viewed reminders that a view has viewed
	 * @param user this is the user we want to remove the reminders for
	 */
	public void delReminder(String user) 
			throws ClassNotFoundException, SQLException
	{
		executeUpdate("DELETE FROM remind WHERE Recipient =\"" + user + "\"");
	}
	
	
	public void addQuote(String user, String quote) 
			throws SQLException, ClassNotFoundException
	{
		executeUpdate("INSERT INTO Quote(User,Message) " +
				"VALUES(\"" + user + "\", \"" + quote + "\")");
	}
	
	public String[] getQuotes(String username) 
			throws SQLException, ClassNotFoundException 
	{
		connect();
		
		ResultSet exist = (handle.createStatement().executeQuery(
				"SELECT * FROM Quote WHERE User =\"" + username + "\""));
		
		ArrayList<String> temp = new ArrayList<String>();
		
		while(exist.next())
			temp.add(exist.getString("User") + ": " + exist.getString("Message"));
		
		disconnect();
		
		return temp.toArray(new String[temp.size()]);
	}
	
	public void delQuote(String message) 
			throws ClassNotFoundException, SQLException
	{
		executeUpdate("DELETE FROM Quote WHERE Message =\"" + message + "\"");
	}
	
	public void addReminderEvent(String time, String reminder) 
			throws SQLException, ClassNotFoundException
	{
		executeUpdate("INSERT INTO Reminder(time,event) " +
				"VALUES(\"" + time + "\", \"" + reminder + "\")");
	}
	
	public String[] getReminderEvents(String time) 
			throws SQLException, ClassNotFoundException 
	{
		connect();
		
		ResultSet exist = (handle.createStatement().executeQuery(
				"SELECT * FROM Reminder WHERE time =\"" + time + "\""));
		
		ArrayList<String> temp = new ArrayList<String>();
		
		while(exist.next())
			temp.add("Reminder: " + exist.getString("event"));
		
		disconnect();
		
		return temp.toArray(new String[temp.size()]);
	}
	
	public void delReminderEvent(String time) 
			throws ClassNotFoundException, SQLException
	{
		executeUpdate("DELETE FROM Reminder WHERE time =\"" + time + "\"");
	}
}
