package Tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ParameterizedTestUsingConstructor.class})

public class AllTests {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(AllTests.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
    }

}