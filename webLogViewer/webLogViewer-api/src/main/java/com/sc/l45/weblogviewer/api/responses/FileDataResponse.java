package com.sc.l45.weblogviewer.api.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileDataResponse {
	public String name;
	public boolean isFile;
	
	public FileDataResponse() {};
	
	public FileDataResponse(String name, boolean isFile) {
		this.name = name;
		this.isFile = isFile;
	}
}
