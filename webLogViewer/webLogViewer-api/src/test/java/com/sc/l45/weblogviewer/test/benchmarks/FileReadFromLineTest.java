package com.sc.l45.weblogviewer.test.benchmarks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.reader.BufferedReaderLineSeparator;
import com.sc.l45.weblogviewer.test.config.TestConf;
import com.sc.l45.weblogviewer.test.utils.MathUtils;

public class FileReadFromLineTest extends TestConf{
    private final static Logger logger = LoggerFactory.getLogger(ReverseFileReadTest.class);

    @Test
    public void benchmarkReadFromFile() throws IOException {
        final int repeat = 1;
        final int lineFromStartRead = 100000;
        
        //La prima volta che leggo il file ci mette di pi�, quindi effettuo una lettura per non falsare i risultati
        readLastLinesWithBufferedReader(lineFromStartRead);
        
        List<Long> timeList2 = new ArrayList<>();
        List<String> result2 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result2 = readLastLinesWithBufferedReader(lineFromStartRead);
            timeList2.add(timer.timeInMillis());
        }
        OptionalDouble average2 = MathUtils.avarage(timeList2);
        logger.info("Lettura {} righe con BufferedReader {}ms", result2.size(), average2.getAsDouble());
        
        List<Long> timeList1 = new ArrayList<>();
        List<String> result1 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result1 = readLastLinesWithFileUtils(lineFromStartRead);
            timeList1.add(timer.timeInMillis());
        }
        OptionalDouble average1 = MathUtils.avarage(timeList1);
        logger.info("Lettura {} righe con FileUtils {}ms", result1.size(), average1.getAsDouble());
        
        List<Long> timeList5 = new ArrayList<>();
        List<String> result5 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result5 = readLastLinesWithMyBufferedReader(lineFromStartRead);
            timeList5.add(timer.timeInMillis());
        }
        OptionalDouble average5 = MathUtils.avarage(timeList5);
        logger.info("Lettura {} righe con BufferedReader custom {}ms", result5.size(), average5.getAsDouble());
    }
    
    private List<String> readLastLinesWithBufferedReader(int linesToRead) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(testFile);InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);) {
            List<String> readContent = new ArrayList<>();
            
            String line;
            int nLines = 0;
            while((line = br.readLine()) != null) {
                nLines++;
                if(nLines >= linesToRead) {
                    readContent.add(line);
                }
            }
            
            if(linesToRead > readContent.size()) {
                linesToRead = readContent.size();
            }
            
            return readContent;
        }
    }
    
    private List<String> readLastLinesWithMyBufferedReader(int linesToRead) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(testFile);InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReaderLineSeparator br = new BufferedReaderLineSeparator(isr);) {
            List<String> readContent = new ArrayList<>();
            
            String line;
            int nLines = 0;
            while((line = br.readLine()) != null) {
                nLines++;
                if(nLines >= linesToRead) {
                    readContent.add(line);
                }
            }
            
            if(linesToRead > readContent.size()) {
                linesToRead = readContent.size();
            }
            
            return readContent;
        }
    }
    
    private List<String> readLastLinesWithFileUtils(int linesToRead) throws IOException {
        List<String> content = new ArrayList<>();
        List<String> allLines = FileUtils.readLines(testFile, StandardCharsets.UTF_8);
        
        if(linesToRead > allLines.size()) {
            linesToRead = allLines.size();
        }
        
        content = allLines.subList(linesToRead - 1, allLines.size());
        return content;
    }
    
}
