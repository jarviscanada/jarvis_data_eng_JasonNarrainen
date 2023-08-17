package ca.jrvs.apps.grep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepLambdaImp implements JavaGrep{

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);
    private String regex;
    private String rootPath;
    private String outFile;

    @Override
    public void process() throws IOException {
        List<File> filesToSearch = listFiles(this.rootPath);
        List<String> lines = new LinkedList<>();
        filesToSearch.forEach(file -> {
            lines.addAll(readLines(file));
        });
        List<String> matchedLines = lines.stream().filter(this::containsPattern).collect(Collectors.toList());
        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        File rootFile = new File(rootDir);
        List<File> fileList = new LinkedList<>();
        try (Stream<Path> walk = Files.walk(rootFile.toPath())){
            fileList = walk.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return fileList;
    }

    @Override
    public List<String> readLines(File inputFile) {
        Stream.Builder<String> linesStreamBuilder = Stream.builder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile.getPath()));
            reader.lines().forEach(linesStreamBuilder);
        } catch (Exception e) {
            this.logger.error("Error reading file", e);
        }
        return linesStreamBuilder.build().collect(Collectors.toList());
    }

    @Override
    public boolean containsPattern(String line) {
        return Pattern.matches(this.regex, line);
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter( fw );
        lines.forEach(line -> {
            try {
                bw.write(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        bw.close();
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getOutFile() {
        return outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
