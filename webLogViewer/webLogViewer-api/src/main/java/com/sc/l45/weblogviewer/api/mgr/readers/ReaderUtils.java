package com.sc.l45.weblogviewer.api.mgr.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.responses.FileContentResponse;

public class ReaderUtils {
	static List<String> convertBytesArrayToStringList(byte[] buffer) {
		List<String> allLines = new ArrayList<>();
		Pattern pattern = FileConstants.END_LINE_PATTERN;
		
		Matcher matcher;
		String line = new String(buffer, FileConstants.ENCODING);
		matcher = pattern.matcher(line);
		int lastIndex = 0;
		while(matcher.find()) {
			allLines.add(line.substring(lastIndex, matcher.end()));
			lastIndex = matcher.end();
		}
		String lastLine = line.substring(lastIndex, line.length());
		if(!lastLine.isEmpty()) {
			allLines.add(lastLine);
		}
		
		return allLines;
	}
	
	public static Integer getStringLengthFromList(List<String> lines) {
		int length = 0;
		for(String content : lines) {
			length += content.length();
		}
		return length;
	}
	
	static int countLinesInFile(File file) throws IOException {
   	 try(FileInputStream inputStream = new FileInputStream(file);
   			 InputStreamReader isr = new InputStreamReader(inputStream, FileConstants.ENCODING);
                BufferedReader br = new BufferedReader(isr);) {
            
            int linesCount = 0;
            
            while(br.readLine() != null) {
                linesCount++;
            }
            
            return linesCount;
        }
   }
}
