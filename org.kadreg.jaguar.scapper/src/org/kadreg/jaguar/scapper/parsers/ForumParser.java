package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ForumParser extends AbstractParser {

	// the document of the parsed page
	private Document doc;

	// the baseURI of the site being pparsed
	private String base;

	private String padding = "";

	public ForumParser(String url) throws IOException {
		base = url;
		doc = getDocument(url);
	}

	public ForumParser(String base2, String href, String padding2) throws IOException {
		base = base2;
		doc = getDocument(base2 + href);
		padding = padding2;
	}

	public void parse() throws IOException {
		Elements forumALinkElement = doc.select("a.forumtitle");
		for (org.jsoup.nodes.Element element : forumALinkElement) {
			String href = element.attr("href");
			String forumName = element.text();
			if (href.indexOf('?') != -1) {
				System.out.println(padding + forumName + " " + href.substring(0, href.indexOf('?')));
			} else {
				System.out.println(padding + forumName + " " + href);
			}
			parseSubForum(href);
		}

		// parsing des topics du forum
		Elements topicALinkElement = doc.select("a.topictitle");
		for (org.jsoup.nodes.Element element : topicALinkElement) {
			String href = element.attr("href");
			String topicName = element.text();

			parseTopic(href, topicName);
		}
		// page suivante ?
		try {
		String suivant = getSuivantLink(doc);
		if (suivant != null) {
			try {
				ForumParser parser = new ForumParser(base, suivant, padding);
				parser.parse();
			} catch (HttpStatusException e) {
				System.err.println(e.getMessage());
			}			
		}
		} catch (BadScrappingException e) {
			e.printStackTrace();
		}
	}

	private void parseTopic(String href, String topicName) throws IOException {
		try {
			TopicParser parser = new TopicParser(base, href, padding + "    ");
			parser.parse();
		} catch (HttpStatusException e) {
			System.err.println(e.getMessage());
		}
	}

	private void parseSubForum(String href) throws IOException {
		try {
			ForumParser parser = new ForumParser(base, href, padding + "    ");
			parser.parse();
		} catch (HttpStatusException e) {
			System.err.println(e.getMessage());
		}
	}

}
