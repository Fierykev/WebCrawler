package tests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class URLGen {

    private final static int MAX_PAGES = 10000;

    private final static int MAX_BODIES = 10;

    private final static int MAX_RANDOM = 100;

    private final static int MAX_URL_RANDOM = 10;

    private final static int TOP_CHAR = 100;

    private final static int SPACE = 10;

    private ArrayList<Integer> newPages = new ArrayList<>();

    private ArrayList<Integer> usedPages = new ArrayList<>();

    private ArrayList<Integer> foundPages = new ArrayList<>();

    HashMap<URL, ArrayList<String>> allWords = new HashMap<>();

    HashMap<URL, ArrayList<ArrayList<String>>> allLinks = new HashMap<>();

    private URL curURL;

    private final String template = "<html><body>";

    final String endingTemplate = "</body></html>";

    /**
     * Create text for the body.
     * 
     * @return The text in the body.
     */
    private String createText(boolean ref) {
        String body = "";

        Random r = new Random();
        int numChars = r.nextInt(MAX_RANDOM) + 15;
        int charsToSpace = r.nextInt(SPACE) + 2;
        String word = "";

        // add one to the list
        if (!ref) {
            if (allWords.get(curURL) == null)
                allWords.put(curURL, new ArrayList<>());
        } else {
            if (allLinks.get(curURL) == null) {
                allLinks.put(curURL, new ArrayList<>());
            }

            allLinks.get(curURL).add(new ArrayList<>());
        }

        // randomly generate non-digit characters
        for (int i = 0; i < numChars; i++) {
            charsToSpace--;

            // check if a space should be added
            if (charsToSpace == 0) {
                body += word + ' ';

                // add the word
                if (!ref)
                    allWords.get(curURL).add(word.toLowerCase());
                else
                    allLinks.get(curURL).get(allLinks.get(curURL).size() - 1)
                            .add(word.toLowerCase());

                word = "";
                charsToSpace = r.nextInt(SPACE) + 2;
                continue;
            }

            // generate a random character
            char[] c = Character.toChars(r.nextInt(TOP_CHAR) + 10);

            boolean randomError = false;

            // check the array is valid
            for (char arrayC : c) {
                // check not a space or punctuation
                if (arrayC == ' '
                        || TestSearchHandler.punctuation.contains(arrayC)
                        || arrayC == '&') {
                    randomError = true;
                }
            }

            if (randomError) {
                // reset the loop
                i--;
                charsToSpace++;

                continue;
            }

            // add the character
            for (char arrayC : c)
                word += arrayC;
        }

        return body;
    }

    /**
     * Create the words used for URL links.
     * 
     * @return The url link names.
     */
    private String createURLWords() {
        String body = template;

        Random r = new Random();
        int numChars = r.nextInt(MAX_URL_RANDOM) + 15;
        int charsToSpace = r.nextInt(SPACE) + 2;
        String word = "";

        // add one to the list
        if (allLinks.get(curURL) == null)
            allLinks.put(curURL, new ArrayList<>());

        allLinks.get(curURL).add(new ArrayList<>());

        // randomly generate non-digit characters
        for (int i = 0; i < numChars; i++) {
            charsToSpace--;

            // check if a space should be added
            if (charsToSpace == 0) {
                body += word + ' ';

                // add the word
                allLinks.get(curURL).get(allLinks.get(curURL).size() - 1)
                        .add(word.toLowerCase());

                word = "";
                charsToSpace = r.nextInt(SPACE) + 2;
                continue;
            }

            // generate a random character
            char[] c = Character.toChars(r.nextInt(TOP_CHAR) + 10);

            boolean randomError = false;

            // check the array is valid
            for (char arrayC : c) {
                // check not a space or punctuation
                if (arrayC == ' '
                        || TestSearchHandler.punctuation.contains(arrayC)) {
                    randomError = true;
                }
            }

            if (randomError) {
                // reset the loop
                i--;
                charsToSpace++;

                continue;
            }

            // add the character
            for (char arrayC : c)
                word += arrayC;
        }

        return body;
    }

    /**
     * Generate a random 100 page website that stores all words contained within
     * it.
     */
    public void createRandomWebsite() {

        Random r = new Random();

        int numPages = r.nextInt(MAX_PAGES);

        // generate the webpage numbers
        for (int i = 0; i < MAX_PAGES; i++)
            newPages.add(i);

        while (!newPages.isEmpty()) {
            String body = "";

            // remove a random page
            int pageNum = r.nextInt(newPages.size());

            // remove the page
            int pageName = newPages.remove(pageNum);

            try {
                curURL = new URL("file:URL Gen/" + pageName + ".html");
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            int numBodies = r.nextInt(MAX_BODIES) + 1;

            int bodyUrlBound = r.nextInt(numBodies) + 1;

            for (int i = 0; i < numBodies; i++) {
                // add the text
                body += "<p class=MsoNormal>" + createText(false) + "</p>";

                // add in an image with random text
                body += "<img src=\"blahblahcardgamesblahblah.jpg\" alt=\"no image\" name=\""
                        + createText(true) + "\">";

                // add a hyperlink with random text
                // randomly generate non-digit characters

                int link = 0;

                int linkType = r.nextInt(3);

                // force one url to be new
                if (i == bodyUrlBound)
                    linkType = 0;

                switch (linkType) {
                    case 0 :
                        if (!usedPages.isEmpty()) {
                            link = usedPages
                                    .remove(r.nextInt(usedPages.size()));

                            foundPages.add(link);

                            break;
                        }
                    case 1 :
                        if (!newPages.isEmpty()) {
                            link = newPages.get(r.nextInt(newPages.size()));

                            foundPages.add(link);

                        } else if (!usedPages.isEmpty()) {
                            link = usedPages
                                    .remove(r.nextInt(usedPages.size()));

                            foundPages.add(link);
                        } else {
                            link = foundPages.get(r.nextInt(foundPages.size()));

                            foundPages.add(link);
                        }

                        break;
                    case 2 :
                        if (!foundPages.isEmpty()) {
                            link = foundPages.get(r.nextInt(foundPages.size()));

                            foundPages.add(link);
                        } else if (!usedPages.isEmpty()) {
                            link = usedPages
                                    .remove(r.nextInt(usedPages.size()));

                            foundPages.add(link);
                        } else {
                            link = newPages.get(r.nextInt(newPages.size()));

                            foundPages.add(link);
                        }

                        break;

                }

                // store the hyperlink in a or iframe
                switch (r.nextInt(4)) {
                    case 0 :
                        body += "<a href=\"" + link + ".html\">"
                                + createText(false) + "</a>";
                        break;
                    case 1 :
                        body += "<iframe src=\"" + link + ".html\"></iframe>";
                        break;
                    case 2 :
                        body += "<A HREF=\"" + link + ".html\">"
                                + createText(false) + "</A>";
                        break;
                    case 3 :
                        body += "<IFRAME SRC=\"" + link + ".html\"></IFRAME>";
                        break;
                }
            }

            // store the page number
            usedPages.add(pageName);

            // end the file
            body += endingTemplate;

            // save the file
            try {
                PrintWriter out = new PrintWriter(
                        "URL Gen/" + pageName + ".html");
                out.write(body);
                out.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
