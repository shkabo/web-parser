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

	// constructor and set url and port to work with
	// @TODO: try catch if url is not set
	public Minion(String url) {
		this.url = url;
	}

	/**
	 * Read URL and return html as string
	 * 
	 * @return String
	 */
	public String readUrl() {

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

		} catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		return result;

	}

	/**
	 * Parse html and get all a href tags and href values
	 * 
	 * @param html
	 * @return String
	 */
	public void getLinks(String html) {
		// parse html and make a list of links
		Document doc = Jsoup.parse(html);
		Elements links = doc.select("a[href]");
		ArrayList<String> str = new ArrayList<>();

		for (Element element : links) {
			str.add(element.attr("href"));
		}
		// make a hash with unique values
		Set<String> set = new HashSet<>();
		for (String string : str) {
			set.add(string);
		}
		
		// dump sorted result to console
		Iterator<String> it = set.stream().sorted().collect(Collectors.toList()).iterator();
		while (it.hasNext()) {
			String tmp = this.fixLink(it.next());
			if (tmp.startsWith(this.protocol)) {
				System.out.println(tmp);
			}

		}
	}
	/**
	 * Change relative to absolute links
	 * 
	 * @param link String
	 * @return String
	 */
	private String fixLink(String link) {
		
		if (link.startsWith("/")) {
			return this.url + link;
		} else if (link.startsWith("#")) {
			return this.url +  "/" + link; 
		} else if (link.startsWith("//")) {
			return this.protocol + ":" +link;
		} else {
			return link;
		}
	}
	
	// TODO: proveriti zasto twitch nece da parsuje kako treba !
	// 10.07.2017. Treba ti phantomjs konje :)

    /**
     * Get collection size
     * @param html
     * @return
     */
	public String getCollection(String html) {
		Document doc = Jsoup.parse(html);
		Elements collection_num = doc.select(".col-xs-12 .panel h2.panel-title");
        String size = "";
		for (Element elem : collection_num) {

            size = elem.childNode(0).toString().split(" ")[0];
        }
        this.collectionSize = size;
        return size;
	}


}
