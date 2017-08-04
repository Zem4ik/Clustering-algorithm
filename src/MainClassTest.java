import Utils.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public class MainClassTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testMainClass1() throws IOException {
        File newFile = folder.newFile("testMainClass1.test");
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(newFile)));
        bufferedWriter.write("AnonID\tQuery.Query\tQueryTime\tItemRank\tClickURL");
        bufferedWriter.newLine();
        for (int i = 0; i < 10; ++i) {
            bufferedWriter.write("1\ta\t2006-03-01 07:17:12\t1\td1");
            bufferedWriter.newLine();
            bufferedWriter.write("1\ta\t2006-03-01 07:17:12\t1\td2");
            bufferedWriter.newLine();
        }
        for (int i = 0; i < 1000; ++i) {
            bufferedWriter.write("1\tb\t2006-03-01 07:17:12\t1\td2");
            bufferedWriter.newLine();
            bufferedWriter.write("1\tb\t2006-03-01 07:17:12\t1\td3");
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        MainClass.main(new String[] {newFile.getAbsolutePath()});
    }

    @Test
    public void testMainClass2() throws IOException {
        File newFile = folder.newFile("testMainClass2.test");
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(newFile)));
        bufferedWriter.write("AnonID\tQuery.Query\tQueryTime\tItemRank\tClickURL");
        bufferedWriter.newLine();
        for (int i = 0; i < 10; ++i) {
            bufferedWriter.write("1\ta\t2006-03-01 07:17:12\t1\td1");
            bufferedWriter.newLine();
        }
        for (int i = 0; i < 1000; ++i) {
            bufferedWriter.write("1\ta\t2006-03-01 07:17:12\t1\td2");
            bufferedWriter.newLine();
            bufferedWriter.write("1\tb\t2006-03-01 07:17:12\t1\td2");
            bufferedWriter.newLine();
            bufferedWriter.write("1\tb\t2006-03-01 07:17:12\t1\td3");
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        MainClass.main(new String[] {newFile.getAbsolutePath()});
    }

    @Test
    public void testMainClass3() throws IOException {
        File newFile = folder.newFile("testMainClass2.test");
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(newFile)));
        bufferedWriter.write("AnonID\tQuery.Query\tQueryTime\tItemRank\tClickURL");
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
        MainClass.main(new String[] {newFile.getAbsolutePath()});
    }
}
