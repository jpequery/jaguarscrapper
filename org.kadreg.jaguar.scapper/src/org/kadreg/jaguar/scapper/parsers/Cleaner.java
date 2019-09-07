package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * le cleaner est un parser qui ne parse rien, mais est capable de remettre une base de donnée phpBB a son état initial.
 * @author Jérôme
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
			statement.addBatch ("DELETE FROM phpbb_forums WHERE phpbb_forums.forum_id > 2");
			statement.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
