package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
	private static ConfigManager _instance; // Singleton

	private Properties prop;
	
	public final String propFileName = "scrap.properties";
	
	private ConfigManager () {
		prop = new Properties();
		
		try {
			InputStream iStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			prop.load (iStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println ("error loading scrap.properties file");
			e.printStackTrace();
		}
	}
	
	public static ConfigManager getInstance() {
		if (_instance == null) {
			_instance = new ConfigManager();
		}
		return _instance;
	}

	public String getSid() {
		return prop.getProperty("sid");
	}

	public String getK() {
		return prop.getProperty("k");
	}
	public String getU() {
		return prop.getProperty("u");
	}

	public String getDatabase() {
		return prop.getProperty("database");
	}

	public String getUser() {
		return prop.getProperty("user");
	}

	public String getServer() {
		return prop.getProperty("server");
	}

	public String getPassword() {
		return prop.getProperty("password");
	}
	
}
