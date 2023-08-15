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
import java.util.Arrays;
import java.util.List;

public class JavaGrepImpTest {

    Path directoryRootPath;
    Path directoryChildPath;
    Path filePath1;
    Path filePath2;
    Path filePath3;

    @Before
    public void setup() {
        try {
            directoryRootPath = Files.createTempDirectory(Paths.get("./src/test"), "tmpDirectory1_");
            directoryChildPath = Files.createTempDirectory(directoryRootPath, "tmpDirectory2_");
            filePath1 = Files.createTempFile(directoryChildPath,"testFile1_", ".txt");
            filePath2 = Files.createTempFile(directoryRootPath,"testFile2_", ".txt");
            filePath3 = Files.createTempFile(directoryChildPath,"testFile3_", ".txt");
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
        Assertions.assertNotNull(fileList);
    }

    @Test
    public void readLinesTest() {
        JavaGrepImp javaGrepImp = new JavaGrepImp();

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
        JavaGrepImp javaGrepImp = new JavaGrepImp();
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
}
