package core.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


/**
 * This class handles all interaction with the IRC client and remote server
 * @author Tom Rosier (XeTK)
 */
public class IRC
{
	//We are using the singleton pattern
	private static IRC instance;
	
	private PrintWriter outToServer;
	private BufferedReader inFromServer;
	private Socket clientSocket;
	
	/**
	 * To comply with the singleton pattern we return the instance of the object, 
	 * If one already exists otherwise we create a new object.
	 * @return return instance of the IRC class that has already been created
	 */
	public static IRC getInstance()
	{
		if (instance == null)
			instance = new IRC();
		
		return instance;
	}
	
	/**
	 * This is to create are connection to the server, 
	 * we create the connection then we can write to it.
	 * @param server
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connectServer(String server, int port) 
			throws UnknownHostException, IOException
	{
		clientSocket = new Socket(server, port);
		outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		inFromServer = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));
	}
	
	/**
	 * Check if the connection has been created else we throw an exception.
	 * @throws IRCException if there is an error with the connection.
	 */
	private void checkConnection() throws IRCException
	{
		if (instance == null)
			throw new IRCException("IRC class has not yet been declared");
		
		if (clientSocket == null)
			throw new IRCException("Remote Socket has not been opened");
		
		if (outToServer == null)
			throw new IRCException("Stream to server has not been opened");
		
		if (inFromServer == null)
			throw new IRCException("Stream in from server has not been opened");
	}
	
	/**
	 * This is the method of sending data to the server, 
	 * through the already open connection.
	 * @param in_str Is the information that is sent to the open server connection
	 * @throws IRCException is thrown if we have a problem during the transition
	 * @throws IOException is thrown if we cannot write the data out to the server
	 */
	public void sendServer(String in_str) throws IRCException, IOException
	{
			checkConnection();
			outToServer.write(in_str + "\r\n");
			outToServer.flush();
			System.out.println("\u001B[34m<- " +in_str);
	}
	
	/**
	 * Makes the privmsg stick to the IRC Standard, 
	 * along with shortening long server messages
	 * @param channel this is the place the message will be sent to.
	 * @param message this is the message.
	 * @throws IOException 
	 * @throws IRCException 
	 */
	public void sendPrivmsg(String channel, String message) 
			throws IRCException, IOException
	{
		sendServer("PRIVMSG " + channel + " :" + message);
	}
	
	/**
	 * This method gets the data from the server 
	 * and returns it to be used within the application
	 * @return this returns the string in from the server
	 * @throws IRCException is thrown if we are not ready for the input
	 * @throws IOException is where we can't receive the data from the server
	 */
	public String getFromServer() throws IRCException, IOException
	{
			checkConnection();
			String out = inFromServer.readLine();
			System.out.println("\u001B[31m-> " + out);
			return out;
	}
	
	/**
	 * When all is done then we close the connection to the server
	 * @throws IOException is thrown if there was a problem closing the connection
	 */
	public void closeConnection() throws IOException
	{
		clientSocket.close();
	}
}
