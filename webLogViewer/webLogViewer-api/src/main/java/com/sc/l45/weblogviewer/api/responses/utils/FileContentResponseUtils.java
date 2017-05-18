package com.sc.l45.weblogviewer.api.responses.utils;

import java.util.Iterator;

import com.sc.l45.weblogviewer.api.responses.FileContentResponse;

public class FileContentResponseUtils {
	public static String getRowsAsString(FileContentResponse fcr) {
	    StringBuilder sb = new StringBuilder();
	    Iterator<String> i = fcr.readContent.iterator();
	    while(i.hasNext()) {
	        sb.append(i.next());
	    }
	    return sb.toString();
	}
}
