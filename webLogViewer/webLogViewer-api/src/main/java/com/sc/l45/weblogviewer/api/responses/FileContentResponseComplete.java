package com.sc.l45.weblogviewer.api.responses;

import java.util.ArrayList;
import java.util.List;

import com.sc.l45.weblogviewer.api.constants.FileConstants;

public class FileContentResponseComplete extends FileContentResponse{
    public String rowsInFile;
    
    @SuppressWarnings("unused")
    private FileContentResponseComplete() {
        this(new ArrayList<>(), "0", "0", FileConstants.ENCODING.toString(), "0", "0");
    }
    
    public FileContentResponseComplete(List<String> readContent, String rowsRead, String size, String encoding, String currentPointer, String rowsInFile) {
        super(readContent, rowsRead, size, encoding, currentPointer);
        this.rowsInFile = rowsInFile;
    }
}
