import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class QueryCollectionParser {
    private BufferedReader bufferedReader;

    public QueryCollectionParser() {}

    public void openFile(String filePath) throws IOException {
        File file = new File(filePath);
        openFile(file);
    }

    public void openFile(File file) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        bufferedReader.readLine();
    }

    public Query nextQuery() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("u-M-d H:m:s");
        Query query = null;
        String nextLine;
        if ((nextLine = bufferedReader.readLine()) != null) {
            try {
                formatter.parse(nextLine.trim().substring(nextLine.trim().lastIndexOf("\t") + 1));
                query = new QueryOnly(nextLine);
            } catch (DateTimeParseException e) {
                query = new ClickThrough(nextLine);
            }
        }
        return query;
    }

}
