package assignment;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexSaveManager implements Runnable {

    private static final int BOUNDED_BUFFER_MAX_SIZE = 100;

    private static final int CHUNK_SIZE = 10;

    public static int jobsPerFile = Integer.MAX_VALUE;

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    BoundedBuffer<IndexPriority> jobs;

    public IndexSaveManager() {
        // reset save number to zero
        WriteIndextoFile.saveNum.set(0);

        // reset shutdown
        shutdown.set(false);

        // reset jobs
        jobs = new BoundedBuffer<>(BOUNDED_BUFFER_MAX_SIZE, CHUNK_SIZE);
    }

    /**
     * Add a job.
     * 
     * @param webIndex
     *            The job to be added.
     */
    public void addJob(IndexPriority webIndex) {
        jobs.add(webIndex);
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown.set(shutdown);
    }

    private boolean getShutdown() {
        return shutdown.get();
    }

    @Override
    public void run() {
        // remove all old index files
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

        ExecutorService manager = Executors.newCachedThreadPool();

        // run till told to shutdown
        while (!getShutdown()) {
            synchronized (jobs) {
                if (jobsPerFile <= jobs.size()) {
                    WriteIndextoFile toFile = new WriteIndextoFile();

                    for (int i = 0; i < jobsPerFile; i++) {
                        toFile.addIndex(jobs.delete());
                    }

                    // launch the save thread
                    manager.execute(toFile);
                }
            }
        }

        // save the remaining jobs to a file
        WriteIndextoFile toFile = new WriteIndextoFile();
        while (jobs.size() != 0) {
            toFile.addIndex(jobs.delete());
        }

        // launch the save thread
        manager.execute(toFile);

        // wait for the save threads to end
        manager.shutdown();

        while (!manager.isTerminated()) {

        }
    }

}

class WriteIndextoFile implements Runnable {
    public static AtomicInteger saveNum = new AtomicInteger(0);

    WebIndex webIndex = new WebIndex();

    public void addIndex(IndexPriority element) {
        webIndex.table.add(element);
    }

    @Override
    public void run() {
        // store the save number as a local variable to free up locks
        // update the save number to reflect this data being saved
        int localSaveNum;
        localSaveNum = saveNum.getAndIncrement();
        try {
            webIndex.save("index" + localSaveNum + ".db");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}