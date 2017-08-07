import Parser.QueryCollectionParser;
import Query.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainClass {

    //default files to be processed
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

    //maps query string (vertex of bipartite graph) to another map, which contains incident edges
    private static Map<String, Map<String, Integer>> queries;
    //maps site string (vertex of bipartite graph) to another map, which contains incident edges
    private static Map<String, Map<String, Integer>> sites;
    //maps query to list of other queries in its cluster
    private static Map<String, ArrayList<String>> clusters;

    /**
     * This is main class which calculates clusters from search logs
     *
     * @param args list of files with logs
     * @throws IOException if error occurs while working with files
     */
    public static void main(String[] args) throws IOException {
        //setting default list of files if null passed
        if (args == null || args.length == 0) {
            args = FILE_PATHS;
        }
        //saving time of starting program
        long startTime = System.currentTimeMillis();

        //initializing maps
        queries = new HashMap<>();
        sites = new HashMap<>();
        clusters = new HashMap<>();

        //reading logs and creating graph
        for (String filePath : args) {
            processFile(filePath);
        }

        //for each query creating empty list, it will be used while uniting vertices
        for (Map.Entry<String, Map<String, Integer>> entry : queries.entrySet()) {
            clusters.put(entry.getKey(), new ArrayList<>());
        }

        //do iteration while united vertices have enough similarity
        while (doIteration()) {
            //debugging information about process
            if ((clusters.size() / 1000.0) % 1 == 0) {
                System.out.println(clusters.size() + " time: " +
                        (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
            }
        }

        //information about graph
        System.out.println("Total queries: " + queries.size());
        System.out.println("Total sites: " + sites.size());
        //clearing two unnecessary maps
        queries.clear();
        sites.clear();

        //writing results of clustering to file
        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(new File("results.txt")), "UTF-8"))) {
            for (Map.Entry<String, ArrayList<String>> entry : clusters.entrySet()) {
                bufferedWriter.write(entry.getKey());
                bufferedWriter.newLine();
                for (String query : entry.getValue()) {
                    bufferedWriter.write(query);
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }
        }

        //showing working time
        System.out.println("working time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
    }

    /**
     * Reads logs from file and puts new vertices in graph
     *
     * @param filePath file path
     */
    private static void processFile(String filePath) {
        long startTime = System.currentTimeMillis();

        //creating parser for paring file
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

        //reading all queries
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

        //showing information about file processing
        System.out.println("statistic for file: " + filePath);
        System.out.println("Query.ClickThrough: " + ClickThrough_number);
        System.out.println("Query.QueryOnly: " + QueryOnly_number);
        System.out.println("Working time: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec");
        System.out.println();
    }

    /**
     * Find and unites two most similar queries and then two most similar sites
     *
     * @return method returns true, if similarity of queries enough for uniting
     */
    private static boolean doIteration() {
        //finding two most similar queries
        Pair<Pair<String, String>, Double> resultPair = getMostSimilarVertices(queries, sites);

        Pair<String, String> queriesPair = resultPair.getFirst();
        double maxSimilarity = resultPair.getSecond();

        //if similarity isn't big enough
        if (maxSimilarity == -1 || maxSimilarity < 0.5) {
            return false;
        }

        //uniting clusters of two queries
        final String finalSecondString = queriesPair.getSecond();
        clusters.compute(queriesPair.getFirst(), (k, v) -> {
            ArrayList<String> arrayList = clusters.remove(finalSecondString);
            if (arrayList != null) {
                v.addAll(arrayList);
            }
            v.add(finalSecondString);
            return v;
        });

        //uniting this queries in graph
        uniteVertices(queries, sites, queriesPair.getFirst(), queriesPair.getSecond());

        //finding and uniting two most similar sites
        resultPair = getMostSimilarVertices(sites, queries);
        uniteVertices(sites, queries, resultPair.getFirst().getFirst(), resultPair.getFirst().getSecond());
        return true;
    }

    /**
     * Unites two given vertices
     *
     * @param firstMap     map where this vertices are kept (queries in {@link MainClass#queries}
     *                     and sites in {@link MainClass#sites}
     * @param secondMap    another map(for queries it's {@link MainClass#queries}
     *                     and for sites it's {@link MainClass#sites}
     * @param firstString  string associated with first vertex
     * @param secondString string associated with second vertex
     */
    private static void uniteVertices(Map<String, Map<String, Integer>> firstMap,
                                      Map<String, Map<String, Integer>> secondMap,
                                      String firstString,
                                      String secondString) {
        //find two sets of vertices which are connected with first vertex
        Set<String> connectedWithFirst = firstMap.get(firstString).keySet();
        //find two sets of vertices which are connected with second vertex
        Set<String> connectedWithSecond = firstMap.get(secondString).keySet();

        //calculating set of vertices which are connected with both vertices
        List<String> connectedWithBoth = new ArrayList<>(connectedWithFirst);
        connectedWithBoth.retainAll(connectedWithSecond);

        //calculating set of vertices which are connected with second vertex
        //but not connected wit first
        List<String> connectedWithSecondOnly = new ArrayList<>(connectedWithSecond);
        connectedWithSecondOnly.removeAll(connectedWithFirst);

        //uniting edges which are incident to the same vertex in second part
        for (String vertex : connectedWithBoth) {
            Map<String, Integer> map = secondMap.get(vertex);
            map.compute(firstString, (k, v) -> v + map.remove(secondString));
        }
        //redirecting edges to the first vertex in second part
        for (String vertex : connectedWithSecondOnly) {
            Map<String, Integer> map = secondMap.get(vertex);
            map.put(firstString, map.remove(secondString));
        }

        //updating first part
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

    /**
     * Finds two vertices between queries or sites with biggest vale of
     * similarity function
     *
     * @param firstMap  map where this vertices are kept (queries in {@link MainClass#queries}
     *                  and sites in {@link MainClass#sites}
     * @param secondMap another map(for queries it's {@link MainClass#queries}
     *                  and for sites it's {@link MainClass#sites}
     * @return {@link Pair} of {@link Pair} of strings associated with vertices
     * and value of similarity
     */
    private static Pair<Pair<String, String>, Double>
    getMostSimilarVertices(Map<String, Map<String, Integer>> firstMap,
                           Map<String, Map<String, Integer>> secondMap) {
        Pair<String, String> pair = null;
        double maxSimilarity = -1;
        //cycle across all vertices in part
        for (Map.Entry<String, Map<String, Integer>> startingEntry : firstMap.entrySet()) {
            //calculating similarity with each neighbour vertex
            Set<String> neighboringQueries = getAdjacentVertices(startingEntry, secondMap);
            for (String neighboringQuery : neighboringQueries) {
                double similarity = calculateSimilarity(startingEntry.getValue(), firstMap.get(neighboringQuery));
                //finding the most similar
                if (pair == null) {
                    pair = new Pair<>(startingEntry.getKey(), neighboringQuery);
                    maxSimilarity = similarity;
                } else if (maxSimilarity < similarity) {
                    pair.setFirst(startingEntry.getKey());
                    pair.setSecond(neighboringQuery);
                    maxSimilarity = similarity;
                }
                if (maxSimilarity == 1) return new Pair<>(pair, maxSimilarity);
            }
        }
        return new Pair<>(pair, maxSimilarity);
    }

    /**
     * Finds all vertices in the same part which are adjacent to the same vertices
     * from another part
     *
     * @param startingEntry map with edges of initial vertex
     * @param secondMap     map with vertices from second part
     * @return all adjacent vertices
     */
    private static Set<String> getAdjacentVertices(Map.Entry<String, Map<String, Integer>> startingEntry,
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

    /**
     * Calculates similarity between two vertices
     *
     * @param firstMap  map where this vertices are kept (queries in {@link MainClass#queries}
     *                  and sites in {@link MainClass#sites}
     * @param secondMap another map(for queries it's {@link MainClass#queries}
     *                  and for sites it's {@link MainClass#sites}
     * @return valuse of similarity function
     */
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

}
