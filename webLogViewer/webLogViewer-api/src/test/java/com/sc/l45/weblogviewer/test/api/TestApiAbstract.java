package com.sc.l45.weblogviewer.test.api;

import javax.ws.rs.core.EntityTag;

abstract class TestApiAbstract implements TestApiInterface {
	@Override
	public String getTailText(String filePath, String rowsfromEnd, String pointer, String isTotRowsToGet, String getFileLength) {
	    return getTailText(filePath, rowsfromEnd, pointer, isTotRowsToGet, null, null);
	}
	
	public String getTailText(String filePath, String rowsfromEnd, String isTotRowsToGet, String pointer, EntityTag eTag) {
        return getTailText(filePath, rowsfromEnd, pointer, isTotRowsToGet, null, eTag);
    }
	
	public <T> T getTailText(String filePath, String rowsfromEnd, String pointer, String isTotRowsToGet, Class<T> responseType) {
        return getTailText(filePath, rowsfromEnd, pointer, isTotRowsToGet, responseType, null);
    }
	
	public abstract <T> T getTailText(String filePath, String rowsfromEnd, String pointer, String isTotRowsToGet, Class<T> responseType, EntityTag eTag);
	
	@Override
	public String getTextFromPointer(String filePath, String pointer, String isTotRowsToGet) {
        return getTextFromPointer(filePath, pointer, isTotRowsToGet, null, null);
    }
    
    public String getTextFromPointer(String filePath, String pointer, String isTotRowsToGet, EntityTag eTag) {
        return getTextFromPointer(filePath, pointer, isTotRowsToGet, null, eTag);
    }
    
    public <T> T getTextFromPointer(String filePath, String pointer, String isTotRowsToGet, Class<T> responseType) {
        return getTextFromPointer(filePath, pointer, isTotRowsToGet, responseType, null);
    }
    
    public abstract <T> T getTextFromPointer(String filePath, String pointer, String isTotRowsToGet, Class<T> responseType, EntityTag eTag);
    
    @Override
    public String getTextFromLine(String filePath, String pointer, String isTotRowsToGet) {
        return getTextFromLine(filePath, pointer, isTotRowsToGet, null, null);
    }
    
    public String getTextFromLine(String filePath, String pointer, String isTotRowsToGet, EntityTag eTag) {
        return getTextFromLine(filePath, pointer, isTotRowsToGet, null, eTag);
    }
    
    public <T> T getTextFromLine(String filePath, String pointer, String isTotRowsToGet, Class<T> responseType) {
        return getTextFromLine(filePath, pointer, isTotRowsToGet, responseType, null);
    }
    
    public abstract <T> T getTextFromLine(String filePath, String pointer, String isTotRowsToGet, Class<T> responseType, EntityTag eTag);
}
