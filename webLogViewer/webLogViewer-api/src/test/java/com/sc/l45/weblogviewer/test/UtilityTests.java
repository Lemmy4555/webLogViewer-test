package com.sc.l45.weblogviewer.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.responses.FileDataResponse;
import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.test.config.ApiTestConf;
import com.sc.l45.weblogviewer.test.config.TestConf;

public class UtilityTests extends ApiTestConf {
	private final static Logger logger = LoggerFactory.getLogger(ApiTest.class);
	
	@Test
    @RunAsClient
    public void readFileData(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		Timer timer = new Timer();
		
        String filePath = testFile.getAbsolutePath();
        String plainResp = api(baseUrl).getFileData(filePath);
        ObjectMapper mapper = new ObjectMapper();
        FileDataResponse response = mapper.readValue(plainResp, FileDataResponse.class);
        Assert.assertEquals(response.name, TestConf.TEST_FILE.NAME);
        Assert.assertEquals(response.isFile, true);

        filePath = testFile.getParentFile().getAbsolutePath();
        plainResp = api(baseUrl).getFileData(filePath);
        mapper = new ObjectMapper();
        response = mapper.readValue(plainResp, FileDataResponse.class);
        Assert.assertEquals(response.isFile, false);
//        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
    }
}
