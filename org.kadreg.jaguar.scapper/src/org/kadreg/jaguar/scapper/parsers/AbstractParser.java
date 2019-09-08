package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class AbstractParser {
	protected AbstractParser parentParser; 
	
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
	
	public AbstractParser (AbstractParser parent) {
		parentParser = parent;
	}
	
	private Connection getConnection (String url) {
		return Jsoup.connect(url).userAgent("Mozilla/ jsoup").cookies (cookiesMap);
	}
	
	public static java.sql.Connection getJDBCConnection () {
		if (_jdbcConnection == null) {
			try {
				_jdbcConnection = DriverManager.getConnection("jdbc:mysql://" + MYSQL_SERVER + "/"+DATABASE_NAME+"?" +
					                                   "user="+DATABASE_USER+"&password="+DATABASE_PWD+"&serverTimezone=UTC");
			} catch (SQLException e) {
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
	
	protected java.util.Date normalizeDate(String date) throws ParseException {
		try {
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		int indexDeuxPoint = date.indexOf(':');
		
		if (date.startsWith("il y a ")) {
			int num = Integer.valueOf(date.substring(7, 9));
			LocalDate t = today.minus(num, ChronoUnit.MINUTES);
			return new java.sql.Date (t.toEpochDay());
		}
		
		int minutes = Integer.valueOf(date.substring(indexDeuxPoint+1));
		int heures = Integer.valueOf(date.substring(indexDeuxPoint-2, indexDeuxPoint));
		
		
		if (date.startsWith("Hier")) {
			Timestamp result = new Timestamp(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), heures, minutes, 0, 0);
			return new java.sql.Date (result.toInstant().getEpochSecond());
		} else if (date.startsWith("Aujourd’hui")) { // mauvais apostrophe :/
			Timestamp result = new Timestamp(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), heures, minutes, 0, 0);
			return new java.sql.Date (result.toInstant().getEpochSecond());
		}
		
		int day = Integer.valueOf(date.substring(0, 2));
		int month = getMonthNumber (date.substring(3, 7));
		int year = Integer.valueOf(date.substring(7+(month==6||month==7?1:0), 11+(month==6||month==7?1:0)));
		Timestamp result = new Timestamp(year, month, day, heures, minutes, 0, 0);
		return new java.sql.Date (result.toInstant().getEpochSecond());
		} catch (NumberFormatException e) {
			throw e;
		}
	}

	private int getMonthNumber(String s) {
		if (s.startsWith("Jan")) return 1;
		if (s.startsWith("Fév")) return 2;
		if (s.startsWith("Mar")) return 3;
		if (s.startsWith("Avr")) return 4;
		if (s.startsWith("Mai")) return 5;
		if (s.startsWith("Juin")) return 6;
		if (s.startsWith("Juil")) return 7;
		if (s.startsWith("Aoû")) return 8;
		if (s.startsWith("Sep")) return 9;
		if (s.startsWith("Oct")) return 10;
		if (s.startsWith("Nov")) return 11;
		if (s.startsWith("Déc")) return 12;
		throw new RuntimeException("Unknown month "+ s);
	}

}
