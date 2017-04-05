package com.sc.l45.weblogviewer.api;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.responses.ErrorResponse;

class ApiErrorUtil {
    private final static Logger logger = LoggerFactory.getLogger(RestApi.class);
    
    public static Response jsonError(String message, String endpoint, Exception e) {
        logger.error("Errore nella chiamata a {}", endpoint, e);
        if (message == null) {
            message = "null pointer";
        }
        return Response.status(500).entity(new ErrorResponse(message)).build();
    }
}
