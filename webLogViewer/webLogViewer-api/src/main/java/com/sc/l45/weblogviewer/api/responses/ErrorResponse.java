package com.sc.l45.weblogviewer.api.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse {
    public String error;
    
    @SuppressWarnings("unused")
    private ErrorResponse() {};
    
    public ErrorResponse(String error) {
        this.error = error;
    }
}
