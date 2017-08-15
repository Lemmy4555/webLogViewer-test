package com.sc.l45.weblogviewer.api.mgr.readers;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;

public abstract class FileReaderAbstract {
	
	static FileContentResponse createFileContentResponse(ReadLinesResult readLinesResult, long fileLength, Integer rowsInFile) {
    	FileContentResponse response;
    	
    	int size = readLinesResult.linesRead.size();
    	
    	if(!readLinesResult.isFirstLineFull) {
    		size--;
    	}
    	
    	if(!readLinesResult.isLastLineFull) {
    		size--;
    	}
		
    	if(rowsInFile != null) {
    		response = new FileContentResponseComplete(
    				readLinesResult.linesRead, Integer.toString(size), 
    				Long.toString(fileLength), FileConstants.ENCODING.name(),
    				Long.toString(readLinesResult.pointer),
    				Integer.toString(rowsInFile)
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
	
	static void removeExceedingLines(ReadLinesResult result, Integer maxLinesToRead) {
		if(maxLinesToRead != null) {
			if(result.linesRead.size() < maxLinesToRead) {
				maxLinesToRead = result.linesRead.size();
			}
			result.linesRead = result.linesRead.subList(0, maxLinesToRead);
			result.isLastLineFull = true;
		}
	}
	
	static void removeExceedingLinesReverse(ReadLinesResult result, Integer maxLinesToRead) {
		if(maxLinesToRead != null) {
			if(result.linesRead.size() < maxLinesToRead) {
				return;
			}
			result.linesRead = result.linesRead.subList(result.linesRead.size() - maxLinesToRead, result.linesRead.size());
			result.isFirstLineFull = true;
		}
	}
}
