package com.sc.l45.weblogviewer.api.mgr.readers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.utils.ListUtils;

public class FileReaderFromLine extends FileReaderAbstract{
    static public FileContentResponse read(File file, int lineFromStartRead, boolean isTotRowsToGet) throws IOException {
     	long fileLength = file.length();
    	
        if(lineFromStartRead <= 0) {
            lineFromStartRead = 1;
        }
        
        Integer rowsInFile = null;
        if(isTotRowsToGet) {
            rowsInFile = ReaderUtils.countLinesInFile(file);
        }
        
        return createFileContentResponse(readFileWithBufferedReader(file, lineFromStartRead, fileLength), fileLength, rowsInFile);
    }
 

	private static ReadLinesResult readFileWithBufferedReader(File file, int lineFromStartRead, long fileLength) throws IOException {
    	int BUFFER_SIZE = FileConstants.MAX_READABLE_TEXT_SIZE;
        
    	ReadLinesResult result = new ReadLinesResult();
        
    	/* At the beginning so much character as buffer size, in a second moment i'll 
    	 * determine if last line is terminated so actually i don't need to read extra chars */
        byte[] buffer = new byte[BUFFER_SIZE];
    	
    	try(RandomAccessFile raf = new RandomAccessFile(file, "r");) {
    		//This is used to determine if i reached the line i want to read from
            int nLines = 0;
            
            List<String> linesRead = new ArrayList<>();
            String lastLineRead = null;
            while(true) {
            	//Continue to read until i didn't reached the line i want to start read from
            	
            	List<String> allLines = new ArrayList<>();
            	if(raf.read(buffer) != -1) {
            		allLines = ReaderUtils.convertBytesArrayToStringList(buffer);
                    if(allLines.size() == 0) {
                    	return result;
                    }
                    
                    //-1 because I have to check if last line read has been terminated
                    nLines += allLines.size() - 1;
                    
                    String nextLine = allLines.get(0);
                    if(lastLineRead != null) {
                    	/* If this is not the first iteration i've to check if the last line
                    	 * I read in the previous iteration is terminated */
                    	if(lastLineRead.endsWith("\n")) {
                    		//The line I read in the previous iteration have a LF, it's terminated for sure
                    		nLines++;
                    	} else if(lastLineRead.endsWith("\r") && nextLine.equals("\n")) {
                    		/*The line I read in hte previous iteration have a CR and the first line I read 
                    		 * in the current iteration it a LF so the two line are a single line terminating with CRLF*/
                    		ListUtils.setFirstLine(allLines, lastLineRead.concat(nextLine));
                    	}
                    }
                    
                    if(nLines >= lineFromStartRead) {
                    	//I've reached the line i want to read from
                    	
                    	//+1 because i never count the last line because i've to check if it's really finished
                    	int nLinesToReadAlreadyRead = nLines + 1 - lineFromStartRead;
                    	
                    	linesRead = allLines.subList(allLines.size() - nLinesToReadAlreadyRead - 1, allLines.size());
                    	break;
                    }
                    
                    lastLineRead = ListUtils.getLastLine(allLines);
                    
                    if(allLines.size() > 1) {
                    	//Free space in memory
                    	allLines = new ArrayList<>();
                    	allLines.add(lastLineRead);
                    }
                    
                    if(raf.getFilePointer() == fileLength) {
                    	//EOF reached and line where I want to start read from has not been reached
                    	return result;
                    }
            	} else {
            		return result;
            	}
            }
            
            //It's needed to know if the last line read is terminated
            int extraChars = ReaderConstants.EXTRA_CHARS;
            int lengthOfLinesAlreadyRead = ReaderUtils.getStringLengthFromList(linesRead);
                       
            if(raf.getFilePointer() + BUFFER_SIZE > fileLength) {
            	BUFFER_SIZE -= fileLength - raf.getFilePointer();
            	extraChars = 0;
            }
            
            /* Here i create a buffer to read content from file to match the BUFFER_SIZE, if i've jumped 10 lines,
             * I'll read the bytes needed to fill the ignored lines ad the begininng */
            buffer = new byte[BUFFER_SIZE - lengthOfLinesAlreadyRead + extraChars];
            if(raf.read(buffer) != -1) {
            	List<String> remainingLines = ReaderUtils.convertBytesArrayToStringList(buffer);
            	linesRead = ReaderUtils.concatList(linesRead, remainingLines);
            	result.pointer = raf.getFilePointer() - extraChars;
            	result.linesRead = linesRead;
            	
            	if(!ReaderUtils.isFirstLineTerminated(linesRead)) {
            		result.isFirstLineFull = false;
            	}
            	
            	if(!ReaderUtils.isLastLineTerminated(linesRead, extraChars)) {
            		String lastLine = ListUtils.getLastLine(linesRead);
            		ListUtils.setLastLine(linesRead, lastLine.substring(0, lastLine.length() - extraChars));
            		result.isLastLineFull = false;
            	} else {
            		//If last line is terminated i know that the last line in the list is composed only by extra chars
            		ListUtils.removeLastLine(linesRead);
            	}
            	
            	return result;
            } else {
            	result.pointer = raf.getFilePointer();
            	result.linesRead = linesRead;
            	return result;
            }
        }
    }
	
}
