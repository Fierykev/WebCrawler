package tests;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.net.*;

import org.attoparser.ParseException;
import org.attoparser.simple.*;

import assignment.CrawlingMarkupHandler;
import assignment.HTMLSpecialChars;
import assignment.Pair;

public class TestSearchHandler extends AbstractSimpleMarkupHandler {

    public static final HashSet<Character> punctuation = new HashSet<>();
    static {
        punctuation.add('\\');
        punctuation.add('/');
        punctuation.add('"');
        punctuation.add('\n');
        punctuation.add('\r');
        punctuation.add('\t');
        punctuation.add(',');
        punctuation.add(':');
        punctuation.add('[');
        punctuation.add(']');
        punctuation.add('.');
        punctuation.add(';');
        punctuation.add('!');
        punctuation.add('?');
        punctuation.add('(');
        punctuation.add(')');
        punctuation.add('{');
        punctuation.add('}');
        punctuation.add('<');
        punctuation.add('>');
        punctuation.add('%');
    }

    public static HashSet<URL> foundURLs = new HashSet<>();

    private LinkedList<URL> newURLs = new LinkedList<>();

    public static HashMap<String, HashSet<Pair<URL, Integer>>> hashWord = new HashMap<>();

    public static HashSet<URL> errorURLs = new HashSet<>();

    private String s = "";

    private int wordPos = 0;

    private URL curURL;

    private ArrayList<String> refArray = new ArrayList<>();

    private boolean ignoreText = false;

    public TestSearchHandler() {
    }

    /**
     * Set the current URL being looked at.
     * 
     * @param url
     *            The url being looked at.
     */
    public void setURL(URL url) {
        curURL = url;
    }

    /**
     * Store the URL and return if doing so was successful. The method checks
     * the URL exists as well as the fact that the URL is correctly formatted.
     * 
     * @param website
     *            The URL to look at.
     * @return Whether storing it was successful.
     */

    private void storeURL(String website) {

        URL url;

        // search for # and ? to remove queries and anchor links
        char c;
        String newWebsite = "";

        // remove anchor links by finding # and ?
        for (int i = 0; i < website.length(); i++) {
            c = Character.toLowerCase(website.charAt(i));

            if (c == '#' || c == '?')
                break;
            else
                newWebsite += c;
        }

        // convert the url to a path on the computer
        try {
            url = new URL(curURL, newWebsite);
        } catch (MalformedURLException e) {
            // no such url
            return;
        }

        // check the file exists
        if (!(new File(url.getFile()).exists()))
            return;

        // add the URL
        if (foundURLs.add(url))
            newURLs.add(url);
    }

    /**
     * This method is to communicate any URLs we find back to the Crawler.
     */
    public LinkedList<URL> newURLs() {
        return newURLs;
    }

    /**
     * These are some of the methods from AbstractSimpleMarkupHandler. All of
     * its method implementations are NoOps, so we've added some things to do;
     * please remove all the extra printing before you turn in your code.
     *
     * Note: each of these methods defines a line and col param, but you
     * probably don't need those values. You can look at the documentation for
     * the superclass to see all of the handler methods.
     */

    /**
     * Called when the parser first starts reading a document.
     * 
     * @param startTimeNanos
     *            the current time (in nanoseconds) when parsing starts
     * @param line
     *            the line of the document where parsing starts
     * @param col
     *            the column of the document where parsing starts
     */
    public void handleDocumentStart(long startTimeNanos, int line, int col) {
        // System.out.println("Start of document");
    }

    /**
     * Called when the parser finishes reading a document.
     * 
     * @param endTimeNanos
     *            the current time (in nanoseconds) when parsing ends
     * @param totalTimeNanos
     *            the difference between current times at the start and end of
     *            parsing
     * @param line
     *            the line of the document where parsing ends
     * @param col
     *            the column of the document where the parsing ends
     */
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos,
            int line, int col) {
        // add the last word to the index for this page
        if (!s.isEmpty()) {
            addWord(s, wordPos++);

            s = "";
        }

        // move the position up by one to put a space between this ref and
        // the other text
        int refPos = wordPos + 1;

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

    public void searchTag(Map<String, String> attributes, String type,
            boolean URL) {
        String val = attributes.get(type);

        // not present
        if (val == null)
            return;

        if (URL) // store the URL
            storeURL(val);
        else // add a reference phrase
            refArray.add(val);
    }

    /**
     * Called at the start of any tag.
     * 
     * @param elementName
     *            the element name (such as "div")
     * @param attributes
     *            the element attributes map, or null if it has no attributes
     * @param line
     *            the line in the document where this elements appears
     * @param col
     *            the column in the document where this element appears
     */
    public void handleOpenElement(String elementName,
            Map<String, String> attributes, int line, int col) {
        // add any lingering text
        if (!s.isEmpty()) {
            // add the word to the index for this page
            addWord(s, wordPos++);

            s = "";
        }

        // check attributes is not null
        if (attributes == null) {
            // check for style and script which have null attributes
            switch (elementName) {
                case "script" :
                    ignoreText = true;
                    break;
                case "style" :
                    ignoreText = true;
                    break;
                case "SCRIPT" :
                    ignoreText = true;
                    break;
                case "STYLE" :
                    ignoreText = true;
                    break;
            }
            return;
        }

        switch (elementName) {
            case "a" :
                searchTag(attributes, "href", true);
                searchTag(attributes, "HREF", true);
                break;
            case "iframe" :
                searchTag(attributes, "src", true);
                searchTag(attributes, "SRC", true);
                break;
            case "A" :
                searchTag(attributes, "href", true);
                searchTag(attributes, "HREF", true);
                break;
            case "IFRAME" :
                searchTag(attributes, "src", true);
                searchTag(attributes, "SRC", true);
                break;
        }
    }

    /**
     * Called for tags which are not closed.
     * 
     * @param elementName
     *            the element name (such as "img")
     * @param attributes
     *            the element attributes map, or null if it has no attributes
     * @param minimized
     *            true if this tag is minimized
     * @param line
     *            the line in the document where this elements appears
     * @param col
     *            the column in the document where this element appears
     */
    public void handleStandaloneElement(String elementName,
            Map<String, String> attributes, boolean minimized, int line,
            int col) {
        // add any lingering text
        if (!s.isEmpty()) {
            // add the word to the index for this page
            addWord(s, wordPos++);

            s = "";
        }

        // check attributes is not null
        if (attributes == null)
            return;

        switch (elementName) {
            case "img" :
                searchTag(attributes, "name", false);
                searchTag(attributes, "NAME", false);
                break;
            case "IMG" :
                searchTag(attributes, "name", false);
                searchTag(attributes, "NAME", false);
                break;
        }
    }

    /**
     * Called at the end of any tag.
     * 
     * @param elementName
     *            the element name (such as "div").
     * @param line
     *            the line in the document where this elements appears.
     * @param col
     *            the column in the document where this element appears.
     */
    public void handleCloseElement(String elementName, int line, int col) {
        // turn off ignore text
        switch (elementName) {
            case "script" :
                ignoreText = false;
                break;
            case "style" :
                ignoreText = false;
                break;
            case "SCRIPT" :
                ignoreText = false;
                break;
            case "STYLE" :
                ignoreText = false;
                break;
        }
    }

    /**
     * Called whenever characters are found inside a tag. Note that the parser
     * is not required to return all characters in the tag in a single chunk.
     * Whitespace is also returned as characters.
     * 
     * @param ch
     *            buffer containint characters; do not modify this buffer
     * @param start
     *            location of 1st character in ch
     * @param length
     *            number of characters in ch
     */
    public void handleText(char ch[], int start, int length, int line,
            int col) {
        // do not parse the text if it is within a script or style tag
        if (ignoreText)
            return;

        // replace all punctuation with blank space
        // replace all punctuation with blank space
        for (int i = start; i < start + length; i++) {
            if (ch[i] == '&' || punctuation.contains(ch[i]) || ch[i] == ' ') {
                if (!s.isEmpty()) {
                    // add the word to the index for this page
                    addWord(s, wordPos);

                    // reset s
                    s = "";

                    wordPos++;
                }

                // store & if escape character
                if (ch[i] == '&')
                    s = "&";
            } else
                s += Character.toLowerCase(ch[i]);
        }
    }

    /**
     * Add a word to the number of words found.
     * 
     * @param s2
     * @param wordPos2
     */

    private void addWord(String s2, int wordPos2) {
        Character c;

        // replace the word with an HTML special character if needed
        if ((c = HTMLSpecialChars.specialChars.get(s2)) != null)
            s2 = c.toString();

        hashWord.putIfAbsent(s2, new HashSet<Pair<URL, Integer>>());

        // add the URL and position
        hashWord.get(s2).add(new Pair<>(curURL, wordPos2));
    }
}
