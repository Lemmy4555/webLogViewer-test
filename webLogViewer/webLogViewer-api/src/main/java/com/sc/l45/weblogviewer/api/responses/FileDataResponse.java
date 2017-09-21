package com.sc.l45.weblogviewer.api.responses;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDataResponse {
	public String name;
	public boolean isFile;
	
	public FileDataResponse() {};
	
	public FileDataResponse(String name, boolean isFile) {
		this.name = name;
		this.isFile = isFile;
	}

	@Override
	public String toString() {
		return "FileDataResponse [name=" + name + ", isFile=" + isFile + "]";
	}
}
