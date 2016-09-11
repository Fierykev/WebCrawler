package assignment;

import java.io.*;
import java.util.ArrayList;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient ArrayList<WebIndex> combinedIndex;

    // CAN MODIFY OBJECT SERIALIZATION LEARN ABOUT TRANSIENT CAN BE MULTI FILES
    public static Index load(String filename)
            throws IOException, ClassNotFoundException {
        // get the actual file name and the file extension
        int lastPoint = filename.lastIndexOf('.');
        String actualFileName = filename.substring(0, lastPoint);
        String extension = filename.substring(lastPoint);

        // create the index
        Index index = new WebIndex();
        index.initCombinedIndex();

        // read until a file cannot be found
        int iteration = 0;
        while (true) {
            File file = new File(actualFileName + iteration + extension);

            boolean exists = file.exists();

            if (exists) {
                index.addCombinedIndex(WebIndex.load(file.getPath()));
            } else {
                break;
            }
            iteration++;
        }

        return index;
    }

    public void save(String filename) throws IOException {
        WebIndex.save(filename, this);
    }

    /**
     * Init the combine ArrayList
     */
    public void initCombinedIndex() {
        combinedIndex = new ArrayList<>();
    }

    /**
     * Add to the combined index.
     * 
     * @param wi
     *            The element to add to the combined index.
     */
    public void addCombinedIndex(WebIndex wi) {
        combinedIndex.add(wi);
    }

    /**
     * Get the combined index.
     * 
     * @return The combined index.
     */
    public ArrayList<WebIndex> getCombinedIndex() {
        return combinedIndex;
    }
}
