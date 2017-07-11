package com.shkabo.minions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Minion {

	private String url;
	private String protocol;
	private String collectionSize;
	private ArrayList<String> collectionItems;
	private String collectionName;
	private String page;

	// constructor and set url and port to work with
	// @TODO: try catch if url is not set
	public Minion(String url) {
		this.url = url;
		//first let's get our page!
		this.readUrl();
		if (this.page.isEmpty()) {
            System.out.println("There was an error fetching the page ....");
            System.exit(0);
        }
		// check collection name
		this.getCollectionName( this.page );

		//TODO: finish this part of the workflow !
	}

	/**
	 * Read URL and return html as string
	 * 
	 * @return String
	 */
	private void readUrl() {

		String result = null;
		try {
			
			URLConnection address = new URL(this.url).openConnection();
			address.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			// set protocol
			this.protocol = address.getURL().getProtocol();
			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(address.getInputStream(), Charset.forName("UTF-8")));

			String line = null;
			StringBuilder sb = new StringBuilder();

			// read line by line and build html string
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			result = sb.toString();
			this.page = result;
		}  catch (IOException e) {
			System.err.println(e);
		}
	}

    /**
     * Get collection size
     * @param html
     */
	private void getCollection(String html) {
		Document doc = Jsoup.parse(html);
		Elements collection_num = doc.select(".col-xs-12 .panel h2.panel-title");
        String size = "";
		for (Element elem : collection_num) {

            size = elem.childNode(0).toString().split(" ")[0];
        }
		System.out.println("This collection has: " + size + " items.");
		this.collectionSize = size;
	}

    /**
     * Get collection name for future references
     * @param html
     */
	private void getCollectionName(String html) {
		Document doc = Jsoup.parse(html);
		Elements collection = doc.select("li > strong");
		String name = "";
		for (Element elem : collection) {
			name = elem.childNode(0).toString().trim().replace("«","").replace("»","");
		}
		System.out.println("Collection name: " + name);
		this.collectionName = name;
	}

}
