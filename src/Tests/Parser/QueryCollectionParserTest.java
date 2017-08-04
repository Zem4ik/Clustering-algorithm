package Tests.Parser;

import Parser.QueryCollectionParser;
import Utils.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

public class QueryCollectionParserTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = IOException.class)
    public void openingNonExistentFile() throws IOException {
        QueryCollectionParser parser = new QueryCollectionParser();
        parser.openFile(new File("notExistentFile.test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void readingWrongFormatQuery() throws IOException {
        File newFile = folder.newFile("fileWithWrongFormat.test");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile)));

        bufferedWriter.write(Utils.DEFAULT_BEGGINING_OF_FILE);
        bufferedWriter.newLine();
        bufferedWriter.write(Utils.generateRandomString(Utils.MAX_LENGTH));
        bufferedWriter.close();

        QueryCollectionParser parser = new QueryCollectionParser();
        parser.openFile(newFile);
        parser.nextQuery();
    }

}
