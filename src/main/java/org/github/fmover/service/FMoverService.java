package org.github.fmover.service;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author iamsinghankit
 */
public interface FMoverService {

    void moveFile(Path file, Path desDir)throws IOException;

    enum CopyOption {
        REPLACE, RENAME, FAIL
    }
}
