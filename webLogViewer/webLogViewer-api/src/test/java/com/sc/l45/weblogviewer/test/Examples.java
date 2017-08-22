package com.sc.l45.weblogviewer.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.client.Invocation.Builder;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.responses.FileDataResponse;
import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.test.config.ApiTestConf;

/**
 * This class get a panoramic on how to write new tests and explain the architecture
 * 
 * ApiTestConf has to be extended because it contains the code to run a new embedded Glassfish server with Arquillian.
 * It also provides
 * @author Lemmy
 *
 */
public class Examples extends ApiTestConf {
	private final static Logger logger = LoggerFactory.getLogger(ApiTest.class);
	
	@Test
    @RunAsClient
    public void readFileDataExample(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
		Timer timer = new Timer();
		
        
        // Plain text and mapping example
		String filePath = testFile.getAbsolutePath();
		String plainResp = api(baseUrl).getFileData(filePath);
        ObjectMapper mapper = new ObjectMapper();
        FileDataResponse response = mapper.readValue(plainResp, FileDataResponse.class);
        logger.info("Letti i seguenti dati dal file in {}: \n\tfile name: {}\n\tis a file: {}\n\nreturned plain response: {}",
        		timer.time(), response.name, response.isFile, plainResp);
        
        //Timer reset for log new requests
        timer.reset();
        
        // Mapped response
        filePath = testFile.getParentFile().getAbsolutePath();
        response = api(baseUrl).getFileData(filePath, FileDataResponse.class);
        logger.info("Letti i seguenti dati dal file in {}: \n\tfile name: {}\n\tis a file: {}\n\n(mapped response)",
        		timer.time(), response.name, response.isFile);
        
        timer.reset();
        
        // You can also get the request Builder and manually call it
        filePath = testFile.getParentFile().getAbsolutePath();
        Builder builder = api(baseUrl).getFileData(filePath, Builder.class);
        response = builder.get().readEntity(FileDataResponse.class);
        logger.info("Letti i seguenti dati dal file in {}: \n\tfile name: {}\n\tis a file: {}\n\n(builder with mapped response)",
        		timer.time(), response.name, response.isFile);
        
        // TODO: implement test with CacheControl
    }
}
