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
import com.sc.l45.weblogviewer.api.utils.ListUtils;

public class ReaderUtils {
	/**
	 * Convert content read from a file in {@link ArrayList} with one element per line
	 * @param buffer content read
	 * @return content read as {@link ArrayList}
	 */
	static List<String> convertBytesArrayToStringList(byte[] buffer) {
		List<String> allLines = new ArrayList<>();
		Pattern pattern = FileConstants.END_LINE_PATTERN;
		
		Matcher matcher;
		String contentReadAsString = new String(buffer, FileConstants.ENCODING);
		matcher = pattern.matcher(contentReadAsString);
		int lastIndex = 0;
		while(matcher.find()) {
			allLines.add(contentReadAsString.substring(lastIndex, matcher.end()));
			lastIndex = matcher.end();
		}
		String lastLine;
		if(contentReadAsString.charAt(contentReadAsString.length() - 1) == 0) {
			// If I reached EOF, the last byte of the buffer is 0 because i'm reading one extra chars that doesn't exists
			lastLine = contentReadAsString.substring(lastIndex, contentReadAsString.length() - 1);
		} else {
			lastLine = contentReadAsString.substring(lastIndex, contentReadAsString.length());
		}
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
	
	public static List<String> concatList(List<String> list, List<String> toConcat) {	
		List<String> result = new ArrayList<>();
		
		if(toConcat == null && list == null) {
			return result;
		}
		if(list == null || list.size() == 0) {
			return toConcat;
		}
		if(toConcat == null || toConcat.size() == 0) {
			return list;
		}
		
		List<String> toConcatInner = new ArrayList<>();
		toConcatInner.addAll(toConcat);
		List<String> listInner = new ArrayList<>();
		listInner.addAll(list);
		
		String lastLineList = ListUtils.getLastLine(listInner);
		String firstLineToConcat = ListUtils.getFirstLine(toConcatInner);
		if(lastLineList.endsWith("\r")) {
			if(firstLineToConcat.equals("\n")) {
				ListUtils.removeFirstLine(toConcatInner);
				lastLineList = lastLineList.concat(firstLineToConcat);
				ListUtils.setLastLine(listInner, lastLineList);
			}
		} else if(!lastLineList.endsWith("\n")) {
			ListUtils.removeFirstLine(toConcatInner);
			lastLineList = lastLineList.concat(firstLineToConcat);
			ListUtils.setLastLine(listInner, lastLineList);
		}
		
		result.addAll(listInner);
		result.addAll(toConcatInner);
		
		return result;
	}
	
	/**
	 * Method to check if the last line of a list is terminated using extra chars to check it
	 * @param list List to check
	 * @param extraChars number of extra chars used
	 * @return true is last line is terminated otherwise false
	 */
	public static boolean isLastLineTerminated(List<String> allLines, int extraChars) {
		String lastLine = ListUtils.getLastLine(allLines);
		
		if(lastLine.length() == extraChars) {
			/* The last line has the same n of characters of the extra chars read, 
			 * so if i exclude the last line from the list, I'm sure that the last line
			 * is terminated */
			return true;
		}
		
		return false;
	}

	public static boolean isFirstLineTerminated(List<String> allLines) {
		if(allLines.size() > 1) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isFirstLineTerminatedReverse(List<String> allLines, int extraChars) {
		String firstLine = ListUtils.getLastLine(allLines);

		if(firstLine.length() == extraChars) {
			/* The first line has the same n of characters of the extra chars read, 
			 * so if i exclude the last line from the list, I'm sure that the last line
			 * is terminated */
			return true;
		}
		
		return false;
	}
	
	public static boolean isLastLineTerminatedReverse(List<String> allLines) {
		if(allLines.size() > 1) {
			return true;
		}
		
		return false;
	}
}
