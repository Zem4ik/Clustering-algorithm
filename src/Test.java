import java.io.IOException;
import java.util.*;

public class Test {
    private static String[] FILE_PATHS = {"user-ct-test-collection-01.txt",
            "user-ct-test-collection-02.txt",
            "user-ct-test-collection-03.txt",
            "user-ct-test-collection-04.txt",
            "user-ct-test-collection-05.txt",
            "user-ct-test-collection-06.txt",
            "user-ct-test-collection-07.txt",
            "user-ct-test-collection-08.txt",
            "user-ct-test-collection-09.txt",
            "user-ct-test-collection-10.txt"};

//    private static Map<String, Map<String, Integer>> queries;
//    private static Map<String, Map<String, Integer>> sites;
    private static Map<Pair<String, String>, Integer> sites;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        //queries = new HashMap<>();
        sites = new HashMap<>();
        for (String filePath : FILE_PATHS) {
            processFile(filePath);
        }
        //System.out.println(queries.size());
        System.out.println(sites.size());
        System.out.println((System.currentTimeMillis() - startTime) / 1000.0);
    }

    private static void processFile(String filePath) {
        QueryCollectionParser queryCollectionParser = new QueryCollectionParser();
        try {
            queryCollectionParser.openFile(filePath);
        } catch (IOException e) {
            System.err.println("Can't open file " + filePath);
        }
        Query query = new QueryOnly();
        int ClickThrough_number = 0;
        int QueryOnly_number = 0;
        for (int i = 1; query != null; ++i) {
            try {
                query = queryCollectionParser.nextQuery();
                if (query instanceof ClickThrough) {
                    ++ClickThrough_number;
//                    queries.computeIfAbsent(query.getQuery(), k -> new HashMap<>());
                    //sites.computeIfAbsent(((ClickThrough) query).getClickURL(), k -> new HashMap<>());
//                    queries.get(query.getQuery()).compute(((ClickThrough) query).getClickURL(),
//                            (k, v) -> (v != null) ? (v + 1) : 1);
//                    sites.get(((ClickThrough) query).getClickURL()).compute(query.getQuery(),
//                            (k, v) -> (v != null) ? (v + 1) : 1);
                    sites.compute(new Pair<>(query.getQuery(), ((ClickThrough) query).getClickURL()),
                            (k, v) -> (v != null) ? (v + 1) : 1);
                } else {
                    ++QueryOnly_number;
                }
            } catch (Exception e) {
                System.err.println("Can't parse " + i + " line in file " + filePath);
                e.printStackTrace();
            }
        }
        System.out.println("statistic for file: " + filePath);
        System.out.println("ClickThrough: " + ClickThrough_number);
        System.out.println("QueryOnly: " + QueryOnly_number);
        System.out.println();
    }

}
