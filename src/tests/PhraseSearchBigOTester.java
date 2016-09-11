package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import assignment.CrawlerThreadManager;
import assignment.Index;
import assignment.Parser;
import assignment.WebIndex;

public class PhraseSearchBigOTester {

    private static int RUNS = 100;

    private void createAndSaveIndex(int numURLs, int queryLength,
            int numWords) {
        // create the index
        IndexGenerator ig = new IndexGenerator(numURLs, queryLength, numWords);
        WebIndex webIndex = ig.generateURL();

        // remove old saves
        int iteration = 0;
        while (true) {
            File file = new File("index" + iteration + ".db");
            boolean exists = file.exists();

            if (exists) {
                while (!file.delete()) {
                    System.err.println("Cannot delete file " + file.getName()
                            + " will try again in 100 miliseconds.");

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                break;
            }

            iteration++;
        }

        // save the web index
        try {
            webIndex.save("index0.db");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get the timings for the number of words on the page.
     */

    @Test
    public void numWordsRuntime() {
        int numURLs = 10;
        int queryLength = 1;
        int numWords[] = {10, 20, 30, 40};

        for (int i = 0; i < numWords.length; i++) {
            // create and save the index
            createAndSaveIndex(numURLs, queryLength, numWords[i]);

            // load the index into memory
            Index index = null;

            try {
                index = (WebIndex) Index.load("index.db");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // run the search
            String query = "\"";

            for (int j = 0; j < queryLength; j++) {
                query += j + " ";
            }

            query += "\"";

            // start the timer
            long time = System.nanoTime();

            // run the search
            for (int j = 0; j < RUNS; j++) {
                // setup parser
                Parser parser = new Parser(query);
                assertTrue(parser.toPostFix());
                parser.runSearch(index.getCombinedIndex());
            }

            // get the end time in seconds
            double endTime = (System.nanoTime() - time) / 1000000000.0 / RUNS;

            // print the time to the console
            System.out.println(endTime);
        }
    }

    /**
     * Get the timings for the number of words on the page.
     */

    @Test
    public void queryLengthRuntime() {
        int numURLs = 10;
        int queryLength[] = {10, 20, 30, 40};
        int numWords = 200;

        for (int i = 0; i < queryLength.length; i++) {
            // create and save the index
            createAndSaveIndex(numURLs, queryLength[i], numWords);

            // load the index into memory
            Index index = null;

            try {
                index = (WebIndex) Index.load("index.db");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // run the search
            String query = "\"";

            for (int j = 0; j < queryLength[i]; j++) {
                query += j + " ";
            }
            
            query += "\"";

            // start the timer
            long time = System.nanoTime();

            // run the search
            for (int j = 0; j < RUNS; j++) {
                // setup parser
                Parser parser = new Parser(query);
                assertTrue(parser.toPostFix());
                parser.runSearch(index.getCombinedIndex());
            }

            // get the end time in seconds
            double endTime = (System.nanoTime() - time) / 1000000000.0 / RUNS;

            // print the time to the console
            System.out.println(endTime);
        }
    }

    /**
     * Get the timings for the number of URLs.
     */

    @Test
    public void URLRuntime() {
        int numURLs[] = {60, 140, 160, 180};
        int queryLength = 1;
        int numWords = 200;

        for (int i = 0; i < numURLs.length; i++) {
            // create and save the index
            createAndSaveIndex(numURLs[i], queryLength, numWords);

            // load the index into memory
            Index index = null;

            try {
                index = (WebIndex) Index.load("index.db");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // run the search
            String query = "\"";

            for (int j = 0; j < queryLength; j++) {
                query += j + " ";
            }
            
            query += "\"";

            // start the timer
            long time = System.nanoTime();

            // run the search
            for (int j = 0; j < RUNS; j++) {
                // setup parser
                Parser parser = new Parser(query);
                assertTrue(parser.toPostFix());
                parser.runSearch(index.getCombinedIndex());
            }

            // get the end time in seconds
            double endTime = (System.nanoTime() - time) / 1000000000.0 / RUNS;

            // print the time to the console
            System.out.println(endTime);
        }
    }

    /**
     * Get the timings for the average page.
     */

    @Test
    public void pageRuntime() {
        int numURLs = 40;
        int queryLength = 3;
        int numWords[] = {80, 200, 240, 280};

        for (int i = 0; i < numWords.length; i++) {
            // create and save the index
            createAndSaveIndex(numURLs, queryLength, numWords[i]);

            // load the index into memory
            Index index = null;

            try {
                index = (WebIndex) Index.load("index.db");
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // run the search
            String query = "\"";

            for (int j = 0; j < queryLength; j++) {
                query += j + " ";
            }
            
            query += "\"";

            // start the timer
            long time = System.nanoTime();

            // run the search
            for (int j = 0; j < RUNS; j++) {
                // setup parser
                Parser parser = new Parser(query);
                assertTrue(parser.toPostFix());
                parser.runSearch(index.getCombinedIndex());
            }

            // get the end time in seconds
            double endTime = (System.nanoTime() - time) / 1000000000.0 / RUNS;

            // print the time to the console
            System.out.println(endTime);
        }
    }
}
