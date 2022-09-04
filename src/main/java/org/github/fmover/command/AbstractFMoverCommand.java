package org.github.fmover.command;

import org.github.fmover.Config;
import org.github.fmover.service.FMoverService;
import org.github.fmover.service.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static org.github.fmover.service.FMoverService.getExt;

/**
 * @author iamsinghankit
 */
abstract class AbstractFMoverCommand implements Callable<Integer> {
    private static final String DEFAULT_CONFIG = ".fmover.properties";

    protected static final String COMMENTS = """
            This is a config file for fmover, key and value pair represents directory_path=file_extensions
            """;

    protected Map<String, String> defaultEntry() {
        var entry = new HashMap<String, String>(5);
        entry.put("Images", "png,jpeg,jpg,heic,gif");
        entry.put("Document", "txt,pdf,xlsx,xls,ppt,pptx,doc,docx");
        entry.put("Video", "mkv,mp4,avi");
        entry.put("Compressed", "rar,jar,war,zip,tar");
        entry.put("Application", "dmg,app,exe,deb,rpm");
        return entry;
    }

    protected Path defaultConfig() {
        String parameter = System.getProperty("user.home") + File.separator + DEFAULT_CONFIG;
        Path path = Path.of(parameter);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createFile(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return path;
    }

    protected Function<String, Path> keyMapper(Path baseDir) {
        return (dir) -> {
            if (baseDir == null) {
                return Path.of(dir + File.separator);
            }
            String base = baseDir.toFile().getAbsolutePath() + File.separator;
            return Path.of(base + dir + File.separator);
        };
    }

    protected void move(Path path, FMoverService fMover, List<Config.ConfigMapping<Path, String>> mappings) {
        File file = path.toFile();
        String ext = getExt(file.getName());
        var mapping = mappings.stream().filter(e -> e.isValueExists(ext)).findFirst();
        mapping.ifPresentOrElse(m -> fMover.moveFile(path, m.key()), () -> Logger.log(() -> "No mapping found, ignoring : " + file.getName()));
    }
}
