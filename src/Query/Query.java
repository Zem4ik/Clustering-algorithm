package Query;

import java.time.LocalDateTime;

public interface Query {

    int getAnonId();

    String getQuery();

    LocalDateTime getQueryTime();

    int setAnonId(int id);

    String setQuery(String query);

    LocalDateTime setQueryTime(String queryTime);

    LocalDateTime setQueryTime(LocalDateTime queryTime);

}
