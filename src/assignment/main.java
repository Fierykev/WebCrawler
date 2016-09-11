package assignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class main {

    public static void main(String[] args) {
        Parser parser = new Parser("3");
        if (!parser.toPostFix())
            return;

        ArrayList<WebIndex> combinedIndex = new ArrayList<>();

        try {
            combinedIndex = Index.load("index.db").getCombinedIndex();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("DONE LOADING");

        Collection<Page> pages = parser.runSearch(combinedIndex);

        for (Page p : pages) {
            System.out.println(p.getURL().toString());
        }
    }

}
