package com.shkabo.minions;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;

public class Minion {

	private String url;
	private String protocol;
	private Integer collectionSize = 0;
	private List<TapetItem> collectionItems = new ArrayList<>();
	private String collectionName;
	private String page;
	private int numPages = 0;
	private ArrayList<String> collectionPages = new ArrayList<>();

	// constructor and set url and port to work with
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

		// check number of items in collection
        this.getCollectionSize( this.page );

        if (this.collectionSize == 0 ) {
            System.out.println("There are no items in this collection. Sorry :(");
            System.exit(0);
        }

        // collect items from the page
        this.getCollectionItems( this.page );

        // check if collection has more pages ?
        this.getCollectionPages( this.page );

        // if we have more pages let's check items on them !
        if (this.numPages > 0) {
            // get only unique items from the list !
            Set<String> uniqPages = new HashSet<String>(this.collectionPages);

            for (String s : uniqPages) {
                this.url = "http://newwalls.as-creation.com" + s;
                this.readUrl();
                this.getCollectionSize( this.page );
                this.getCollectionItems( this.page );
            }

        } else {
            // get collection items so we can later process them
            this.getCollectionItems( this.page );
        }

        System.out.println("no items: "+this.collectionSize);
        System.out.println("items: " + this.collectionItems.size());
        // now that we have everything
		// wee need to collect each set
        // but first let's create a folder of this collection !
        try {
            new File( this.collectionName ).mkdirs();
        } catch (SecurityException se) {
            System.out.println("An error occured while creating collection directory: " + se.getMessage());
        }

        // now we visit each item, create it's folder, download images !
        // main image should be 900x720
        // other images should be width 900px, height corresponding to width
        for ( TapetItem item : this.collectionItems ) {
            System.out.println("Getting item: " + item.title );

            try {
               new File( this.collectionName + File.separator + item.title ).mkdirs();
            } catch (SecurityException se) {
                System.out.println("An error occured while creating collection item directory: " + se.getMessage());
            }
            // check url !
            this.url = "http://newwalls.as-creation.com" + item.link;
            System.out.println(this.url);
            this.readUrl();
           //System.out.println(Paths.get(this.collectionName + File.separator + item.title));
            // let's get items
            this.getItemImages( this.page, this.collectionName + File.separator + item.title, item.title);
        }


		// make new folder with their name and id
		// download images in it
		// optional resize those images
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
			
			URLConnection address = new URL( this.url ).openConnection();
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
	private void getCollectionSize(String html) {
		Document doc = Jsoup.parse(html);
		Elements collection_num = doc.select(".col-xs-12 .panel h2.panel-title");
        String size = "";
		for (Element elem : collection_num) {

            size = elem.childNode(0).toString().split(" ")[0];
        }
		this.collectionSize += Integer.parseInt(size);
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
		this.collectionName = name;
	}

    /**
     * Get collection items
     * @param html
     */
	private void getCollectionItems(String html) {
	    Document doc = Jsoup.parse(html);
	    Elements items = doc.select("div.col-xs-4.col-sm-3 > a.thumbnail[title]");
		for (Element item : items) {
		    TapetItem tapet = new TapetItem();
		    tapet.title = item.attr("title");
		    tapet.link = item.attr("href");
            this.collectionItems.add(tapet);
		}
    }

    /**
     * Check if we have multiple pages for our collection
     * @param html
     */
    private void getCollectionPages(String html) {
	    Document doc = Jsoup.parse(html);
	    Elements pages = doc.select("ul.pagination > li > a[href]");
        this.numPages = pages.size();
        for (Element page : pages) {
            this.collectionPages.add(page.attr("href"));
        }
    }

    private void getItemImages(String html, String path, String filename) {
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("div.thumbBarInner > a[href]");
        int i = 0;
        for (Element image : images) {
            System.out.println("processing  image: " + i);
            i++;
            this.saveImage(image.attr("href"), path, filename + " ("+i+").jpg");
        }
    }

    private void saveImage( String url, String path, String filename) {
        try {
            InputStream in = new URL( "http:"+url ).openStream();
            Files.copy(in, Paths.get(path + File.separator + filename));
        } catch (MalformedURLException ex) {
            System.out.println("Couldn't download image. Wrong or bad url: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("An Error occured while downloading image. " + ex.getMessage());
        }
    }

}
