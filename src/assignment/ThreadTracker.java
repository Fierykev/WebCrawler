package assignment;

import java.util.concurrent.Semaphore;

public class ThreadTracker {

    private Integer activeThreads = 0;

    private Semaphore mutex = new Semaphore(1);

    /**
     * Get the number of threads running.
     * 
     * @return The above.
     */
    public synchronized int getActiveThread() {
        return activeThreads;
    }

    /**
     * Increases the number of threads being tracked by one.
     */
    public void incActiveThread() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        activeThreads++;

        mutex.release();
    }
// atomic integer call dec
    /**
     * Decreases the number of threads being tracked by one.
     */
    public void decActiveThread() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        activeThreads--;

        mutex.release();
    }

}
