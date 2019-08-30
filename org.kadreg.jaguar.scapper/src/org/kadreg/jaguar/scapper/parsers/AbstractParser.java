package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
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
	
	static {
//		cookiesMap.put("phpbb3_hwrqg_k", "XXXXXXXXXXXXXXXX");
//		cookiesMap.put("phpbb3_hwrqg_sid", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
//		cookiesMap.put("phpbb3_hwrqg_u", "ZZZZ");
		
	}

	public abstract void parse () throws IOException;
	
	private Connection getConnection (String url) {
		return Jsoup.connect(url).userAgent("Mozilla/ jsoup").cookies (cookiesMap);
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
