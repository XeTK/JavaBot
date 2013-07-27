package core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * This class handles all interaction with the IRC client and remote server
 * 
 * @author Tom Rosier (XeTK)
 */
public class IRC {
	// This is the maximum size that a message can be that is sent.
	private final int MSG_MAX_SIZE_ = 410;

	// We are using the singleton pattern
	private static IRC instance_;

	private PrintWriter outToServer_;
	private BufferedReader inFromServer_;
	private Socket clientSocket_;

	/**
	 * To comply with the singleton pattern we return the instance of the
	 * object, If one already exists otherwise we create a new object.
	 * 
	 * @return return instance of the IRC class that has already been created
	 */
	public static IRC getInstance() {
		if (instance_ == null)
			instance_ = new IRC();

		return instance_;
	}

	/**
	 * This is to create are connection to the server, we create the connection
	 * then we can write to it.
	 * 
	 * @param server
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connectServer(String server, int port)
			throws UnknownHostException, IOException {
		clientSocket_ = new Socket(server, port);
		outToServer_ = new PrintWriter(new OutputStreamWriter(
				clientSocket_.getOutputStream()));
		inFromServer_ = new BufferedReader(new InputStreamReader(
				clientSocket_.getInputStream()));
	}

	/**
	 * Check if the connection has been created else we throw an exception.
	 * 
	 * @throws IRCException
	 *             if there is an error with the connection.
	 */
	private void checkConnection() throws IRCException {
		if (instance_ == null)
			throw new IRCException("IRC class has not yet been declared");

		if (clientSocket_ == null)
			throw new IRCException("Remote Socket has not been opened");

		if (outToServer_ == null)
			throw new IRCException("Stream to server has not been opened");

		if (inFromServer_ == null)
			throw new IRCException("Stream in from server has not been opened");
	}

	/**
	 * This is the method of sending data to the server, through the already
	 * open connection.
	 * 
	 * @param instr
	 *            Is the information that is sent to the open server connection
	 * @throws IRCException
	 *             is thrown if we have a problem during the transition
	 * @throws IOException
	 *             is thrown if we cannot write the data out to the server
	 */
	public void sendServer(String instr) throws IRCException, IOException {
		checkConnection();
		outToServer_.write(instr + "\r\n");
		outToServer_.flush();
		System.out.println("\u001B[34m<- " + instr);
	}

	/**
	 * Makes the privmsg stick to the IRC Standard, along with shortening long
	 * server messages
	 * 
	 * @param channel
	 *            this is the place the message will be sent to.
	 * @param message
	 *            this is the message.
	 * @throws IOException
	 * @throws IRCException
	 */
	public void sendPrivmsg(String channel, String message)
			throws IRCException, IOException {
		String[] lines = message.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String[] subLines = breakLongLines(lines[i]);
			for (int j = 0; j < subLines.length; j++)
				sendServer("PRIVMSG " + channel + " :" + subLines[j]);
		}
	}

	/**
	 * This a method for sending /me message as the bot.
	 * 
	 * @param channel
	 *            this is the destination of the message.
	 * @param message
	 *            this is the text of the message.
	 * @throws IRCException
	 * @throws IOException
	 */
	public void sendActionMsg(String channel, String message)
			throws IRCException, IOException {
		sendPrivmsg(channel, '\001' + "ACTION " + message + '\001');
	}

	/**
	 * This method gets the data from the server and returns it to be used
	 * within the application
	 * 
	 * @return this returns the string in from the server
	 * @throws IRCException
	 *             is thrown if we are not ready for the input
	 * @throws IOException
	 *             is where we can't receive the data from the server
	 */
	public String getFromServer() throws IRCException, IOException {
		checkConnection();
		String out = inFromServer_.readLine();
		System.out.println("\u001B[31m-> " + out);
		return out;
	}

	/**
	 * When all is done then we close the connection to the server
	 * 
	 * @throws IOException
	 *             is thrown if there was a problem closing the connection
	 */
	public void closeConnection() throws IOException {
		clientSocket_.close();
	}

	/**
	 * This breaks down long messages so that they can be sent via IRC.
	 * 
	 * @param inStr
	 *            this is the text we want to break down into small chunks.
	 * @return this is the broken down string.
	 */
	private String[] breakLongLines(String inStr) {
		ArrayList<String> splitStr = new ArrayList<String>();
		String buffer = inStr;
		while (true) {
			if (buffer.length() >= MSG_MAX_SIZE_) {
				int lastSpace = 0;
				for (int i = MSG_MAX_SIZE_; i > 0; i--) {
					if (buffer.charAt(i) == ' ') {
						lastSpace = i;
						break;
					}
				}
				splitStr.add(buffer.substring(0, lastSpace));
				buffer = buffer.substring(lastSpace + 1);
			} else {
				splitStr.add(buffer);
				break;
			}
		}
		return splitStr.toArray(new String[0]);
	}
}
