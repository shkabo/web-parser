package com.shkabo.webscrapper;

import com.shkabo.minions.Minion;

public class Scrapper {

	public static void main(String[] args) {
		String[] links = new String[] {
//				"http://newwalls.as-creation.com/en/collections/372400.html", //happy spring
//				"http://newwalls.as-creation.com/en/collections/354700.html", //x-ray
//				"http://newwalls.as-creation.com/en/collections/377400.html", //best of brands
				"http://newwalls.as-creation.com/en/collections/371700.html", //graze - 12 items
//                "http://newwalls.as-creation.com/en/collections/371800.html", //april
//                "http://newwalls.as-creation.com/en/collections/363700.html", //chateau 5

		};

		for(String link : links) {
			Minion edd = new Minion(link);
		}
    }
}