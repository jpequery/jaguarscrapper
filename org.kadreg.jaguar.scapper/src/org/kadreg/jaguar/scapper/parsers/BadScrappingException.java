package org.kadreg.jaguar.scapper.parsers;

public class BadScrappingException extends Exception {

	private String uri;

	public BadScrappingException(String baseUri) {
		uri = baseUri;
	}

}
