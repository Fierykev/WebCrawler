package BTree;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import assignment.BinarySearch;
import assignment.Index;
import assignment.Pair;

public class BTree<K> {

    private final int maxSize;
    private final int minSize;

    private Node root;

    public BTree(int minSize) {
        this.maxSize = minSize * 2 - 1;
        this.minSize = minSize;

        root = new Node(maxSize);
        root.leaf = true;

        writeBlock(root);
    }

    /**
     * Write block n into memory
     * 
     * @param n
     */
    private void writeBlock(Node n) {
        // write the data
        FileOutputStream fout = null;
        GZIPOutputStream gzipout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream("BTree/" + n.ID);
            gzipout = new GZIPOutputStream(fout);
            oout = new ObjectOutputStream(gzipout);

            // write the data
            oout.writeObject(n);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oout != null)
                    oout.close();
                else if (gzipout != null)
                    gzipout.close();
                else if (fout != null)
                    fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read block n into memory
     * 
     * @param id
     * @return
     */
    private Node readBlock(int id) {
        // read the block
        InputStream fin = null;
        GZIPInputStream gzipin = null;
        ObjectInputStream oin = null;
        try {
            fin = new FileInputStream("BTree/" + id);
            gzipin = new GZIPInputStream(fin);
            oin = new ObjectInputStream(gzipin);

            // load the data
            return (Node) oin.readObject();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (oin != null)
                    oin.close();
                else if (gzipin != null)
                    gzipin.close();
                else if (fin != null)
                    fin.close();
            } catch (IOException e) {}
        }

        return null;
    }

    /**
     * Find a block of data in the B tree.
     * 
     * @param el
     * @param n
     * @return
     */
    private boolean find(K el, Node n) {

        while (n != null) {
            // check where the leaf might be
            final Pair<Boolean, Integer> index = BinarySearch.binarySearchBTree(
                    n.data, (Comparable) el, 0, n.blocksFilled - 1);

            // check if found
            if (index.getFirstElement()) {
                K returnData = (K) n.data[index.getSecondElement()].key;

                for (int i = 0; i < n.blocksFilled; i++) {
                    if (n.data[i].key == null)
                        System.exit(0);
                }

                return true;
            }

            // nothing else to search since leaf
            if (n.leaf)
                return false;

            // set n
            n = readBlock(n.data[index.getSecondElement()].child);
        }

        return false;
    }

    private void insert(K el, Node n) {
        if (n.leaf) {
            int keyPos;

            // make room for the element to be inserted by moving things to the
            // right

            for (keyPos = n.blocksFilled; 1 <= keyPos
                    && 0 < n.data[keyPos - 1].key.compareTo(el); keyPos--) {
                n.data[keyPos].key = n.data[keyPos - 1].key;
            }

            // place the new key
            n.data[keyPos].key = (Comparable) el;

            // update n's size
            n.blocksFilled++;

            // re-write the block
            writeBlock(n);
        } else {
            int keyPos;

            // find the child to look at
            // keyPos = BinarySearch.binarySearchBTree(n.data,
            // (Pair<Comparable, Object>) el, 0, n.blocksFilled - 1)
            // .getSecondElement();

            for (keyPos = n.blocksFilled; 1 <= keyPos
                    && 0 < n.data[keyPos - 1].key.compareTo(el); keyPos--) {}

            // get the data about the child
            Node child = readBlock(n.data[keyPos].child);

            // child is full
            if (child.blocksFilled == maxSize) {

                // split the child
                split(n, child, keyPos);

                // update key position for the next child
                if (n.data[keyPos].key.compareTo(el) < 0)
                    keyPos++;

                // read in the new child
                child = readBlock(n.data[keyPos].child);
            }

            insert(el, child);

            return;
        }
    }

    /**
     * Split the node.
     * 
     * @param n
     *            The new node
     * @param s
     * @param index
     * @return
     */
    private void split(Node n, Node s, int index) {
        // create a new node
        Node newN = new Node(maxSize);

        // copy the leaf
        newN.leaf = s.leaf;

        // remove one from the blocks filled
        newN.blocksFilled = minSize - 1;

        // copy elements into new node
        for (int i = 0; i < minSize - 1; i++) {
            // copy data
            newN.data[i].key = s.data[i + minSize].key;

            // remove data
            s.data[i + minSize].key = null;
        }

        // copy over the children if s is not a leaf
        if (!s.leaf) {
            for (int i = 0; i < minSize; i++) {
                // copy child
                newN.data[i].child = s.data[i + minSize].child;

                // remove child
                s.data[i + minSize].child = null;
            }
        }

        // update the size of s
        s.blocksFilled = minSize - 1;

        // move children over to the right to make room to insert
        for (int i = n.blocksFilled + 1; index + 1 < i; i--)
            n.data[i].child = n.data[i - 1].child;

        // move keys over to the right to make room to insert
        for (int i = n.blocksFilled; index < i; i--)
            n.data[i].key = n.data[i - 1].key;

        // place the new node
        n.data[index + 1].child = newN.ID;

        // place the new middle key
        n.data[index].key = s.data[minSize - 1].key;

        // destroy the link to the key
        s.data[minSize - 1].key = null;

        n.blocksFilled++;

        // write to disk the new data
        writeBlock(newN);
        writeBlock(n);
        writeBlock(s);
    }

    /**
     * Add an element to the tree.
     * 
     * @param el
     */
    public void add(K el) {
        // check if root is full
        if (root.blocksFilled == maxSize) {
            // run a split on the root
            Node newN = new Node(maxSize);

            // swap new node with the root
            Node formerRoot = root;
            root = newN;
            newN.data[0].child = formerRoot.ID;

            // split the children of the former root
            split(newN, formerRoot, 0);

            // insert an element now since the node isn't full anymore
            insert(el, newN);
        } else {
            insert(el, root);
        }
    }

    /**
     * Retun the value associated with the key.
     * 
     * @param el
     *            The key to look for.
     * @return Returns null if no key exists.
     */
    public boolean find(K el) {

        return find(el, root);
    }

    private StringBuilder BTreetoString(Node n, StringBuilder s) {
        if (n == null)
            return s;

        for (int i = 0; i < n.blocksFilled && !n.leaf; i++) {
            s.append(n.data[i].key + "\n");

            BTreetoString(readBlock(n.data[i].child), s);
        }

        // traverse last node
        if (!n.leaf)
            BTreetoString(readBlock(n.data[n.blocksFilled].child), s);

        return s;
    }

    public String toString() {
        return BTreetoString(root, new StringBuilder()).toString();
    }
}

class Node implements Serializable {
    NodeData data[];

    static int IDAssigned = 0;

    int ID;

    int blocksFilled = 0;

    boolean leaf = false;

    public Node(int size) {
        ID = IDAssigned++;

        size++;

        data = new NodeData[size];

        for (int i = 0; i < size; i++)
            data[i] = new NodeData();
    }
}
