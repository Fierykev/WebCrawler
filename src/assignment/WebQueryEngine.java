package assignment;
import java.util.*;

public class WebQueryEngine {
    // stores the database
    private static ArrayList<WebIndex> webIndex;

    /**
     * Returns a WebQueryEngine that uses the given Index to constructor answers
     * to queries.
     *
     * @param index
     *            The WebIndex this WebQueryEngine should use
     * @return A WebQueryEngine ready to be queried
     */
    public static WebQueryEngine fromIndex(WebIndex index) {
        webIndex = index.getCombinedIndex();

        return new WebQueryEngine();
    }

    /**
     * Returns a Collection of URLs (as Strings) of web pages satisfying the
     * query expression.
     *
     * @param query
     *            a query expression
     * @return a Collection of web pages satisfying the query
     */
    public Collection<Page> query(String query) {
        // parse the query
        Parser parser = new Parser(query);

        // turn to post fix
        if (!parser.toPostFix())
            return new ArrayList<>();

        // process the data
        return parser.runSearch(webIndex);
    }
}
