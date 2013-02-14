package run;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class IRC
{
	private static DataOutputStream outToServer;
	private static BufferedReader inFromServer;
	private static Socket clientSocket;
	
	public static void connectServer(String server, int port) throws UnknownHostException, IOException
	{
		clientSocket = new Socket(server, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	public static void sendServer(String in_str)
	{
			try
			{
				outToServer.writeBytes(in_str + '\n');
				System.out.println("->" +in_str);
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public static String getFromServer()
	{
		try
		{
			String out = inFromServer.readLine();
			System.out.println("<-" + out);
			return out;
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void closeConnection() throws IOException
	{
		clientSocket.close();
	}
}
