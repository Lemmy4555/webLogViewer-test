package com.sc.l45.weblogviewer.test.config;

import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import com.sc.l45.weblogviewer.test.api.TestApi;

@RunWith(Arquillian.class)
public class ApiTestConf extends TestConf {
    private TestApi testApi;
    
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "webLogViewer.war").addPackages(true, "com.sc.l45.weblogviewer")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsResource("arquillian.xml");
    }
    
    public TestApi api(URL baseUrl) throws URISyntaxException {
        if(testApi == null) {
            testApi = new TestApi(baseUrl);
        }
        return testApi;
    }
}
