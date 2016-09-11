package assignment;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.simple.ISimpleMarkupParser;
import org.attoparser.simple.SimpleMarkupParser;

public class CrawlerThreadManager implements Runnable {

    // TMP
    static Integer allJobs = 0;

    private static int MAX_THREADS = 1;

    private static int BOUNDED_BUFFER_MAX_SIZE = 100000000;

    private static int CHUNK_SIZE = 200;

    private static ConcurrentHashMap<URL, Object> pagesVisited = new ConcurrentHashMap<>();

    private static BoundedBuffer<URL> jobs = new BoundedBuffer<>(
            BOUNDED_BUFFER_MAX_SIZE, CHUNK_SIZE);

    static Semaphore mutex = new Semaphore(1);

    private static Integer activeThreads = 0;

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

    synchronized void addAllJobs() {
        allJobs++;
    }

    synchronized int getAllJobs() {
        return allJobs;
    }

    /**
     * Manage all of the threads as well as spawn new threads.
     */
    public void crawlerThreadManager() {
        // no starting job
        if (jobs.size() == 0)
            return;

        // TMP

        try {
            tmpURL = new File("rhf/rhf/www.netfunny.com/inetjoke.html").toURI()
                    .toURL();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // start the WebIndexManager
        ExecutorService indexManagerThread = Executors
                .newSingleThreadExecutor();
        IndexManager indexManager = new IndexManager();
        IndexManager.webIndex = new WebIndexPriorityQueue(); // reset WebIndex
        indexManagerThread.execute(indexManager);

        // start creating threads
        CrawlerThreadLauncher threadLauncher = new CrawlerThreadLauncher(
                MAX_THREADS);

        // check for when the jobs queue is equal to the number of threads
        while (!STOP) {//jobs.size() != -threadLauncher.getNumThreads()) {
            // create more threads to fix the lockup
            if (jobs.size() != 0 && jobs.getEmptyPermitsLength() <= 0) {
                new Thread(new CrawlerThreadManager()).start();

                threadLauncher.incJob();
            }

            // launch more threads if needed
            threadLauncher.launch();
            
//            System.out.println(getAllJobs() + " " + jobs.getEmptyPermitsLength()
//            + " " + jobs.getFullPermitsLength() + " "
//            + threadLauncher.getNumThreads() + " " + " "
//            + jobs.getFullPermitsLength() + " " + jobs.size());
        }

        // signal for all crawler threads to die
        jobs.freeze();

        // shutdown the index thread

        indexManager.setShutdown(true);
        indexManagerThread.shutdown();

        // wait for the index manager to shutdown
        while (!indexManagerThread.isTerminated()) {

        }
    }

    static URL tmpURL;
    static boolean STOP = false;
    int i = 0;

    synchronized URL tmpJ() {
        if (i++ < 100)
            return tmpURL;
        else
            return null;
    }

    @Override
    public void run() {
        while (true) {
            URL url = null;

            while (url == null) {
                url = null;

                // get work
//                url = jobs.delete();

                url = tmpJ();
                
                if (url == null)
                {
                    STOP = true;
                }
                
                // freeze was invoked
//                if (url == null)
//                    return;

//                if (pagesVisited.putIfAbsent(url, new Object()) != null)
//                    url = null;
            }

            // setup the handler
            CrawlingMarkupHandler handler = new CrawlingMarkupHandler();
            handler.setURL(url);

            // Create a parser from the attoparser library
            ISimpleMarkupParser parser = new SimpleMarkupParser(
                    ParseConfiguration.htmlConfiguration());

            // Parse the next URL's page
            try {
                parser.parse(new InputStreamReader(url.openStream()), handler);
            } catch (ParseException e) {
                // URL is not correctly formatted

                continue;
            } catch (IOException e) {
                // cannot find the file

                continue;
            }

            URL urlArray[] = new URL[handler.URLList.size()];
            handler.URLList.toArray(urlArray);

            // add all of the jobs
            jobs.addAll(urlArray);

            // add the index
            final ACertainMagicalIndex index = (ACertainMagicalIndex) handler
                    .getIndex();

            // convert the Tree to an ArrayList
            ArrayList<Pair<String, Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>>> dataList = new ArrayList<>();

            // convert the tree to an ArrayList
            for (Entry<String, Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>> m : index.table
                    .entrySet()) {
                dataList.add(
                        new Pair<String, Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>>(
                                m.getKey(), m.getValue()));
            }

            // add the data to the combine queue synchronized
            // IndexManager.combineData.add(new Pair<>(index.getURL(),
            // dataList));

            addAllJobs();
        }
    }
}

class CrawlerThreadLauncher {

    private int maxThreads;

    private int numThreads = 0;

    public CrawlerThreadLauncher(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public synchronized void incJob() {
        maxThreads++;
    }

    public synchronized int getNumThreads() {
        return numThreads;
    }

    private synchronized void incThreads() {
        numThreads++;
    }

    synchronized void launch() {
        if (numThreads < maxThreads) {
            new Thread(new CrawlerThreadManager()).start();

            incThreads();
        }
    }

}
