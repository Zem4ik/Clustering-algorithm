public class Utils {

    public static String[] splitByIndexes(String inputString, int[] indexes) {
        String[] result = new String[indexes.length + 1];
        result[0] = inputString.substring(0, indexes[0]);
        int previousIndex = indexes[0];
        for (int i = 1; i < indexes.length; ++i) {
            result[i] = inputString.substring(previousIndex + 1, indexes[i]);
        }
        result[result.length - 1] = inputString.substring(indexes[indexes.length - 1], inputString.length());
        return result;
    }

}
