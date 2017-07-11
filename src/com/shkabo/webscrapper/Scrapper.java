package com.shkabo.webscrapper;

import com.shkabo.minions.Minion;

public class Scrapper {

	public static void main(String[] args) {
		Minion edd = new Minion("http://newwalls.as-creation.com/en/collections/376500.html");
		System.out.println("Request sent! Fetching URL\n\r");
		String url = edd.readUrl();
		System.out.println(edd.getCollection(url));
		edd.getCollectionName(url);
	}
}