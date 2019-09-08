package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

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
		
		// <div class="postbody">
		//     <p class="author">
		//     <div class="content">
		Elements posts = doc.select ("div.postBody");
		for (Element post : posts) {
			
			try {
				String author = post.select("p.author a").text();
				String authorHref = post.select("p.author a").attr("href");
				String content = post.select("d.content").text();
				String date = post.select("p.author").text();
				date = date.substring(date.indexOf('»') + 2);
				java.util.Date sqlDate = normalizeDate (date);

				AuthorParser.getInstance().author (author, base + authorHref);
//				System.out.println(padding + "  post de " + author);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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

	private void convert() {
		String topicName = doc.select ("h3.first a").text();
		System.out.println(padding + "topic : " + topicName);

		
		ForumParser forumParser = (ForumParser) parentParser;
		int topicId = getTopicId ();
		
		int topicPoster = 0;
		Date topicDate = new Date (0);
		
		try {
			Connection connect = getJDBCConnection();
			PreparedStatement statement = connect.prepareStatement("INSERT INTO phpbb_topics (forum_id, topic_id, topic_title, topic_poster, topic_time) "
					+ "VALUES (?, ?, ?, ?, ?)");
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
