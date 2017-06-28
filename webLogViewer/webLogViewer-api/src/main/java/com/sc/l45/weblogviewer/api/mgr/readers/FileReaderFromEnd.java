package com.sc.l45.weblogviewer.api.mgr.readers;

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
            rowsInFile = ReaderUtils.countLinesInFile(file);
        }
        
        if(maxRowsToRead <= 0 || pointer <= 0) {
            return createFileContentResponse(new ReadLinesResult(), fileLength, rowsInFile);
        }
        return createFileContentResponse(readRawTextReverse(file, maxRowsToRead, pointer, fileLength), fileLength, rowsInFile);
    }
    
    private static ReadLinesResult readRawTextReverse(File file, int maxRowsToRead, long pointer, long fileLength) throws IOException {
    	int BUFFER_SIZE = FileConstants.MAX_READABLE_TEXT_SIZE;
        
        List<String> allLines = new ArrayList<>();
        
        if(pointer > fileLength) {
        	pointer = fileLength;
        }
        
        int extraChars = ReaderConstants.EXTRA_CHARS;
        
        long newPointer = pointer - BUFFER_SIZE;
        if(newPointer <= 0) {
        	BUFFER_SIZE = (int)( (long)(BUFFER_SIZE) + newPointer);
        	//In this case i'll not read extrachars because start of file has been reached.
        	extraChars = 0;
        	newPointer = 0;
        } else if(newPointer == 1) {
        	//TODO this case could be handled better to reduce api calls by one.
        	//If only one chars remains to read i can read only 1 extra char.
        	extraChars = 1;
        	newPointer = 0;
        } else {
        	newPointer -= extraChars;
        }
        
        byte[] buffer = new byte[BUFFER_SIZE];
    	
        try(RandomAccessFile raf = new RandomAccessFile(file, "r");) {
            raf.seek(newPointer);
            
            if(raf.read(buffer) != -1) {
            	
            	allLines = ReaderUtils.convertBytesArrayToStringList(buffer);
                if(allLines.size() == 0) {
                	return new ReadLinesResult();
                }
                
                /* After I've read a fixed buffer from a file, i've to return only the rows
                 * I need and ignore all extra rows I read. */
                if(allLines.size() == 0) {
                	return new ReadLinesResult();
                } else {
                	int linesReadOverLimit = allLines.size() - maxRowsToRead;
                	ReadLinesResult result = new ReadLinesResult();
                	result.pointer = newPointer;
                	if(linesReadOverLimit > 0) {
            			result.linesRead = allLines.subList(linesReadOverLimit, allLines.size());
            			return result;
                	} else {
                		//I've to remove the extra chars read to identify line termination
                		String firstLine = allLines.get(0);
                		if(firstLine.length() > extraChars) {
                			allLines.set(0, firstLine.substring(extraChars, firstLine.length()));
                			if(newPointer > 0) {
                				result.isFirstLineFull = false;
                			}
                		} else if(firstLine.length() == extraChars) {
                			allLines.remove(0);
                		} else {
                			allLines.remove(0);
                			firstLine = allLines.get(0);
                			allLines.set(0, firstLine.substring(1, firstLine.length()));
                			result.isFirstLineFull = false;
                		}
                		
                		result.linesRead = allLines;
                		return result;
                	}
                }
            }
            
            return new ReadLinesResult();
        }
    }
    
    private static FileContentResponse createFileContentResponse(ReadLinesResult readLinesResult, long fileLength, Integer rowsInFile) {
    	FileContentResponse response;
    	
    	int size = readLinesResult.linesRead.size();
    	if(!readLinesResult.isFirstLineFull) {
    		size--;
    	}
		
    	if(rowsInFile != null) {
    		response = new FileContentResponseComplete(
    				readLinesResult.linesRead, Integer.toString(size), 
    				Long.toString(fileLength), FileConstants.ENCODING.name(),
    				Integer.toString(rowsInFile),
    				Long.toString(readLinesResult.pointer)
    				);
    	} else {
    		response = new FileContentResponse(
    				readLinesResult.linesRead, Integer.toString(size), 
    				Long.toString(fileLength), FileConstants.ENCODING.name(),
    				Long.toString(readLinesResult.pointer)
    				);
    	}
    	return response;
    }

}
