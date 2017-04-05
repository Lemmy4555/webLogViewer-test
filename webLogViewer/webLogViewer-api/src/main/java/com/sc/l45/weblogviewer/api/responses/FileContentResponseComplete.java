package com.sc.l45.weblogviewer.api.responses;

import java.util.List;

public class FileContentResponseComplete extends FileContentResponse{
    public String rowsInFile;
    
    @SuppressWarnings("unused")
    private FileContentResponseComplete() {
        super();
    }
    public FileContentResponseComplete(List<String> readContent, String rowsRead, String fileSize, String encoding, String rowsInFile) {
        super(readContent, rowsRead, fileSize, encoding);
        this.rowsInFile = rowsInFile;
    }
}
