package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({BTreeTest.class, PhraseSearchBigOTester.class,
        ShuntingYardTest.class, SingleThreadTest.class, TestDiv.class,
        TestURLGen.class})
public class AllTests {

}
