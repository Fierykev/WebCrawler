package BTree;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.TreeMap;

import assignment.Pair;

public class BTreeHashSet<K extends Comparable> {

    public static int maxHashSetSize = 10000000;

    private final int INIT_FREQ = 10;

    public static boolean enableBTree = false;

    BTree<K> bTree;

    HashMap<K, Integer> hashMap;

    PriorityQueue<ComparablePair<Integer, K>> priorityQueue;

    /**
     * Create a BTree that stores it's key as hashes of the object it stores.
     * 
     * @param minSize
     */
    public BTreeHashSet(int minSize) {
        if (enableBTree) {
            bTree = new BTree<K>(minSize);
            priorityQueue = new PriorityQueue<>();
        }

        hashMap = new HashMap<K, Integer>();
    }

    /**
     * Add a value to be stored
     * 
     * @param value
     * @return
     */
    public boolean add(K value) {
        // do not invoke the b-tree and priority queue until a threshold is
        // reached

        if (!enableBTree)
            return hashMap.put(value, 0) == null;
        else {
            // number of occurrences
            Integer frequency = hashMap.get(value);

            // already exists
            if (frequency != null) {
                // remove from the queue
                priorityQueue.remove(new ComparablePair<>(frequency, value));

                // re-add to the queue
                priorityQueue.add(new ComparablePair<>(frequency + 1, value));

                return false;
            } else if (!bTree.find(value)) // does not exist in map or bTree
            {
                // if no room available so remove the smallest element from the
                // queue and the map and add it to the b-tree
                if (maxHashSetSize <= priorityQueue.size()) {

                    // remove from queue
                    K el = priorityQueue.poll().data.getSecondElement();

                    // remove from map
                    hashMap.remove(el);

                    // add to b-tree
                    bTree.add(el);

                    // add the new element to the queue
                    priorityQueue.add(new ComparablePair<>(INIT_FREQ, value));

                    // put into hashMap
                    hashMap.put(value, INIT_FREQ);
                } else {
                    // add the new element to the queue
                    priorityQueue.add(new ComparablePair<>(0, value));

                    // put into hashMap
                    hashMap.put(value, 0);
                }

                return true;
            }
            
            // not found
            return false;
        }
    }
}

class ComparablePair<K extends Comparable, V extends Comparable>
        implements
            Comparable {
    Pair<K, V> data;

    public ComparablePair(K key, V value) {
        data = new Pair<>(key, value);
    }

    @Override
    public int compareTo(Object obj) {
        if (!(obj instanceof ComparablePair))
            return -1;

        ComparablePair cp = (ComparablePair) obj;

        int difference = data.getFirstElement()
                .compareTo(cp.data.getFirstElement());

        if (difference == 0) {
            return data.getSecondElement()
                    .compareTo(cp.data.getSecondElement());
        }

        return difference;
    }

}
