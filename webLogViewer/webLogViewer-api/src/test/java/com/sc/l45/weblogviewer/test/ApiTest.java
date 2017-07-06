package com.sc.l45.weblogviewer.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.responses.DefaultDirResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.api.responses.FileDataResponse;
import com.sc.l45.weblogviewer.api.responses.utils.FileContentResponseUtils;
import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.test.api.TestApiMgr;
import com.sc.l45.weblogviewer.test.config.ApiTestConf;
import com.sc.l45.weblogviewer.test.config.TestConf;

public class ApiTest extends ApiTestConf {
    private final static Logger logger = LoggerFactory.getLogger(ApiTest.class);
    
    @Test
    @RunAsClient
    public void readFileData(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
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
    }

    @Test
    @RunAsClient
    public void getTailText(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        Timer timer = new Timer();
        String rowsToReadFromEnd = "10";
        
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = new TestApiMgr(api(baseUrl)).getTailText(filePath, rowsToReadFromEnd, Long.toString(testFile.length()), "true");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {} righe dal file {} in: {}", response.rowsRead, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "400000";
        response =  new TestApiMgr(api(baseUrl)).getTailText(filePath, rowsToReadFromEnd, Long.toString(testFile.length()), "true");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {} righe dal file {} in: {}", response.rowsRead, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    @RunAsClient
    //TODO to be updated
    public void getTailTextWithCache(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        Timer timer = new Timer();
        String rowsToReadFromEnd = "400000";
        
        String filePath = testFile.getAbsolutePath();
        Response rsResponse = api(baseUrl).getTailText(filePath, rowsToReadFromEnd, Long.toString(testFile.length()), "true", Response.class);
        FileContentResponseComplete response = rsResponse.readEntity(FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response rsResponse304 = api(baseUrl).getTailText(filePath, rowsToReadFromEnd, Long.toString(testFile.length()), "true", Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(304, rsResponse304.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        response = api(baseUrl).getTailText(filePath, rowsToReadFromEnd, Long.toString(testFile.length()), "true", FileContentResponseComplete.class, rsResponse.getEntityTag());
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
    }

    @Test
    @RunAsClient
    //TODO rows in file must be added
    public void getTextFromLine(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        
        String fromLine = "10";
        
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = new TestApiMgr(api(baseUrl)).getTextFromLine(filePath, fromLine, "true");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, response.rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "400000";
        response = new TestApiMgr(api(baseUrl)).getTextFromLine(filePath, fromLine, "true");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, response.rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "310000";
        response = new TestApiMgr(api(baseUrl)).getTextFromLine(filePath, fromLine, "true");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, response.rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    @RunAsClient
    public void getTextFromLineWithCache(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        String fromLine = "10";
        
        final String filePath = testFile.getAbsolutePath();
        Response rsResponse = api(baseUrl).getTextFromLine(filePath, fromLine, "true", Response.class);
        FileContentResponseComplete response = rsResponse.readEntity(FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, response.rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response response304 = api(baseUrl).getTextFromLine(filePath, fromLine, "false", Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(304, response304.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        fromLine = "310000";
        response = api(baseUrl).getTextFromLine(filePath, fromLine, "false", FileContentResponseComplete.class, rsResponse.getEntityTag());
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, response.rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    @RunAsClient
    public void readFileWithCache(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        final String filePath = testFile.getAbsolutePath();
        Response rsResponse = api(baseUrl).readFile(filePath, Response.class);
        FileContentResponseComplete response = rsResponse.readEntity(FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response response304 = api(baseUrl).readFile(filePath, Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(304, response304.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
    }
    
    @Test
    @RunAsClient
    public void getTextFromPointer(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        final String rowsToReadFromEnd = "10";
        Timer timer = new Timer();
        
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = api(baseUrl).getTailText(filePath, rowsToReadFromEnd, "true", Long.toString(testFile.length()), FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        String toRead = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        
        FileContentResponse responsePointer = api(baseUrl).getTextFromPointer(filePath, toRead, "false", FileContentResponse.class);
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    @RunAsClient
    public void getTextFromPointerWithCache(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        final String rowsToReadFromEnd = "10";
        Timer timer = new Timer();
        
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = api(baseUrl).getTailText(filePath, rowsToReadFromEnd, "true", Long.toString(testFile.length()), FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(response.rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        String toRead = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length()) ).toString();
        timer.reset();
        
        Response rsResponse = api(baseUrl).getTextFromPointer(filePath, toRead, "false", Response.class);
        FileContentResponse responsePointer = rsResponse.readEntity(FileContentResponse.class);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, response.rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response response304 = api(baseUrl).getTextFromPointer(filePath, toRead, "false", Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(response304.getStatus(), 304);
        logger.info("Tornato 304 in: {}", timer.time());
    }
    
    @Test
    @RunAsClient
    public void getHomeDir(@ArquillianResource URL baseUrl) throws URISyntaxException {
        logger.info("Home dir: " + api(baseUrl).getHomeDir(DefaultDirResponse.class).path);
    }

}
