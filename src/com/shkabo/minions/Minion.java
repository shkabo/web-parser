package com.shkabo.minions;

import net.coobird.thumbnailator.Thumbnails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Minion {

	private String url;
	private String protocol;
	private Integer collectionSize = 0;
	private List<TapetItem> collectionItems = new ArrayList<>();
	private String collectionName;
	private String page;
	private int numPages = 0;
	private ArrayList<String> collectionPages = new ArrayList<>();
	private String folder_prefix = "uploads" + File.separator;
	private String collection_path;


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
        System.out.println("Collection: " + this.collectionName);

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

        System.out.println("Number of items: "+this.collectionSize);
        // now that we have everything
		// wee need to collect each set
        // but first let's create a folder of this collection !

        try {
            this.collection_path = this.folder_prefix + this.toPrettyURL(this.collectionName);
            new File( this.collection_path ).mkdirs();
        } catch (SecurityException se) {
            System.out.println("An error occured while creating collection directory: " + se.getMessage());
        }

        // get collection main image !
        this.getCollectionImage(this.page, this.collection_path, this.toPrettyURL(this.collectionName));

        // silent counter :)
        int silent_counter = 1;

        // now we visit each item, create it's folder, download images !
        // main image should be 900x720
        // other images should be width 900px, height corresponding to width
        for ( TapetItem item : this.collectionItems ) {
            System.out.println("Getting item " + silent_counter + "/"+ this.collectionSize +" : " + this.collectionName + " - " + item.title );

            try {
               new File( this.collection_path + File.separator + this.toPrettyURL(item.title) ).mkdirs();
            } catch (SecurityException se) {
                System.out.println("An error occured while creating collection item directory: " + se.getMessage());
            }
            // check url !
            this.url = "http://newwalls.as-creation.com" + item.link;
            System.out.println(this.url);
            this.readUrl();

            // let's get items images, resize and rename them
            this.getItemImages( this.page, this.collection_path + File.separator + this.toPrettyURL(item.title), this.toPrettyURL(item.title));
            // get item's metadata
            this.getItemMetadata( this.page, this.collection_path + File.separator + this.toPrettyURL(item.title), this.collectionName.toUpperCase() );

            // silent counter check
            silent_counter = (silent_counter == this.collectionSize) ? 0 : silent_counter + 1;

        }

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

    /**
     * Collect all images for the category item and save them
     * @param html
     * @param path
     * @param filename
     */
    private void getItemImages(String html, String path, String filename) {
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("div.thumbBarInner > a[href]");
        int i = 0;
        for (Element image : images) {
            i++;
            System.out.println("processing  image: " + i);
            this.saveImage(image.attr("href"), path, filename + "-"+i+".jpg");
        }
    }

    
    private void getCollectionImage(String html, String path, String filename) {
        Document doc = Jsoup.parse(html);
        Elements image = doc.select("div.thumbnail.hidden-xs > img");
        for (Element promoImage : image) {
            this.saveCollectionImage(promoImage.attr("src"), path, filename + ".jpg");
        }
    }

    private void saveCollectionImage( String url, String path, String filename) {
        try {
            Thumbnails.of(new URL("http:" + url))
                    .forceSize(500,550)
                    .outputQuality(0.5)
                    .toFile(path + File.separator + filename);
        } catch (MalformedURLException ex)  {
            System.out.println("There was an error downloading Catalog image: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("There was an IO exception: " + ex.getMessage());
        }
    }

    /**
     * Save and resize image to the filesystem
     * @param url
     * @param path
     * @param filename
     */
    private void saveImage( String url, String path, String filename) {
        try {
            Thumbnails.of(new URL("http:" + url))
                    .size(900, 720)
                    .outputQuality(0.5)
                    .toFile(path + File.separator + filename);
        } catch (MalformedURLException ex) {
            System.out.println("There was an error downloading image: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("There was IOException: " + ex.getMessage());
        }

    }

    /**
     * Get item description properties
     * @param html
     * @param path
     * @param title
     */
    private void getItemMetadata( String html, String path, String title ) {
        try {

            Document doc = Jsoup.parse( html);
            Elements info = doc.select("span.stage-item-title");
            Elements table = doc.select("div.stage-tab > table.table > tbody > tr");


            PrintWriter writer = new PrintWriter(path + File.separator + info.text() + ".txt");
            writer.println("<h4>" + title + "</h4>");
            writer.println("Šifra: " + info.text().split(" ")[info.text().split(" ").length - 1]);

            for (int i = 0; i < table.size(); i++) {
                String row = table.get(i).childNode(0).childNode(0).toString();
                switch (row) {
                    case "Colour": writer.println("Boja: " + table.get(i).childNode(1).childNode(0).toString());
                        break;
                    case "Style": writer.println("Stil: " + table.get(i).childNode(1).childNode(0).toString());
                        break;
                    case "Material":  writer.println("Materijal: Flis"); //+ table.get(i).childNode(1).childNode(0).toString()
                        break;
                    case "Size": writer.println("Dimenzije: " + table.get(i).childNode(1).childNode(0).toString());
                        break;
                    default:
                        break;

                }

            }
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. " + ex.getMessage());
        }

    }

    /**
     * Convert string to SEO friendly slug url
     * Idea is to have SEO friendly folders/images in the upload folder
     * @param string
     * @return
     */
    private String toPrettyURL(String string) {
        return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
    }

}
