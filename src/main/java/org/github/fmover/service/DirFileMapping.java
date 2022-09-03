package org.github.fmover.service;

import java.nio.file.Path;
import java.util.List;

/**
 * @author iamsinghankit
 */
public record DirFileMapping(Path dir, List<String> ext) {


    public boolean checkExt(String actualExt) {
        return ext.stream().anyMatch(e -> e.equalsIgnoreCase(actualExt));
    }
}
