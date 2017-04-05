package com.sc.l45.weblogviewer.api.responses;

public class DefaultDirResponse extends FileListDataResponse{
	public String path;
	
	@SuppressWarnings("unused")
    private DefaultDirResponse() {
	    super();
	};
	
	public DefaultDirResponse(String path, FileListDataResponse fileListResponse) {
		super(fileListResponse.fileList);
		this.path = path;
	}
}
