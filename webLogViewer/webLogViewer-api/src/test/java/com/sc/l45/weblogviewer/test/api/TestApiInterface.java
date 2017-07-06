package com.sc.l45.weblogviewer.test.api;

interface TestApiInterface {

	public String getTailText(String filePath, String rowsfromEnd, String pointer, String isTotRowsToGet, String getFileLength);

	public String getTextFromPointer(String filePath, String pointer, String isTotRowsToGet);

	public String getTextFromLine(String filePath, String pointer, String isTotRowsToGet);

}
