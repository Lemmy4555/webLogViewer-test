package com.sc.l45.weblogviewer.api.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileConstants {
    /**
     * Max file's read text size 100KB
     */
    public final static int MAX_READABLE_TEXT_SIZE = 102400;
    
    /**
     * Encoding supportato da webLogViewer
     */
    public final static Charset ENCODING = StandardCharsets.UTF_8;
}
