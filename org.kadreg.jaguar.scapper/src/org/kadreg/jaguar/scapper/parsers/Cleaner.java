package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * le cleaner est un parser qui ne parse rien, mais est capable de remettre une base de donn�e phpBB a son �tat initial.
 * @author J�r�me
 *
 */
public class Cleaner extends AbstractParser {

	public Cleaner() {
		super(null);
	}

	@Override
	public void parse() throws IOException {
		Connection jdbc = getJDBCConnection();
		try {
			Statement statement = jdbc.createStatement();
			statement.addBatch ("DELETE FROM phpbb_users WHERE phpbb_users.user_id > 47");
			statement.addBatch ("DELETE FROM phpbb_forums WHERE phpbb_forums.forum_id > 1");
			statement.addBatch ("DELETE FROM phpbb_topics");
			statement.addBatch ("DELETE FROM phpbb_posts");
			statement.addBatch ("DELETE FROM phpbb_acl_groups");
			
			statement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException("unable to clean up databases");
		}
	}

}
