package assignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexManager implements Runnable {

    public static int maxThreads = 500;

    public static int maxDataSize = Integer.MAX_VALUE;

    private static int BOUNDED_BUFFER_MAX_SIZE = 10000;

    private static int CHUNK_SIZE = 1;

    static BoundedBuffer<Pair<URL, ArrayList<Pair<String,
        Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>>>>> combineData;

    static AtomicInteger tableSize = new AtomicInteger(0); // the table size is
    // not the size of
    // the
    // priority
    // queue since elements may be being combined.

    private final int STARTING_CAP = 100;

    private final int ENDING_CAP = 1;

    private static AtomicBoolean shutdown = new AtomicBoolean(false);

    static WebIndexPriorityQueue webIndex;

    public IndexManager() {
        // create the combine data
        combineData = new BoundedBuffer<>(BOUNDED_BUFFER_MAX_SIZE, CHUNK_SIZE);

        // create the table size
        tableSize.set(0);

        // reset shutdown
        shutdown.set(false);
    }

    public void setShutdown(boolean shutdown) {
        IndexManager.shutdown.set(shutdown);
    }

    private boolean getShutdown() {
        return shutdown.get();
    }

    @Override
    public void run() {
        // turns true when shutdown so that
        // ENDING_CAP can be met
        boolean endThreads = false;

        // set the segment cap
        int segmentCap = STARTING_CAP;

        // future manager
        ExecutorService futureManager = Executors.newSingleThreadExecutor();
        FutureHandler futureHandler = new FutureHandler();
        futureManager.submit(futureHandler);

        ThreadPoolExecutor manager = (ThreadPoolExecutor) Executors
                .newCachedThreadPool();

        ThreadTracker threadTracker = new ThreadTracker();

        while (!manager.isTerminated()) {
            // start shutdown when the global is turned to true
            if (!endThreads) {
                if (getShutdown()) {
                    // set the ultimate table size
                    segmentCap = ENDING_CAP;

                    if (combineData.size() == 0
                            && tableSize.get() <= segmentCap) {
                        // kill the threads
                        manager.shutdown();
                        endThreads = true;
                        continue;
                    }
                }

                if (threadTracker.getActiveThread() <= maxThreads
                        || combineData.getEmptyPermitsLength() <= 0)
                // limit the number of threads to prevent reading too many files
                // at once. Go over the limit if and only if the jobs Bounded
                // Buffer if stuck
                {
                    // create threads if stop is not called
                    if (combineData.size() != 0) {
                        Pair<URL, ArrayList<Pair<String,
                            Pair<Pair<Integer, Integer>,
                            ArrayList<IndexTable>>>>> data;

                        if (tableSize.get() < segmentCap) {
                            data = combineData.delete();

                            tableSize.incrementAndGet();

                            threadTracker.incActiveThread();

                            futureHandler.addFuture(manager
                                    .submit(new CreateIndexPriorityThread(data,
                                            threadTracker)));
                        } else if (webIndex.table.size() != 0) // combine the
                                                               // data
                                                               // with the
                                                               // smallest
                                                               // element
                        {
                            data = combineData.delete();

                            final IndexPriority data1;

                            synchronized (webIndex.table) {
                                data1 = webIndex.table.poll();
                            }

                            threadTracker.incActiveThread();

                            futureHandler.addFuture(
                                    manager.submit(new MergeDataThread(data1,
                                            data.getFirstElement(),
                                            data.getSecondElement(),
                                            threadTracker)));
                        }

                    } else if (!webIndex.table.isEmpty()
                            && 1 < webIndex.table.size())
                    // start
                    // combining
                    // data
                    // blocks
                    // from
                    // the
                    // table
                    {
                        final IndexPriority data1, data2;

                        synchronized (webIndex.table) {
                            data1 = webIndex.table.poll();
                            data2 = webIndex.table.poll();
                        }

                        // after the merge there will be one block less
                        tableSize.decrementAndGet();

                        threadTracker.incActiveThread();

                        // merge and add to the table
                        futureHandler.addFuture(
                                manager.submit(new MergeIndexPriorityThread(
                                        data1, data2, threadTracker)));
                    }
                }
            }
        }

        // shutdown future manager
        futureHandler.setShutdown(true);
        futureManager.shutdown();

        while (!futureManager.isTerminated()) {

        }
    }

    public String toString() {
        String output = "";

        output += "---------------------------\n";
        output += "MAP TABLE\n";
        output += "---------------------------\n";

        for (IndexPriority ip : webIndex.table) {
            for (Map.Entry<Integer, URL> m : ip.URLsymbolTable.entrySet()) {
                // swap key and value when printing because it's easier to look
                // at when the number is first
                output += "(" + m.getValue() + " " + m.getKey() + ")\n";
            }
        }

        output += "---------------------------\n";
        output += "INDEX TABLE\n";
        output += "---------------------------\n";
        for (IndexPriority ip : webIndex.table) {
            for (Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                    ArrayList<IndexTable>>> m : ip.data) {
                output += m.getFirstElement() + ": ";

                for (Pair<Integer, Integer> table : m.getSecondElement()
                        .getFirstElement()) {

                    output += "<" + table.getFirstElement() + ", "
                            + table.getSecondElement() + ">";
                }

                for (IndexTable it : m.getSecondElement().getSecondElement())
                    output += "[" + it.position + "] ";

                output += "\n";
            }

        }

        return output;
    }
}

/**
 * The below class adds futures back to the queue when they are ready to be
 * added.
 * 
 * @author Kevin
 *
 */
class FutureHandler implements Runnable {
    private boolean shutdown = false;
    private ArrayList<Future<IndexPriority>> future = new ArrayList<>();

    private ExecutorService indexSaveManagerExecutor;
    private IndexSaveManager indexSaveManager;

    public FutureHandler() {
        // start the index save manager
        indexSaveManagerExecutor = Executors.newSingleThreadExecutor();
        indexSaveManager = new IndexSaveManager();
        indexSaveManagerExecutor.execute(indexSaveManager);
    }

    public synchronized void addFuture(Future<IndexPriority> future2) {
        future.add(future2);
    }

    public synchronized int getFutureSize() {
        return future.size();
    }

    public synchronized void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    private synchronized boolean getShutdown() {
        return shutdown;
    }

    private synchronized void cleanFuture() {
        for (int i = 0; i < getFutureSize(); i++) {
            if (future.get(i).isDone()) {
                try {
                    // check the future is not too large
                    if (future.get(i).get().size() < IndexManager.maxDataSize) {
                        synchronized (IndexManager.webIndex.table) {
                            // add back to queue and remove from list
                            try {
                                IndexManager.webIndex.table
                                        .add(future.remove(i).get());
                                i--;
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // add the job to be saved
                        indexSaveManager.addJob(future.remove(i).get());

                        // decrease the table size since the element
                        // was not read to the table
                        IndexManager.tableSize.decrementAndGet();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        // record the future size before looping because another thread may
        // change it
        while (!getShutdown()) {
            cleanFuture();
        }

        synchronized (IndexManager.webIndex.table) {
            // add all of the webIndex to be saved
            while (!IndexManager.webIndex.table.isEmpty())
                indexSaveManager.addJob(IndexManager.webIndex.table.poll());
        }

        // wait for the save manager to shutdown
        indexSaveManager.setShutdown(true);
        indexSaveManagerExecutor.shutdown();

        while (!indexSaveManagerExecutor.isTerminated()) {

        }
    }
}

/**
 * The below class merges a IndexPriority data set with a raw web page index.
 * 
 * @author Kevin
 *
 */
class MergeDataThread implements Callable<IndexPriority> {
    IndexPriority data1;
    URL url;
    ArrayList<Pair<String, Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>>> data2;
    final ThreadTracker threadTracker;

    public MergeDataThread(IndexPriority data1, URL url,
            ArrayList<Pair<String, Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>>> data2,
            ThreadTracker threadTracker) {
        this.data1 = data1;
        this.url = url;
        this.data2 = data2;
        this.threadTracker = threadTracker;
    }

    @Override
    public IndexPriority call() {

        IndexPriority indexPriority = new WebIndexHelper().mergeData(data1, url,
                data2);

        // decrease active threads
        threadTracker.decActiveThread();

        return indexPriority;
    }
}

/**
 * The below class merges two IndexPriority data sets into one.
 * 
 * @author Kevin
 *
 */
class MergeIndexPriorityThread implements Callable<IndexPriority> {
    IndexPriority data1, data2;
    final ThreadTracker threadTracker;

    public MergeIndexPriorityThread(IndexPriority data1, IndexPriority data2,
            ThreadTracker threadTracker) {
        this.data1 = data1;
        this.data2 = data2;
        this.threadTracker = threadTracker;
    }

    @Override
    public IndexPriority call() {

        IndexPriority indexPriority = new WebIndexHelper()
                .mergeIndexPriority(data1, data2);

        // decrease active threads
        threadTracker.decActiveThread();

        return indexPriority;
    }
}

/**
 * The below class creates an IndexPriority from a raw web page index.
 * 
 * @author Kevin
 *
 */
class CreateIndexPriorityThread implements Callable<IndexPriority> {
    Pair<URL, ArrayList<Pair<String, Pair<Pair<Integer, Integer>,
        ArrayList<IndexTable>>>>> unformattedData;
    final ThreadTracker threadTracker;

    public CreateIndexPriorityThread(
            Pair<URL, ArrayList<Pair<String, Pair<Pair<Integer, Integer>,
            ArrayList<IndexTable>>>>> unformattedData,
            ThreadTracker threadTracker) {
        this.unformattedData = unformattedData;
        this.threadTracker = threadTracker;
    }

    @Override
    public IndexPriority call() {
        IndexPriority indexPriority = new IndexPriority(unformattedData);

        // decrease active threads
        threadTracker.decActiveThread();

        return indexPriority;
    }
}