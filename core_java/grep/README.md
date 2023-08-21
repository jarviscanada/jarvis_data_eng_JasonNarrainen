# Introduction
This app works as a java implementation of the grep tool. It allows searching
a regex pattern recursively in a given folder. It is a portable app that can 
be run anywhere with Docker. This project uses Java, Lambda and Stream, JUnit, 
Maven and Docker

# Quick Start
```shell
# Pull image from DockerHub
docker pull jasonn46/grep:latest

# Run Docker image and run app command 
docker run -v `pwd`/data:/data -v `pwd`/log:/log \
jasonn46/grep regex_pattern /data /log/grep.out
```

# Implementation
## Pseudocode
Process method pseudocode:  

```text
matchedLines = []
for file in listFilesRecursively(rootDir)
    for line in readLines(file)
        if containsPattern(line)
            matchedLines.add(line)
writeToFile(matchedLines);
```

## Performance Issue
Searching big text files can impact performance or cause OutOfMemoryErrors so 
the use of Stream instead of loops has been implemented for working 
sequentially and buffered readers and writers are used instead of normal ones.

# Test
Happy path tests were made for each of the methods for test-driven development. 
Temporary files and folders are created before every test and the result is
compared to expected data.

# Deployment
The app has been dockerized with the openjdk base image for the java dependency.

# Improvement
- Additional testing for edge-cases
- Allow additional input sources
- Allow additional tags for more features such as get the line number for the 
matched line
