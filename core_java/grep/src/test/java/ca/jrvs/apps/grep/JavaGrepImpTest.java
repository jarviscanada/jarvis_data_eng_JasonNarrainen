package ca.jrvs.apps.grep;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaGrepImpTest {

    Path directoryRootPath;
    Path directoryChildPath;
    Path filePath1;
    Path filePath2;
    Path filePath3;
    List<File> files;

    @Before
    public void setup() {
        try {
            directoryRootPath = Files.createTempDirectory(Paths.get("./src/test"), "tmpDirectory1_");
            directoryChildPath = Files.createTempDirectory(directoryRootPath, "tmpDirectory2_");
            filePath1 = Files.createTempFile(directoryChildPath,"testFile1_", ".txt");
            filePath2 = Files.createTempFile(directoryRootPath,"testFile2_", ".txt");
            filePath3 = Files.createTempFile(directoryChildPath,"testFile3_", ".txt");
            files = new ArrayList<>();
            files.add(filePath1.toFile());
            files.add(filePath2.toFile());
            files.add(filePath3.toFile());
            FileWriter fw1 = new FileWriter(filePath1.toFile());
            BufferedWriter bw1 = new BufferedWriter( fw1 );
            bw1.write( "content for file1\n" + "Testing\n" + "the\n" + "test");
            bw1.close();
            FileWriter fw2 = new FileWriter(filePath2.toFile());
            BufferedWriter bw2 = new BufferedWriter( fw2 );
            bw2.write( "content for file2\n" + "Testing\n" + "the\n" + "test");
            bw2.close();
            if (filePath1 != null) {
                directoryRootPath.toFile().deleteOnExit();
            }
        } catch (Exception e) {
            System.err.println("Error creating temporary test file in " + this.getClass().getSimpleName());
        }
    }

    @After
    public void reset() {
        try {
            Files.delete(filePath3);
            Files.delete(filePath2);
            Files.delete(filePath1);
            Files.delete(directoryChildPath);
        } catch (IOException e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }

    @Test
    public void listFilesTest() {
        JavaGrepImp javaGrepImp = new JavaGrepImp();
        List<File> fileList = javaGrepImp.listFiles(directoryRootPath.toFile().getPath());
        Assertions.assertEquals(fileList.size(), files.size());
        Assertions.assertTrue(fileList.containsAll(files));
    }

    @Test
    public void listFilesLambdaTest() {
        JavaGrep javaGrepLambda = new JavaGrepLambdaImp();
        List<File> fileList = javaGrepLambda.listFiles(directoryRootPath.toFile().getPath());
        fileList.forEach(e -> System.out.println(e.getPath()));
        Assertions.assertEquals(fileList.size(), files.size());
        Assertions.assertTrue(fileList.containsAll(files));
    }

    @Test
    public void readLinesTest() {
        JavaGrepImp javaGrepImp = new JavaGrepImp();
        List<String> lines = javaGrepImp.readLines(filePath1.toFile());
        Assertions.assertEquals(lines.size(), 4);
    }

    @Test
    public void readLinesLambdaTest() {
        JavaGrep javaGrepImp = new JavaGrepLambdaImp();
        List<String> lines = javaGrepImp.readLines(filePath1.toFile());
        Assertions.assertEquals(lines.size(), 4);
    }

    @Test
    public void containsPatternTest() {
        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex("[a-zA-Z0-9]{4}");

        Assertions.assertTrue(javaGrepImp.containsPattern("test"));
        Assertions.assertFalse(javaGrepImp.containsPattern("testing"));
    }

    @Test
    public void processTest() {
        JavaGrepLambdaImp javaGrepImp = new JavaGrepLambdaImp();
        javaGrepImp.setRootPath(directoryChildPath.toFile().getPath());
        javaGrepImp.setRegex("[a-zA-Z0-9]{4}");
        javaGrepImp.setOutFile(filePath3.toFile().getPath());

        try {

            Assertions.assertEquals(Files.size(filePath3), 0);
            javaGrepImp.process();
            Assertions.assertNotEquals(Files.size(filePath3), 0);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void processTest2() {
        JavaGrepLambdaImp javaGrepImp = new JavaGrepLambdaImp();
        javaGrepImp.setRootPath("./data");
        javaGrepImp.setRegex(".*Romeo.*Juliet.*");
        javaGrepImp.setOutFile("./out/grep.txt");

        try {

            Assertions.assertEquals(Files.size(filePath3), 0);
            javaGrepImp.process();
            Assertions.assertNotEquals(Files.size(Paths.get("./out/grep.txt")), 0);
        } catch (IOException e) {
            Assertions.fail(e);
        }
    }
}
