package assignment;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;

import tests.TestSearchHandler;

public class WebIndexHelper {

    /**
     * Merge two IndexPriorities into one.
     * 
     * @param data1
     *            The first IndexPriority.
     * @param data2
     *            The second IndexPriority.
     * @return The merged IndexPriority.
     */
    public IndexPriority mergeIndexPriority(IndexPriority data1,
            IndexPriority data2) {
        // since both are sorted, can use a mergesort like method to combine
        // them
        ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
            ArrayList<IndexTable>>>> newData = new ArrayList<>();

        int bytes = data1.bytes + data2.bytes;

        int index1 = 0, index2 = 0;

        final int IDOffset = data1.URLsymbolTable.size();

        HashMap<Integer, URL> newURLTable = new HashMap<Integer, URL>(
                data1.URLsymbolTable);

        // put all URLs from data2 into the HashMap
        for (Map.Entry<Integer, URL> m : data2.URLsymbolTable.entrySet())
            newURLTable.put(m.getKey() + IDOffset, m.getValue());

        // merge the data
        while (index1 != data1.size() && index2 != data2.size()) {
            if (data1.data.get(index1).getFirstElement()
                    .compareTo(data2.data.get(index2).getFirstElement()) < 0) {
                newData.add(data1.data.get(index1++));
            } else if (data1.data.get(index1).getFirstElement()
                    .compareTo(data2.data.get(index2).getFirstElement()) > 0) {
                // update the URL table
                ArrayList<Pair<Integer, Integer>> URLList = new ArrayList<>();

                for (Pair<Integer, Integer> urlData : data2.data.get(index2)
                        .getSecondElement().getFirstElement()) {
                    URLList.add(new Pair<Integer, Integer>(
                            urlData.getFirstElement() + IDOffset,
                            urlData.getSecondElement()));
                }

                // add the data to the list
                newData.add(
                        new Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                            ArrayList<IndexTable>>>(
                                data2.data.get(index2).getFirstElement(),
                                new Pair<ArrayList<Pair<Integer, Integer>>,
                                    ArrayList<IndexTable>>(
                                        URLList,
                                        data2.data.get(index2)
                                                .getSecondElement()
                                                .getSecondElement())));

                index2++;
            } else {
                // add in the first data set
                newData.add(data1.data.get(index1++));

                // get the block containing specific data
                final Pair<ArrayList<Pair<Integer, Integer>>,
                    ArrayList<IndexTable>> infoBlock = newData
                        .get(newData.size() - 1).getSecondElement();

                // get the offset needed for adding in the new URLs
                final Integer URLBlockOffset = infoBlock.getSecondElement()
                        .size();

                // add in the second data set's URL table
                // update and add the URL table
                for (Pair<Integer, Integer> urlData : data2.data.get(index2)
                        .getSecondElement().getFirstElement()) {
                    infoBlock.getFirstElement().add(new Pair<Integer, Integer>(
                            urlData.getFirstElement() + IDOffset,
                            urlData.getSecondElement() + URLBlockOffset));
                }

                // add in the indices
                infoBlock.getSecondElement().addAll(data2.data.get(index2)
                        .getSecondElement().getSecondElement());

                // subtract from total bytes since the row is combined
                bytes -= 4;
                bytes -= data2.data.get(index2).getFirstElement()
                        .getBytes().length;
                bytes -= 8;

                index2++;
            }
        }

        // add the remaining data into the table from data set 1
        while (index1 != data1.size()) {
            newData.add(data1.data.get(index1++));
        }

        // add the remaining data into the table from data set 2
        while (index2 != data2.size()) {
            // update the URL table
            ArrayList<Pair<Integer, Integer>> URLList = new ArrayList<>();

            for (Pair<Integer, Integer> urlData : data2.data.get(index2)
                    .getSecondElement().getFirstElement()) {
                URLList.add(new Pair<Integer, Integer>(
                        urlData.getFirstElement() + IDOffset,
                        urlData.getSecondElement()));
            }

            // add the data to the list
            newData.add(
                    new Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                        ArrayList<IndexTable>>>(
                            data2.data.get(index2).getFirstElement(),
                            new Pair<ArrayList<Pair<Integer, Integer>>,
                                ArrayList<IndexTable>>(
                                    URLList,
                                    data2.data.get(index2).getSecondElement()
                                            .getSecondElement())));

            index2++;
        }

        return new IndexPriority(newURLTable, newData, bytes);
    }

    /**
     * Merge one URL data with an IndexPriority.
     * 
     * @param data1
     *            The first IndexPriority.
     * @param data2
     *            The second data set.
     * @return The combined IndexPriority.
     */
    public IndexPriority mergeData(IndexPriority data1, URL url,
            ArrayList<Pair<String, Pair<Pair<Integer, Integer>,
                ArrayList<IndexTable>>>> data2) {
        // since both are sorted, can use a mergesort like method to combine
        // them
        ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
            ArrayList<IndexTable>>>> newData = new ArrayList<>();

        int bytes = data1.bytes;

        int index1 = 0, index2 = 0;

        final int IDOffset = data1.URLsymbolTable.size();

        HashMap<Integer, URL> newURLTable = new HashMap<Integer, URL>(
                data1.URLsymbolTable);

        // put the new URL into the HashMap
        newURLTable.put(data1.URLsymbolTable.size(), url);

        // add the new url to the bytes size
        bytes += 4;
        bytes += 4;
        bytes += url.toString().getBytes().length;

        // merge the data
        while (index1 != data1.size() && index2 != data2.size()) {
            if (data1.data.get(index1).getFirstElement()
                    .compareTo(data2.get(index2).getFirstElement()) < 0) {
                newData.add(data1.data.get(index1++));
            } else if (data1.data.get(index1).getFirstElement()
                    .compareTo(data2.get(index2).getFirstElement()) > 0) {
                final Pair<Integer, Integer> URLInfo = data2.get(index2)
                        .getSecondElement().getFirstElement();

                // add the URL table and account for the URL offset
                Pair<Integer, Integer> updatedURLList
                    = new Pair<Integer, Integer>(
                        URLInfo.getFirstElement() + IDOffset,
                        URLInfo.getSecondElement());

                // create the new data list of URL's
                ArrayList<Pair<Integer, Integer>> URLList = new ArrayList<>();
                URLList.add(updatedURLList);

                // convert and add the data to the list
                newData.add(
                        new Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                            ArrayList<IndexTable>>>(
                                data2.get(index2).getFirstElement(),
                                new Pair<ArrayList<Pair<Integer, Integer>>,
                                    ArrayList<IndexTable>>(
                                        URLList,
                                        data2.get(index2).getSecondElement()
                                                .getSecondElement())));

                // add in the new bytes
                bytes += 4;
                bytes += data2.get(index2).getFirstElement()
                        .getBytes().length;
                bytes += 8;
                bytes += 8;
                bytes += data2.get(index2).getSecondElement().getSecondElement()
                        .size() * 4;

                index2++;
            } else {
                // add in the first data set
                newData.add(data1.data.get(index1++));

                final Pair<ArrayList<Pair<Integer, Integer>>,
                    ArrayList<IndexTable>> infoBlock = newData
                        .get(newData.size() - 1).getSecondElement();

                final Integer lastURLBlockPosition = infoBlock
                        .getSecondElement().size();

                // add in the second data set's URL table
                final Pair<Integer, Integer> URLInfo = data2.get(index2)
                        .getSecondElement().getFirstElement();

                infoBlock.getFirstElement().add(new Pair<Integer, Integer>(
                        URLInfo.getFirstElement() + IDOffset,
                        URLInfo.getSecondElement() + lastURLBlockPosition));

                // add in the indices
                infoBlock.getSecondElement().addAll(data2.get(index2)
                        .getSecondElement().getSecondElement());

                // add in the new bytes
                bytes += 8;
                bytes += data2.get(index2).getSecondElement().getSecondElement()
                        .size() * 4;

                index2++;
            }
        }

        // add the remaining data into the table from data set 1
        while (index1 != data1.size()) {
            newData.add(data1.data.get(index1++));
        }

        // add the remaining data into the table from data set 2
        while (index2 != data2.size()) {
            final Pair<Integer, Integer> URLInfo = data2.get(index2)
                    .getSecondElement().getFirstElement();

            // add the URL table and account for the URL offset
            Pair<Integer, Integer> updatedURLList = new Pair<Integer, Integer>(
                    URLInfo.getFirstElement() + IDOffset,
                    URLInfo.getSecondElement());

            // create the new data list of URL's
            ArrayList<Pair<Integer, Integer>> URLList = new ArrayList<>();
            URLList.add(updatedURLList);

            // convert and add the data to the list
            newData.add(
                    new Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                        ArrayList<IndexTable>>>(
                            data2.get(index2).getFirstElement(),
                            new Pair<ArrayList<Pair<Integer, Integer>>,
                                ArrayList<IndexTable>>(
                                    URLList,
                                    data2.get(index2).getSecondElement()
                                            .getSecondElement())));

            // add in the new bytes
            bytes += 4;
            bytes += data2.get(index2).getFirstElement().getBytes().length;
            bytes += 8;
            bytes += 8;
            bytes += data2.get(index2).getSecondElement().getSecondElement()
                    .size() * 4;

            index2++;
        }

        return new IndexPriority(newURLTable, newData, bytes);
    }
}