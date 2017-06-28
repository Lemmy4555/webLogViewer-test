package com.sc.l45.weblogviewer.api.mgr.readers;

import java.util.ArrayList;
import java.util.List;

class ReadLinesResult {
	boolean isFirstLineFull = true;
	List<String> linesRead = new ArrayList<>();
	long pointer = 0;
}
