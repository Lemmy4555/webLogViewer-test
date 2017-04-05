package com.sc.l45.weblogviewer.api.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import com.sc.l45.weblogviewer.api.constants.FileConstants;

public class FileUtils {
    public static List<String> readLines(File file, Charset encoding) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(inputStream, FileConstants.ENCODING.name());
                BufferedReader br = new BufferedReader(isr);) {
            List<String> content = new ArrayList<>();
            
            String line;
            while((line = br.readLine()) != null) {
                content.add(line);
            }
            
            return content;
        }
    }
    
    /**
     * Metodo specifico per Windows/others... per ottenere una lista di file di un folder in cui e possibile navigare
     * @return
     */
    public static File[] getLegalFileList(File folder) {
        return folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(SystemUtils.IS_OS_WINDOWS) {
                    Path path = Paths.get(file.getAbsolutePath());
                    DosFileAttributes dfa;
                    try {
                        dfa = Files.readAttributes(path, DosFileAttributes.class);
                    } catch (IOException e) {
                        return !file.isHidden();
                    }
                    return (!dfa.isHidden() && !dfa.isSystem());
                } else {
                    return !folder.isHidden();
                }
                
            }
        });
    }
}
