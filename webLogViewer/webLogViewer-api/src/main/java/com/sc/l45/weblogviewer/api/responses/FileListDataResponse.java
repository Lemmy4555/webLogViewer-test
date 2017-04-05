package com.sc.l45.weblogviewer.api.responses;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileListDataResponse {
	public Collection<FileDataResponse> fileList;
	
	protected FileListDataResponse() {}

	public FileListDataResponse(Collection<FileDataResponse> fileList) {
		this.fileList = fileList;
	}
}
