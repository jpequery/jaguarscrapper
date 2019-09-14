package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class AuthorParser extends AbstractParser {

	public AuthorParser() {
		super(null); // the author parser never have a parent
	}



	private static AuthorParser instance;
	
	private Map<String, Integer> users = new HashMap<String, Integer>();  

	@Override
	public void parse() throws IOException {
		throw new IOException(new OperationNotSupportedException());
	}

	
	public int parse (String username, String authorHref) throws IOException {
		Document doc = getDocument(authorHref);
		String strUserId = getUrlParameter (authorHref, "u");
		if (strUserId == "") throw new RuntimeException("error parsing href " + authorHref);
		int userId = Integer.valueOf(strUserId);
		
		Map<String, String> details = parseUsersDetails(doc.select("dl.left-box.details").first());
		String rang = details.get("Rang");
		String localisation = details.get("Localisation");
		String age = details.get("Age");
		String emploi = details.get ("Emploi");
		String interets = details.get ("centres d'intérêts");
		
		String signature = doc.select("div.postbody div.signature").text();
		Connection jdbc = getJDBCConnection();
		try {
			PreparedStatement statement = jdbc.prepareStatement("INSERT INTO phpbb_users (user_id, username, username_clean, user_permissions, user_sig) "+
					"VALUES (?, ?, ?, ?, ?)");
			statement.setInt(1, userId);
			statement.setString(2, username);
			statement.setString(3, cleanUserName (username));
			statement.setString(4, "");
			statement.setString(5, signature);
			statement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private Map<String, String> parseUsersDetails(Element element) {
		Map<String, String> result = new HashMap<String, String>();
		String current = "";
		for (Element child : element.children()) {
			if (child.tagName().equals("dt")) {
				current = child.text().replace(":", "");
			} else if (child.tagName().equals("dd")) {
				result.put (current, child.text());
			} 
		}
		return result;
	}


	private String cleanUserName(String username) {	
		return username.toLowerCase();
	}


	public static AuthorParser getInstance() {
		if (instance == null) {
			instance = new AuthorParser();
		}
		return instance;
	}
	
	

	public int author(String author, String authorHref) {
		if (users.get(author) == null) {
			// parsing de l'auteur
			try {
				parse (author, authorHref);
				users.put(author, 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return users.get(author);
	}

}
