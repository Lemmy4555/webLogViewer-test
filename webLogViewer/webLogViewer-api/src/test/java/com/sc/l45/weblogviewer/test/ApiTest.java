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
import com.sc.l45.weblogviewer.test.api.ApiBridgeMgr;
import com.sc.l45.weblogviewer.test.config.ApiTestConf;
import com.sc.l45.weblogviewer.test.config.TestConf;

public class ApiTest extends ApiTestConf {
    private final static Logger logger = LoggerFactory.getLogger(ApiTest.class);
    
    private final int rowsInFile = 393210;

    @Test
    @RunAsClient
    public void getTailText(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        Timer timer = new Timer();
        String rowsToReadFromEnd = "400000";
        
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        response =  new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = null;
        response =  new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "0";
        response =  new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "393210";
        response =  new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "5000";
        response =  new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    @RunAsClient
    //TODO to be updated
    public void getTailTextWithCache(@ArquillianResource URL baseUrl) throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        Timer timer = new Timer();
        String rowsToReadFromEnd = "400000";
        
        String filePath = testFile.getAbsolutePath();
        Response rsResponse = api(baseUrl).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", Response.class);
        FileContentResponseComplete response = rsResponse.readEntity(FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response rsResponse304 = api(baseUrl).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(304, rsResponse304.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        response = api(baseUrl).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", FileContentResponseComplete.class, rsResponse.getEntityTag());
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
    }

    @Test
    @RunAsClient
    public void getTextFromLine(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        
        String fromLine = "393210";
        String maxRowsToRead = null;
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = null;

        fromLine = "393210";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine, maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "393200";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine, maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "400000";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "310000";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
    	timer.reset();
        
        fromLine = "0";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = null;
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = "0";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = "393210";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());

        timer.reset();
        
        fromLine = "393210";
        maxRowsToRead = "393210";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());

        timer.reset();
        
        fromLine = "391210";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());

        timer.reset();
        
        fromLine = "391210";
        maxRowsToRead = "400000";
        response = new ApiBridgeMgr(api(baseUrl)).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} con un massimo di {} da leggere in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), maxRowsToRead, timer.time());
    }
    
    @Test
    @RunAsClient
    //TODO to be updated
    public void getTextFromLineWithCache(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        String fromLine = "10";
        
        final String filePath = testFile.getAbsolutePath();
        Response rsResponse = api(baseUrl).getTextFromLine(filePath, fromLine,  null, "true", Response.class);
        FileContentResponseComplete response = rsResponse.readEntity(FileContentResponseComplete.class);
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response response304 = api(baseUrl).getTextFromLine(filePath, fromLine,  null, "false", Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(304, response304.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        fromLine = "310000";
        response = api(baseUrl).getTextFromLine(filePath, fromLine,  null, "false", FileContentResponseComplete.class, rsResponse.getEntityTag());
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    @RunAsClient
    public void getTextFromPointer(@ArquillianResource URL baseUrl) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        String rowsToReadFromEnd;
        Timer timer = new Timer();
        String maxRowsToRead = null;
        FileContentResponseComplete response;
        FileContentResponse responsePointer;
        String pointer;
        String filePath = testFile.getAbsolutePath();
        
        rowsToReadFromEnd = "10";
        maxRowsToRead = null;
        response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10000";
        maxRowsToRead = null;
        response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10000";
        maxRowsToRead = "1000";
        response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10000";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "50";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api(baseUrl)).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false");
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = "10";
        pointer = "0";
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = "1000";
        pointer = "0";
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = null;
        pointer = "0";
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = "10";
        pointer = "78968582";
        responsePointer = new ApiBridgeMgr(api(baseUrl)).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false");
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
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
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        String toRead = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length()) ).toString();
        timer.reset();
        
        Response rsResponse = api(baseUrl).getTextFromPointer(filePath, toRead,  null, "false", Response.class);
        FileContentResponse responsePointer = rsResponse.readEntity(FileContentResponse.class);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        Response response304 = api(baseUrl).getTextFromPointer(filePath, toRead,  null, "false", Response.class, rsResponse.getEntityTag());
        Assert.assertEquals(response304.getStatus(), 304);
        logger.info("Tornato 304 in: {}", timer.time());
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
	    Assert.assertNotNull(rowsInFile);
	    logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
	    
	    timer.reset();
	    
	    Response response304 = api(baseUrl).readFile(filePath, Response.class, rsResponse.getEntityTag());
	    Assert.assertEquals(304, response304.getStatus());
	    logger.info("Tornato 304 in: {}", timer.time());
	}

	@Test
    @RunAsClient
    public void getHomeDir(@ArquillianResource URL baseUrl) throws URISyntaxException {
        logger.info("Home dir: " + api(baseUrl).getHomeDir(DefaultDirResponse.class).path);
    }

}
