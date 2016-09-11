package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.simple.ISimpleMarkupParser;
import org.attoparser.simple.SimpleMarkupParser;
import org.junit.Test;

import assignment.CrawlingMarkupHandler;
import assignment.Index;
import assignment.Parser;
import assignment.WebIndex;

public class TestSearchHelper {

    /**
     * Single thread crawl the URL.
     * 
     * @param url
     *            THe starting url.
     */
    public void crawl(List<URL> urlAdd) {
        LinkedList<URL> allURLs = new LinkedList<>();
        allURLs.addAll(urlAdd);

        URL url;

        while (!allURLs.isEmpty()) {
            url = allURLs.removeFirst();

            // setup the handler
            TestSearchHandler handler = new TestSearchHandler();
            handler.setURL(url);

            // Create a parser from the attoparser library
            ISimpleMarkupParser parser = new SimpleMarkupParser(
                    ParseConfiguration.htmlConfiguration());

            // Parse the next URL's page
            try {
                parser.parse(new InputStreamReader(url.openStream()), handler);
            } catch (ParseException e) {
                // URL is not correctly formatted so continue

                TestSearchHandler.errorURLs.add(url);

                continue;
            } catch (IOException e) {
                // cannot find the file so continue

                TestSearchHandler.errorURLs.add(url);

                continue;
            }

            // grab the new urls
            allURLs.addAll(handler.newURLs());
        }
    }

    /**
     * Load the WebIndex data into memory.
     * 
     * @return The WebIndex data.
     */
    public static ArrayList<WebIndex> loadCrawlerOutput() {
        ArrayList<WebIndex> combinedIndex = new ArrayList<>();

        try {
            combinedIndex = Index.load("index.db").getCombinedIndex();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return combinedIndex;
    }

}
