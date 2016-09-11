package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import BTree.BTree;
import BTree.BTreeHashSet;

public class BTreeTest {

    static Double varsInTree[] = new Double[1000];

    static {
        for (int i = 0; i < 1000; i++) {
            varsInTree[i] = Math.random();
        }
    }

    /**
     * Test that storing 1000 doubles is contained in the tree.
     */

    @Test
    public void storageTest() {
        BTree tree2 = new BTree(10);

        for (int i = 0; i < 1000; i++) {
            tree2.add(varsInTree[i]);
        }

        for (int i = 0; i < 1000; i++) {
            assertTrue(tree2.find(varsInTree[i]));
        }
    }

    /**
     * Test that storing 1000 doubles is contained in the tree.
     */

    @Test
    public void hashSetTest() {
        BTreeHashSet<Double> hashSet = new BTreeHashSet<Double>(10);

        for (int i = 0; i < 100; i++) {
            hashSet.add(varsInTree[i]);
        }

        for (int i = 0; i < 100; i++) {
            assertFalse(hashSet.add(varsInTree[i]));
        }
    }

}
