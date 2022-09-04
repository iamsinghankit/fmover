package org.github.fmover.service;

import java.nio.file.Path;

/**
 * @author iamsinghankit
 */
public interface FMoverService {

    static String getExt(String fileName) {
        String ext = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            ext = fileName.substring(index + 1);
        }
        return ext;
    }

    static String removeExt(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    void moveFile(Path file, Path desDir);

    enum CopyOption {
        REPLACE, RENAME, FAIL
    }
}
