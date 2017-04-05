package com.sc.l45.weblogviewer.api.file.mgr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import com.sc.l45.weblogviewer.api.constants.FileConstants;
import com.sc.l45.weblogviewer.api.exceptions.FileTooBigException;

public class FileMgr {
    public void checkFile(String filePath) throws FileNotFoundException, IOException, FileTooBigException {
        File file = new File(filePath);
        checkFilePath(file);
        checkFileSize(file);
    }
    
    public void checkFileSize(File file) throws IOException, FileTooBigException {
        BasicFileAttributes fileAttr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if(fileAttr.size() > FileConstants.MAX_SUPPORTED_FILE_SIZE) {
            throw new FileTooBigException(file, fileAttr.size(), FileConstants.MAX_SUPPORTED_FILE_SIZE);
        }
    }
    
    public void checkFilePath(File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " non e un file o una directory esistente");
        }
        if (!file.canRead()) {
            if (!file.isFile()) {
                throw new IOException("La directory " + file.getAbsolutePath() + " non puo essere letta");
            }
        }
    }
    
}
