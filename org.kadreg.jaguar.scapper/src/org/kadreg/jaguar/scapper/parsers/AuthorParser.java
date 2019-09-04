package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.jsoup.nodes.Document;

public class AuthorParser extends AbstractParser {

	private static AuthorParser instance;
	
	private Map<String, Object> users = new HashMap<String, Object>();  

	@Override
	public void parse() throws IOException {
		throw new IOException(new OperationNotSupportedException());
	}

	
	public void parse(String authorHref) throws IOException {
		Document doc = getDocument(authorHref);
		Connection jdbc = getJDBCConnection();
		
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
			try {
				parse (authorHref);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
