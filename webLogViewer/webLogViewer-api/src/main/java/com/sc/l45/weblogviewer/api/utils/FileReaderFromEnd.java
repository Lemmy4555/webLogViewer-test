package com.sc.l45.weblogviewer.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;

/**
 * Classe creata per via della necessita di dover utilizzare due classi diverse per leggere un file in input a seconda
 * dei dati in input per migliorare le performance.
 */
public class FileReaderFromEnd {
   
    public static FileContentResponse read(File file, int maxRowsToRead, long pointer, boolean isTotRowsToGet) throws IOException {
    	long fileLength = file.length();
    	Integer rowsInFile = null;
        if(isTotRowsToGet) {
            rowsInFile = countLinesInFile(file);
        }
        
        if(maxRowsToRead <= 0 || pointer <= 0) {
            return createFileContentResponse(new ArrayList<>(), fileLength, rowsInFile);
        }
        return createFileContentResponse(readRawTextReverse(file, maxRowsToRead, pointer, fileLength), fileLength, rowsInFile);
    }
    
    private static int countLinesInFile(File file) throws IOException {
    	 try(FileInputStream inputStream = new FileInputStream(file);
    			 InputStreamReader isr = new InputStreamReader(inputStream, FileConstants.ENCODING);
                 BufferedReader br = new BufferedReader(isr);) {
             
             int linesCount = 0;
             
             while(br.readLine() != null) {
                 linesCount++;
             }
             
             return linesCount;
         }
    }
    
    private static List<String> readRawTextReverse(File file, int maxRowsToRead, long pointer, long fileLength) throws IOException {
    	int BUFFER_SIZE = FileConstants.MAX_READABLE_TEXT_SIZE;
    	Pattern pattern = Pattern.compile("\r\n|\r|\n");
        Matcher matcher = null;
    	
        try(RandomAccessFile raf = new RandomAccessFile(file, "r");) {
            byte[] buffer = new byte[BUFFER_SIZE];
        	
            List<String> allLines = new ArrayList<>();
            
            if(pointer > fileLength) {
            	pointer = fileLength;
            }
            
            pointer = fileLength - BUFFER_SIZE;
            raf.seek(pointer);
            
            if(raf.read(buffer) != -1) {
            	
            	String line = new String(buffer, StandardCharsets.UTF_8);
                matcher = pattern.matcher(line);
                int lastIndex = 0;
                while(matcher.find()) {
                	allLines.add(line.substring(lastIndex, matcher.end()));
                	lastIndex = matcher.end();
                }
                String lastLine = line.substring(lastIndex, line.length());
                if(!lastLine.isEmpty()) {
                	allLines.add(lastLine);
                }
                if(allLines.size() == 0) {
                	return allLines;
                }
                
                if(allLines.size() == 0) {
                	return allLines;
                } else {
                	int linesReadOverLimit = allLines.size() - maxRowsToRead;
                	if(linesReadOverLimit > 0) {
                		return allLines.subList(linesReadOverLimit, allLines.size());
                	} else {
                		return allLines;
                	}
                }
            }
            
            return allLines;
        }
    }
    
    private static FileContentResponse createFileContentResponse(List<String> content, long fileLength, Integer rowsInFile) {
    	FileContentResponse response;
    	if(rowsInFile != null) {
    		response = new FileContentResponseComplete(
    				content, Integer.toString(content.size()), 
    				Long.toString(fileLength), FileConstants.ENCODING.name(),
    				Integer.toString(rowsInFile)
    				);
    	} else {
    		response = new FileContentResponse(
    				content, Integer.toString(content.size()), 
    				Long.toString(fileLength), FileConstants.ENCODING.name()
    				);
    	}
    	return response;
    }

}
