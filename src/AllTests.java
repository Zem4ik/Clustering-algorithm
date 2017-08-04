import Tests.Parser.QueryCollectionParserTest;
import Tests.Query.ClickThroughTest;
import Tests.Query.QueryOnlyTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import java.io.*;

@RunWith(Suite.class)
@SuiteClasses({
        QueryOnlyTest.class,
        ClickThroughTest.class,
        QueryCollectionParserTest.class,
        MainClassTest.class})

public class AllTests {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AllTests.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }

}