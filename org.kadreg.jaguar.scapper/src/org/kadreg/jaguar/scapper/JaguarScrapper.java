package org.kadreg.jaguar.scapper;

import java.time.Duration;
import java.time.LocalTime;

import org.kadreg.jaguar.scapper.parsers.ForumParser;

public class JaguarScrapper {

	public static void main(String[] args) {
		LocalTime start = LocalTime.now();
 
		try {			
			ForumParser parser = new ForumParser ("http://passion-jaguar.forumprod.com/");
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LocalTime end = LocalTime.now();
		System.out.println("Scrapping duration in seconds : " + Duration.between(start, end).getSeconds());
	}

}
