package com.sc.l45.weblogviewer.test.api;

import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.mgr.readers.ReaderUtils;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.api.utils.ListUtils;

public class ApiBridgeMgr {
	private ApiBridge testApi;
	private static final String isTotRowsToGetFirstCall = "true";
	private static final String isTotRowsToGetNextCalls = "false";
	
	public ApiBridgeMgr(ApiBridge testApi) {
		if(testApi == null) {
			throw new NullPointerException("testApi has to be created in order to user Mgr");
		}
		this.testApi = testApi;
	}

	public FileContentResponseComplete getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToRead) {
		FileContentResponseComplete response = testApi.getTailText(filePath, pointer, maxRowsToRead, isTotRowsToRead, FileContentResponseComplete.class);
		
		Integer rowsfromEndInt;
		if(maxRowsToRead == null) {
			rowsfromEndInt = 0;
		} else {
			rowsfromEndInt = Integer.parseInt(maxRowsToRead);
		}
		
		Integer rowsRead = Integer.parseInt(response.rowsRead);
		Integer newPointer = Integer.parseInt(response.currentPointer);
		
		if(maxRowsToRead != null && rowsRead.equals(rowsfromEndInt)) {
			return response;
		}
		
		if(rowsRead > 0) {
			while(true) {
				Integer totRowsRead = Integer.parseInt(response.rowsRead);
				FileContentResponse responsePart;
				if(rowsfromEndInt != null) {
					responsePart = testApi.getTailText(filePath, newPointer.toString(), String.valueOf(rowsfromEndInt - totRowsRead), isTotRowsToGetNextCalls, FileContentResponse.class);
				} else {
					responsePart = testApi.getTailText(filePath, newPointer.toString(), null, isTotRowsToGetNextCalls, FileContentResponse.class);
				}
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				newPointer = Integer.parseInt(responsePart.currentPointer);;
				concatFileReponseAtBeginning(response, responsePart);
				if(rowsfromEndInt != null && rowsfromEndInt.equals(Integer.parseInt(response.rowsRead))) {
					break;
				} else if(newPointer.equals(0)) {
					break;
				}
			}
		}
		
		return response;
	}
	
	public FileContentResponseComplete getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToRead) {
		FileContentResponseComplete response = testApi.getTextFromLine(filePath, fromLine, maxRowsToRead, isTotRowsToRead, FileContentResponseComplete.class);
		
		Integer maxRowsToReadInt;
		if(maxRowsToRead == null) {
			maxRowsToReadInt = null;
		} else {
			maxRowsToReadInt = Integer.parseInt(maxRowsToRead);
		}
		
		Long fileLength = Long.parseLong(response.size);
		Long newPointer = Long.parseLong(response.currentPointer);
		Integer rowsRead = Integer.parseInt(response.rowsRead);
		
		if(rowsRead > 0 && !newPointer.equals(fileLength)) {
			//The first line read is extra
			rowsRead--;
			response.rowsRead = rowsRead.toString();
		}
		
		if((maxRowsToRead != null && maxRowsToReadInt.equals(rowsRead)) || newPointer.equals(fileLength)) {
			return response;
		}

		if(rowsRead > 0) {
			while(true) {
				Integer totRowsRead = Integer.parseInt(response.rowsRead);
				FileContentResponse responsePart;
				if(maxRowsToReadInt != null) {
					responsePart = testApi.getTextFromPointer(filePath, newPointer.toString(), String.valueOf(maxRowsToReadInt - totRowsRead), isTotRowsToGetNextCalls, FileContentResponse.class);
				} else {
					responsePart = testApi.getTextFromPointer(filePath, newPointer.toString(), null, isTotRowsToGetNextCalls, FileContentResponse.class);
				}
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				newPointer = Long.parseLong(responsePart.currentPointer);
				concatFileReponseAtTheEnd(response, responsePart);
				if(maxRowsToReadInt != null && maxRowsToReadInt.equals(Integer.parseInt(response.rowsRead))) {
					break;
				} else if(newPointer.equals(fileLength)) {
					break;
				}
			}
		}
		
		return response;
	}

	public FileContentResponse getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToRead) {
		FileContentResponseComplete response = testApi.getTextFromPointer(filePath, pointer, maxRowsToRead, isTotRowsToRead, FileContentResponseComplete.class);
		Integer maxRowsToReadInt;
		if(maxRowsToRead == null) {
			maxRowsToReadInt = null;
		} else {
			maxRowsToReadInt = Integer.parseInt(maxRowsToRead);
		}
		
		Integer fileLength = Integer.parseInt(response.size);
		Integer newPointer = Integer.parseInt(response.currentPointer);
		Integer rowsRead = Integer.parseInt(response.rowsRead);
		
		if((maxRowsToRead != null && maxRowsToReadInt.equals(rowsRead)) || newPointer.equals(fileLength)) {
			return response;
		}
		
		Integer totRowsRead = rowsRead;
		
		if(totRowsRead > 0) {
			while(true) {
				String newMaxRowsToReadInt;
				if(maxRowsToReadInt == null) {
					newMaxRowsToReadInt = null;
				} else {
					newMaxRowsToReadInt = String.valueOf(maxRowsToReadInt - totRowsRead);
				}
				
				FileContentResponse responsePart = testApi.getTextFromPointer(filePath, newPointer.toString(), newMaxRowsToReadInt, isTotRowsToGetNextCalls, FileContentResponse.class);
				rowsRead = Integer.parseInt(responsePart.rowsRead);
				newPointer = Integer.parseInt(responsePart.currentPointer);
				concatFileReponseAtTheEnd(response, responsePart);
				totRowsRead += rowsRead;
				if(maxRowsToReadInt != null && totRowsRead.equals(maxRowsToReadInt)) {
					break;
				} else if(newPointer.equals(fileLength)) {
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
