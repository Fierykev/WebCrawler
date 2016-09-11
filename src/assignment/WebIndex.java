package assignment;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class WebIndex extends Index {
    // TODO: look up PAGE RANK
    private static final long serialVersionUID = 657246932553471981L;

    public transient ArrayList<IndexPriority> table = new ArrayList<>();

    /**
     * This method calculate the byte size of a WebIndex.
     * 
     * @param webIndex
     *            The WebIndex to look at.
     * @return The number of bytes saving this WebIndex will take.
     */
    @Deprecated
    public static int calcSize(WebIndex webIndex) {
        int size = 4;

        // calculate the size
        for (IndexPriority ip : webIndex.table) {
            size += 4;

            for (Entry<Integer, URL> urlPair : ip.URLsymbolTable.entrySet()) {
                size += 4;
                size += 4;
                size += urlPair.getValue().toString().getBytes().length;
            }

            size += 4;

            for (Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                    ArrayList<IndexTable>>> data : ip.data) {
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
        }

        return size;
    }

    /**
     * Save an Index to file.
     * 
     * @param filename
     *            The filename to save to.
     * @param index
     *            The Index to be saved.
     */
    public static void save(String filename, Index index) {

        // can't be saved
        if (!(index instanceof WebIndex))
            return;

        WebIndex webIndex = (WebIndex) index;

        // calculate the size
        int size = 4;

        for (IndexPriority ip : webIndex.table) {
            size += ip.bytes + 8;
        }

        try {
            RandomAccessFile out = new RandomAccessFile(filename, "rw");
            FileChannel file = out.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(size);

            // cannot use file mapping due to the Java bug
            // TODO: reimplement mapping when the bug is addressed
            // file.map(FileChannel.MapMode.READ_WRITE, 0, size);

            // write the number of tables
            buffer.putInt(webIndex.table.size());

            // write the HashMap
            for (IndexPriority ip : webIndex.table) {
                // write the size of the URL table
                buffer.putInt(ip.URLsymbolTable.size());

                // write the table
                for (Entry<Integer, URL> urlPair : ip.URLsymbolTable
                        .entrySet()) {
                    buffer.putInt(urlPair.getKey());
                    buffer.putInt(
                            urlPair.getValue().toString().getBytes().length);
                    buffer.put(urlPair.getValue().toString().getBytes());
                }

                // write the size of the data info
                buffer.putInt(ip.data.size());

                // write the data
                for (Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                        ArrayList<IndexTable>>> data : ip.data) {
                    // write the key
                    buffer.putInt(data.getFirstElement().getBytes().length);
                    buffer.put(data.getFirstElement().getBytes());

                    // write the URL block size
                    buffer.putInt(
                            data.getSecondElement().getFirstElement().size());

                    // write the URL block
                    for (Pair<Integer, Integer> urlPair : data
                            .getSecondElement().getFirstElement()) {
                        buffer.putInt(urlPair.getFirstElement());
                        buffer.putInt(urlPair.getSecondElement());
                    }

                    // write the IndexTable block size
                    buffer.putInt(
                            data.getSecondElement().getSecondElement().size());

                    // write the IndexTable
                    for (IndexTable indexTable : data.getSecondElement()
                            .getSecondElement())
                        buffer.putInt(indexTable.position);
                }
            }

            // write the buffer

            buffer.flip();

            while (buffer.hasRemaining()) {
                file.write(buffer);
            }

            // close the file

            file.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read an integer in.
     * 
     * @param in
     *            The InputStream.
     * @return The integer read in.
     * @throws IOException
     */
    public static int readInt(InputStream in) throws IOException {
        return (int) (in.read() << 32 | in.read() << 16 | in.read() << 8
                | in.read());
    }

    /**
     * Read a string in.
     * 
     * @param in
     *            The Input Stream.
     * @param len
     *            The length of String.
     * @return The String after being read in.
     * @throws IOException
     */
    public static String readString(InputStream in, int len)
            throws IOException {
        byte bytes[] = new byte[len];

        // read the bytes
        in.read(bytes, 0, len);

        return new String(bytes);
    }

    /**
     * Load a file into memory.
     * 
     * @param filename
     *            The file to load.
     * @return The WebIndex representation of the file.
     */
    public static WebIndex load(String filename) {
        WebIndex webIndex = null;

        try {
            InputStream in =
                    new BufferedInputStream(new FileInputStream(filename));

            webIndex = new WebIndex();

            // create the table
            webIndex.table = new ArrayList<>();

            final int numberOfTables = readInt(in); // the number of tables to
                                                    // read

            for (int i = 0; i < numberOfTables; i++) {

                final int symbolTableSize = readInt(in); // size of the symbol
                                                         // table

                HashMap<Integer, URL> URLsymbolTable = new HashMap<>();

                // add all key, value pairs to the table
                for (int j = 0; j < symbolTableSize; j++) {
                    URLsymbolTable.put(readInt(in),
                            new URL(readString(in, readInt(in))));
                }

                // get the size of the data info
                final int dataInfoSize = readInt(in);

                ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                    ArrayList<IndexTable>>>> data =
                        new ArrayList<>();

                for (int j = 0; j < dataInfoSize; j++) {
                    final String key = readString(in, readInt(in));

                    final int urlBlockSize = readInt(in);

                    ArrayList<Pair<Integer, Integer>> urlBlock =
                            new ArrayList<>();

                    // add in the URL Block data
                    for (int k = 0; k < urlBlockSize; k++)
                        urlBlock.add(new Pair<>(readInt(in), readInt(in)));

                    final int indexTableSize = readInt(in);

                    ArrayList<IndexTable> indexTable = new ArrayList<>();

                    // add in the index table
                    for (int k = 0; k < indexTableSize; k++)
                        indexTable.add(new IndexTable(readInt(in)));

                    // compile the data collected
                    data.add(new Pair<>(key, new Pair<>(urlBlock, indexTable)));
                }

                // add the data to the table
                webIndex.table.add(new IndexPriority(URLsymbolTable, data, 0));
            }

            in.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return webIndex;
    }

    /**
     * Write the WebIndex to a file.
     * 
     * @param out
     *            The out stream.
     * @throws IOException
     */
    @Deprecated
    private void writeObject(ObjectOutputStream out) throws IOException {
        // default write operation
        out.defaultWriteObject();

        // write the number of tables
        out.writeInt(table.size());

        // write the HashMap
        for (IndexPriority ip : table) {
            // write the size of the URL table
            out.writeInt(ip.URLsymbolTable.size());

            // write the table
            for (Entry<Integer, URL> urlPair : ip.URLsymbolTable.entrySet()) {
                out.writeInt(urlPair.getKey());
                out.writeObject(urlPair.getValue());
            }

            // write the size of the data info
            out.writeInt(ip.data.size());

            // write the data
            for (Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                    ArrayList<IndexTable>>> data : ip.data) {
                // write the key
                out.writeObject(data.getFirstElement());

                // write the URL block size
                out.writeInt(data.getSecondElement().getFirstElement().size());

                // write the URL block
                for (Pair<Integer, Integer> urlPair : data.getSecondElement()
                        .getFirstElement()) {
                    out.writeInt(urlPair.getFirstElement());
                    out.writeInt(urlPair.getSecondElement());
                }

                // write the IndexTable block size
                out.writeInt(data.getSecondElement().getSecondElement().size());

                // write the IndexTable
                for (IndexTable indexTable : data.getSecondElement()
                        .getSecondElement())
                    out.writeInt(indexTable.position);
            }
        }
    }

    /**
     * Read an object into memory.
     * 
     * @param in
     *            The input stream.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Deprecated
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        // create the table
        table = new ArrayList<>();

        final int numberOfTables = in.readInt(); // the number of tables to read

        for (int i = 0; i < numberOfTables; i++) {

            final int symbolTableSize = in.readInt(); // size of the symbol
                                                      // table

            HashMap<Integer, URL> URLsymbolTable = new HashMap<>();

            // add all key, value pairs to the table
            for (int j = 0; j < symbolTableSize; j++) {
                URLsymbolTable.put(in.readInt(), (URL) in.readObject());
            }

            // get the size of the data info
            final int dataInfoSize = in.readInt();

            ArrayList<Pair<String, Pair<ArrayList<Pair<Integer, Integer>>,
                ArrayList<IndexTable>>>> data =
                    new ArrayList<>();

            for (int j = 0; j < dataInfoSize; j++) {
                final String key = (String) in.readObject();

                final int urlBlockSize = in.readInt();

                ArrayList<Pair<Integer, Integer>> urlBlock = new ArrayList<>();

                // add in the URL Block data
                for (int k = 0; k < urlBlockSize; k++)
                    urlBlock.add(new Pair<>(in.readInt(), in.readInt()));

                final int indexTableSize = in.readInt();

                ArrayList<IndexTable> indexTable = new ArrayList<>();

                // add in the index table
                for (int k = 0; k < indexTableSize; k++)
                    indexTable.add(new IndexTable(in.readInt()));

                // compile the data collected
                data.add(new Pair<>(key, new Pair<>(urlBlock, indexTable)));
            }

            // add the data to the table
            table.add(new IndexPriority(URLsymbolTable, data, 0));
        }
    }
}
