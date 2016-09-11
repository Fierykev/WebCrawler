package assignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ACertainMagicalIndex extends Index {

    private static final long serialVersionUID = -3231103646713249812L;

    private URL curURL;

    TreeMap<String, Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>> table =
            new TreeMap<>();

    private ArrayList<String> refArray = new ArrayList<>();

    private int lastPosition = 0;

    /**
     * Set the URL being used and add it to the Symbol Table
     * 
     * @param url
     */
    public void setURL(URL url) {
        this.curURL = url;
    }

    /**
     * Get the URL currently being looked at.
     * 
     * @return The URL
     */
    public URL getURL() {
        return curURL;
    }

    /**
     * Add a word to the table
     */
    public void addWord(String word, int position) {

        Character c;

        // replace the word with an HTML special character if needed
        if ((c = HTMLSpecialChars.specialChars.get(word)) != null)
            word = c.toString();

        // search for where the word should be inserted
        table.putIfAbsent(word,
                new Pair<Pair<Integer, Integer>, ArrayList<IndexTable>>(
                        new Pair<Integer, Integer>(0, 0),
                        new ArrayList<IndexTable>()));

        IndexTable tableInput = new IndexTable(position);

        // add to the ArrayList
        table.get(word).getSecondElement().add(tableInput);

        // store the last position
        lastPosition = position;
    }

    /**
     * Add ref word to be processed later.
     */
    public void addRefPhrase(String phrase) {
        refArray.add(phrase);
    }

    /**
     * Finish processing the text.
     */
    public void finishProcessing() {
        // move the position up by one to put a space between this ref and
        // the other text
        int refPos = lastPosition + 1;

        for (String refList : refArray) {
            // skip a space
            refPos++;

            String s = "";

            // replace all punctuation with blank space
            // replace all punctuation with blank space
            for (int i = 0; i < refList.length(); i++) {
                if (refList.charAt(i) == '&'
                        || CrawlingMarkupHandler.punctuation
                                .contains(refList.charAt(i))
                        || refList.charAt(i) == ' ') {
                    if (!s.isEmpty()) {
                        // add the word to the index for this page
                        addWord(s, refPos);

                        // reset s
                        s = "";

                        // increment for next word
                        refPos++;
                    }

                    // store & if escape character
                    if (refList.charAt(i) == '&')
                        s = "&";
                } else
                    s += Character.toLowerCase(refList.charAt(i));
            }
        }
    }

    public String toString() {
        String output = "";

        output += "---------------------------\n";
        output += "INDEX TABLE\n";
        output += "---------------------------\n";
        for (Entry<String, Pair<Pair<Integer, Integer>,
                ArrayList<IndexTable>>> m : table
                .entrySet()) {
            output += m.getKey() + ": ";

            final Pair<Integer, Integer> val = m.getValue().getFirstElement();

            output += "<" + val.getFirstElement() + ", "
                    + val.getSecondElement() + ">";

            for (IndexTable it : m.getValue().getSecondElement())
                output += "[" + it.position + "] ";

            output += "\n";

        }

        return output;
    }
}
