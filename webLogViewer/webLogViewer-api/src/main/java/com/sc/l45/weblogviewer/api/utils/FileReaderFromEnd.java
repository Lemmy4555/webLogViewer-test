package com.sc.l45.weblogviewer.api.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;
import com.sc.l45.weblogviewer.api.responses.FileContentResponseComplete;
import com.sc.l45.weblogviewer.reader.BufferedReaderLineSeparator;
import com.sc.l45.weblogviewer.reader.ReversedLinesFileReaderLineSeparator;

/**
 * Classe creata per via della necessita di dover utilizzare due classi diverse per leggere un file in input a seconda
 * dei dati in input per migliorare le performance.
 */
public class FileReaderFromEnd {
    /**
     * Massimo numero di righe che vengono lette utilizzando il {@link ReversedLinesFileReaderLineSeparator}, dopo di che
     * si utilizza {@link FileUtils#readLines(File)}
     * <br><br>
     * Dopo alcuni benchmark e stato constato che leggere un file con il {@link ReversedLinesFileReaderLineSeparator}
     * diventa piu lento che leggerlo completamente dall'inizio dopo questo numero di righe.
     */
    private static final int REVERSED_LINE_READER_MAX_LINES = 20000;
    
    public static FileContentResponse read(File file, int linesToReadFromEnd, boolean isTotRowsToGet) throws IOException {
        if(isTotRowsToGet || linesToReadFromEnd > REVERSED_LINE_READER_MAX_LINES) {
            //Il numero ri righe nel file puo essere ottenuto solo leggendo tutto il file.
            return readFileWithBufferedReader(file, linesToReadFromEnd);
        } else {
            if(linesToReadFromEnd <= 0) {
                return new FileContentResponse(new ArrayList<>(), "0", Long.toString(file.length()), FileConstants.ENCODING.name());
            }
            return readFileWithReversedLineFileReader(file, linesToReadFromEnd);
        }
    }
    
    private static FileContentResponse readFileWithReversedLineFileReader(File file, int linesToReadFromEnd) throws IOException {
        List<String> content = new ArrayList<>();
        
        try(ReversedLinesFileReaderLineSeparator fileReader = new ReversedLinesFileReaderLineSeparator(file, FileConstants.ENCODING)) {
            String line;
            int i = 0;
            while((line = fileReader.readLine()) != null) {
                content.add(line);
                if(i >= linesToReadFromEnd - 1) {
                    break;
                }
                i++;
            }
        }
        
        content = Lists.reverse(content);
        
        FileContentResponse response = new FileContentResponse(
                content, Integer.toString(content.size()), Long.toString(file.length()), FileConstants.ENCODING.name()
            );

        return response;
    }
    
    private static FileContentResponse readFileWithBufferedReader(File file, int linesToReadFromEnd) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(inputStream, FileConstants.ENCODING.name());
                BufferedReaderLineSeparator br = new BufferedReaderLineSeparator(isr);) {
        
            List<String> content = new ArrayList<>();
            
            String line;
            while((line = br.readLine()) != null) {
                content.add(line);
            }
            
            if(content.size() < linesToReadFromEnd) {
                linesToReadFromEnd = content.size();
            }
            
            List<String> contentToRead = content.subList(content.size() - linesToReadFromEnd, content.size());
            
            FileContentResponseComplete response;
            response = new FileContentResponseComplete(
                    contentToRead, Integer.toString(linesToReadFromEnd), 
                    Long.toString(file.length()), FileConstants.ENCODING.name(),
                    Integer.toString(content.size())
                );
            return response;
        }
    }
}
