package assignment;

import java.util.concurrent.Semaphore;

public class BoundedBuffer<K> {

    private boolean freeze = false;

    private final int chunk;

    private final int maxBufferSize;

    private K data[];

    private int numEl, inPos, outPos;

    private Semaphore lockMutex, lockFull, lockEmpty;

    private int size;

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int maxBufferSize, int chunk) {
        // set the maximum size of the buffer
        this.maxBufferSize = maxBufferSize;

        // set the chunk size for add all
        this.chunk = chunk;

        // create the buffer data
        data = (K[]) new Object[maxBufferSize]; // note can't create a generic
                                                // array of objects

        // init all vars to zero since there are no elements in the array
        numEl = 0;
        inPos = 0;
        outPos = 0;
        size = 0;

        // use semaphores as locks
        lockMutex = new Semaphore(1);
        lockFull = new Semaphore(0);
        lockEmpty = new Semaphore(maxBufferSize);
    }

    synchronized void updateSize(int num) {
        size += num;
    }

    /**
     * Add a set of data to the Bounded Buffer
     * 
     * @param el
     * @param start
     * @param dataSize
     */
    private void addDataSet(K[] el, int start, int dataSize) {
        try {
            // get access for writing the data
            lockEmpty.acquire(dataSize);
            lockMutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // copy the element into the data
        for (int i = 0; i < dataSize; i++) {
            data[inPos] = el[start + i];
            inPos = (inPos + 1) % maxBufferSize;
        }

        // update the number of elements in the buffer
        numEl += dataSize;

        // release the locks
        lockMutex.release();
        lockFull.release(dataSize);
    }

    /**
     * Add all elements into the Bounded Buffer but, if the array exceeds the
     * chunk size, split it up into blocks.
     * 
     * @param el
     */
    public void addAll(K[] el) {

        // update the size for when add is performed
        updateSize(el.length);

        int blockSize = ((el.length / chunk) - 1) * chunk;
        int chunkStart = 0;

        // break the data into chunks to add
        for (chunkStart = 0; chunkStart < blockSize; chunkStart += chunk) {
            addDataSet(el, chunkStart, chunk);
        }

        // add in the partial chunk
        addDataSet(el, chunkStart, el.length - chunkStart);
    }

    /**
     * Add an element to the Bounded Buffer.
     * 
     * @param el
     *            The element to add.
     * @throws InterruptedException
     */
    public void add(K el) {
        // update the size for when add is performed
        updateSize(1);

        try {
            // get access
            lockEmpty.acquire();
            lockMutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // copy the element into the data
        data[inPos] = el;

        // update the number of elements in the buffer
        numEl++;

        // account for wrapping around when adding
        inPos = (inPos + 1) % maxBufferSize;

        // release the locks
        lockMutex.release();
        lockFull.release();
    }

    /**
     * Remove an element from the Bounded Buffer.
     * 
     * @return The element removed.
     * @throws InterruptedException
     */
    public K delete() {

        // update the size for when delete is performed
        updateSize(-1);

        try {
            // get access
            lockFull.acquire();
            lockMutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // read access not allowed
        if (freeze)
            return null;

        // take out an element from the buffer
        K el = data[outPos];

        // account for wrapping around when adding
        outPos = (outPos + 1) % maxBufferSize;

        // release the locks
        lockMutex.release();
        lockEmpty.release();

        return el;
    }

    public int getMutexPermitsLength() {
        return lockMutex.availablePermits();
    }

    public int getFullPermitsLength() {
        return lockFull.availablePermits();
    }

    public int getEmptyPermitsLength() {
        return lockEmpty.availablePermits();
    }

    // signal for the bounded buffer to freeze read access and to free
    // up all tickets trying to delete
    public synchronized void freeze() {
        freeze = true;

        // free all delete tickets
        lockMutex.release(lockFull.getQueueLength());
        lockFull.release(lockFull.getQueueLength());
    }

    /**
     * Get the size of the buffer.
     * 
     * @return The size of the buffer.
     */
    public int size() {
        int cpySize = 0;

        try {
            lockMutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        cpySize = size;

        lockMutex.release();

        return cpySize;
    }
}
