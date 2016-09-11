package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import assignment.Parser;

public class ShuntingYardTest {

    Parser parser;

    /**
     * Test and works with the parser.
     */
    @Test
    public void andTest() {
        parser = new Parser("Aaa & Bbbb");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[aaa, bbbb, &]", reversePoles);
    }

    /**
     * Test or works with the parser.
     */
    @Test
    public void orTest() {
        parser = new Parser("Aaa | Bbbb");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[aaa, bbbb, |]", reversePoles);
    }

    /**
     * Test not works with the parser.
     */
    @Test
    public void notTest() {
        parser = new Parser("!Aaa");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[aaa, !]", reversePoles);
    }

    /**
     * Test parentheses work with the parser.
     */
    @Test
    public void parenthasesTest() {
        parser = new Parser("(Mmmm & (Ssss | (Aaaa & Bbbb)))");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[mmmm, ssss, aaaa, bbbb, &, |, &]", reversePoles);
    }

    /**
     * Test phrases work with the parser.
     */
    @Test
    public void phraseTest() {
        parser = new Parser("\"Hello World\"");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[hello world]", reversePoles);
    }

    /**
     * Test implicit and works with the parser. (Tests double spaces)
     */
    @Test
    public void implicitAndTest1() {
        parser = new Parser("Mmmm Aaaa");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[mmmm, aaaa, &]", reversePoles);
    }

    /**
     * Test implicit and works with the parser. (Tests space before parens turns
     * to and)
     */
    @Test
    public void implicitAndTest2() {
        parser = new Parser("Mmmm ((Aaaa Bbbb) Zzzz)");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[mmmm, aaaa, bbbb, &, zzzz, &, &]", reversePoles);
    }

    /**
     * Test the parser works for complex statements combining multiple
     * expressions. This one checks that ! works with other operators present
     * and multiple !'s combined together.
     */
    @Test
    public void complexParseTest1() {
        parser = new Parser(
                "((!!!!(!Rrrr) | ((Aaa & Bbb) & (Zzzz))) & Mmmm) & !Llll");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals(
                "[rrrr, !, !, !, !, !, aaa, bbb, &, zzzz, &, |, mmmm, &, llll, !, &]",
                reversePoles);
    }

    /**
     * Test the parser works for complex statements combining multiple
     * expressions. This one checks that ! works when quotes and other operators
     * are present.
     */
    @Test
    public void complexParseTest2() {
        parser = new Parser("!(\"Aaaa\" & \"Bbbb | Cccc\") | !Mmm & Sss");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals("[aaaa, bbbb | cccc, &, !, mmm, !, sss, &, |]",
                reversePoles);
    }

    /**
     * Test the parser works for complex statements combining multiple
     * expressions. This one checks implicit and works when two quoted
     * expressions are together.
     */
    @Test
    public void complexParseTest3() {
        parser = new Parser(
                "(Aaaa & Bbbb | Cccc) \"Dddd\" \"Eeee Ffff Gggg\" | Hhhh");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals(
                "[aaaa, bbbb, &, cccc, |, dddd, eeee ffff gggg, &, &, hhhh, |]",
                reversePoles);
    }

    /**
     * Test the parser works for complex statements combining multiple
     * expressions. This one checks implicit and works when parens are back to
     * back.
     */
    @Test
    public void complexParseTest4() {
        parser = new Parser(
                "(Aaaa & Bbbb Cccc Dddd | Eeee) (Mmmm | Ssss & Ppp)");

        // check post fix worked
        assertTrue(parser.toPostFix());

        // get the reverse poles representation
        String reversePoles = parser.toString();

        // check the representation is correct
        assertEquals(
                "[aaaa, bbbb, cccc, dddd, &, &, &, eeee, |, mmmm, ssss, ppp, &, |, &]",
                reversePoles);
    }
}
