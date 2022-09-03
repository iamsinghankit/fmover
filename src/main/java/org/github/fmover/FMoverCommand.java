package org.github.fmover;

import org.github.fmover.service.*;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author iamsinghankit
 */
@Command(name = "fmover",
         mixinStandardHelpOptions = true,
         description = "File mover automatically copies files from source to destination whenever new file is created.",
         version = "fmover v1.0")
public class FMoverCommand implements Callable<Integer> {


    @Parameters(description = "Source directory to watch for changes.",
                paramLabel = "<src-dir>")
    private Path srcDir;

    @Option(names = {"--des"},
            description = "Destination base directory where to move all the changes.",
            paramLabel = "<dest-dir>")
    private Path desDir;


    @Option(names = {"-c", "--config"},
            description = "Config file location.",
            paramLabel = "<config-file>")
    private Path configPath;

    @Option(names = {"-d", "--debug"},
            description = "To print out debug statement.",
            paramLabel = "<debug>")
    private boolean debug;

    @Override
    public Integer call() throws Exception {
        Logger.setDebug(debug);
        var listener = new FMoverService(destDirMapping());
        var watchDir = new WatchDir(srcDir, listener, true);
        watchDir.processEvents();
        return 0;
    }


    private List<DirFileMapping> destDirMapping() throws Exception {
        Config config = null;
        if (configPath == null) {
            config = new Config();
        } else {
            config = new Config(configPath.toFile().getAbsolutePath());
        }

        Set<Map.Entry<Object, Object>> entries = config.get().entrySet();
        List<DirFileMapping> mappings = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries) {
            var mapping = new DirFileMapping(getBaseDir(entry.getKey().toString()), List.of(entry.getValue().toString().split(",")));
            mappings.add(mapping);
        }
        return mappings;
    }


    private Path getBaseDir(String ext) {
        if (desDir == null) {
            return Path.of(ext + File.separator);
        }
        String base = desDir.toFile().getAbsolutePath() + File.separator;
        return Path.of(base + ext + File.separator);
    }
}
