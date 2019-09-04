package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class AbstractParser {
	static Map<String,String> cookiesMap = new HashMap<String, String>();
	private static java.sql.Connection _jdbcConnection;

	private static String DATABASE_NAME = ConfigManager.getInstance().getDatabase();
	private static final String MYSQL_SERVER = ConfigManager.getInstance().getServer ();
	private static final String DATABASE_USER=ConfigManager.getInstance().getUser();
	private static final String DATABASE_PWD=ConfigManager.getInstance().getPassword();
	
	
	static {
		cookiesMap.put("phpbb3_hwrqg_k", ConfigManager.getInstance().getK());
		cookiesMap.put("phpbb3_hwrqg_sid", ConfigManager.getInstance().getSid());
		cookiesMap.put("phpbb3_hwrqg_u", ConfigManager.getInstance().getU());		
	}

	public abstract void parse () throws IOException;
	
	private Connection getConnection (String url) {
		return Jsoup.connect(url).userAgent("Mozilla/ jsoup").cookies (cookiesMap);
	}
	
	public static java.sql.Connection getJDBCConnection () {
		if (_jdbcConnection == null) {
			try {
				_jdbcConnection = DriverManager.getConnection("jdbc:mysql://" + MYSQL_SERVER + "/"+DATABASE_NAME+"?" +
					                                   "user="+DATABASE_USER+"&password="+DATABASE_PWD+"&serverTimezone=UTC");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return _jdbcConnection;
	}
	
	protected Document getDocument (String url) {
		Connection connection = getConnection(url);
		while (true) {
			try {
				Document doc = connection.get();
				return doc;
			} catch (HttpStatusException e) {
				System.err.println("error while getting " + url + " " + e.getMessage());
				return null;
			} catch (IOException e) {
				System.err.println("error while getting " + url + " waiting 5 seconds");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {	}
			}
		}
	}
	
	protected String getSuivantLink (Document doc) throws BadScrappingException {
		Elements nodes = doc.select ("fieldset.display-options a");
		if (nodes.size () == 0) {
			return null;
		} else {
			// looking for Suivant
			for (Element element : nodes) {
				if (element.text().equals("Suivante")) {
					return element.attr("href");
				}
			}
			return null;
		}
	}
}
