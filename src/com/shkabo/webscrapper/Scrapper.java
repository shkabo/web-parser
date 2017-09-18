package com.shkabo.webscrapper;

import com.shkabo.minions.Minion;
import java.util.ArrayList;

public class Scrapper {

	public static void main(String[] args) {
		String[] links = new String[] {
				"http://newwalls.as-creation.com/en/collections/201150.html",
				"http://newwalls.as-creation.com/en/collections/201232.html",
				"http://newwalls.as-creation.com/en/collections/351600.html"
		};

		for(String link : links) {
			Minion edd = new Minion(link);
		}
		//Minion edd = new Minion("http://newwalls.as-creation.com/en/collections/201150.html");
		//System.out.println("Request sent! Fetching URL\n\r");
 		//http://newwalls.as-creation.com/en/collections/376500.html rust
		//http://newwalls.as-creation.com/en/collections/370700.html kind of white
	}
}