package com.sc.l45.weblogviewer.test.bridge.responses;

import javax.ws.rs.core.EntityTag;

import com.sc.l45.weblogviewer.api.responses.FileContentResponse;

public class FileContentBridgeResponse<T extends FileContentResponse> extends BridgeResponseAbstract<T>{
	public FileContentBridgeResponse(T response, int status, EntityTag eTag) {
		super(response, status, eTag);
	}
}
