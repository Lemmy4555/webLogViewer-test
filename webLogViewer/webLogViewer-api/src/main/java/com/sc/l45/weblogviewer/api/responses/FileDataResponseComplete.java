package com.sc.l45.weblogviewer.api.responses;

public class FileDataResponseComplete extends FileDataResponse {
	public String rowsInFile;
	
	public FileDataResponseComplete(String name, boolean isFile, String rowsInFile) {
		super(name, isFile);
	}

	@Override
	public String toString() {
		return "FileDataResponseComplete [rowsInFile=" + rowsInFile + ", name=" + name + ", isFile=" + isFile + "]";
	}
}
