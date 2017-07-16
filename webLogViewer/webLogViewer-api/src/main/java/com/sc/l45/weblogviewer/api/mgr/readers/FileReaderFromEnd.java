package com.sc.l45.weblogviewer.api.mgr.readers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.utils.ListUtils;

/**
 * Classe creata per via della necessita di dover utilizzare due classi diverse per leggere un file in input a seconda
 * dei dati in input per migliorare le performance.
 */
public class FileReaderFromEnd extends FileReaderAbstract{
   
    static public FileContentResponse read(File file, int maxRowsToRead, long pointer, boolean isTotRowsToGet) throws IOException {
	    long fileLength = file.length();
	    
        if(pointer < 0) {
        	pointer = 0;
        }
        
        if(pointer > fileLength) {
        	pointer = fileLength;
        }
        
    	Integer rowsInFile = null;
        if(isTotRowsToGet) {
            rowsInFile = ReaderUtils.countLinesInFile(file);
        }
        
        if(maxRowsToRead <= 0 || pointer <= 0) {
            return createFileContentResponse(new ReadLinesResult(), fileLength, rowsInFile);
        }
        
        ReadLinesResult result = readFileWithBufferedReaderReverse(file, maxRowsToRead, pointer, fileLength);
        removeExceedingLinesReverse(result, maxRowsToRead);
        
        return createFileContentResponse(result, fileLength, rowsInFile);
    }
    
    private static ReadLinesResult readFileWithBufferedReaderReverse(File file, Integer maxRowsToRead, long pointer, long fileLength) throws IOException {
    	ReadLinesResult result = new ReadLinesResult();
    	int BUFFER_SIZE = FileConstants.MAX_READABLE_TEXT_SIZE;
        
        List<String> allLines = new ArrayList<>();
        
        int extraChars = ReaderConstants.EXTRA_CHARS;
        
        if(pointer == 0) {
        	return result;
        }
        
        
        if(pointer - BUFFER_SIZE <= 0) {
        	BUFFER_SIZE += pointer - BUFFER_SIZE;
        	extraChars = 0;
        }
        
        long newPointer = pointer - BUFFER_SIZE;
        
        byte[] buffer = new byte[BUFFER_SIZE + extraChars];
    	
        result.pointer = newPointer;
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
            		if(!ReaderUtils.isLastLineTerminatedReverse(allLines)) {
            			result.isLastLineFull = false;
            		}
            		
            		if(extraChars == 0) {
            			result.isFirstLineFull = true;
            		} else if (!ReaderUtils.isFirstLineTerminatedReverse(allLines, extraChars)) {
            			String firstLine = ListUtils.getFirstLine(allLines);
                		ListUtils.setFirstLine(allLines, firstLine.substring(0 + extraChars, firstLine.length()));
                		result.isFirstLineFull = false;
                	} else {
                		//If last line is terminated i know that the last line in the list is composed only by extra chars
                		ListUtils.removeFirstLine(allLines);
                	}
            		
            		result.linesRead = allLines;
            		return result;
                }
            }
            
            return new ReadLinesResult();
        }
    }

}
