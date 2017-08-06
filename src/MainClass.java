import Parser.QueryCollectionParser;
import Query.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainClass {
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
    private static Map<String, ArrayList<String>> clusters;
    private static SortedSet<Pair<Pair<String, String>, Double>> queriesSimilarity;
    private static SortedSet<Pair<Pair<String, String>, Double>> sitesSimilarity;

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            args = FILE_PATHS;
        }
        long startTime = System.currentTimeMillis();

        queries = new HashMap<>();
        sites = new HashMap<>();
        clusters = new HashMap<>();
        queriesSimilarity = new TreeSet<>(Comparator.comparingDouble(Pair::getSecond));
        sitesSimilarity = new TreeSet<>(Comparator.comparingDouble(Pair::getSecond));

        for (String filePath : args) {
            processFile(filePath);
        }

        for (Map.Entry<String, Map<String, Integer>> entry : queries.entrySet()) {
            clusters.put(entry.getKey(), new ArrayList<>());
        }

//        preprocessing(queries, sites, queriesSimilarity);
//        System.out.println("queries preprocessing finished, time: " +
//                        (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
//        preprocessing(sites, queries, sitesSimilarity);
//        System.out.println("sites preprocessing finished, time: " +
//                (System.currentTimeMillis() - startTime) / 1000.0 + " sec");


        while (doIteration()) {
            if ((clusters.size() / 1000.0) % 1 == 0) {
                System.out.println(clusters.size() + " time: " +
                        (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
            }
        }

        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File("results.txt")), "UTF-8"));

        queries.clear();
        sites.clear();
        for (Map.Entry<String, ArrayList<String>> entry : clusters.entrySet()) {
            bufferedWriter.write(entry.getKey());
            bufferedWriter.newLine();
            for (String query : entry.getValue()) {
                bufferedWriter.write(query);
                bufferedWriter.newLine();
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

//        System.out.println("Total queries: " + queries.size());
//        System.out.println("Total sites: " + sites.size());
        System.out.println("working time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
    }

    private static void processFile(String filePath) {
        long startTime = System.currentTimeMillis();
        QueryCollectionParser queryCollectionParser = new QueryCollectionParser();
        try {
            queryCollectionParser.openFile(filePath);
        } catch (IOException e) {
            System.err.println("Can't open file " + filePath);
            return;
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
        System.out.println("Query.ClickThrough: " + ClickThrough_number);
        System.out.println("Query.QueryOnly: " + QueryOnly_number);
        System.out.println("Working time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
        System.out.println();
    }

    private static void preprocessing(Map<String, Map<String, Integer>> firstMap,
                                      Map<String, Map<String, Integer>> secondMap,
                                      SortedSet<Pair<Pair<String, String>, Double>> resultSet) {
        Set<Pair<String, String>> precessedPairs = new HashSet<>();

        for (Map.Entry<String, Map<String, Integer>> startingEntry : firstMap.entrySet()) {
            Set<String> neighboringQueries = getNeighboringQueries(startingEntry, secondMap);
            for (String neighboringQuery : neighboringQueries) {
                Pair<String, String> pair1 = new Pair<>(startingEntry.getKey(), neighboringQuery);
                Pair<String, String> pair2 = new Pair<>(startingEntry.getKey(), neighboringQuery);
                if (!precessedPairs.contains(pair1) && !precessedPairs.contains(pair2)) {
                    double similarity = calculateSimilarity(startingEntry.getValue(), firstMap.get(neighboringQuery));
                    precessedPairs.add(pair1);
                    resultSet.add(new Pair<>(pair1, similarity));
                }
            }
        }
    }

    private static boolean doIteration() {
        Pair<Pair<String, String>, Double> resultPair = getMostSimilarVertex(queries, sites);

        Pair<String, String> queriesPair = resultPair.getFirst();
        double maxSimilarity = resultPair.getSecond();

        if (maxSimilarity == -1 || maxSimilarity < 0.5) {
            return false;
        }

        final String finalSecondString = queriesPair.getSecond();
        clusters.compute(queriesPair.getFirst(), (k, v) -> {
            ArrayList<String> arrayList = clusters.remove(finalSecondString);
            if (arrayList != null) {
                v.addAll(arrayList);
            }
            v.add(finalSecondString);
            return v;
        });

        uniteVertices(queries, sites, queriesPair.getFirst(), queriesPair.getSecond());

        resultPair = getMostSimilarVertex(sites, queries);
        uniteVertices(sites, queries, resultPair.getFirst().getFirst(), resultPair.getFirst().getSecond());
        return true;
    }

    private static void uniteVertices(Map<String, Map<String, Integer>> firstMap,
                               Map<String, Map<String, Integer>> secondMap,
                               String firstString,
                               String secondString) {
        Set<String> connectedWithFirst = firstMap.get(firstString).keySet();
        Set<String> connectedWithSecond = firstMap.get(secondString).keySet();

        List<String> connectedWithBoth = new ArrayList<>(connectedWithFirst);
        connectedWithBoth.retainAll(connectedWithSecond);

        List<String> connectedWithSecondOnly = new ArrayList<>(connectedWithSecond);
        connectedWithSecondOnly.removeAll(connectedWithFirst);

        for (String vertex : connectedWithBoth) {
            Map<String, Integer> map = secondMap.get(vertex);
            map.compute(firstString, (k, v) -> v + map.remove(secondString));
        }
        for (String vertex : connectedWithSecondOnly) {
            Map<String, Integer> map = secondMap.get(vertex);
            map.put(firstString, map.remove(secondString));
        }

        Map<String, Integer> newMap = Stream.of(firstMap.get(firstString), firstMap.get(secondString))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum,
                        HashMap::new
                ));
        firstMap.remove(secondString);
        firstMap.put(firstString, newMap);
    }

    private static Pair<Pair<String, String>, Double>
    getMostSimilarVertex(Map<String, Map<String, Integer>> firstMap,
                         Map<String, Map<String, Integer>> secondMap) {
        Pair<String, String> pair = null;
        double maxSimilarity = -1;
        for (Map.Entry<String, Map<String, Integer>> startingEntry : firstMap.entrySet()) {
            Set<String> neighboringQueries = getNeighboringQueries(startingEntry, secondMap);
            for (String neighboringQuery : neighboringQueries) {
                double similarity = calculateSimilarity(startingEntry.getValue(), firstMap.get(neighboringQuery));
                if (pair == null) {
                    pair = new Pair<>(startingEntry.getKey(), neighboringQuery);
                    maxSimilarity = similarity;
                } else if (maxSimilarity < similarity) {
                    pair.setFirst(startingEntry.getKey());
                    pair.setSecond(neighboringQuery);
                    maxSimilarity = similarity;
                }
                if (maxSimilarity == 1) return  new Pair<>(pair, maxSimilarity);
            }
        }
        return new Pair<>(pair, maxSimilarity);
    }

    private static Set<String> getNeighboringQueries(Map.Entry<String, Map<String, Integer>> startingEntry,
                                                     Map<String, Map<String, Integer>> secondMap) {
        Set<String> neighboringQueries = new HashSet<>();
        Map<String, Integer> map = startingEntry.getValue();
        for (Map.Entry<String, Integer> firstEntry : map.entrySet()) {
            for (Map.Entry<String, Integer> secondEntry : secondMap.get(firstEntry.getKey()).entrySet()) {
                if (!secondEntry.getKey().equals(startingEntry.getKey())) {
                    neighboringQueries.add(secondEntry.getKey());
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

//    @Deprecated
//    private static SortedSet<Pair<String, Double>> getSimilarQueries(Map.Entry<String, Map<String, Integer>> startingEntry) {
//        SortedSet<Pair<String, Double>> resultSet = new TreeSet<>(Comparator.comparing(Pair::getSecond));
//        Set<String> neighboringQueries = getNeighboringQueries(startingEntry);
//        for (String neighboringQuery : neighboringQueries) {
//            double similarity = calculateSimilarity(startingEntry.getValue(), queries.get(neighboringQuery));
//            if (similarity > 0.001) {
//                resultSet.add(new Pair<>(neighboringQuery, similarity));
//            }
//        }
//        return resultSet;
//    }

}
