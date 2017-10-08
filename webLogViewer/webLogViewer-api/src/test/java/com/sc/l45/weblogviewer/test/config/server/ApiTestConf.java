package com.sc.l45.weblogviewer.test.config.server;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.glassfish.jersey.client.JerseyClientBuilder;

import com.sc.l45.weblogviewer.test.bridge.ApiBridge;
import com.sc.l45.weblogviewer.test.config.TestConf;

public class ApiTestConf extends TestConf {
    private ApiBridge testApi;
    private URL baseUrl;
    
    /**
     * This will create a single istance of the "API Bridge" to get easy access to all methods exposed by APIs
     * 
     * @param baseUrl this value is injected by Arquillian in JUnit methods and is mandatory to build a request, 
     * 			generally it is something like: localhost:8000/rest
     * @return Always the same istance of {@link ApiBridge} that give you access to all methods exposed by APIs
     * 
     * @throws URISyntaxException Exception throwed by {@link JerseyClientBuilder} when inserted URL is not valid
     * @throws MalformedURLException 
     * @throws IllegalArgumentException if baseUrl is null
     */
    public ApiBridge api() throws URISyntaxException, MalformedURLException {
    	if(baseUrl == null) {
    		baseUrl = new URL("http://localhost:8080/webLogViewer-api");
    	}
        if(testApi == null) {
            testApi = new ApiBridge(baseUrl);
        }
        return testApi;
    }
}
