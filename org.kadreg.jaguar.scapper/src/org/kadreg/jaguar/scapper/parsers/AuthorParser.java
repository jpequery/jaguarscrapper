package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.jsoup.nodes.Document;

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
		
		Connection jdbc = getJDBCConnection();
		try {
			PreparedStatement statement = jdbc.prepareStatement("INSERT INTO phpbb_users (user_id, username, username_clean, user_permissions, user_sig) "+
					"VALUES (?, ?, ?, ?, ?)");
			statement.setInt(1, 0);
			statement.setString(2, username);
			statement.setString(3, username);
			statement.setString(4, "");
			statement.setString(5, "");
			statement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
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
