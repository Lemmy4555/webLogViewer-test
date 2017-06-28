package com.sc.l45.weblogviewer.api.responses;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileContentResponse {
	
	public List<String> readContent;
	public String rowsRead;
	public String size;
    public String encoding;
    public String currentPointer;
	
	protected FileContentResponse() {}
	
	public FileContentResponse(List<String> readContent, String rowsRead, String size, String encoding, String currentPointer) {
		this.readContent = readContent;
		this.rowsRead = rowsRead;
		this.size = size;
		this.encoding = encoding;
		this.currentPointer = currentPointer;
	}

}
