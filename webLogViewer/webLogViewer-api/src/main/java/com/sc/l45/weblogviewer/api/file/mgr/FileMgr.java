package com.sc.l45.weblogviewer.api.file.mgr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
public class FileMgr {
    public void checkFile(String filePath) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        checkFilePath(file);
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
