import java.time.format.DateTimeParseException;

public class ClickThrough extends QueryOnly {

    private int itemRank;
    private String clickURL;

//    //TODO: specify exceptions
//    private String[] parseInputString(String inputString) throws IllegalArgumentException {
//        inputString = inputString.trim();
//        int[] splittingIndexes = new int[4];
//        //getting last index of part with AnonID
//        splittingIndexes[0] = inputString.indexOf(' ');
//        if (!(splittingIndexes[0] > 0 && splittingIndexes[0] < inputString.length() - 1)) {
//            throw new IllegalArgumentException("Wrong input string format");
//        }
//        //getting first index of part with ClickURL
//        int i = inputString.length() - 1;
//        for (; i > 0 && inputString.charAt(i) != ' '; i--) {}
//        splittingIndexes[3] = i;
//        if (!(splittingIndexes[3] > splittingIndexes[0] && splittingIndexes[3] < inputString.length() - 1)) {
//            throw new IllegalArgumentException("Wrong input string format");
//        }
//        //getting first index of part with string with ItemRank
//        --i;
//        for (; i > 0 && inputString.charAt(i) != ' '; i--) {}
//        splittingIndexes[2] = i;
//        if (!(splittingIndexes[2] > splittingIndexes[0] && splittingIndexes[2] < splittingIndexes[3])) {
//            throw new IllegalArgumentException("Wrong input string format");
//        }
//        //getting first index of part with string with date and time of query
//        --i;
//        for (int calc = 2; i > 0 && calc > 0; i--) {
//            if (inputString.charAt(i) == ' ') {
//                --calc;
//            }
//        }
//        splittingIndexes[1] = i;
//        if (splittingIndexes[1] > splittingIndexes[0] && splittingIndexes[1] < splittingIndexes[2]) {
//            throw new IllegalArgumentException("Wrong input string format");
//        }
//        //splitting string in three parts
//        return Utils.splitByIndexes(inputString, splittingIndexes);
//    }

    //TODO: specify exceptions
    ClickThrough(String inputString) throws IllegalArgumentException {
        String[] splitteredString = inputString.trim().split("\t");
        try {
            setAnonId(Integer.parseInt(splitteredString[0]));
            setQuery(splitteredString[1]);
            setQueryTime(splitteredString[2]);
            setItemRank(Integer.parseInt(splitteredString[3]));
            setClickURL(splitteredString[4]);
        } catch (NumberFormatException | DateTimeParseException e ) {
            throw new IllegalArgumentException("Wrong input string format", e);
        }
    }

    public int getItemRank() {
        return itemRank;
    }

    public int setItemRank(int itemRank) {
        int oldItemRank = this.itemRank;
        this.itemRank = itemRank;
        return oldItemRank;
    }

    public String getClickURL() {
        return clickURL;
    }

    public String setClickURL(String clickURL) {
        String oldClickURL = this.clickURL;
        this.clickURL = clickURL;
        return oldClickURL;
    }
}
