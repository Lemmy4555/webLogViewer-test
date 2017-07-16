package com.sc.l45.weblogviewer.test.api;

import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.mgr.readers.ReaderUtils;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.api.utils.ListUtils;

public class TestApiMgr {
	private TestApi testApi;
	
	public TestApiMgr(TestApi testApi) {
		if(testApi == null) {
			throw new NullPointerException("testApi has to be created in order to user Mgr");
		}
		this.testApi = testApi;
	}

	public FileContentResponseComplete getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet) {
		FileContentResponseComplete response = testApi.getTailText(filePath, pointer, maxRowsToRead, isTotRowsToGet, FileContentResponseComplete.class);
		
		Integer rowsfromEndInt;
		if(maxRowsToRead == null) {
			rowsfromEndInt = null;
		} else {
			rowsfromEndInt = Integer.parseInt(maxRowsToRead);
		}
		
		Integer rowsInFile = Integer.parseInt(response.rowsInFile);
		
		if(rowsfromEndInt > rowsInFile) {
			rowsfromEndInt = rowsInFile;
		}

		Integer rowsRead = Integer.parseInt(response.rowsRead);
		Integer rowsToRead = rowsfromEndInt - rowsRead;
		Integer newPointer = Integer.parseInt(response.currentPointer);
		

		if(rowsToRead > 0) {
			while(true) {
				FileContentResponse responsePart = testApi.getTailText(filePath, newPointer.toString(), rowsToRead.toString(), "false", FileContentResponse.class);
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				newPointer = Integer.parseInt(responsePart.currentPointer);;
				concatFileReponseAtBeginning(response, responsePart);
				rowsToRead -= rowsRead;
				if(rowsToRead <= 0) {
					break;
				}
			}
		}
		
		return response;
	}
	
	public FileContentResponseComplete getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToGet) {
		FileContentResponseComplete response = testApi.getTextFromLine(filePath, fromLine, maxRowsToRead, isTotRowsToGet, FileContentResponseComplete.class);
		
		Integer maxRowsToReadInt;
		if(maxRowsToRead == null) {
			maxRowsToReadInt = null;
		} else {
			maxRowsToReadInt = Integer.parseInt(maxRowsToRead);
		}
		
		Integer rowsInFile = Integer.parseInt(response.rowsInFile);
		Integer rowsRead = Integer.parseInt(response.rowsRead);
		Integer fromLineInt = Integer.parseInt(fromLine);
		
		if(rowsRead > 0 && !rowsInFile.equals(fromLineInt)) {
			//The first line read is extra
			rowsRead--;
			response.rowsRead = rowsRead.toString();
		}
		
		Integer newPointer = Integer.parseInt(response.currentPointer);
		
		Integer rowsToRead;
		if(maxRowsToReadInt != null && fromLineInt + maxRowsToReadInt < rowsInFile) {
			rowsToRead = maxRowsToReadInt - rowsRead;
		} else {
			rowsToRead = rowsInFile - rowsRead - fromLineInt;
		}
		 
		
		
		if(fromLineInt > rowsInFile) {
			return response;
		}

		if(rowsToRead > 0) {
			while(true) {
				FileContentResponse responsePart = testApi.getTextFromPointer(filePath, newPointer.toString(), String.valueOf(rowsToRead), "false", FileContentResponse.class);
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				newPointer = Integer.parseInt(responsePart.currentPointer);
				concatFileReponseAtTheEnd(response, responsePart);
				rowsToRead -= rowsRead;
				if(rowsToRead <= 0) {
					break;
				}
			}
		}
		
		return response;
	}

	private FileContentResponse concatFileReponseAtTheEnd(FileContentResponse response, FileContentResponse toConcat) {
		List<String> content = ReaderUtils.concatList(response.readContent, toConcat.readContent);
		response.readContent = content;
		Integer totRowsRead = Integer.parseInt(response.rowsRead) + Integer.parseInt(toConcat.rowsRead);
		response.rowsRead = totRowsRead.toString();
		response.currentPointer = toConcat.currentPointer;
		return response;
	}

	private FileContentResponse concatFileReponseAtBeginning(FileContentResponse response, FileContentResponse toConcat) {
		
		List<String> content = new ArrayList<>();
		
		if(response.readContent.size() > 0) {
			String lastLineToConcat = ListUtils.getLastLine(toConcat.readContent);
			String firstLineRes = ListUtils.getFirstLine(response.readContent);
			if(lastLineToConcat.endsWith("\r")) {
				if(firstLineRes.equals("\n")) {
					toConcat.readContent.remove(toConcat.readContent.size() - 1);
					lastLineToConcat = lastLineToConcat.concat(firstLineRes);
					response.readContent.set(0, lastLineToConcat);
				}
			} else if(!lastLineToConcat.endsWith("\n")) {
				toConcat.readContent.remove(toConcat.readContent.size() - 1);
				lastLineToConcat = lastLineToConcat.concat(firstLineRes);
				response.readContent.set(0, lastLineToConcat);
			}
		}
		
		content.addAll(toConcat.readContent);
		content.addAll(response.readContent);
		response.readContent = content;
		Integer totRowsRead = Integer.parseInt(response.rowsRead) + Integer.parseInt(toConcat.rowsRead);
		response.rowsRead = totRowsRead.toString();
		response.currentPointer = toConcat.currentPointer;
		return response;
	}
	
}
