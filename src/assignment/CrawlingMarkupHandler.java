package assignment;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.net.*;

import org.attoparser.ParseException;
import org.attoparser.simple.*;

public class CrawlingMarkupHandler extends AbstractSimpleMarkupHandler {

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

    List<URL> URLList = new LinkedList<URL>();

    ACertainMagicalIndex index = new ACertainMagicalIndex();

    boolean ignoreText = false;

    private String s = "";

    private int wordPos = 0;

    public CrawlingMarkupHandler() {
    }

    /**
     * Set the current URL being looked at.
     * 
     * @param url
     *            The url being looked at.
     */
    public void setURL(URL url) {
        index.setURL(url);
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
            // NOTE: Josh said changing c to lower cause causes problems on Linux
            // c = Character.toLowerCase(website.charAt(i));

            c = website.charAt(i);

            if (c == '#' || c == '?')
                break;
            else
                newWebsite += c;
        }

        // convert the url to a path on the computer
        try {
            url = new URL(index.getURL(), newWebsite);
        } catch (MalformedURLException e) {
            // no such url
            return;
        }

        // check the file exists
        if (!(new File(url.getFile()).exists()))
            return;

        // add the URL
        URLList.add(url);
    }

    /**
     * This method should return a completed Index after we've parsed things.
     */
    public Index getIndex() {
        return index;
    }

    /**
     * This method is to communicate any URLs we find back to the Crawler.
     */
    public List<URL> newURLs() {
        return URLList;
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
            index.addWord(s, wordPos++);

            s = "";
        }

        // process the ref text from the table
        index.finishProcessing();
    }

    /**
     * Store a tag attribute as either a new URL or add it to the reference
     * phrase list for post processing.
     * 
     * @param attributes
     *            The attributes of the tag.
     * @param type
     *            The type of the tag.
     * @param URL
     *            True if the value from the attribute should be interpreted as
     *            a URL. False if it should be interpreted as a reference
     *            phrase.
     */
    public void searchTag(Map<String, String> attributes, String type,
            boolean URL) {
        String val = attributes.get(type);

        // not present
        if (val == null)
            return;

        if (URL) // store the URL
            storeURL(val);
        else // add a reference phrase
            index.addRefPhrase(val);
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
            index.addWord(s, wordPos++);

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
            index.addWord(s, wordPos++);

            s = "";
        }

        // check attributes is not null
        if (attributes == null)
            return;

        switch (elementName) {
            case "img" :
                searchTag(attributes, "title", false);
                searchTag(attributes, "TITLE", false);
                break;
            case "IMG" :
                searchTag(attributes, "title", false);
                searchTag(attributes, "TITLE", false);
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
     *            buffer containing characters; do not modify this buffer
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
        for (int i = start; i < start + length; i++) {
            if (ch[i] == '&' || punctuation.contains(ch[i]) || ch[i] == ' ') {
                if (!s.isEmpty()) {
                    // add the word to the index for this page
                    index.addWord(s, wordPos);

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
}
