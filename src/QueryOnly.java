import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.StringTokenizer;

public class QueryOnly implements Query {

    private int anonID;
    private String query;
    private LocalDateTime queryTime;

    //TODO: specify exceptions
    QueryOnly(String inputString) throws IllegalArgumentException {
        String[] splitteredString = inputString.trim().split("\t");
        try {
            setAnonId(Integer.parseInt(splitteredString[0]));
            setQuery(splitteredString[1]);
            setQueryTime(splitteredString[2]);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("Wrong input string format", e);
        }
    }

    QueryOnly() {
        anonID = 0;
        query = null;
        queryTime = null;
    }

    @Override
    public int getAnonId() {
        return anonID;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public LocalDateTime getQueryTime() {
        return queryTime;
    }

    @Override
    public int setAnonId(int anonID) {
        int oldAnonID = this.anonID;
        this.anonID = anonID;
        return oldAnonID;
    }

    @Override
    public String setQuery(String query) {
        String oldQuery = this.query;
        this.query = query;
        return oldQuery;
    }

    @Override
    public LocalDateTime setQueryTime(String queryTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("u-M-d H:m:s");
        LocalDateTime oldQueryTime = this.queryTime;
        this.queryTime = LocalDateTime.parse(queryTime, formatter);
        return oldQueryTime;
    }

    @Override
    public LocalDateTime setQueryTime(LocalDateTime queryTime) {
        LocalDateTime oldQueryTime = this.queryTime;
        this.queryTime = queryTime;
        return oldQueryTime;
    }
}
