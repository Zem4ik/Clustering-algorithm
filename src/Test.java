import java.io.*;
import java.util.*;

public class Test {
    private static String[] FILE_PATHS = {
            "user-ct-test-collection-01.txt",
//            "user-ct-test-collection-02.txt",
//            "user-ct-test-collection-03.txt",
//            "user-ct-test-collection-04.txt",
//            "user-ct-test-collection-05.txt",
//            "user-ct-test-collection-06.txt",
//            "user-ct-test-collection-07.txt",
//            "user-ct-test-collection-08.txt",
//            "user-ct-test-collection-09.txt",
//            "user-ct-test-collection-10.txt"
    };

    private static Map<String, Map<String, Integer>> queries;
    private static Map<String, Map<String, Integer>> sites;

    public static void main(String[] args) throws IOException {
        //writeTestFile();

        long startTime = System.currentTimeMillis();
        queries = new HashMap<>();
        sites = new HashMap<>();
        for (String filePath : FILE_PATHS) {
            processFile(filePath);
        }
        long summary = 0;
        Pair<String, Double>[] similarQueriesArray;
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("results.txt"))));
        for (Map.Entry<String, Map<String, Integer>> entry : queries.entrySet()) {
            Set<Pair<String, Double>> similarQueries = getSimilarQueries(entry);
            similarQueriesArray = similarQueries.stream().toArray(Pair[]::new);
            Arrays.sort(similarQueriesArray, Collections.reverseOrder(Comparator.comparing(Pair::getSecond)));
            bufferedWriter.write(entry.getKey() + " " + 1.0);
            bufferedWriter.newLine();
            System.out.println(Integer.toString(similarQueriesArray.length));
            summary += similarQueriesArray.length;
            for (Pair<String, Double> pair : similarQueriesArray) {
                bufferedWriter.write(pair.getFirst() + " " + pair.getSecond());
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        System.out.println("summary: " + summary);
        System.out.println("Total queries: " + queries.size());
        System.out.println("Total sites: " + sites.size());
        System.out.println("working time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
    }

    private static void processFile(String filePath) {
        long startTime = System.currentTimeMillis();
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
                    queries.computeIfAbsent(query.getQuery(), k -> new HashMap<>());
                    sites.computeIfAbsent(((ClickThrough) query).getClickURL(), k -> new HashMap<>());
                    queries.get(query.getQuery()).compute(((ClickThrough) query).getClickURL(),
                            (k, v) -> (v != null) ? (v + 1) : 1);
                    sites.get(((ClickThrough) query).getClickURL()).compute(query.getQuery(),
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
        System.out.println("Working time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
        System.out.println();
    }

    private static Set<String> getNeighboringQueries(Map.Entry<String, Map<String, Integer>> startingEntry) {
        Set<String> neighboringQueries = new HashSet<>();
        Map<String, Integer> map = startingEntry.getValue();
        for (Map.Entry<String, Integer> queryEntry : map.entrySet()) {
            for (Map.Entry<String, Integer> siteEntry : sites.get(queryEntry.getKey()).entrySet()) {
                if (!siteEntry.getKey().equals(startingEntry.getKey()) &&
                        !neighboringQueries.contains(siteEntry.getKey())) {
                    neighboringQueries.add(siteEntry.getKey());
                }
            }
        }
        return neighboringQueries;
    }

    private static double calculateSimilarity(Map<String, Integer> firstMap, Map<String, Integer> secondMap) {
        double sameDocuments = 0;
        long firstDocuments, secondDocuments;
        firstDocuments = secondDocuments = 0;
        for (Map.Entry<String, Integer> entry : firstMap.entrySet()) {
            firstDocuments += entry.getValue();
            if (secondMap.containsKey(entry.getKey())) {
                sameDocuments += entry.getValue() + secondMap.get(entry.getKey());
            }
        }
        for (Map.Entry<String, Integer> entry : secondMap.entrySet()) {
            secondDocuments += entry.getValue();
        }
        return sameDocuments / (firstDocuments + secondDocuments);
    }

    private static Set<Pair<String, Double>> getSimilarQueries(Map.Entry<String, Map<String, Integer>> startingEntry) {
        Set<Pair<String, Double>> resultSet = new HashSet<>();
        Set<String> neighboringQueries = getNeighboringQueries(startingEntry);
        for (String neighboringQuery : neighboringQueries) {
            double similarity = calculateSimilarity(startingEntry.getValue(), queries.get(neighboringQuery));
            if (similarity > 0.001) {
                resultSet.add(new Pair<>(neighboringQuery, similarity));
            }
        }
        return resultSet;
    }

    private static void writeTestFile() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("test.txt"))));
        bufferedWriter.write("AnonID\tQuery\tQueryTime\tItemRank\tClickURL");
        bufferedWriter.newLine();
        for (int i = 0; i < 10; ++i) {
            bufferedWriter.write("1\ta\t2006-03-01 07:17:12\t1\td2");
            bufferedWriter.newLine();
        }
        for (int i = 0; i < 1000; ++i) {
            bufferedWriter.write("1\ta\t2006-03-01 07:17:12\t1\td1");
            bufferedWriter.newLine();
            bufferedWriter.write("1\tb\t2006-03-01 07:17:12\t1\td2");
            bufferedWriter.newLine();
            bufferedWriter.write("1\tb\t2006-03-01 07:17:12\t1\td3");
            bufferedWriter.newLine();
        }
        for (int i = 0; i < 100; ++i) {
            bufferedWriter.write("1\tc\t2006-03-01 07:17:12\t1\td3");
            bufferedWriter.newLine();
        }
        bufferedWriter.write("1\tc\t2006-03-01 07:17:12\t1\td2");
        bufferedWriter.newLine();
        bufferedWriter.close();
    }
}
