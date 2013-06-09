package program;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

public class JSON
{
	public static Object loadGSON(String in_Path, Class<?> classdef) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(in_Path));
		
		String tempWord = "", json = "";
		
		while ((tempWord = reader.readLine()) != null)
			json += tempWord + "\n";
		
		reader.close();
		
		return new Gson().fromJson(json, classdef);
	}
	
	public static void saveGSON(String in_Path, Object saveable) throws IOException
	{
		File filePath = new File(in_Path);
		
		if (!filePath.exists())
			filePath.createNewFile();
		else
			filePath.delete();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		writer.write(new Gson().toJson(saveable));
		writer.close();
		System.out.println("\u001B[33mJSON Saved: " + in_Path);
	}
}
