package com.sc.l45.weblogviewer.logging;

import org.slf4j.Logger;

public class LoggerFactory {
    public static Logger getLogger(Class<?> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }
    
    public static Logger getPerformanceLogger() {
        return org.slf4j.LoggerFactory.getLogger("performance");
    }
    
    public static Logger getApiCallsLogger() {
        return org.slf4j.LoggerFactory.getLogger("api-calls");
    }
}
