package Tests.Query;

import Utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import Query.*;

import java.time.LocalDateTime;
import java.util.Random;

public class QueryOnlyTest {

    @Test
    public void testingWrongFormatPassing() {
        String otherPart;

        otherPart = "\t" + Utils.DEFAULT_QUERY[1] + "\t" + Utils.DEFAULT_QUERY[2];
        for (int i = 0; i < 20; ++i) {
            passingWrongFormattedString(Utils.generateRandomStringWithLetter(Utils.MAX_LENGTH) +
                    otherPart);
        }

        otherPart = Utils.DEFAULT_QUERY[0] + "\t" + Utils.DEFAULT_QUERY[1] + "\t";
        for (int i = 0; i < 20; ++i) {
            passingWrongFormattedString(otherPart + Utils.generateRandomStringWithLetter(Utils.MAX_LENGTH));
        }
    }

    private void passingWrongFormattedString(String wrongFormattedString) {
        try {
            Query query = new QueryOnly(wrongFormattedString);
            Assert.fail("Error expected while passing wrong formatted string to " +
                    "QueryOnly constructor: " + wrongFormattedString);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testingGettersAndSetters() {
        QueryOnly queryOnly = new QueryOnly();

        Random random = new Random();
        for (int i = 0; i < 20; ++i) {
            int anonID = random.nextInt();
            String query = Utils.generateRandomString(Utils.MAX_LENGTH);
            LocalDateTime localDateTime = Utils.randomLocalDateTime();

            queryOnly.setAnonId(anonID);
            queryOnly.setQuery(query);
            queryOnly.setQueryTime(localDateTime);

            Assert.assertEquals(anonID, queryOnly.getAnonId());
            Assert.assertEquals(query, queryOnly.getQuery());
            Assert.assertEquals(localDateTime, queryOnly.getQueryTime());
        }
    }

}
