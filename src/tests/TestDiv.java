package tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import assignment.CrawlerThreadManager;
import assignment.Page;
import assignment.Parser;
import assignment.WebIndex;

public class TestDiv {

    ArrayList<WebIndex> combinedIndex;

    /**
     * Setup the website.
     */
    @Before
    public void setup() {
        // create the URL
        URL url = null;
        try {
            url = new URL("file:URL Gen/0.html");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList<URL> urlArrayList = new ArrayList<URL>();

        urlArrayList.add(url);

        // create the page
        TestPatternURLGen tpug = new TestPatternURLGen();
        tpug.createRandomWebsite();

        // crawl the webpage with the real web crawler
        CrawlerThreadManager ctm = new CrawlerThreadManager();

        // Run the crawler
        ctm = new CrawlerThreadManager();
        ctm.startingJobs(urlArrayList);
        ctm.crawlerThreadManager();
        
        // store all data from the crawler
        // load the tables into memory
        combinedIndex = TestSearchHelper.loadCrawlerOutput();
    }

    /**
     * Search for websites that have names divisible by 3.
     */
    @Test
    public void allPagesTest() {
        Parser parser = new Parser("!Disney");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            try {
                urlList.add(new URL("file:URL Gen/" + i + ".html"));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Search for websites that have divthree. (Those that are divisible by
     * three).
     */
    @Test
    public void div3Test() {
        Parser parser = new Parser("divthree");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Search for websites that have notdiv. (Those that are not divisible by
     * three).
     */
    @Test
    public void notDivTest() {
        Parser parser = new Parser("notdiv");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 != 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Search for websites that do not have notdiv. (Those that are divisible by
     * three).
     */
    @Test
    public void notNotDivTest() {
        Parser parser = new Parser("!notdiv");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Search for websites that do not have divthree. (Those that are not
     * divisible by three).
     */
    @Test
    public void notDiv3Test() {
        Parser parser = new Parser("!divthree");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 != 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test and works. Pages that are divisible by three and two should show up.
     */
    @Test
    public void div2And3Test() {
        Parser parser = new Parser("divthree & divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0 && i % 2 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test or works. Pages that are divisible by three or two should show up.
     */
    @Test
    public void div2Or3Test() {
        Parser parser = new Parser("divthree | divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0 || i % 2 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test and works. Pages that are not divisible by three and two should show
     * up.
     */
    @Test
    public void notDiv2And3Test() {
        Parser parser = new Parser("!divthree & divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 != 0 && i % 2 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test and works. Pages that are divisible by three and not two should show
     * up.
     */
    @Test
    public void div2AndNot3Test() {
        Parser parser = new Parser("divthree & !divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0 && i % 2 != 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test or works. Pages that are not divisible by three or two should show
     * up.
     */
    @Test
    public void notDiv2Or3Test() {
        Parser parser = new Parser("!divthree | divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 != 0 || i % 2 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test or works. Pages that are divisible by three or not two should show
     * up.
     */
    @Test
    public void div2OrNot3Test() {
        Parser parser = new Parser("divthree | !divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 == 0 || i % 2 != 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test and works. Pages that are not divisible by three and not two should
     * show up.
     */
    @Test
    public void notDiv2AndNot3Test() {
        Parser parser = new Parser("!divthree & !divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 != 0 && i % 2 != 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test or works. Pages that are not divisible by three or not two should
     * show up.
     */
    @Test
    public void notDiv2OrNot3Test() {
        Parser parser = new Parser("!divthree | !divtwo");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 3 != 0 || i % 2 != 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Check phrase query works. Should be only pages that are divisible by 7.
     */
    @Test
    public void phraseTest() {
        Parser parser = new Parser("\"le phrase query\"");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 7 == 0) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test that complex queries with parentheses work.
     */
    @Test
    public void complexQuery1() {
        Parser parser = new Parser(
                "\"nope query please\" | ((!divthree | divtwo) & notdiv2)");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (i % 7 != 0 || ((i % 3 != 0 || i % 2 == 0) && i % 2 != 0)) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }

    /**
     * Test that complex queries with implicit ands work
     */
    @Test
    public void complexQuery2() {
        Parser parser = new Parser(
                "!(\"nope query please\" & !Josh notdiv | divtwo \"nope query please\")");

        if (!parser.toPostFix())
            fail("Conversion to postfix issue.");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        HashSet<URL> urlList = new HashSet<>();

        // create the expected webpages
        for (int i = 0; i < 100; i++) {
            if (!((i % 7 != 0 && i % 3 != 0) || (i % 2 == 0 && i % 7 != 0))) {
                try {
                    urlList.add(new URL("file:URL Gen/" + i + ".html"));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // check all pages are correct
        for (Page p : pages) {
            assertTrue(urlList.remove(p.getURL()));
        }

        // check all pages found
        assertEquals(0, urlList.size());
    }
}
