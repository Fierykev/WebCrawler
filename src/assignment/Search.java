package assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class Search implements Runnable {

    private IndexPriority table;

    private int tableID, tableNumber;

    private String phrase;

    private ConcurrentHashMap<Pair<Integer,
        Pair<Integer, Integer>>, Object> map;

    /**
     * Search for a string in the data structure.
     * 
     * @param tableID
     * 
     * @param s
     * 
     */
    public Search(IndexPriority table, int tableNumber, int tableID,
            String phrase,
            ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object> map2) {
        this.table = table;
        this.tableNumber = tableNumber;
        this.tableID = tableID;
        this.phrase = phrase;
        this.map = map2;
    }

    @Override
    public void run() {
        // split the word in the case of a phrase query
        String wordList[] = phrase.split("\\s");

        // get the first word
        String word = wordList[0];

        // store the locations of the last word found

        // search the index for the first word
        int index = BinarySearch.binarySearch(table.data,
                new SetFirstElCompare(word), 0, table.data.size() - 1);

        // not found
        if (index == -1)
            return;

        // store the information associated with the word
        ArrayList<Pair<Integer,
            ArrayList<Integer>>> possibleURLs = new ArrayList<>();

        for (int i = 0; i < table.data.get(index).getSecondElement()
                .getFirstElement().size(); i++) {

            Pair<Integer, Integer> urlInfo = table.data.get(index)
                    .getSecondElement().getFirstElement().get(i);

            // add in the url to the data set
            possibleURLs.add(new Pair<>(urlInfo.getFirstElement(),
                    new ArrayList<Integer>()));

            // add in all values associated with that url
            // find the start and end of the data block
            int start = urlInfo.getSecondElement();
            int end;

            if (i + 1 < table.data.get(index).getSecondElement()
                    .getFirstElement().size())
                end = table.data.get(index).getSecondElement().getFirstElement()
                        .get(i + 1).getSecondElement() - 1;
            else
                end = table.data.get(index).getSecondElement()
                        .getSecondElement().size() - 1;

            for (int j = start; j <= end; j++) {
                // copy the data block into the possible URLs
                possibleURLs.get(possibleURLs.size() - 1).getSecondElement()
                        .add(table.data.get(index).getSecondElement()
                                .getSecondElement().get(j).position);
            }
        }

        // get the data block
        Pair<ArrayList<Pair<Integer, Integer>>,
            ArrayList<IndexTable>> dataBlock = table.data
                .get(index).getSecondElement();

        // look for the rest of the data
        for (int i = 1; i < wordList.length; i++) {
            // get the current word
            word = wordList[i];

            // get the next index
            index = BinarySearch.binarySearch(table.data,
                    new SetFirstElCompare<String>(word), 0,
                    table.data.size() - 1);

            // not found
            if (index == -1)
                return;

            // update the data block
            dataBlock = table.data.get(index).getSecondElement();

            // get the URL block
            ArrayList<Pair<Integer, Integer>> urlBlock = dataBlock
                    .getFirstElement();

            for (int j = 0; j < possibleURLs.size(); j++) {
                // binary search the block for the URL desired
                final int urlDataIndex = BinarySearch.binarySearch(urlBlock,
                        new SetFirstElCompare<Integer>(
                                possibleURLs.get(j).getFirstElement()),
                        0, urlBlock.size() - 1);

                // not a valid URL in the set so remove it
                if (urlDataIndex == -1) {
                    possibleURLs.remove(j);
                    j--;

                    continue;
                }

                // find the start and end of the data block
                int start = urlBlock.get(urlDataIndex).getSecondElement();
                int end;

                if (urlDataIndex + 1 < urlBlock.size())
                    end = urlBlock.get(urlDataIndex + 1).getSecondElement() - 1;
                else {
                    end = dataBlock.getSecondElement().size() - 1;
                }

                // binary search the URL to see if the data comes right after
                // the last set of data
                for (int k = 0; k < possibleURLs.get(j).getSecondElement()
                        .size(); k++) {

                    // check the word comes after what was previously checked
                    final int results = BinarySearch.binarySearch(
                            dataBlock.getSecondElement(),
                            new IndexTableCompare<Integer>(possibleURLs.get(j)
                                    .getSecondElement().get(k) + i),
                            start, end);

                    // remove the result as it is not valid
                    if (results == -1) {
                        possibleURLs.get(j).getSecondElement().remove(k);

                        k--;
                    }
                }

                // remove the url if it has no elements
                if (possibleURLs.get(j).getSecondElement().size() == 0) {
                    possibleURLs.remove(j);

                    j--;
                }
            }
        }

        // add the key and value to the map
        for (Pair<Integer, ArrayList<Integer>> toMap : possibleURLs)
            map.put(new Pair<>(tableNumber,
                    new Pair<>(tableID, toMap.getFirstElement())),
                    new Object());
    }
}

class SetFirstElCompare<K extends Comparable> implements Comparable {
    private K el;

    SetFirstElCompare(K el) {
        this.el = el;
    }

    @Override
    public int compareTo(Object o) {

        // not a pair
        if (!(o instanceof Pair<?, ?>))
            return -1;

        final Pair<?, ?> pair = (Pair<?, ?>) o;

        // run the comparison
        return el.compareTo(pair.getFirstElement());
    }
}

class IndexTableCompare<K extends Comparable> implements Comparable {
    private K el;

    IndexTableCompare(K el) {
        this.el = el;
    }

    @Override
    public int compareTo(Object o) {

        // not an index table
        if (!(o instanceof IndexTable))
            return -1;

        final IndexTable it = (IndexTable) o;

        // run the comparison
        return el.compareTo(it.position);
    }
}