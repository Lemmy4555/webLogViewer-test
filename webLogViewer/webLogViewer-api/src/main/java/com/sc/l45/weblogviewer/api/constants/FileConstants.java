package com.sc.l45.weblogviewer.api.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class FileConstants {
    /**
     * Max file's read text size 1MB.
     */
    public final static int MAX_READABLE_TEXT_SIZE = 1048576;
    
    /**
     * Encoding supported by webLogViewer.
     */
    public final static Charset ENCODING = StandardCharsets.UTF_8;
    
    /**
     * Pattern used to find line terminations in file.
     */
    public final static Pattern END_LINE_PATTERN = Pattern.compile("\r\n|\r|\n");
}
