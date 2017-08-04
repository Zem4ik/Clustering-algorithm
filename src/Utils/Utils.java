package Utils;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static final String DEFAULT_BEGGINING_OF_FILE = "AnonID\tQuery.Query\tQueryTime\tItemRank\tClickURL";
    public static final int MAX_LENGTH = 20;
    public static final String[] DEFAULT_QUERY = {
            "1",
            "google",
            "2017-08-04 16:00:00",
            "1",
            "www.google.ru"};
    public static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final Random random = new Random();
    public static final int START_CHAR = (int) '!';
    public static final int END_CHAR = (int) '~';


    public static String generateRandomString(int maxLength) {
        final int length = random.nextInt(maxLength + 1);
        return random.ints(length, START_CHAR, END_CHAR + 1)
                .mapToObj(i -> (char) i)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public static String generateRandomStringWithLetter(int maxLength) {
        return generateRandomString(maxLength - 1) + ALPHABET.charAt(random.nextInt(ALPHABET.length()));
    }

    public static LocalDateTime randomLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        int year = 60 * 60 * 24 * 365;
        return now.plusSeconds((long) random.nextInt(2 * year));// ~ +2 years;
    }

}
