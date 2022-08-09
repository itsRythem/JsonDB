package dev.rythem.api.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonDatabase {

	/**
	 * database file
	 */
	private final File file;
	
	/**
	 * parser instance
	 */
	private final JsonParser parser;
	
	/**
	 * builder instance
	 */
	private final GsonBuilder gsonBuilder;
	
	/**
	 * gson instance for converting obj to string
	 */
	private final Gson builder;
	
	/**
	 * Array users are stored in temporarily
	 */
	private JsonArray users = new JsonArray();
	
	/**
	 * contructor for calling init method and updating array with already existing users
	 * @param file
	 */
	public JsonDatabase(final File file)
	{
		this.file = file;
		this.parser = new JsonParser();
		this.gsonBuilder = new GsonBuilder();
		this.builder = gsonBuilder.setPrettyPrinting().create();
	
		this.init();
	}
	
	/**
	 * Initializes the database
	 */
	private void init()
	{
		if(this.file.length() == 0)
		{
			final JsonObject obj = new JsonObject();
			final JsonArray users = new JsonArray();
			
			obj.add("users", users);
			
			try {
				final FileWriter writer = new FileWriter(this.file);
				writer.write(builder.toJson(obj));
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			final JsonElement element = this.parser.parse(new FileReader(this.file));
			if(element.isJsonObject())
			{
				final JsonObject obj = element.getAsJsonObject();
				if(obj.has("users"))
				{
					final JsonArray users = obj.get("users").getAsJsonArray();
					this.users = users;
				}
			}
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a user to the database
	 * @param token
	 * @param username
	 */
	public void addUser(final String token, final String username)
	{
		final JsonObject obj = new JsonObject();
		final JsonObject data = new JsonObject();
		data.addProperty("username", username);
		obj.add(token, data);
		
		this.users.add(obj);
		this.update();
	}
	
	/**
	 * removes a user from the database
	 * @param token
	 */
	public void removeUser(final String token)
	{
		for(int i = 0; i < this.users.size(); i++)
		{
			final JsonObject obj = this.users.get(i).getAsJsonObject();
			String key = obj.keySet().toString();
			key = key.substring(1, key.length() - 1);
		
			if(key.equals(token))
				this.users.remove(i);
		}
		this.update();
	}
	
	/**
	 * Checks if a user exists within the database
	 * @param token
	 * @return
	 */
	public boolean hasUser(final String token)
	{
		for(int i = 0; i < this.users.size(); i++)
		{
			final JsonObject obj = this.users.get(i).getAsJsonObject();
			String key = obj.keySet().toString();
			key = key.substring(1, key.length() - 1);
		
			if(key.equals(token))
				return true;
		}
		
		return false;
	}
	
	/**
	 * gets a user from the database in json object form
	 * @param token
	 * @return
	 */
	public JsonObject getUser(final String token)
	{
		for(int i = 0; i < this.users.size(); i++)
		{
			final JsonObject obj = this.users.get(i).getAsJsonObject();
			String key = obj.keySet().toString();
			key = key.substring(1, key.length() - 1);
		
			if(key.equals(token))
			{
				return this.users.get(i).getAsJsonObject().get(token).getAsJsonObject();
			}
		}
		
		return null;
	}
	
	/**
	 * updates the database
	 */
	public void update()
	{
		final JsonObject obj = new JsonObject();
		obj.add("users", this.users);
		
		try {
			final FileWriter writer = new FileWriter(this.file);
			writer.write(builder.toJson(obj));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
