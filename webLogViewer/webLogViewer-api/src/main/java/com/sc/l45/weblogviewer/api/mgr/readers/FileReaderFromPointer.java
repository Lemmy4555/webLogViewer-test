package com.sc.l45.weblogviewer.api.mgr.readers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.utils.ListUtils;

public class FileReaderFromPointer extends FileReaderAbstract{
	 static public FileContentResponse read(File file, long pointer, Integer maxLinesToRead, boolean isTotRowsToGet) throws IOException {
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
        
        ReadLinesResult result = readFileWithBufferedReader(file, pointer, fileLength);
        removeExceedingLines(result, maxLinesToRead);
        
        return createFileContentResponse(result, fileLength, rowsInFile);
    }
	
	private static ReadLinesResult readFileWithBufferedReader(File file, long pointer, long fileLength) throws IOException, FileNotFoundException {
		ReadLinesResult result = new ReadLinesResult();
		int BUFFER_SIZE = FileConstants.MAX_READABLE_TEXT_SIZE;
        
        List<String> allLines = new ArrayList<>();
        
        int extraChars = ReaderConstants.EXTRA_CHARS;
        
        if(pointer == fileLength) {
        	return result;
        }
        
        if(pointer + BUFFER_SIZE > fileLength) {
        	BUFFER_SIZE = (int) (fileLength - pointer);
        	extraChars = 0;
        }
        
        byte[] buffer = new byte[BUFFER_SIZE + extraChars];
    	
        try(RandomAccessFile raf = new RandomAccessFile(file, "r");) {
            raf.seek(pointer);
            
            if(raf.read(buffer) != -1) {
            	result.pointer = raf.getFilePointer();
            	
            	allLines = ReaderUtils.convertBytesArrayToStringList(buffer);
                if(allLines.size() == 0) {
                	return new ReadLinesResult();
                }
                
                /* After I've read a fixed buffer from a file, i've to return only the rows
                 * I need and ignore all extra rows I read. */
                if(allLines.size() == 0) {
                	return new ReadLinesResult();
                } else {
                	long newPointer = raf.getFilePointer() - extraChars;
                	result.pointer = newPointer;
                	
            		if(!ReaderUtils.isFirstLineTerminated(allLines)) {
            			result.isFirstLineFull = false;
            		}
            		
            		if(extraChars == 0) {
            			result.isLastLineFull = true;
            		} else if (!ReaderUtils.isLastLineTerminated(allLines, extraChars)) {
                		String lastLine = ListUtils.getLastLine(allLines);
                		ListUtils.setLastLine(allLines, lastLine.substring(0, lastLine.length() - extraChars));
                		result.isLastLineFull = false;
                	} else {
                		//If last line is terminated i know that the last line in the list is composed only by extra chars
                		ListUtils.removeLastLine(allLines);
                	}
            		
            		result.linesRead = allLines;
            		return result;
                }
            }
            
            return new ReadLinesResult();
        }
    }
}
