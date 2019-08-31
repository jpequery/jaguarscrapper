package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TopicParser extends AbstractParser {

	private String base;
	Document doc;
	private String padding;

	public TopicParser(String base, String href, String padding) throws IOException {
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
			String author = post.select("p.author a").text();
			String authorHref = post.select("p.author a").attr("href");
			String content = post.select("d.content").text();
			String date = post.select("p.author").text();
			
			AuthorParser.getInstance().author (author, base + authorHref);
//			System.out.println(padding + "  post de " + author);
		}
;
		try {
			String suivant = getSuivantLink(doc);
			if (suivant != null) {
				try {
					TopicParser parser = new TopicParser(base, suivant, padding);
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
