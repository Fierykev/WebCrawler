package assignment;

import java.io.*;
import java.net.*;
import java.util.*;

import org.attoparser.simple.*;

import BTree.BTreeHashSet;

import org.attoparser.config.ParseConfiguration;

//WEBCRAWLER.MAIN MUST RUN WHEN CALLED FROM CMD LINE
//INDEXES CAN LOAD AND STORE
//CAN MAKE A WEBINDEX AND MAKES A PAGE WITH URL

public class WebCrawler {

    /**
     * The WebCrawler's main method starts crawling a set of pages. You can
     * change this method as you see fit, as long as it takes URLs as inputs and
     * saves an Index at "index.db".
     */
    public static void main(String[] args) {

        // Basic usage information
        if (args.length == 0) {
            System.out.println("No URLs specified.");
            System.exit(1);
        }

        // We'll throw all of the args into a list
        List<URL> remaining = new LinkedList<>();
        for (int i = 0; i < args.length; i++) {
            try {
                remaining.add(new URL(args[i]));
            } catch (MalformedURLException e) {
                // check if the argument fulfills any other command line arg
                switch (args[i]) {
                    case "--BTreeHashSet" :
                        BTreeHashSet.enableBTree = true;
                        continue;
                    case "--BTreeMaxHash" :

                        if (args.length <= i + 1) {
                            System.err.println("Error with Max Hash # inputs.");
                            System.exit(-1);
                        }

                        // update i
                        i++;

                        // set the MAX_HASH_SET_SIZE value to the integer value
                        try {
                            BTreeHashSet.maxHashSetSize =
                                    Integer.parseInt(args[i]);
                        } catch (NumberFormatException e2) {
                            System.err.println("Cannot convert " + args[i]
                                    + " to a number");
                            System.exit(-1);
                        }

                        continue;
                    case "--MaxThreadManager" :

                        if (args.length <= i + 1) {
                            System.err.println(
                                    "Error with Max Thread Manager # inputs.");
                            System.exit(-1);
                        }

                        // update i
                        i++;

                        try {
                            CrawlerThreadManager.maxThreads =
                                    Integer.parseInt(args[i]);
                        } catch (NumberFormatException e2) {
                            System.err.println("Cannot convert " + args[i]
                                    + " to a number");
                            System.exit(-1);
                        }

                        continue;
                    case "--MaxThreadIndex" :

                        if (args.length <= i + 1) {
                            System.err.println(
                                    "Error with Max Thread Index # inputs.");
                            System.exit(-1);
                        }

                        // update i
                        i++;

                        try {
                            IndexManager.maxThreads = Integer.parseInt(args[i]);
                        } catch (NumberFormatException e2) {
                            System.err.println("Cannot convert " + args[i]
                                    + " to a number");
                            System.exit(-1);
                        }

                        continue;
                    case "--TablesPerJob" :

                        if (args.length <= i + 1) {
                            System.err.println(
                                    "Error with Max Tables Per Job # inputs.");
                            System.exit(-1);
                        }

                        // update i
                        i++;

                        try {
                            IndexManager.maxDataSize =
                                    Integer.parseInt(args[i]);
                        } catch (NumberFormatException e2) {
                            System.err.println("Cannot convert " + args[i]
                                    + " to a number");
                            System.exit(-1);
                        }

                        continue;
                    case "--JobsPerFile" :

                        if (args.length <= i + 1) {
                            System.err.println(
                                    "Error with Max Thread Save # inputs.");
                            System.exit(-1);
                        }

                        // update i
                        i++;

                        try {
                            IndexSaveManager.jobsPerFile =
                                    Integer.parseInt(args[i]);
                        } catch (NumberFormatException e2) {
                            System.err.println("Cannot convert " + args[i]
                                    + " to a number");
                            System.exit(-1);
                        }

                        continue;
                    case "--FileReader1" :
                        CrawlerThreadManager.fileReader.set(0);
                        continue;
                    case "--FileReader2" :
                        CrawlerThreadManager.fileReader.set(1);
                        continue;
                    case "--FileReader3" :
                        CrawlerThreadManager.fileReader.set(2);
                        continue;
                }

                // incorrect input error
                System.err.println("Error with the input " + args[i]);
                System.exit(-1);
            }
        }

        // Run the crawler
        CrawlerThreadManager manager = new CrawlerThreadManager();
        manager.startingJobs(remaining);
        manager.crawlerThreadManager();
    }
}
