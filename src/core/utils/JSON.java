package core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

/**
 * This is a static class to save and load JSON objects out from the application
 * for later user, this is vital for the database functionality of this application.
 * @author Tom Rosier(XeTK)
 */
public class JSON
{
	/**
	 * Converts a file back into a JSON object and eventually into a usable class object.
	 * @param in_Path this is the file location of the JSON object that we want to load in.
	 * @param classdef this is the class we want to convert the object back into.
	 * @return this is the object we want at the end with all the information we need.
	 * @throws IOException this is for if the file was read incorrectly.
	 */
	public static Object loadGSON(String in_Path, Class<?> classdef) throws IOException
	{
		
		BufferedReader reader = new BufferedReader(new FileReader(in_Path));
		
		// Keep the current line we are working on, along with tabs of the whole file.
		String tempWord = "", json = "";
		
		// Loop through the file till we reach the end adding each line as we go.
		while ((tempWord = reader.readLine()) != null)
			json += tempWord + "\n";
		
		// Close the file read to prevent us problems late.
		reader.close();
		
		// Finally convert that text into a class object that we can actually use.
		return new Gson().fromJson(json, classdef);
	}
	
	/**
	 * This saves any object out to a JSON object so we can preserve the data for later use.
	 * @param in_Path this is the location we want to save the file to.
	 * @param saveable this is the object that we want to save out to the file.
	 * @throws IOException if we have a error during the saving of the object.
	 */
	public static void saveGSON(String in_Path, Object saveable) throws IOException
	{
		File filePath = new File(in_Path);
	
		// Check if the file exists and the path is there else create it.
		if (!filePath.exists())
			filePath.createNewFile();
		else
			filePath.delete();
		
		// Now we open the file for writing
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		
		// Convert from a object into some JSON text which then gets written to the file
		writer.write(new Gson().toJson(saveable));
		
		// Close the file so it can be accessed from another place. 
		writer.close();
		
		// Print out that we have saved the object.
		System.out.println("\u001B[33mJSON Saved: " + in_Path);
	}
}