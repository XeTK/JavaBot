package addons;
import java.util.ArrayList;
import java.util.Date;

public class User
{
	private String user, host;
	private long msgSent, joins, quits;
	private Date lastOnline;
	private boolean isDirty = false;
	
	private ArrayList<String> quotes = new ArrayList<String>();
	private ArrayList<String> reminders = new ArrayList<String>();
	public User(String user, String host)
	{
		this.user = user;
		this.host = host;
	}
	public void incMsgSent(String host)
	{
		msgSent++;
		lastOnline = new Date();
		checkDirtyness(host);
	}
	public void incQuits()
	{
		quits++;
	}
	public void incjoins(String host)
	{
		joins++;
		checkDirtyness(host);
	}
	private void checkDirtyness(String host)
	{
		if (reminders.size() > 0)
			isDirty = true;
		else
			isDirty = false;
		
		if (!this.host.equals(host))
		{
			this.host = host;
			isDirty = true;
		}
	}
	
	public String[] getQuotes()
	{
		return quotes.toArray(new String[0]);
	}
	public void addQuote(String message)
	{
		quotes.add(message);
	}
	public void addReminder(String message)
	{
		reminders.add(message);
	}
	public void delQuote(String message)
	{
		quotes.remove(message);
	}
	public void setUser(String user)
	{
		this.user = user;
	}
	
	
	public String getUser()
	{
		return user;
	}
	public long getMsgSent()
	{
		return msgSent;
	}
	public Date getLastOnline()
	{
		return lastOnline;
	}
	public long getJoins()
	{
		return joins;
	}
	public long getQuits()
	{
		return quits;
	}
	public boolean isDirty()
	{
		return isDirty;
	}
	public String[] getReminders()
	{
		String[] reminderss = reminders.toArray(new String[0]);
		reminders = new ArrayList<String>();
		return reminderss;
	}
	
}