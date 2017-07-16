package com.sc.l45.weblogviewer.test.api;

import javax.ws.rs.client.Invocation.Builder;

interface TestApiInterface {
	public String sendRequest(Builder request);

	public String getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, String getFileLength);

	public String getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet);

	public String getTextFromLine(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet);

	public String readFile(String filePath);

	public String getHomeDir();
}
