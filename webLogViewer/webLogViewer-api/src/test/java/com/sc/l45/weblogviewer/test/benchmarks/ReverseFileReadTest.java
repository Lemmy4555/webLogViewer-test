package com.sc.l45.weblogviewer.test.benchmarks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.reader.ReversedLinesFileReaderLineSeparator;
import com.sc.l45.weblogviewer.test.config.TestConf;
import com.sc.l45.weblogviewer.test.utils.MathUtils;

import jersey.repackaged.com.google.common.collect.Lists;

public class ReverseFileReadTest extends TestConf {
    private final static Logger logger = LoggerFactory.getLogger(ReverseFileReadTest.class);
    
    @Test
    public void benchmarkReverseReadFile() throws IOException {
        final int repeat = 10;
        final int linesToRead = 60000;
        
        //La prima volta che leggo il file ci mette di pi�, quindi effettuo una lettura per non falsare i risultati
        readLastLinesWithBufferedReader(linesToRead);
        
        List<Long> timeList = new ArrayList<>();
        List<String> result = null;
        for(int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result = readLastLinesWithReverseLinesFileReader(linesToRead);
            timeList.add(timer.timeInMillis());
        }
        OptionalDouble average = MathUtils.avarage(timeList);
        logger.info("Lettura {} righe con ReverseFileReader {}ms", result.size(), average.getAsDouble());
        
        List<Long> timeList6 = new ArrayList<>();
        List<String> result6 = null;
        for(int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result6 = readLastLinesWithReverseLinesFileReaderCustom(linesToRead);
            timeList6.add(timer.timeInMillis());
        }
        OptionalDouble average6 = MathUtils.avarage(timeList6);
        logger.info("Lettura {} righe con ReverseFileReader custom {}ms", result6.size(), average6.getAsDouble());
        
        List<Long> timeList2 = new ArrayList<>();
        List<String> result2 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result2 = readLastLinesWithBufferedReader(linesToRead);
            timeList2.add(timer.timeInMillis());
        }
        OptionalDouble average2 = MathUtils.avarage(timeList2);
        logger.info("Lettura {} righe con BufferedReader {}ms", result2.size(), average2.getAsDouble());
        
        List<Long> timeList1 = new ArrayList<>();
        List<String> result1 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result1 = readLastLinesWithFileUtils(linesToRead);
            timeList1.add(timer.timeInMillis());
        }
        OptionalDouble average1 = MathUtils.avarage(timeList1);
        logger.info("Lettura {} righe con FileUtils {}ms", result1.size(), average1.getAsDouble());
        
        List<Long> timeList5 = new ArrayList<>();
        List<String> result5 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result5 = readLastLinesWithBufferedReaderAndLinkedList(linesToRead);
            timeList5.add(timer.timeInMillis());
        }
        OptionalDouble average5 = MathUtils.avarage(timeList5);
        logger.info("Lettura {} righe con BufferedReader e LinkedList {}ms", result5.size(), average5.getAsDouble());
        
        List<Long> timeList7 = new ArrayList<>();
        List<String> result7 = null;
        for (int i = 0; i < repeat; i++) {
            Timer timer = new Timer();
            result7 = readRawTextWithBuffer(linesToRead);
            timeList7.add(timer.timeInMillis());
        }
        OptionalDouble average7 = MathUtils.avarage(timeList7);
        logger.info("Lettura {} righe con RandomAccessFile e BufferedReader {}ms", result7.size(), average7.getAsDouble());
    }
    
    private List<String> readRawTextWithBuffer(int linesToRead) throws IOException {
    	int BUFFER_SIZE = 102400;
    	
        try(RandomAccessFile raf = new RandomAccessFile(testFile, "r");) {
            byte[] buffer = new byte[BUFFER_SIZE];
        	
            List<String> allLines = new ArrayList<>();
            
            long length = testFile.length();
            long pointer = length - BUFFER_SIZE;
            if(pointer < 0) {
            	pointer = 0;
            }
            raf.seek(pointer);
            
            String possibleTruncatedLine = null;
            while(raf.read(buffer) != -1 && allLines.size() < linesToRead) {
                String line = new String(buffer, StandardCharsets.UTF_8);
                String[] lines = line.split("\r\n|\r|\n");
                if(lines.length == 0) {
                	break;
                }
                if(lines.length > 1) {
                	if(possibleTruncatedLine != null) {
                		possibleTruncatedLine = lines[lines.length - 1].concat(possibleTruncatedLine);
                		allLines.add(0, possibleTruncatedLine);
                		allLines.addAll(0, Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length - 1)));
                		possibleTruncatedLine = lines[0];
                	} else {
                		allLines.addAll(0, Arrays.asList(Arrays.copyOfRange(lines, 1, lines.length)));
                		possibleTruncatedLine = lines[0];
                	}
                } else {
                	if(possibleTruncatedLine != null) {
                		possibleTruncatedLine = lines[0].concat(possibleTruncatedLine);
                	} else {
                		possibleTruncatedLine = lines[0];
                	}
                }
                
                if(pointer > 0) {
                	pointer = pointer - BUFFER_SIZE;
                	if(pointer < 0) {
                		pointer = 0;
                	}
                } else {
                	break;
                }
                raf.seek(pointer);
            }
            
            if(allLines.size() > linesToRead) {
            	allLines = allLines.subList(allLines.size() - linesToRead, allLines.size());
            } else if(allLines.size() < linesToRead && possibleTruncatedLine != null) {
            	allLines.add(possibleTruncatedLine);
            }
            
            if(linesToRead > allLines.size()) {
                linesToRead = allLines.size();
            }
            
            List<String> content = allLines.subList(allLines.size() - linesToRead, allLines.size());
            return content;
        }
    }
    
    private List<String> readLastLinesWithBufferedReader(int linesToRead) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(testFile);InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);) {
            
            List<String> allLines = new ArrayList<>();
            
            String line;
            while((line = br.readLine()) != null) {
                allLines.add(line);
            }
            
            if(linesToRead > allLines.size()) {
                linesToRead = allLines.size();
            }
            
            List<String> content = allLines.subList(allLines.size() - linesToRead, allLines.size());
            return content;
        }
    }
    
    private List<String> readLastLinesWithBufferedReaderAndLinkedList(int linesToRead) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(testFile);InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(isr);) {
            
            
            List<String> allLines = new LinkedList<>();
            
            String line;
            while((line = br.readLine()) != null) {
                allLines.add(line);
            }
            
            if(linesToRead > allLines.size()) {
                linesToRead = allLines.size();
            }
            
            List<String> content = allLines.subList(allLines.size() - linesToRead, allLines.size());
            return content;
        }
    }
    
    private List<String> readLastLinesWithReverseLinesFileReader(int linesToRead) throws IOException {
        try(ReversedLinesFileReader fileReader = new ReversedLinesFileReader(testFile, StandardCharsets.UTF_8)) {
            List<String> content = new ArrayList<>();
            String line;
            int i = 0;
            while((line = fileReader.readLine()) != null) {
                content.add(line);
                if(i >= linesToRead - 1) {
                    break;
                }
                i++;
            }
            return Lists.reverse(content);
        }
    }
    
    private List<String> readLastLinesWithReverseLinesFileReaderCustom(int linesToRead) throws IOException {
        try(ReversedLinesFileReaderLineSeparator fileReader = new ReversedLinesFileReaderLineSeparator(testFile, StandardCharsets.UTF_8)) {
            List<String> content = new ArrayList<>();
            String line;
            int i = 0;
            while((line = fileReader.readLine()) != null) {
                content.add(line);
                if(i >= linesToRead - 1) {
                    break;
                }
                i++;
            }
            return Lists.reverse(content);
        }
    }
    
    private List<String> readLastLinesWithFileUtils(int linesToRead) throws IOException {
        List<String> content = new ArrayList<>();
        List<String> allLines = FileUtils.readLines(testFile, StandardCharsets.UTF_8);
        
        if(linesToRead > allLines.size()) {
            linesToRead = allLines.size();
        }
        
        content = allLines.subList(allLines.size() - linesToRead, allLines.size());
        return content;
    }
    
}
