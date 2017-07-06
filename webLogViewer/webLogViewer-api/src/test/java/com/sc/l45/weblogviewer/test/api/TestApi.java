package com.sc.l45.weblogviewer.test.api;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClientBuilder;

@SuppressWarnings("unchecked")
public class TestApi extends TestApiAbstract {
	private WebTarget client;
	
	public TestApi(URL baseUrl) throws URISyntaxException {
	    if(baseUrl == null) {
	        throw new IllegalArgumentException("La baseUrl non puo essere null");
	    }
	    client = JerseyClientBuilder.newClient().target(baseUrl.toURI());
	}
	
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
	
	public String getFileData(String filePath) {
		return client
        .path("api/getFileData")
        .queryParam("filePath", filePath)
        .request(MediaType.APPLICATION_JSON)
        .get()
        .readEntity(String.class);
	}
	
	@Override
    public <T> T getTailText(String filePath, String rowsfromEnd, String pointer, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
	    responseType = (Class<T>) TestApiHelper.checkResponseType(responseType);
	    Builder request = TestApiHelper.setETag(
	            client
                    .path("api/getTailText")
                    .queryParam("filePath", filePath)
                    .queryParam("maxRowsToRead", rowsfromEnd)
                    .queryParam("pointer", pointer)
                    .queryParam("isTotRowsToGet", isTotRowsToGet)
                    .request(MediaType.APPLICATION_JSON),
                eTag);
	    if(responseType.equals(Builder.class)) {
	        return (T) request;
	    }
	    return TestApiHelper.createResponse(responseType, request.get());
	}

    public <T> T getTextFromLine(String filePath, String pointer, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
        responseType = (Class<T>) TestApiHelper.checkResponseType(responseType);
        Builder request = TestApiHelper.setETag(client
                .path("api/getTextFromLine")
                .queryParam("filePath", filePath)
                .queryParam("lineFrom", pointer)
                .queryParam("isTotRowsToGet", isTotRowsToGet)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return TestApiHelper.createResponse(responseType, request.get());
	}
    
    public String getHomeDir() {
        return getHomeDir(null, null);
    }
    
    public String getHomeDir(EntityTag eTag) {
        return getHomeDir(null, eTag);
    }
    
    public <T> T getHomeDir(Class<T> responseType) {
        return getHomeDir(responseType, null);
    }
	
	public <T> T getHomeDir(Class<T> responseType, EntityTag eTag) {
	    responseType = (Class<T>) TestApiHelper.checkResponseType(responseType);
	    Builder request = TestApiHelper.setETag(client
                .path("api/getHomeDir")
                .request(MediaType.APPLICATION_JSON),
            eTag);
	    if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return TestApiHelper.createResponse(responseType, request.get());
    }
	
	@Override
    public <T> T getTextFromPointer(String filePath, String pointer, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
        responseType = (Class<T>) TestApiHelper.checkResponseType(responseType);
        Builder request = TestApiHelper.setETag(client
                .path("api/getTextFromPointer")
                .queryParam("filePath", filePath)
                .queryParam("pointer", pointer)
                .queryParam("isTotRowsToGet", isTotRowsToGet)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return TestApiHelper.createResponse(responseType, request.get());
    }
    
    public String readFile(String filePath) {
        return readFile(filePath, null, null);
    }
    
    public String readFile(String filePath, EntityTag eTag) {
        return readFile(filePath, null, eTag);
    }
    
    public <T> T readFile(String filePath, Class<T> responseType) {
        return readFile(filePath, responseType, null);
    }

    public <T> T readFile(String filePath, Class<T> responseType, EntityTag eTag) {
        responseType = (Class<T>) TestApiHelper.checkResponseType(responseType);
        Builder request = TestApiHelper.setETag(client
                .path("api/readFile")
                .queryParam("filePath", filePath)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return TestApiHelper.createResponse(responseType, request.get());
    }

}
