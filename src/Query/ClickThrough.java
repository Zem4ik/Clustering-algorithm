package Query;

import Query.QueryOnly;

import java.time.format.DateTimeParseException;

public class ClickThrough extends QueryOnly {

    private int itemRank;
    private String clickURL;

    public ClickThrough() {
        super();
        itemRank = 0;
        clickURL = null;
    }

    //TODO: specify exceptions
    public ClickThrough(String inputString) throws IllegalArgumentException {
        String[] splitteredString = inputString.trim().split("\t");
        try {
            setAnonId(Integer.parseInt(splitteredString[0]));
            setQuery(splitteredString[1]);
            setQueryTime(splitteredString[2]);
            setItemRank(Integer.parseInt(splitteredString[3]));
            setClickURL(splitteredString[4]);
        } catch (NumberFormatException | DateTimeParseException | IndexOutOfBoundsException e ) {
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
