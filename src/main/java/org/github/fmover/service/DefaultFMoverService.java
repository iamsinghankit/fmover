package org.github.fmover.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author iamsinghankit
 */
public class DefaultFMoverService implements FMoverService {

    private final CopyOption copyOption;

    public DefaultFMoverService(CopyOption copyOption) {
        this.copyOption = copyOption;
    }


    @Override
    public void moveFile(Path originalFile, Path dir) {
        try {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.createDirectories(dir);
            }
            Logger.log(() -> "Moving file %s to %s ".formatted(originalFile, dir));
            switch (copyOption) {
                case REPLACE -> Files.move(originalFile, dir.resolve(originalFile.getFileName()), REPLACE_EXISTING);
                case RENAME -> {
                    String name = originalFile.toFile().getName();
                    String newName = FMoverService.removeExt(name) + Instant.now().toEpochMilli() + "." + FMoverService.getExt(name);
                    Files.move(originalFile, dir.resolve(originalFile.getFileName().resolveSibling(newName)));
                }
                case FAIL -> Files.move(originalFile, dir.resolve(originalFile.getFileName()));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
