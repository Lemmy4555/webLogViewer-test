package com.sc.l45.weblogviewer.test.config.arquillian;

import java.net.URISyntaxException;
import java.net.URL;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import com.sc.l45.weblogviewer.test.bridge.ApiBridge;
import com.sc.l45.weblogviewer.test.config.TestConf;

/**
 * This class has all the configuration to setup JUnit tests, it has to be extended by Test Classes
 * 
 * This will provide you Arquillian support, an API Bridge, the reference to a {@link TestConf#testFile}, 
 * a Watcher to log JUnit methods in case of uncaught Exceptions and also time needed to a test method to finish
 * 
 * @author Lemmy4555
 */
@RunWith(Arquillian.class)
public class ApiTestConf extends TestConf {
    private ApiBridge testApi;
    
    /**
     * This is necessary to Arquillian to create a new istance of an embedded server, 
     * I choose Glassfish because it was the easiest to use with Arquillian.
     * @return a {@link WebArchive} used by Arquillian, it's like a .war file
     */
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "webLogViewer.war").addPackages(true, "com.sc.l45.weblogviewer")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsResource("arquillian.xml");
    }
    
    /**
     * This will create a single istance of the "API Bridge" to get easy access to all methods exposed by APIs
     * 
     * @param baseUrl this value is injected by Arquillian in JUnit methods and is mandatory to build a request, 
     * 			generally it is something like: localhost:8000/rest
     * @return Always the same istance of {@link ApiBridge} that give you access to all methods exposed by APIs
     * 
     * @throws URISyntaxException Exception throwed by {@link JerseyClientBuilder} when inserted URL is not valid
     * @throws IllegalArgumentException if baseUrl is null
     */
    public ApiBridge api(URL baseUrl) throws URISyntaxException {
        if(testApi == null) {
            testApi = new ApiBridge(baseUrl);
        }
        return testApi;
    }
}
