package org.kadreg.jaguar.scapper.parsers;

import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ForumParser extends AbstractParser {

	// the document of the parsed page
	private Document doc;

	// the baseURI of the site being pparsed
	private String base;

	private String padding = "";

	/**
	 * Entry point forum parser, for root forums
	 * @param url
	 * @throws IOException
	 */
	public ForumParser(String url) throws IOException {
		super (null);
		base = url;
		doc = getDocument(url);
	}

	public ForumParser(AbstractParser parent, String base2, String href, String padding2) throws IOException {
		super (parent);
		base = base2;
		doc = getDocument(base2 + href);
		padding = padding2;
	}

	public void parse() throws IOException {
		String id = getForumId ();
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
				ForumParser parser = new ForumParser(this, base, suivant, padding);
				parser.parse();
			} catch (HttpStatusException e) {
				System.err.println(e.getMessage());
			}			
		}
		} catch (BadScrappingException e) {
			e.printStackTrace();
		}
	}

	private String getForumId() {
		Elements options = doc.select("form#jumpbox option");
		for (Element option : options) {
			if (option.hasAttr("selected")) {
				return option.attr("value");
			}
		}
		return "0";
	}

	private void parseTopic(String href, String topicName) throws IOException {
		try {
			TopicParser parser = new TopicParser(this, base, href, padding + "    ");
			parser.parse();
		} catch (HttpStatusException e) {
			System.err.println(e.getMessage());
		}
	}

	private void parseSubForum(String href) throws IOException {
		try {
			ForumParser parser = new ForumParser(this, base, href, padding + "    ");
			parser.parse();
		} catch (HttpStatusException e) {
			System.err.println(e.getMessage());
		}
	}

}
