package org.github.fmover.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author iamsinghankit
 */
public class FMoverService implements WatchDir.WatchDirListener {

    private final List<DirFileMapping> dirFileMappings;
    private final CopyOption copyOption;

    public FMoverService(List<DirFileMapping> dirFileMappings, CopyOption copyOption) {
        this.dirFileMappings = dirFileMappings;
        this.copyOption = copyOption;
    }

    @Override
    public void fileCreated(Path path) {
        File file = path.toFile();
        String ext = getExt(file.getName());
        Optional<DirFileMapping> mapping = dirFileMappings.stream().filter(e -> e.checkExt(ext)).findFirst();
        mapping.ifPresentOrElse(m -> moveFile(m, path), () -> Logger.log(() -> "No mapping found, ignoring : " + file.getName()));
    }


    private void moveFile(DirFileMapping mapping, Path originalFile) {
        Path dir = mapping.dir();
        try {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.createDirectories(dir);
            }
            Logger.log(() -> "Moving file %s to %s ".formatted(originalFile, dir));
            switch (copyOption) {
                case REPLACE -> Files.move(originalFile, dir.resolve(originalFile.getFileName()), REPLACE_EXISTING);
                case RENAME -> {
                    String name = originalFile.toFile().getName();
                    String newName = removeExt(name) + Instant.now().toEpochMilli() + "." + getExt(name);
                    Files.move(originalFile, dir.resolve(originalFile.getFileName().resolveSibling(newName)));
                }
                case FAIL -> Files.move(originalFile, dir.resolve(originalFile.getFileName()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private String getExt(String fileName) {
        String ext = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            ext = fileName.substring(index + 1);
        }
        return ext;
    }

    private String removeExt(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    public enum CopyOption {
        REPLACE, RENAME, FAIL
    }
}
