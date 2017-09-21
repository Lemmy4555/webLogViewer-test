package com.sc.l45.weblogviewer.test.bridge.responses;

import javax.ws.rs.core.EntityTag;

class BridgeResponseAbstract<T> {
	private EntityTag eTag;
	private T response;
	private int status;
	
	BridgeResponseAbstract(T response, int status, EntityTag eTag) {
		this.eTag = eTag;
		this.response = response;
		this.status = status;
	}
	
	public EntityTag getETag() {
		return eTag;
	}
	
	public T getResponse() {
		return response;
	}
	
	public int getStatus() {
		return status;
	}
}
