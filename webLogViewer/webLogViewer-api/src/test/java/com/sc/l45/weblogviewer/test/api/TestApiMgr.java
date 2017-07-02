package com.sc.l45.weblogviewer.test.api;

import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.mgr.readers.ReaderUtils;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;

public class TestApiMgr {
	private TestApi testApi;
	
	public TestApiMgr(TestApi testApi) {
		if(testApi == null) {
			throw new NullPointerException("testApi has to be created in order to user Mgr");
		}
		this.testApi = testApi;
	}

	public FileContentResponseComplete getTailText(String filePath, String rowsfromEnd, String pointer, String isTotRowsToGet) {
		FileContentResponseComplete response = testApi.getTailText(filePath, rowsfromEnd, pointer, isTotRowsToGet, FileContentResponseComplete.class);
		
		Integer rowsInFile = Integer.parseInt(response.rowsInFile);
		Integer rowsRead = Integer.parseInt(response.rowsRead);
		Integer rowsToRead = Integer.parseInt(rowsfromEnd);
		Integer contentReadLength = ReaderUtils.getStringLengthFromList(response.readContent);
		Integer newPointer = Integer.parseInt(pointer) - contentReadLength;
		
		if(rowsToRead > rowsInFile) {
			rowsToRead = rowsInFile;
		}
		rowsToRead -= rowsRead;
		if(rowsToRead > 0) {
			while(true) {
				if(rowsToRead < 1000) {
					System.out.print(0);
				}
				FileContentResponse responsePart = testApi.getTailText(filePath, rowsToRead.toString(), newPointer.toString(), "false", FileContentResponse.class);
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				contentReadLength = ReaderUtils.getStringLengthFromList(responsePart.readContent);
				newPointer -= contentReadLength;
				concatFileReponseAtBeginning(response, responsePart);
				rowsToRead -= rowsRead;
				if(rowsToRead <= 0) {
					break;
				}
			}
		}
		
		return response;
	}
	
	private FileContentResponse concatFileReponse(FileContentResponse response, FileContentResponse toConcat) {
		response.readContent.addAll(toConcat.readContent);
		Integer totRowsRead = Integer.parseInt(response.rowsRead) + Integer.parseInt(toConcat.rowsRead);
		response.rowsRead = totRowsRead.toString();
		return response;
	}
	
	private FileContentResponse concatFileReponseAtBeginning(FileContentResponse response, FileContentResponse toConcat) {
		
		List<String> content = new ArrayList<>();
		
		if(response.readContent.size() > 0) {
			String lastLineToConcat = toConcat.readContent.get(toConcat.readContent.size() - 1);
			String firstLineRes = response.readContent.get(0);
			if(lastLineToConcat.endsWith("\r")) {
				if(firstLineRes.equals("\n")) {
					toConcat.readContent.remove(toConcat.readContent.size() - 1);
					lastLineToConcat = lastLineToConcat.concat(firstLineRes);
					response.readContent.set(0, lastLineToConcat);
				}
			} else if(!lastLineToConcat.endsWith("\n") && !lastLineToConcat.endsWith("\r\n")) {
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

	public FileContentResponseComplete getTextFromLine(String filePath, String fromLine, String isTotRowsToGet) {
		FileContentResponseComplete response = testApi.getTextFromLine(filePath, fromLine, isTotRowsToGet, FileContentResponseComplete.class);
		
		Integer rowsInFile = Integer.parseInt(response.rowsInFile);
		Integer rowsRead = Integer.parseInt(response.rowsRead);
		Integer fromLineInt = Integer.parseInt(fromLine);
		Integer contentReadLength = ReaderUtils.getStringLengthFromList(response.readContent);
		Integer newPointer = contentReadLength;
		Integer rowsToRead = rowsInFile - rowsRead;
		
		if(fromLineInt > rowsInFile) {
			return response;
		}
		rowsToRead -= rowsRead;
		if(rowsToRead > 0) {
			while(true) {
				FileContentResponse responsePart = testApi.getTailText(filePath, rowsToRead.toString(), "false", newPointer.toString(), FileContentResponse.class);
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				contentReadLength = ReaderUtils.getStringLengthFromList(response.readContent);
				newPointer -= contentReadLength;
				concatFileReponseAtBeginning(response, responsePart);
				rowsToRead -= rowsRead;
				if(rowsToRead <= 0) {
					break;
				}
			}
		}
		
		return response;
	}
	
}
