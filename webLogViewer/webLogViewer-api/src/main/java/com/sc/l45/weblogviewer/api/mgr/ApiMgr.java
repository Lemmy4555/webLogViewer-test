package com.sc.l45.weblogviewer.api.mgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.file.mgr.FileMgr;
import com.sc.l45.weblogviewer.api.mgr.readers.FileReaderFromEnd;
import com.sc.l45.weblogviewer.api.mgr.readers.FileReaderFromLine;
import com.sc.l45.weblogviewer.api.mgr.readers.FileReaderFromPointer;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.api.responses.FileDataResponse;
import com.sc.l45.weblogviewer.api.responses.FileListDataResponse;
import com.sc.l45.weblogviewer.api.utils.FileUtils;
import com.sc.l45.weblogviewer.api.utils.Timer;
import com.sc.l45.weblogviewer.reader.BufferedReaderLineSeparator;

public class ApiMgr {
	private final static Logger logger = LoggerFactory.getLogger(ApiMgr.class);
	private final static Logger perfLogger = LoggerFactory.getLogger("performance");
	
	@Inject private FileMgr fileMgr;
	
	public FileListDataResponse getFileList(String path) throws FileNotFoundException, IOException {
		File folder = new File(path);
		fileMgr.checkFilePath(folder);
		Set<FileDataResponse> fileDataSetForResponse = new HashSet<>();
		File[] fileList = FileUtils.getLegalFileList(folder);
		for (final File fileEntry : fileList) {
			FileDataResponse data = new FileDataResponse(fileEntry.getName(), fileEntry.isFile());
			fileDataSetForResponse.add(data);
		}
		FileListDataResponse response = new FileListDataResponse(fileDataSetForResponse);
		return response;
	}
	
	public FileContentResponse getTailText(File file, int maxRowsToRead, long pointer, boolean isTotRowsToGet) throws IOException {
	    Timer timer = new Timer();
        FileContentResponse response = FileReaderFromEnd.read(file, maxRowsToRead, pointer, isTotRowsToGet);
        logRowsRead(response, file, timer);
        return response;
	}
	
	public FileContentResponse getTextFromLine(File file, int lineFromStartRead, boolean isTotRowsToGet) throws IOException {
	    Timer timer = new Timer();
	    FileContentResponse response = FileReaderFromLine.read(file, lineFromStartRead, isTotRowsToGet);
	    logRowsRead(response, file, timer);
	    return response;
	}
	
	public FileContentResponse readFile(File file) throws IOException {
        List<String> allLines = FileUtils.readLines(file, FileConstants.ENCODING);
        
        FileContentResponse response;
        response = new FileContentResponseComplete(
                allLines, Integer.toString(allLines.size()), 
                Long.toString(file.length()), FileConstants.ENCODING.name(),
                Integer.toString(allLines.size()),
                Long.toString(file.length())
            );
        return response;
    }

    public FileContentResponse getTextFromPointer(File file, int pointer, boolean isTotRowsToGet) throws IOException {
        Timer timer = new Timer();
        FileContentResponse response = FileReaderFromPointer.read(file, pointer, isTotRowsToGet);
        logRowsRead(response, file, timer);
        return response;
    }

    private void logRowsRead(FileContentResponse response, File file, Timer timer) {
        perfLogger.info("Lettura {} righe del file {} in {}", response.rowsRead, file.getAbsolutePath(), timer.time());
    }
	
}
