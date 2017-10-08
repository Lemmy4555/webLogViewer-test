package com.sc.l45.weblogviewer.test.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.responses.DefaultDirResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.api.responses.FileDataResponse;
import com.sc.l45.weblogviewer.api.responses.utils.FileContentResponseUtils;
import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.test.bridge.ApiBridgeMgr;
import com.sc.l45.weblogviewer.test.bridge.responses.FileContentBridgeResponse;
import com.sc.l45.weblogviewer.test.config.server.ApiTestConf;

public class ApiTest extends ApiTestConf {
    private final static Logger logger = LoggerFactory.getLogger(ApiTest.class);
    
    private final int rowsInFile = 393210;

    @Test
    public void getTailText() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        Timer timer = new Timer();
        String rowsToReadFromEnd = "400000";
        
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        response =  new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = null;
        response =  new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "0";
        response =  new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "393210";
        response =  new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "5000";
        response =  new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    public void getTailTextWithCache() throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        Timer timer = new Timer();
        String rowsToReadFromEnd = "400000";
        
        String filePath = testFile.getAbsolutePath();
        FileContentBridgeResponse<FileContentResponseComplete> bridgeResponse = new ApiBridgeMgr(api()).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", FileContentResponseComplete.class);
        FileContentResponseComplete response = bridgeResponse.getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        bridgeResponse = new ApiBridgeMgr(api()).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", FileContentResponseComplete.class, bridgeResponse.getETag());
        Assert.assertEquals(304, bridgeResponse.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        bridgeResponse = new ApiBridgeMgr(api()).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", FileContentResponseComplete.class, bridgeResponse.getETag());
        response = bridgeResponse.getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        bridgeResponse = new ApiBridgeMgr(api()).getTailText(filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "true", FileContentResponseComplete.class, bridgeResponse.getETag());
        Assert.assertEquals(304, bridgeResponse.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
    }

    @Test
    public void getTextFromLine() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        
        String fromLine = "393210";
        String maxRowsToRead = null;
        String filePath = testFile.getAbsolutePath();
        FileContentResponseComplete response = null;

        fromLine = "393210";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine, maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "393200";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine, maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "10";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "400000";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = "310000";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
    	timer.reset();
        
        fromLine = "0";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = null;
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = "0";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        fromLine = null;
        maxRowsToRead = "393210";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());

        timer.reset();
        
        fromLine = "393210";
        maxRowsToRead = "393210";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());

        timer.reset();
        
        fromLine = "391210";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());

        timer.reset();
        
        fromLine = "391210";
        maxRowsToRead = "400000";
        response = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} con un massimo di {} da leggere in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), maxRowsToRead, timer.time());
    }
    
    @Test
    public void getTextFromLineWithCache() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        Timer timer = new Timer();
        String fromLine = "10";
        
        final String filePath = testFile.getAbsolutePath();
        FileContentBridgeResponse<FileContentResponseComplete> genericResponse = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  null, "true", FileContentResponseComplete.class);
        FileContentResponseComplete response = genericResponse.getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        genericResponse = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  null, "true", FileContentResponseComplete.class, genericResponse.getETag());
        Assert.assertEquals(304, genericResponse.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        fromLine = "310000";
        genericResponse = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  null, "false", FileContentResponseComplete.class, genericResponse.getETag());
        response = genericResponse.getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(rowsInFile);
        logger.info("Lette {}/{} righe dalla riga {} dal file {} in: {}", response.rowsRead, rowsInFile, fromLine, testFile.getAbsolutePath(), timer.time());
    
        timer.reset();
        
        genericResponse = new ApiBridgeMgr(api()).getTextFromLine(filePath, fromLine,  null, "false", FileContentResponseComplete.class, genericResponse.getETag());
        Assert.assertEquals(304, genericResponse.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
    }
    
    @Test
    public void getTextFromPointer() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        String rowsToReadFromEnd;
        Timer timer = new Timer();
        String maxRowsToRead = null;
        FileContentResponseComplete response;
        FileContentResponseComplete responsePointer;
        String pointer;
        String filePath = testFile.getAbsolutePath();
        
        rowsToReadFromEnd = "10";
        maxRowsToRead = null;
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10000";
        maxRowsToRead = null;
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10000";
        maxRowsToRead = "1000";
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10000";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "10";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        Assert.assertNotNull(rowsInFile);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "50";
        maxRowsToRead = "10";
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertNotNull(response.size);
        Assert.assertNotNull(response.encoding);
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        Assert.assertEquals(responsePointer.rowsRead, maxRowsToRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = "10";
        pointer = "0";
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = "1000";
        pointer = "0";
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = null;
        pointer = "0";
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        maxRowsToRead = "10";
        pointer = "78968582";
        responsePointer = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class).getResponse();
        Assert.assertNotNull(responsePointer.readContent);
        Assert.assertNotNull(responsePointer.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", responsePointer.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
    }
    
    @Test
    public void getTextFromPointerWithCache() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
        String filePath = testFile.getAbsolutePath();
        Timer timer = new Timer();
        
        String rowsToReadFromEnd = "10";
        String maxRowsToRead = null;
        FileContentResponseComplete response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        String pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        FileContentBridgeResponse<FileContentResponseComplete> genericResponse = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class);
        response = genericResponse.getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertEquals(response.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        genericResponse = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class, genericResponse.getETag());
        Assert.assertEquals(304, genericResponse.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
        
        timer.reset();
        
        rowsToReadFromEnd = "1000";
        maxRowsToRead = "100";
        response = new ApiBridgeMgr(api()).getTailText(
        		filePath, Long.toString(testFile.length()), rowsToReadFromEnd, "false", FileContentResponseComplete.class).getResponse();
        pointer = ((Integer) (Integer.parseInt(response.size) - FileContentResponseUtils.getRowsAsString(response).length())).toString();
        timer.reset();
        genericResponse = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class);
        response = genericResponse.getResponse();
        Assert.assertNotNull(response.readContent);
        Assert.assertNotNull(response.rowsRead);
        Assert.assertEquals(response.rowsRead, response.rowsRead);
        logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
        
        timer.reset();
        
        genericResponse = new ApiBridgeMgr(api()).getTextFromPointer(filePath, pointer,  maxRowsToRead, "false", FileContentResponseComplete.class, genericResponse.getETag());
        Assert.assertEquals(304, genericResponse.getStatus());
        logger.info("Tornato 304 in: {}", timer.time());
    }
    
    @Test
	public void readFile() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
	    Timer timer = new Timer();
	    
	    final String filePath = testFile.getAbsolutePath();
	    FileContentResponseComplete response = new ApiBridgeMgr(api()).readFile(filePath, FileContentResponseComplete.class).getResponse();
	    Assert.assertNotNull(response.readContent);
	    Assert.assertNotNull(response.rowsRead);
	    Assert.assertNotNull(rowsInFile);
	    logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
	}
    
    @Test
	public void readFileWithCache() throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
	    Timer timer = new Timer();
	    final String filePath = testFile.getAbsolutePath();
	    FileContentBridgeResponse<FileContentResponseComplete> genericResponse = new ApiBridgeMgr(api()).readFile(filePath, FileContentResponseComplete.class);
	    FileContentResponseComplete response = genericResponse.getResponse();
	    Assert.assertNotNull(response.readContent);
	    Assert.assertNotNull(response.rowsRead);
	    Assert.assertNotNull(rowsInFile);
	    logger.info("Lette {}/{} righe dal file {} in: {}", response.rowsRead, rowsInFile, testFile.getAbsolutePath(), timer.time());
	    
	    timer.reset();
	    
	    genericResponse = new ApiBridgeMgr(api()).readFile(filePath, FileContentResponseComplete.class, genericResponse.getETag());
	    Assert.assertEquals(304, genericResponse.getStatus());
	    logger.info("Tornato 304 in: {}", timer.time());
	}
    
    @Test
    public void getFileData() throws URISyntaxException, MalformedURLException {
    	final String filePath = testFile.getAbsolutePath();
    	FileDataResponse response = api().getFileData(filePath, FileDataResponse.class);
    	logger.info(response.toString());
    }

	@Test
    public void getHomeDir() throws URISyntaxException, MalformedURLException {
        logger.info("Home dir: " + api().getHomeDir(DefaultDirResponse.class).path);
    }

}
