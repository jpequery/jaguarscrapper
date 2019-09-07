package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;
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
	
	public TopicParser(AbstractParser parent, String base, String href, String padding) throws IOException {
		super (parent);
		this.base = base;
		doc = getDocument(base + href);
		this.padding = padding;
	}

	@Override
	public void parse() throws IOException {
		if (doc == null) return; // problème de document invalide
		String topicName = doc.select ("h3.first a").text();
		
		System.out.println(padding + "topic : " + topicName);
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
					TopicParser parser = new TopicParser(parentParser, base, suivant, padding);
					parser.parse();
				} catch (HttpStatusException e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (BadScrappingException e) {
			e.printStackTrace();
		}
		
	}

}
