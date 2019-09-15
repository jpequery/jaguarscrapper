package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TopicParser extends AbstractParser {

	private String base;
	Document doc;
	private String padding;

	private final SimpleDateFormat format = new SimpleDateFormat ("dd MMM YYYY, hh:mm");
	private boolean firstPage;
	
	public TopicParser(AbstractParser parent, String base, String href, String padding, boolean firstPage) throws IOException {
		super (parent);
		this.base = base;
		doc = getDocument(base + href);
		this.padding = padding;
		this.firstPage = firstPage;
	}

	@Override
	public void parse() throws IOException {
		if (doc == null) return; // problème de document invalide
		
		
		if (firstPage) convert ();
		boolean firstpost = firstPage;
		// <div class="postbody">
		//     <p class="author">
		//     <div class="content">
		Elements posts = doc.select ("div.postBody");
		for (Element post : posts) {
			convertPost (post, firstpost, posts.last().equals(post));
			firstpost = false;
		}
;
		try {
			String suivant = getSuivantLink(doc);
			if (suivant != null) {
				try {
					TopicParser parser = new TopicParser(parentParser, base, suivant, padding, false);
					parser.parse();
				} catch (HttpStatusException e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (BadScrappingException e) {
			e.printStackTrace();
		}
		
	}


	private void convertPost(Element post, boolean firstpost, boolean lastpost) {
		try {
			String author = post.select("p.author a").text();
			String authorHref = getAuthorUrlFromPost (post);
			String content = post.select("div.content").html();
			String date = post.select("p.author").text();
			String postTitle = post.select("h3.first").text();
			date = date.substring(date.indexOf('»') + 2);
			java.util.Date sqlDate = normalizeDate (date);
			int authorId = AuthorParser.getInstance().author (author, base + authorHref);
			int postId = Integer.valueOf(post.parent().parent().attr("id").substring(1)); // id du post, suppression d'un p devant
			Connection jdbc = getJDBCConnection();
			PreparedStatement statement = jdbc.prepareStatement("INSERT INTO phpbb_posts(post_visibility, post_id, topic_id, forum_id, poster_id, post_time, post_subject, post_text, post_username) "
					+ "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setInt(1, postId);
			statement.setInt(2 , getTopicId());
			statement.setInt(3, ((ForumParser)parentParser).getForumId());
			statement.setInt(4, authorId);
			statement.setLong(5, 0);
			statement.setString(6, postTitle);
			statement.setString(7, content); 	
			statement.setString(8, author); 	
			statement.execute();
			
			if (firstpost) {
				PreparedStatement statementFirstTopic = jdbc.prepareStatement("UPDATE phpbb_topics "
						+ "SET topic_poster=?, topic_first_post_id=?, topic_first_poster_name=? "
						+ "WHERE topic_id=?");
				statementFirstTopic.setInt(1, authorId);
				statementFirstTopic.setInt(2, postId);
				statementFirstTopic.setString(3, author);
				statementFirstTopic.setInt(4, getTopicId());
				statementFirstTopic.execute();
			}
			if (lastpost) {
				PreparedStatement statementLastTopic = jdbc.prepareStatement("UPDATE phpbb_topics "
						+ "SET topic_last_poster_id=?, topic_last_poster_name=?, topic_last_post_id=?, topic_last_post_subject=? "
						+ "WHERE topic_id=?");
				statementLastTopic.setInt(1, authorId);
				statementLastTopic.setString(2, author);
				statementLastTopic.setInt(3, postId);
				statementLastTopic.setString(4, "");
				statementLastTopic.setInt(5, getTopicId());	
				statementLastTopic.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getAuthorUrlFromPost(Element post) {
		Elements elements = post.select("p.author a");
		return elements.last().attr("href");
	}

	private void convert() {
		String topicName = doc.select ("h3.first a").text();
		System.out.println(padding + "topic : " + topicName);

		
		ForumParser forumParser = (ForumParser) parentParser;
		int topicId = getTopicId ();
		
		int topicPoster = 0;
		Date topicDate = new Date (0);
		
		try {
			Connection connect = getJDBCConnection();
			PreparedStatement statement = connect.prepareStatement("INSERT INTO phpbb_topics (topic_visibility, forum_id, topic_id, topic_title, topic_poster, topic_time) "
					+ "VALUES (1, ?, ?, ?, ?, ?)");
			statement.setInt(1, Integer.valueOf(forumParser.getForumId()));
			statement.setInt(2, topicId);
			statement.setString(3, topicName);
			statement.setInt(4, topicPoster);
			statement.setInt(5, 0);
			statement.execute();
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private int getTopicId() {
		Elements elements = doc.select("form#forum-search input");
		for (Element element : elements) {
			if (element.attr("type").equals("hidden")) return Integer.valueOf(element.attr("value"));
		}
		return 0;
	}

}
