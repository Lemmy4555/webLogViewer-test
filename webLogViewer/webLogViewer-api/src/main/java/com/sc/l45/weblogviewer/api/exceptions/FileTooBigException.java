package com.sc.l45.weblogviewer.api.exceptions;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.sc.l45.weblogviewer.api.constants.FileConstants;

/**
 * Eccezione creata per indicare che il file in oggetto e troppo grande e non puo essere gestito.
 * La grandezza massima del file e specificata qua: {@link FileConstants#MAX_SUPPORTED_FILE_SIZE}
 */
public class FileTooBigException extends Exception{
    private static final long serialVersionUID = 1L;
    
    public FileTooBigException(File file, long l, long maxSizeInBytes) {
        super(buildMessage(file, l, maxSizeInBytes));
    }
    
    private static String buildMessage(File file, long l, long maxSizeInBytes) {
        String format = "Il file %s (%d) non deve superare le dimensioni massime supportare (%d)";
        String readableSize = FileUtils.byteCountToDisplaySize(l);
        String readableMaxSize = FileUtils.byteCountToDisplaySize(maxSizeInBytes);
        return String.format(format, file.getAbsolutePath(), readableSize, readableMaxSize);
    }
}
