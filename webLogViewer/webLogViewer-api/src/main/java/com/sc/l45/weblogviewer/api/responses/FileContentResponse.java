package com.sc.l45.weblogviewer.api.responses;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileContentResponse {
	
	public List<String> readContent;
	public String rowsRead;
	public String size;
    public String encoding;
	
	protected FileContentResponse() {}
	
	public FileContentResponse(List<String> readContent, String rowsRead, String size, String encoding) {
		this.readContent = readContent;
		this.rowsRead = rowsRead;
		this.size = size;
		this.encoding = encoding;
	}
	
	@JsonIgnore
	public String getRowsAsString() {
	    StringBuilder sb = new StringBuilder();
	    Iterator<String> i = readContent.iterator();
	    while(i.hasNext()) {
	        sb.append(i.next());
	    }
	    return sb.toString();
	}

}
