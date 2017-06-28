package com.sc.l45.weblogviewer.api.mgr.readers;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;

public class FileReaderFromLine {
    public static FileContentResponse read(File file, int lineFromStartRead, boolean isTotRowsToGet) throws IOException {
        if(lineFromStartRead < 0) {
            lineFromStartRead = 0;
        }
        
        Integer rowsInFile = null;
        if(isTotRowsToGet) {
            rowsInFile = ReaderUtils.countLinesInFile(file);
        }
        
        return createFileContentResponse(readFileWithBufferedReader(file, lineFromStartRead), file.length(), rowsInFile);
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

	private static ReadLinesResult readFileWithBufferedReader(File file, int lineFromStartRead) throws IOException {
    	int BUFFER_SIZE = FileConstants.MAX_READABLE_TEXT_SIZE;
        
    	ReadLinesResult result = new ReadLinesResult();
        
        byte[] buffer = new byte[BUFFER_SIZE];
    	
    	try(RandomAccessFile raf = new RandomAccessFile(file, "r");) {  		
            int nLines = 0;
            
            List<String> linesRead = new ArrayList<>();
            String lastLineRead = null;
            while(true) {
            	List<String> allLines = new ArrayList<>();
            	if(raf.read(buffer) != -1) {
            		allLines = ReaderUtils.convertBytesArrayToStringList(buffer);
                    if(allLines.size() == 0) {
                    	return new ReadLinesResult();
                    }
                    
                    //-1 because I have to check if last line read has been terminated
                    nLines += allLines.size() - 1;
                    
                    String nextLine = allLines.get(0);
                    if(lastLineRead != null) {
                    	if(lastLineRead.endsWith("\r\n") || lastLineRead.endsWith("\n")) {
                    		nLines++;
                    	} else if(lastLineRead.endsWith("\r") && nextLine.equals("\n")) {
                    		nLines++;
                    		allLines.set(0, lastLineRead.concat(nextLine));
                    	}
                    }
                    
                    if(nLines >= lineFromStartRead) {
                    	int nLinesToReadAlreadyRead = nLines - lineFromStartRead;
                    	linesRead = allLines.subList(allLines.size() - nLinesToReadAlreadyRead, allLines.size());
                    	break;
                    }
                    
                    lastLineRead = allLines.get(allLines.size() - 1);
                    
                    //Free space in memory
                    if(allLines.size() > 1) {
                    	allLines = new ArrayList<>();
                    	allLines.add(lastLineRead);
                    }
            	} else {
            		return result;
            	}
            }
            
            //It's needed to know if the last line read is terminated
            int extraChars = 1;
            int lengthOfLinesAlreadyRead = ReaderUtils.getStringLengthFromList(linesRead);
            buffer = new byte[BUFFER_SIZE - lengthOfLinesAlreadyRead + extraChars];
            if(raf.read(buffer) != -1) {
            	List<String> remainingLines = ReaderUtils.convertBytesArrayToStringList(buffer);
            	linesRead.addAll(remainingLines);
            	result.pointer = raf.getFilePointer();
            	result.linesRead = linesRead;
            	String lastLine = remainingLines.get(remainingLines.size() - 1);
            	
            	if(lastLine.length() >= 2) {
            		if(lastLine.charAt(lastLine.length() - 2) == '\r' &&
            				lastLine.charAt(lastLine.length() - 1) == '\n') {
            			result.isFirstLineFull = false;
            		}
            		linesRead.set(linesRead.size() - 1, lastLine.substring(0, lastLine.length() - 2));
            	} else {
            		linesRead.remove(linesRead.size() - 1);
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
