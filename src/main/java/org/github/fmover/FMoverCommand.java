package org.github.fmover;

import org.github.fmover.service.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

import static org.github.fmover.service.FMoverService.getExt;

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

    @Option(names = {"--co", "--copy-option"},
            description = "Action to take if conflict occurs while moving files - possible values: REPLACE,RENAME,FAIL",
            defaultValue = "RENAME",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private FMoverService.CopyOption copyOption;


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
        var watchDirListener = new WatchDirFileMover(new DefaultFMoverService(copyOption));
        var watchDir = new WatchDir(srcDir, watchDirListener, true);
        watchDir.processEvents();
        return 0;
    }


    private class WatchDirFileMover implements WatchDir.WatchDirListener {

        private final FMoverService fMoverService;
        private final List<DirFileMapping> dirFileMappings;

        private WatchDirFileMover(FMoverService fMoverService) throws Exception {
            this.fMoverService = fMoverService;
            this.dirFileMappings = destDirMapping();
        }

        private List<DirFileMapping> destDirMapping() throws Exception {
            Config config = null;
            if (configPath == null) {
                config = new Config();
            } else {
                config = new Config(configPath.toFile().getAbsolutePath());
            }

            Set<Map.Entry<Object, Object>> entries = config.get().entrySet();
            List<DirFileMapping> mappings = new ArrayList<>(entries.size());
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

        @Override
        public void fileCreated(Path path) {
            File file = path.toFile();
            String ext = getExt(file.getName());
            Optional<DirFileMapping> mapping = dirFileMappings.stream().filter(e -> e.checkExt(ext)).findFirst();
            mapping.ifPresentOrElse(m -> fMoverService.moveFile(path, m.dir()), () -> Logger.log(() -> "No mapping found, ignoring : " + file.getName()));
        }

        record DirFileMapping(Path dir, List<String> ext) {

            public boolean checkExt(String actualExt) {
                return ext.stream().anyMatch(e -> e.equalsIgnoreCase(actualExt));
            }
        }
    }
}
