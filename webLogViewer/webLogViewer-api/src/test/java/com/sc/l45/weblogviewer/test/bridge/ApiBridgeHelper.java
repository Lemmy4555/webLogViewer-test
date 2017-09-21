package com.sc.l45.weblogviewer.test.bridge;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

/**
 * Class used by {@link ApiBridge} to serialize server response in the required class
 * 
 * @author Lemmy4555
 */
class ApiBridgeHelper {
    
	/**
	 * Check to prevent {@link NullPointerException} when null is passed
	 * @param responseType required response class
	 * @return the responseType in input or {@link String} in case null is passed 
	 */
    static Class<?> checkResponseType(Class<?> responseType) {
        return responseType != null ? responseType : String.class;
    }
    
    /**
     * Serialize server response in the required class
     * @param responseType the required response class
     * @param response the server response
     * @return an istance of the response in the required class
     */
    @SuppressWarnings("unchecked")
    static <T> T createResponse(Class<T> responseType, Response response) {
        if(responseType.equals(Response.class)) {
            return (T) response;
        } else {
            return response.readEntity(responseType);
        }
    }
    
    /**
     * Add If-None-Match header to the response to allow the server check the eTag of the last response,
     * if the eTag equals than the server will return 304 (Not modified)
     * @param builder request builder
     * @param eTag the eTag of the last response
     * @return the builder with the If-None-Match Header setted
     */
    static Builder setETag(Builder builder, EntityTag eTag) {
        if(eTag != null) {
            builder.header("If-None-Match", "\"" + eTag + "\"");
        }
        return builder;
    }

}
