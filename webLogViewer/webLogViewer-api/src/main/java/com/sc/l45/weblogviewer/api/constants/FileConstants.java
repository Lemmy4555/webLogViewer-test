package com.sc.l45.weblogviewer.api.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileConstants {
    /**
     * La grandezza massima supportata e di 100MB
     */
    public final static int MAX_SUPPORTED_FILE_SIZE = 1000000000;
    
    /**
     * Encoding supportato da webLogViewer
     */
    public final static Charset ENCODING = StandardCharsets.UTF_8;
}
