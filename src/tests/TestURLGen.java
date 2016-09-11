package tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Test;

import assignment.CrawlerThreadManager;
import assignment.IndexPriority;
import assignment.IndexTable;
import assignment.Pair;
import assignment.WebIndex;

public class TestURLGen {

    @Test
    public void testGenWebpage() {
        URL url = null;
        try {
            url = new URL("file:URL Gen/0.html");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList<URL> urlArrayList = new ArrayList<URL>();

        urlArrayList.add(url);

        // create a random website
        URLGen urlGen = new URLGen();
        urlGen.createRandomWebsite();

        // crawl the webpage with the real web crawler
        CrawlerThreadManager ctm = new CrawlerThreadManager();

        // Run the crawler
        ctm = new CrawlerThreadManager();
        ctm.startingJobs(urlArrayList);
        ctm.crawlerThreadManager();

        // store all data from the crawler
        // load the tables into memory
        ArrayList<WebIndex> combinedIndex = TestSearchHelper
                .loadCrawlerOutput();

        HashMap<URL, Pair<Integer, ArrayList<Pair<Integer, String>>>> reverseIndex = new HashMap<>();

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
                            // add in the HashMap
                            reverseIndex.putIfAbsent(curURL, new Pair<>(
                                    data.getSecondElement().getSecondElement()
                                            .get(j).position,
                                    new ArrayList<>()));

                            // add to the HashMap
                            reverseIndex.get(curURL).getSecondElement()
                                    .add(new Pair<>(
                                            data.getSecondElement()
                                                    .getSecondElement()
                                                    .get(j).position,
                                            data.getFirstElement()));
                        }
                    }
                }
            }
        }

        // check the HashMap is correct
        for (Entry<URL, Pair<Integer, ArrayList<Pair<Integer, String>>>> m : reverseIndex
                .entrySet()) {

            ArrayList<Pair<Integer, String>> mapping = m.getValue()
                    .getSecondElement();

            int pos = -1;

            // sort the arraylist
            Collections.sort(m.getValue().getSecondElement());

            int i;

            // check text is the same
            for (i = 0; i < m.getValue().getSecondElement().size(); i++) {

                // check the next position is correct
                if (pos != -1 && pos + 1 != mapping.get(i).getFirstElement())
                    break;

                // get the word
                String word = urlGen.allWords.get(m.getKey()).remove(0);
               
                // compare to actual
                assertEquals(word, mapping.get(i).getSecondElement());

                // update position
                pos = mapping.get(i).getFirstElement();
            }

            // move position up by one for tag check
            pos++;

            // check tag text is correct
            for (; i < m.getValue().getSecondElement().size(); i++) {

                // get the words
                ArrayList<String> words = urlGen.allLinks.get(m.getKey())
                        .remove(0);

                for (String s : words) {
                    // check the next position is correct
                    if (pos != -1
                            && pos + 1 != mapping.get(i).getFirstElement()) {
                        fail("Incorrect Format");
                    }

                    // compare to actual
                    assertEquals(s, mapping.get(i).getSecondElement());
                    pos++;
                    i++;
                }

                // move i back one
                i--;

                // skip one space
                pos++;
            }
        }

        // check hashmap is empty
        for (Entry<URL, ArrayList<String>> m : urlGen.allWords.entrySet()) {
            for (String s : m.getValue())
                fail("Not all elements were recorded by the crawler.\n" + s);
        }
    }

}
