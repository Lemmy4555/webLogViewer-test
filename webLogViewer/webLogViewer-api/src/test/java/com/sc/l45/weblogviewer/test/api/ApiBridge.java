package com.sc.l45.weblogviewer.test.api;

import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.JerseyClientBuilder;

@SuppressWarnings("unchecked")
public class ApiBridge extends ApiBridgeAbstract {
	private WebTarget client;
	
	public ApiBridge(URL baseUrl) throws URISyntaxException {
	    if(baseUrl == null) {
	        throw new IllegalArgumentException("La baseUrl non puo essere null");
	    }
	    client = JerseyClientBuilder.newClient().target(baseUrl.toURI());
	}
	
	@Override
	public <T> T getFileData(String filePath, Class<T> responseType, EntityTag eTag) {
		Builder request =  client
	        .path("api/getFileData")
	        .queryParam("filePath", filePath)
	        .request(MediaType.APPLICATION_JSON);
		if(responseType.equals(Builder.class)) {
	        return (T) request;
	    }
	    return ApiBridgeHelper.createResponse(responseType, request.get());
	}
	
	@Override
    public <T> T getTailText(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
	    responseType = (Class<T>) ApiBridgeHelper.checkResponseType(responseType);
	    Builder request = ApiBridgeHelper.setETag(
	            client
                    .path("api/getTailText")
                    .queryParam("filePath", filePath)
                    .queryParam("pointer", pointer)
                    .queryParam("maxRowsToRead", maxRowsToRead)
                    .queryParam("isTotRowsToGet", isTotRowsToGet)
                    .request(MediaType.APPLICATION_JSON),
                eTag);
	    if(responseType.equals(Builder.class)) {
	        return (T) request;
	    }
	    return ApiBridgeHelper.createResponse(responseType, request.get());
	}
	
	@Override
    public <T> T getTextFromLine(String filePath, String fromLine, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
        responseType = (Class<T>) ApiBridgeHelper.checkResponseType(responseType);
        Builder request = ApiBridgeHelper.setETag(client
                .path("api/getTextFromLine")
                .queryParam("filePath", filePath)
                .queryParam("fromLine", fromLine)
                .queryParam("maxRowsToRead", maxRowsToRead)
                .queryParam("isTotRowsToGet", isTotRowsToGet)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return ApiBridgeHelper.createResponse(responseType, request.get());
	}
	
	public <T> T getHomeDir(Class<T> responseType, EntityTag eTag) {
	    responseType = (Class<T>) ApiBridgeHelper.checkResponseType(responseType);
	    Builder request = ApiBridgeHelper.setETag(client
                .path("api/getHomeDir")
                .request(MediaType.APPLICATION_JSON),
            eTag);
	    if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return ApiBridgeHelper.createResponse(responseType, request.get());
    }
	
	@Override
    public <T> T getTextFromPointer(String filePath, String pointer, String maxRowsToRead, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
        responseType = (Class<T>) ApiBridgeHelper.checkResponseType(responseType);
        Builder request = ApiBridgeHelper.setETag(client
                .path("api/getTextFromPointer")
                .queryParam("filePath", filePath)
                .queryParam("pointer", pointer)
                .queryParam("maxRowsToRead", maxRowsToRead)
                .queryParam("isTotRowsToGet", isTotRowsToGet)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return ApiBridgeHelper.createResponse(responseType, request.get());
    }

    public <T> T readFile(String filePath, Class<T> responseType, EntityTag eTag) {
        responseType = (Class<T>) ApiBridgeHelper.checkResponseType(responseType);
        Builder request = ApiBridgeHelper.setETag(client
                .path("api/readFile")
                .queryParam("filePath", filePath)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return ApiBridgeHelper.createResponse(responseType, request.get());
    }

	public <T> T getTextFromLineToLine(String filePath, String fromLine, String toLine, String isTotRowsToGet, Class<T> responseType, EntityTag eTag) {
		responseType = (Class<T>) ApiBridgeHelper.checkResponseType(responseType);
        Builder request = ApiBridgeHelper.setETag(client
                .path("api/getTextFromLineToLine")
                .queryParam("filePath", filePath)
                .queryParam("fromLine", fromLine)
                .queryParam("lineTo", toLine)
                .queryParam("isTotRowsToGet", isTotRowsToGet)
                .request(MediaType.APPLICATION_JSON),
            eTag);
        if(responseType.equals(Builder.class)) {
            return (T) request;
        }
        return ApiBridgeHelper.createResponse(responseType, request.get());
	}

}
