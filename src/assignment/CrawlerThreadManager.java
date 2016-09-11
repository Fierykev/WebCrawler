package assignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.simple.ISimpleMarkupParser;
import org.attoparser.simple.SimpleMarkupParser;

import BTree.BTreeHashSet;

public class CrawlerThreadManager implements Runnable {

    public static int maxThreads = 7000;

    private static final int BOUNDED_BUFFER_MAX_SIZE = 1000000;

    private static final int CHUNK_SIZE = 1000;

    public static AtomicInteger fileReader = new AtomicInteger(0);

    private static BTreeHashSet<String> pagesVisited;

    private static BoundedBuffer<URL> jobs;

    private final URL url;

    private static AtomicInteger threadTracker;

    /**
     * Add a null starting URL (the manager should be the only one to call this
     * constructor)
     */
    public CrawlerThreadManager() {
        url = null;

        // create the hashmap
        pagesVisited = new BTreeHashSet<>(100);

        // create jobs
        jobs = new BoundedBuffer<>(BOUNDED_BUFFER_MAX_SIZE, CHUNK_SIZE);

        // create the thread tracker
        threadTracker = new AtomicInteger();
    }

    /**
     * Add a starting URL.
     * 
     * @param url
     *            The URL this thread will look at.
     */
    public CrawlerThreadManager(URL url) {
        this.url = url;
    }

    /**
     * Fill the jobs list to start the crawler.
     * 
     * @param jobs
     *            List of jobs to fulfill.
     */
    public void startingJobs(List<URL> addJobs) {
        if (BOUNDED_BUFFER_MAX_SIZE < addJobs.size()) {
            System.err.println(
                    "The program can only handle " + BOUNDED_BUFFER_MAX_SIZE
                            + " elements to be passed in from the console.");
            return;
        }

        // add all of the elements as jobs
        for (URL url : addJobs)
            jobs.add(url);
    }

    /**
     * Manage all of the threads as well as spawn new threads.
     */
    public void crawlerThreadManager() {
        // no starting job
        if (jobs.size() == 0)
            return;

        // start the WebIndexManager
        ExecutorService indexManagerThread =
                Executors.newSingleThreadExecutor();
        IndexManager indexManager = new IndexManager();
        IndexManager.webIndex = new WebIndexPriorityQueue(); // reset WebIndex
        indexManagerThread.execute(indexManager);

        // create crawler thread manager
        ExecutorService manager = Executors.newCachedThreadPool();
        URL curURL = null;

        while (!manager.isTerminated()) {
            if (jobs.size() != 0) {
                if (threadTracker.get() <= maxThreads
                        || jobs.getEmptyPermitsLength() <= 0)
                // limit the number of threads to prevent reading too many files
                // at once. Go over the limit if and only if the jobs Bounded
                // Buffer if stuck
                {
                    // get the top of the list
                    curURL = jobs.delete();

                    // check the job is new
                    if (!pagesVisited.add(curURL.toString()))
                        continue;

                    // add to the job
                    manager.execute(new CrawlerThreadManager(curURL));

                    threadTracker.incrementAndGet();
                }
            } else // check no more threads are running
            {
                // keep running until all tasks issued have completed
                // check jobs again in case coming down this path was a fluke
                // threadTracker must be checked before jobs.size() to prevent
                // ending early when jobs.size() has not been updated yet
                if (threadTracker.get() == 0 && jobs.size() == 0) {
                    manager.shutdown();
                }
            }
        }

        // shutdown the index thread

        indexManager.setShutdown(true);
        indexManagerThread.shutdown();

        // wait for the index manager to shutdown
        while (!indexManagerThread.isTerminated()) {

        }
    }

    @Override
    public void run() {
        // setup the handler
        CrawlingMarkupHandler handler = new CrawlingMarkupHandler();
        handler.setURL(url);

        // Create a parser from the attoparser library
        ISimpleMarkupParser parser =
                new SimpleMarkupParser(ParseConfiguration.htmlConfiguration());

        // Parse the next URL's page
        try {
            switch (fileReader.get()) {
                case 0 :
                    parser.parse(new InputStreamReader(url.openStream()),
                            handler);
                    break;
                case 1 :
                    parser.parse(
                            new BufferedReader(
                                    new FileReader(new File(url.getFile()))),
                            handler);
                    break;
                case 2 :
                    parser.parse(new CustomFileReader(url.getFile()), handler);
                    break;
            }

        } catch (ParseException e) {
            // URL is not correctly formatted so return

            // decrease active threads
            threadTracker.decrementAndGet();
            return;
        } catch (IOException e) {
            // cannot find the file so return

            // decrease active threads
            threadTracker.decrementAndGet();
            return;
        }

        URL urlArray[] = new URL[handler.URLList.size()];
        handler.URLList.toArray(urlArray);

        // add all of the jobs
        jobs.addAll(urlArray);

        // add the index
        final ACertainMagicalIndex index =
                (ACertainMagicalIndex) handler.getIndex();

        // convert the Tree to an ArrayList
        ArrayList<Pair<String, Pair<Pair<Integer, Integer>,
            ArrayList<IndexTable>>>> dataList =
                new ArrayList<>();

        // convert the tree to an ArrayList
        for (Entry<String, Pair<Pair<Integer, Integer>,
                ArrayList<IndexTable>>> m : index.table
                .entrySet()) {
            dataList.add(
                    new Pair<String, Pair<Pair<Integer, Integer>,
                        ArrayList<IndexTable>>>(
                            m.getKey(), m.getValue()));
        }

        // add the data to the combine queue synchronized
        IndexManager.combineData.add(new Pair<>(index.getURL(), dataList));

        // decrease active threads
        threadTracker.decrementAndGet();
    }
}