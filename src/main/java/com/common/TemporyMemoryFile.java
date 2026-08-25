package com.common;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author datlt
 */
@Getter
@Setter
public class TemporyMemoryFile {

    private String path = StringUtils.EMPTY;
    private String fileName = StringUtils.EMPTY;
    private String extention = StringUtils.EMPTY;

    public TemporyMemoryFile() {
    }

    public TemporyMemoryFile(String pFileName, String pPath, String pExtention) {
        this.path = pPath;
        this.fileName = pFileName;
        this.extention = pExtention;
    }

    public void write () {
        
        if (exists()) {
            
        }
    }

    public File getFile () {
        if (exists()) return getPath().toFile();
        return null;
    }

    public boolean exists () {
        if (StringUtils.isAnyBlank(path, fileName, extention)) return false;
        return getPath().toFile().exists();
    }

    public Path getPath () {
        return Paths.get(path, fileName, extention);
    }

    public String getPathString() {
        return Paths.get(path, fileName, extention).toString();
    }
}
