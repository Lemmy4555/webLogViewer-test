package com.sc.l45.weblogviewer.test.api;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

class TestApiHelper {
    
    static Class<?> checkResponseType(Class<?> responseType) {
        return responseType != null ? responseType : String.class;
    }
    
    @SuppressWarnings("unchecked")
    static <T> T createResponse(Class<T> responseType, Response response) {
        if(responseType.equals(Response.class)) {
            return (T) response;
        } else {
            return response.readEntity(responseType);
        }
    }
    
    static Builder setETag(Builder builder, EntityTag eTag) {
        if(eTag != null) {
            builder.header("If-None-Match", "\"" + eTag + "\"");
        }
        return builder;
    }

}
