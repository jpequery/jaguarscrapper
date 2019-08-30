package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthorParser extends AbstractParser {

	private static AuthorParser instance;
	
	private Map<String, Object> users = new HashMap<String, Object>();  

	@Override
	public void parse() throws IOException {
		// TODO Auto-generated method stub

	}

	public static AuthorParser getInstance() {
		if (instance == null) {
			instance = new AuthorParser();
		}
		return instance;
	}

	public void author(String author, String authorHref) {
		if (users.get(author) == null) {
			// parsing de l'auteur
			users.put(author, authorHref);
		}
		
	}

}
