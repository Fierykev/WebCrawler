package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import assignment.CrawlerThreadManager;
import assignment.IndexPriority;
import assignment.IndexTable;
import assignment.Pair;
import assignment.WebIndex;

public class SingleThreadTest {

    /**
     * Compare the Multithreaded web crawler to a naive single threaded one.
     */
    @Test
    public void singleThreadTest() {
        List<URL> url = new LinkedList<>();

        try {
            url.add(new File("superspoof/superspoof/index.html").toURI()
                    .toURL());
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // crawl the webpage with a single thread
        TestSearchHelper sh = new TestSearchHelper();
        sh.crawl(url);

        // crawl the webpage with the real web crawler
        CrawlerThreadManager ctm = new CrawlerThreadManager();

        // Run the crawler
        ctm = new CrawlerThreadManager();
        ctm.startingJobs(url);
        ctm.crawlerThreadManager();

        // load the tables into memory
        ArrayList<WebIndex> combinedIndex = TestSearchHelper
                .loadCrawlerOutput();

        // check the results from the naive approach and the multithread
        // approach are the same
        for (WebIndex wi : combinedIndex) {
            for (IndexPriority ip : wi.table) {
                for (Pair<String, Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<IndexTable>>> data : ip.data) {
                    for (int i = 0; i < data.getSecondElement()
                            .getFirstElement().size(); i++) {
                        // get the URL
                        URL curURL = ip.URLsymbolTable
                                .get(data.getSecondElement().getFirstElement()
                                        .get(i).getFirstElement());

                        // get data block
                        // add in all values associated with that url
                        // find the start and end of the data block
                        int start = data.getSecondElement().getFirstElement()
                                .get(i).getSecondElement();
                        int end;

                        if (i + 1 < data.getSecondElement().getFirstElement()
                                .size())
                            end = data.getSecondElement().getFirstElement()
                                    .get(i + 1).getSecondElement() - 1;
                        else
                            end = data.getSecondElement().getSecondElement()
                                    .size() - 1;

                        for (int j = start; j <= end; j++) {                            
                            // check word contained in hash
                            // remove if contained
                            assertTrue(TestSearchHandler.hashWord
                                    .get(data.getFirstElement())
                                    .remove(new Pair<>(curURL,
                                            data.getSecondElement()
                                                    .getSecondElement()
                                                    .get(j).position)));
                        }
                    }
                }
            }
        }

        // check no elements remain
        for (Map.Entry<String, HashSet<Pair<URL, Integer>>> p : TestSearchHandler.hashWord
                .entrySet()) {
            for (Pair<URL, Integer> v : p.getValue())
                if (!TestSearchHandler.errorURLs.contains(v.getFirstElement()))
                    fail("Doess not contain all URLs");
        }
    }
}
