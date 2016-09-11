package assignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Parser {
    final String query;
    Stack<Character> operator = new Stack<>();
    Queue<String> output = new LinkedList<>();
    String word = ""; // stores the word as it is being built

    // stores the operator and (in the pair) the precedence and the number of
    // words to run the operator

    private static final HashMap<Character, Pair<Integer, Integer>> operatorPrecedence =
            new HashMap<>();
    static {
        operatorPrecedence.put('!', new Pair<Integer, Integer>(2, 1));
        operatorPrecedence.put('&', new Pair<Integer, Integer>(1, 2));
        operatorPrecedence.put('|', new Pair<Integer, Integer>(0, 2));
    }

    /**
     * The query to be parsed.
     * 
     * @param s
     *            The query.
     */
    public Parser(String s) {
        if (s != null)
            query = s.toLowerCase();
        else
            query = "";
    }

    /**
     * Add a word to the output.
     * 
     * @param word
     *            The word to add.
     * @return Only return false if there is an issue adding the word.
     */
    private boolean addWord() {
        // nothing is added even though true is returned since not adding
        // anything satisfies the order
        if (word.length() == 0)
            return true;

        String newWord = "";

        int closingQuote = 0;
        boolean quote = false;

        for (int i = 0; i < word.length(); i++) {
            if (!quote && word.charAt(i) == '\"') {
                quote = true;
                closingQuote = word.lastIndexOf('\"');
                continue;
            }

            // skip anything containing or after the closing quote
            if (quote && i == closingQuote)
                break;

            if (!quote && word.charAt(i) == ' ')
                continue;

            newWord += word.charAt(i);
        }

        // reset word
        word = "";

        // nothing is added even though true is returned since not adding
        // anything satisfies the order
        if (newWord.length() == 0)
            return true;

        // check for operator
        if (newWord.length() == 1) {
            // check the arguments are correctly used
            Pair<Integer, Integer> operatorPair =
                    operatorPrecedence.get(newWord.charAt(0));

            // check the operator can be used
            if (operatorPair != null
                    && output.size() < operatorPair.getSecondElement()) {
                System.err.println("Cannot use " + newWord
                        + " operator on this few of arguments.");

                return false;
            }
        }

        // check if the word is an HTML Special Character
        Character c;

        // replace the word with an HTML special character if needed
        if ((c = HTMLSpecialChars.specialChars.get(newWord)) != null)
            newWord = c.toString();

        // add the word to the output
        output.add(newWord);

        word = "";

        return true;
    }

    /**
     * Add an operator to the stack.
     * 
     * @param operatorC
     *            The operator to add.
     */
    private boolean addOperator(Character operatorC) {
        if (!addWord())
            return false;

        final int curPrecedence =
                operatorPrecedence.get(operatorC).getFirstElement();

        // move elements from the stack to the queue until the desired
        // operator is at the start of the stack or the stack is empty
        while (!operator.isEmpty() && (operator.peek() != '('
                && operator.peek() != ')' && curPrecedence < operatorPrecedence
                        .get(operator.peek()).getFirstElement())) {
            word = operator.pop().toString();

            if (!addWord())
                return false;
        }

        // push onto the operator stacks
        operator.push(operatorC);

        return true;
    }

    /**
     * Use Shunting Yard to turn the prefix input to postfix.
     * 
     * @return Return false if there are errors in the input. True otherwise.
     */

    public boolean toPostFix() {
        // TODO: ADD IMPLICIT AND AT OUTER LEVEL (FOO (BAR | BAZ)), (A | B) (B |
        // C)
        // reset word
        word = "";

        // for error purposes and tracking implicit and with parentheses
        // set to a character that is not '\0' or an operator to start with
        char lastOperator = ' ';

        char qArray[] = query.toCharArray();

        // loop over all of the elements in the query
        for (int i = 0; i < qArray.length; i++) {
            // get the character
            char c = qArray[i];

            // read operators first
            if (operatorPrecedence.containsKey(c)) {
                // check the last operator does not cause an issue with the new
                // one
                if (lastOperator != '\0' && c != '!') {
                    System.err.println(
                            "Two operators that cannot be adjacent"
                            + " are used together.");

                    return false;
                }

                if (!addOperator(c))
                    return false;

                // store the last operator
                lastOperator = c;
            } else {
                if (c == '(') {
                    if (lastOperator == '\0') {
                        // implicit and add the & operator
                        if (!addOperator('&'))
                            return false;

                        // store the operator
                        lastOperator = '&';
                    }

                    if (!addWord())
                        return false;

                    operator.push('(');
                } else if (c == ')') {
                    if (!addWord())
                        return false;

                    // move elements from the stack to the queue until the
                    // opening
                    // brace is at the start of the stack
                    while (!operator.isEmpty() && operator.peek() != '(') {
                        word = operator.pop().toString();
                        if (!addWord())
                            return false;
                    }

                    // error no opening paren
                    if (operator.isEmpty()) {
                        System.err.println(
                                "No operning parenthesis for a"
                                + " closing parenthesis");
                        return false;
                    }

                    // pop the opening paren off the stack
                    operator.pop();

                } else {
                    // implicit and
                    if (1 < word.length()
                            && word.charAt(word.length() - 1) == ' ') {
                        // double spaces are not allowed
                        if (c == ' ' || word.charAt(word.length() - 2) == ' ') {
                            System.err.println(
                                    "Double spaces are not allowed"
                                    + " unless in quotes");
                            return false;
                        }

                        // add the & operator
                        if (!addOperator('&'))
                            return false;

                        // store the and operator
                        lastOperator = '&';
                    } else if (1 < i && word.equals(" ")
                            && qArray[i - 2] == ')') {
                        // add the & operator
                        if (!addOperator('&'))
                            return false;

                        // store the and operator
                        lastOperator = '&';
                    }

                    // check for quote
                    if (c == '\"') {
                        // add in a quote to flag add word not to remove spaces
                        word += '\"';

                        // phrase query
                        // read till the next quote
                        while (i++ < query.toCharArray().length
                                && (c = qArray[i]) != '\"') {
                            // check the character is not punctuation
                            if (!CrawlingMarkupHandler.punctuation.contains(c))
                                word += c;
                        }

                        // add in closing quote
                        word += '\"';

                        // check quote is closed
                        if (i == query.toCharArray().length) {
                            System.err.println("No closing quotes");
                            return false;
                        }

                        // this character is not an operator
                        // do not count spaces
                        lastOperator = '\0';
                    } else {
                        // check the character is not punctuation
                        if (!CrawlingMarkupHandler.punctuation.contains(c))
                            word += c;

                        // this character is not an operator
                        // do not count spaces
                        if (c != ' ')
                            lastOperator = '\0';
                    }
                }
            }
        }

        // if there is a word to add, add it
        if (!addWord())
            return false;

        // move the remaining operators to the output
        while (!operator.isEmpty()) {
            // no parens should be on the stack at this time
            if (operator.peek() == '(' || operator.peek() == ')') {
                System.err.println("Parenthesis mismatch in input");

                return false;
            }

            word = operator.pop().toString();

            if (!addWord())
                return false;
        }

        // check ! is not the only element
        if (output.size() == 1 && output.peek().equals("!")) {
            System.err.println("! is the only element in the query.");
            return false;
        }

        return true;
    }

    /**
     * And two elements together and output the answer in the second input.
     * 
     * @param data2
     *            A
     * @param data1
     *            B
     */
    private void andMap(
            ArrayList<Pair<Integer, Pair<Integer, Integer>>> toBeDeleted,
            Pair<Boolean, ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> data1,
            Pair<Boolean, ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> data2) {
        toBeDeleted.clear();

        // remove all elements that are not contained in both maps
        for (Entry<Pair<Integer, Pair<Integer, Integer>>, Object> m : data2
                .getSecondElement().entrySet()) {

            // mark data for deletion
            if (!data1.getSecondElement().containsKey(m.getKey())) {
                toBeDeleted.add(m.getKey());
            }
        }

        // delete the data from the map
        for (Pair<Integer, Pair<Integer, Integer>> key : toBeDeleted) {
            data2.getSecondElement().remove(key);
        }
    }

    /**
     * Performs !A & B and returns result in the second input.
     * 
     * @param data2
     *            A
     * @param data1
     *            B
     */
    private void notAndMap(
            Pair<Boolean, ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> data2,
            Pair<Boolean, ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> data1) {

        // add all unique elements from the map into this one
        for (Entry<Pair<Integer, Pair<Integer, Integer>>, Object> m : data2
                .getSecondElement().entrySet()) {

            // add the element if both lists contain the key
            if (data1.getSecondElement().containsKey(m.getKey())) {
                data1.getSecondElement().remove(m.getKey());
            }
        }
    }

    /**
     * Or two elements together and output the answer in the second input.
     * 
     * @param data1
     *            A
     * @param data2
     *            B
     */
    private void orMap(
            Pair<Boolean, ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> data1,
            Pair<Boolean, ConcurrentHashMap<Pair<Integer,
                Pair<Integer, Integer>>, Object>> data2) {
        // add all unique elements from the map into this one
        for (Entry<Pair<Integer, Pair<Integer, Integer>>, Object> m : data1
                .getSecondElement().entrySet()) {
            // add the data if the key does not exist
            if (!data2.getSecondElement().containsKey(m.getKey())) {
                data2.getSecondElement().put(m.getKey(), new Object());
            }
        }
    }

    /**
     * Performs A | !B and returns result in the second input.
     * 
     * @param toBeDeleted
     *            Used for deleting elements.
     * @param data2
     *            A
     * @param data1
     *            !B
     */
    private void notOrMap(
            ArrayList<Pair<Integer, Pair<Integer, Integer>>> toBeDeleted,
            Pair<Boolean, ConcurrentHashMap<Pair<Integer, Pair<Integer, Integer>>, Object>> data2,
            Pair<Boolean, ConcurrentHashMap<Pair<Integer, Pair<Integer, Integer>>, Object>> data1) {
        toBeDeleted.clear();

        // remove all elements that are contained in both maps
        for (Entry<Pair<Integer, Pair<Integer, Integer>>, Object> m : data1
                .getSecondElement().entrySet()) {

            // mark data for deletion
            if (data2.getSecondElement().containsKey(m.getKey())) {
                toBeDeleted.add(m.getKey());
            }
        }

        // delete the data from the map
        for (Pair<Integer, Pair<Integer, Integer>> key : toBeDeleted) {
            data1.getSecondElement().remove(key);
        }
    }

    /**
     * Parse the data and run the correct searches.
     * 
     * @param webIndex
     *            The tables to query.
     * @return The url's found that match the query.
     */
    public Collection<Page> runSearch(ArrayList<WebIndex> webIndex) {
        // No data
        if (webIndex.size() == 0 || query.isEmpty())
            return new ArrayList<>();

        ExecutorService manager = Executors.newCachedThreadPool();

        Queue<Future<ConcurrentHashMap<Pair<Integer, Pair<Integer, Integer>>, Object>>> futures =
                new LinkedList<>();

        // loop through the output and start up threads to manage the searches
        for (String s : output) {
            // not an operator so run a search on it
            if (s.length() > 1
                    || !operatorPrecedence.containsKey(s.charAt(0))) {

                // add the job and store the future
                futures.add(manager.submit(new SearchManager(webIndex, s)));
            }
        }

        // kill the manager and wait for its termination
        manager.shutdown();

        while (!manager.isTerminated()) {

        }

        // wait for all of the futures to finish
        boolean finished = false;

        while (!finished) {
            finished = true;
            // wait for all of the threads to finish searches
            for (Future<ConcurrentHashMap<Pair<Integer, Pair<Integer, Integer>>,
                    Object>> map : futures) {
                if (!map.isDone())
                    finished = false;
            }
        }

        Stack<Pair<Boolean, ConcurrentHashMap<Pair<Integer,
            Pair<Integer, Integer>>, Object>>> dataStack =
                new Stack<>();

        ArrayList<Pair<Integer, Pair<Integer, Integer>>> toBeDeleted =
                new ArrayList<>();

        Pair<Boolean, ConcurrentHashMap<Pair<Integer,
            Pair<Integer, Integer>>, Object>> data1, data2;

        // start looking at the data returned while taking into account search
        // logic
        for (String s : output) {
            if (s.length() > 1) {
                // add in the future data
                try {
                    dataStack.push(new Pair<>(new Boolean(false),
                            futures.poll().get()));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            }

            switch (s.charAt(0)) {
                // invert the element's ! value and do not process anything
                // further
                case '!' :
                    dataStack.peek().setFirstElement(
                            !dataStack.peek().getFirstElement());
                    break;
                case '&' :
                    // pop the first two elements of the queue
                    if (dataStack.size() < 2) {
                        System.err.println("Not enough elements for operators");

                        return new ArrayList<>();
                    }
                    data1 = dataStack.pop();
                    data2 = dataStack.pop();

                    // use DeMorgans if both ! since !A & !B = !(A | B)
                    if (data1.getFirstElement() == true
                            && data2.getFirstElement() == true) {
                        orMap(data1, data2);

                        dataStack.push(data2);
                    } else if (data1.getFirstElement() == false
                            && data2.getFirstElement() == false) {
                        andMap(toBeDeleted, data1, data2);

                        dataStack.push(data2);
                    } else if (data1.getFirstElement() == true
                            && data2.getFirstElement() == false) {
                        notAndMap(data1, data2);

                        dataStack.push(data2);
                    } else {
                        notAndMap(data2, data1);

                        dataStack.push(data1);
                    }

                    break;
                case '|' :
                    // pop the first two elements of the queue
                    if (dataStack.size() < 2) {
                        System.err.println("Not enough elements for operators");

                        return new ArrayList<>();
                    }

                    data1 = dataStack.pop();
                    data2 = dataStack.pop();

                    // use DeMorgans if both ! since !A | !B = !(A & B)
                    if (data1.getFirstElement() == true
                            && data2.getFirstElement() == true) {
                        andMap(toBeDeleted, data1, data2);

                        dataStack.push(data2);
                    } else if (data1.getFirstElement() == false
                            && data2.getFirstElement() == false) {
                        orMap(data1, data2);

                        dataStack.push(data2);
                    } else if (data1.getFirstElement() == false
                            && data2.getFirstElement() == true) {
                        notOrMap(toBeDeleted, data1, data2);

                        dataStack.push(data2);
                    } else {
                        notOrMap(toBeDeleted, data2, data1);

                        dataStack.push(data1);
                    }

                    break;
                default :
                    // add in the future data
                    try {
                        dataStack.push(new Pair<>(new Boolean(false),
                                futures.poll().get()));
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
        }

        // the stack is null because all elements entered are punctuation
        if (dataStack.size() == 0)
            return new ArrayList<>();

        Collection<Page> pages = new ArrayList<>();

        boolean notOperator = dataStack.get(0).getFirstElement();

        // create the URL index
        if (!notOperator) {
            // find everything that is listed
            for (Entry<Pair<Integer,
                    Pair<Integer, Integer>>, Object> m : dataStack
                    .get(0).getSecondElement().entrySet()) {

                pages.add(new Page(
                        ((webIndex.get(m.getKey().getFirstElement()).table
                                .get(m.getKey().getSecondElement()
                                        .getFirstElement()).URLsymbolTable.get(m
                                                .getKey().getSecondElement()
                                                .getSecondElement())))));
            }
        } else {
            // find everything that is not listed
            for (int tableNumber = 0; tableNumber < webIndex
                    .size(); tableNumber++) {
                for (int tableID = 0; tableID < webIndex.get(tableNumber).table
                        .size(); tableID++) {
                    for (Map.Entry<Integer, URL> m : webIndex
                            .get(tableNumber).table.get(tableID).URLsymbolTable
                                    .entrySet()) {
                        if (!dataStack.get(0).getSecondElement()
                                .containsKey(new Pair<>(tableNumber,
                                        new Pair<>(tableID, m.getKey())))) {
                            pages.add(new Page(m.getValue()));
                        }
                    }
                }
            }
        }

        return pages;
    }

    public String toString() {
        return output.toString();
    }
}