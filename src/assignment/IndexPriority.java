package assignment;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class IndexPriority implements Comparable<IndexPriority>, Serializable {
    private static final long serialVersionUID = 1L;

    public int bytes;

    public HashMap<Integer, URL> URLsymbolTable;

    public ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
        ArrayList<IndexTable>>>> data;

    public IndexPriority(HashMap<Integer, URL> URLsymbolTable,
            ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                ArrayList<IndexTable>>>> data,
            int bytes) {

        this.URLsymbolTable = URLsymbolTable;

        this.data = data;

        this.bytes = bytes;
    }

    public IndexPriority(
            Pair<URL, ArrayList<Pair<String, Pair<Pair<Integer, Integer>
                , ArrayList<IndexTable>>>>> unformattedData) {

        URLsymbolTable = new HashMap<>();
        URLsymbolTable.put(0, unformattedData.getFirstElement());

        // add URL to the bytes
        bytes += 4;
        bytes += 4;
        bytes += unformattedData.getFirstElement().toString().getBytes().length;

        data = new ArrayList<>();

        for (Pair<String, Pair<Pair<Integer, Integer>,
                ArrayList<IndexTable>>> addData : unformattedData
                .getSecondElement()) {
            // create the new data list of URL's
            ArrayList<Pair<Integer, Integer>> URLList = new ArrayList<>();
            URLList.add(addData.getSecondElement().getFirstElement());

            // convert and add the data to the list
            data.add(
                    new Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                        ArrayList<IndexTable>>>(
                            addData.getFirstElement(),
                            new Pair<ArrayList<Pair<Integer, Integer>>,
                                ArrayList<IndexTable>>(
                                    URLList, addData.getSecondElement()
                                            .getSecondElement())));

            // add to the bytes
            bytes += 8;
            bytes += 4;
            bytes += addData.getFirstElement().getBytes().length;
            bytes += 8;
            bytes += addData.getSecondElement().getSecondElement().size() * 4;
        }
    }

    /**
     * Calculate the size of a table.
     * @return The size of the table in bytes.
     */
    @Deprecated
    public static int calcSize(HashMap<Integer, URL> URLsymbolTableIn,
            ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
            ArrayList<IndexTable>>>> dataIn) {
        int size = 0;
        
        for (Entry<Integer, URL> urlPair : URLsymbolTableIn.entrySet()) {
            size += 4;
            size += 4;
            size += urlPair.getValue().toString().getBytes().length;
        }

        for (Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                ArrayList<IndexTable>>> data : dataIn) {
            size += 4;
            size += data.getFirstElement().getBytes().length;

            size += 4;

            for (Pair<Integer, Integer> urlPair : data.getSecondElement()
                    .getFirstElement()) {
                size += 4;
                size += 4;
            }

            size += 4;

            for (IndexTable indexTable : data.getSecondElement()
                    .getSecondElement())
                size += 4;
        }

        return size;
    }

    /**
     * The size of the data block.
     * 
     * @return The size of the data block.
     */

    public int size() {
        return data.size();
    }

    public void addBytes(int bytes) {

    }

    @Override
    public int compareTo(IndexPriority indexPrio) {
        // compare based on size of table
        return data.size() - indexPrio.data.size();
    }

}