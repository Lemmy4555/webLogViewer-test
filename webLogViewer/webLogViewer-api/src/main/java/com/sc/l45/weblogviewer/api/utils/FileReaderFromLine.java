package com.sc.l45.weblogviewer.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.reader.BufferedReaderLineSeparator;

public class FileReaderFromLine {
    public static FileContentResponseComplete read(File file, int lineFromStartRead) throws IOException {
        if(lineFromStartRead < 0) {
            lineFromStartRead = 0;
        }
        return readFileWithBufferedReader(file, lineFromStartRead);
    }
    
    private static FileContentResponseComplete readFileWithBufferedReader(File file, int lineFromStartRead) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(inputStream, FileConstants.ENCODING.name());
                BufferedReaderLineSeparator br = new BufferedReaderLineSeparator(isr);) {
        
            List<String> content = new ArrayList<>();
            
            int nLines = 0;
            String line;
            while((line = br.readLine()) != null) {
                nLines++;
                if(nLines >= lineFromStartRead) {
                    content.add(line);
                }
            }
            
            if (lineFromStartRead > nLines) {
                lineFromStartRead = nLines;
            }
            
            FileContentResponseComplete response;
            response = new FileContentResponseComplete(
                    content, Integer.toString(content.size()), 
                    Long.toString(file.length()), FileConstants.ENCODING.name(),
                    Integer.toString(nLines)
                );
            return response;
        }
    }
}
