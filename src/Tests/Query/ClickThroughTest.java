package Tests.Query;

import Query.*;
import Utils.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Random;

public class ClickThroughTest {

    @Test
    public void testingWrongFormatPassing() {
        String firstPart, secondPart;

        firstPart = Utils.DEFAULT_QUERY[0] + "\t" + Utils.DEFAULT_QUERY[1] + "\t" +
                Utils.DEFAULT_QUERY[2] + "\t";
        secondPart = "\t" + Utils.DEFAULT_QUERY[4];
        Random random = new Random();
        for (int i = 0; i < 20; ++i) {
            passingWrongFormattedString(firstPart + Utils.generateRandomStringWithLetter(Utils.MAX_LENGTH) +
                    secondPart);
        }
    }

    private void passingWrongFormattedString(String wrongFormattedString) {
        try {
            Query query = new ClickThrough(wrongFormattedString);
            Assert.fail("Error expected while passing wrong formatted string to " +
                    "QueryOnly constructor: " + wrongFormattedString);
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testingGettersAndSetters() {
        ClickThrough clickThrough = new ClickThrough();

        Random random = new Random();
        for (int i = 0; i < 20; ++i) {
            int anonID = random.nextInt();
            String query = Utils.generateRandomString(Utils.MAX_LENGTH);
            LocalDateTime localDateTime = Utils.randomLocalDateTime();
            int itemRank = random.nextInt();
            String clickURL = Utils.generateRandomString(Utils.MAX_LENGTH);

            clickThrough.setAnonId(anonID);
            clickThrough.setQuery(query);
            clickThrough.setQueryTime(localDateTime);
            clickThrough.setItemRank(itemRank);
            clickThrough.setClickURL(clickURL);


            Assert.assertEquals(anonID, clickThrough.getAnonId());
            Assert.assertEquals(query, clickThrough.getQuery());
            Assert.assertEquals(localDateTime, clickThrough.getQueryTime());
            Assert.assertEquals(itemRank, clickThrough.getItemRank());
            Assert.assertEquals(clickURL, clickThrough.getClickURL());
        }
    }

}
