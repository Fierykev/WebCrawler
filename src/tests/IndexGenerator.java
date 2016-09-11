package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import assignment.IndexPriority;
import assignment.IndexTable;
import assignment.Pair;
import assignment.WebIndex;

public class IndexGenerator {

    private int numURLs, queryLength, numWords;

    private int wordsPerPage;

    public IndexGenerator(int numURLs, int queryLength, int numWords) {
        this.numURLs = numURLs;
        this.queryLength = queryLength;
        this.numWords = numWords;
        wordsPerPage = numWords / numURLs;
    }

    /**
     * Create a table that meets the specifications passed in through the
     * constructor.
     */
    public WebIndex generateURL() {
        WebIndex webIndex = new WebIndex();

        HashMap<Integer, URL> map = new HashMap<>();

        // create the mapping
        for (int i = 0; i < numURLs; i++)
            try {
                map.put(i, new URL("file://" + i + ".html"));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        // create a String of values
        PriorityQueue<String> values = new PriorityQueue<>();

        for (int r = 0; r < numWords; r++) {
            values.add(Integer.toString(r));
        }

        ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<IndexTable>>>> data =
                new ArrayList<>();

        // create the rows
        for (int r = 0; r < numWords; r++) {
            ArrayList<Pair<Integer, Integer>> urlBlock = new ArrayList<>();
            ArrayList<IndexTable> positionData = new ArrayList<>();

            for (int url = 0; url < numURLs; url++) {
                // create the url block info and the word data
                for (int urlNum = 0; urlNum < numURLs; urlNum++) {
                    urlBlock.add(new Pair<>(urlNum, wordsPerPage * urlNum));

                    for (int wpp = 0; wpp < wordsPerPage; wpp++) {
                        positionData
                                .add(new IndexTable(r * numURLs * wordsPerPage
                                        + urlNum * wordsPerPage + wpp));
                    }
                }
            }

            // add in the row
            data.add(
                    new Pair<String, Pair<ArrayList<Pair<Integer, Integer>>, ArrayList<IndexTable>>>(
                            values.poll(), new Pair<>(urlBlock, positionData)));
        }

        // create the web index
        webIndex.table.add(new IndexPriority(map, data,
                IndexPriority.calcSize(map, data)));

        return webIndex;
    }
}
