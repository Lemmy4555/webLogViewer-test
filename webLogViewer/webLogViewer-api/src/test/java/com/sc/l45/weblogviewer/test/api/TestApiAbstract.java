package com.sc.l45.weblogviewer.test.api;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.EntityTag;

abstract class TestApiAbstract implements TestApiInterface {
	@Override
	public String sendRequest(Builder request) {
        return sendRequest(request, null, null);
    }
	
    public <T> T sendRequest(Builder request, Class<T> responseType) {
        return sendRequest(request, responseType, null);
    }
    
    public <T> T sendRequest(Builder request, EntityTag eTag) {
        return sendRequest(request, null, eTag);
    }
    
    public <T> T sendRequest(Builder request, Class<T> responseType, EntityTag eTag) {
        return TestApiHelper.createResponse(responseType, TestApiHelper.setETag(request, eTag).get());
    }
	
	@Override
	public String getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, String getFileLength) {
	    return getTailText(filePath, pointer, maxRowsToRead, isTotRowsToGet, null, null);
	}
	
	public String getTailText(String filePath, String isTotRowsToGet, String maxRowsToRead, String pointer, EntityTag eTag) {
        return getTailText(filePath, pointer, maxRowsToRead, isTotRowsToGet, null, eTag);
    }
	
	public <T> T getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType) {
        return getTailText(filePath, pointer, maxRowsToRead, isTotRowsToGet, responseType, null);
    }
	
	public abstract <T> T getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType, EntityTag eTag);
	
	@Override
	public String getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet) {
        return getTextFromPointer(filePath, pointer, maxRowsToRead, isTotRowsToGet, null, null);
    }
    
    public String getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, EntityTag eTag) {
        return getTextFromPointer(filePath, pointer, maxRowsToRead, isTotRowsToGet, null, eTag);
    }
    
    public <T> T getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType) {
        return getTextFromPointer(filePath, pointer, maxRowsToRead, isTotRowsToGet, responseType, null);
    }
    
    public abstract <T> T getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType, EntityTag eTag);
    
    @Override
    public String getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToGet) {
        return getTextFromLine(filePath, fromLine, maxRowsToRead, isTotRowsToGet, null, null);
    }
    
    public String getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToGet, EntityTag eTag) {
        return getTextFromLine(filePath, fromLine, maxRowsToRead, isTotRowsToGet, null, eTag);
    }
    
    public <T> T getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType) {
        return getTextFromLine(filePath, fromLine, maxRowsToRead, isTotRowsToGet, responseType, null);
    }
    
    public abstract <T> T getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType, EntityTag eTag);
        
    @Override
    public String readFile(String filePath) {
        return readFile(filePath, null, null);
    }
    
    public String readFile(String filePath, EntityTag eTag) {
        return readFile(filePath, null, eTag);
    }
    
    public <T> T readFile(String filePath, Class<T> responseType) {
        return readFile(filePath, responseType, null);
    }
    
    public abstract <T> T readFile(String filePath, Class<T> responseType, EntityTag eTag);
    
    @Override
    public String getHomeDir() {
        return getHomeDir(null, null);
    }
    
    public String getHomeDir(EntityTag eTag) {
        return getHomeDir(null, eTag);
    }
    
    public <T> T getHomeDir(Class<T> responseType) {
        return getHomeDir(responseType, null);
    }
    
    public abstract <T> T getHomeDir(Class<T> responseType, EntityTag eTag);
}