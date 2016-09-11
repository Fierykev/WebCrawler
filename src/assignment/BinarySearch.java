package assignment;

import java.util.ArrayList;

import BTree.NodeData;

public class BinarySearch {

    /**
     * Binary search the array to find where to insert.
     * 
     * @param find
     * @return
     */
    public static int binarySearch(ArrayList obj, Comparable find, int start,
            int end) {
        int low = start, high = end;
        int mid;

        while (low <= high) {
            // get the middle
            mid = (high - low) / 2 + low;

            if (find.compareTo(obj.get(mid)) < 0)
                high = mid - 1;
            else if (find.compareTo(obj.get(mid)) > 0)
                low = mid + 1;
            else
                return mid;
        }

        return -1;
    }

    /**
     * Binary search for BTree
     * 
     * @param el
     * @return
     */
    public static Pair<Boolean, Integer> binarySearchBTree(NodeData[] obj,
            Comparable el, int start, int end) {
        int low = start, high = end;
        int mid;

        while (low <= high) {
            // get the middle
            mid = (high - low) / 2 + low;

            if (el.compareTo(obj[mid].key) < 0)
                high = mid - 1;
            else if (el.compareTo(obj[mid].key) > 0)
                low = mid + 1;
            else
                return new Pair<>(true, mid);
        }

        return new Pair<>(false, low);
    }
}
