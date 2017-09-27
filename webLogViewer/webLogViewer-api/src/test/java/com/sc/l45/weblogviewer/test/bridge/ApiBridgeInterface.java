package com.sc.l45.weblogviewer.test.bridge;

import javax.ws.rs.client.Invocation.Builder;

interface ApiBridgeInterface {
	public String sendRequest(Builder request);

	public String getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, String getFileLength);

	public String getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet);

	public String getTextFromLine(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet);

	public String readFile(String filePath);

	public String getHomeDir();
	
	public String getFileData(String filePath);
}