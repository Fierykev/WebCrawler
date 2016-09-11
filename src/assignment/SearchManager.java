package assignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchManager
        implements
            Callable<ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> {

    private ArrayList<WebIndex> webIndex;

    private String phrase;

    private ConcurrentHashMap<Pair<Integer, Pair<Integer, Integer>>,
        Object> map = new ConcurrentHashMap<>();

    public SearchManager(ArrayList<WebIndex> webIndex, String phrase) {
        this.webIndex = webIndex;
        this.phrase = phrase;
    }

    @Override
    public ConcurrentHashMap<Pair<Integer,
        Pair<Integer, Integer>>, Object> call()
            throws Exception {
        ExecutorService manager = Executors.newCachedThreadPool();

        // add a search for each table
        int tableID;
        for (int tableNumber = 0; tableNumber < webIndex
                .size(); tableNumber++) {
            // reset table ID
            tableID = 0;

            for (IndexPriority ip : webIndex.get(tableNumber).table) {
                manager.execute(
                        new Search(ip, tableNumber, tableID, phrase, map));
                tableID++;
            }
        }

        // kill the manager
        manager.shutdown();

        // wait for the threads to exit
        while (!manager.isTerminated()) {

        }

        return map;
    }
}