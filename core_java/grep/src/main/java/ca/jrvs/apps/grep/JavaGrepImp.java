package ca.jrvs.apps.grep;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class JavaGrepImp implements JavaGrep{

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);
    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception e) {
            javaGrepImp.logger.error("Error: Unable to process", e);
        }
    }

    @Override
    public void process() throws IOException {
        List<File> filesToSearch = listFiles(this.rootPath);
        List<String> matchedLines = new ArrayList<>();

        for (File file : filesToSearch) {
            List<String> lines = readLines(file);
            for (String line : lines) {
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }
        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        List<File> fileList = new LinkedList<>();
        File rootFile = new File(rootDir);
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(listFiles(file.getPath()));
                }
                fileList.add(file);
            }
        }
        return fileList;
    }

    @Override
    public List<String> readLines(File inputFile) {
        List<String> lines = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile.getPath()));
            reader.lines().forEach(lines::add);
        } catch (Exception e) {
            this.logger.error("Error reading file", e);
        }
        return lines;
    }

    @Override
    public boolean containsPattern(String line) {
        return Pattern.matches(this.regex, line);
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter( fw );
        for (String line : lines) {
            bw.write(line);
        }
        bw.close();
    }

    @Override
    public String getRootPath() {
        return this.rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex() {
        return this.regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getOutFile() {
        return this.outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
