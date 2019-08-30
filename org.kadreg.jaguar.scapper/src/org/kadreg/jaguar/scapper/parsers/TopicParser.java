package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;

import javax.print.Doc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TopicParser extends AbstractParser {

	private String base;
	Document doc;
	private String padding;

	public TopicParser(String base, String href, String padding) throws IOException {
		this.base = base;
		doc = getConnection(base + href).get();
		this.padding = padding + "    ";
	}

	@Override
	public void parse() throws IOException {
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
			
			AuthorParser.getInstance().author (author, authorHref);
			System.out.println(padding + "  post de " + author);
		}

		
	}

}
