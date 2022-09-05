package org.github.fmover.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author iamsinghankit
 */
public class DefaultFMoverService implements FMoverService {

    private final MoveOption moveOption;

    public DefaultFMoverService(MoveOption moveOption) {
        this.moveOption = moveOption;
    }


    @Override
    public void moveFile(Path originalFile, Path dir) {
        try {
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                Files.createDirectories(dir);
            }
            Logger.log(() -> "Moving file %s to %s ".formatted(originalFile, dir));
            switch (moveOption) {
                case REPLACE -> Files.move(originalFile, dir.resolve(originalFile.getFileName()), REPLACE_EXISTING);
                case RENAME -> Files.move(originalFile, dir.resolve(getRenamePath(originalFile, dir).getFileName()));
                case FAIL -> Files.move(originalFile, dir.resolve(originalFile.getFileName()));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Path getRenamePath(Path originalFile, Path dir) {
        String fullPath = originalFile.getParent().toString() + File.separator;
        String name = originalFile.toFile().getName();
        Path targetFile = originalFile;
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (Files.exists(dir.resolve(targetFile.getFileName()))) {
                name = name.replace("_copy_" + (i - 1), "");
                name = FMoverService.removeExt(name) + "_copy_" + i + "." + FMoverService.getExt(name);
                targetFile = Path.of(fullPath + name);
            } else {
                break;
            }
        }
        return targetFile;
    }
}
