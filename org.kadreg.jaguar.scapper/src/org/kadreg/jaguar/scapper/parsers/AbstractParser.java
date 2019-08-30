package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public abstract class AbstractParser {
	static Map<String,String> cookiesMap = new HashMap<String, String>();
	
	static {
		cookiesMap.put("phpbb3_hwrqg_k", "XXXXXXXXXXXXXXXX");
		cookiesMap.put("phpbb3_hwrqg_sid", "YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
		cookiesMap.put("phpbb3_hwrqg_u", "ZZZZ");
		
	}

	public abstract void parse () throws IOException;
	
	Connection getConnection (String url) {
		return Jsoup.connect(url).userAgent("Mozilla").cookies (cookiesMap);
	}
}
