package org.github.fmover;

import org.github.fmover.service.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static org.github.fmover.service.FMoverService.getExt;

/**
 * @author iamsinghankit
 */
@Command(name = "poll",
         mixinStandardHelpOptions = true,
         description = "File mover poll automatically copies files from source to destination whenever new file is created by polling for changes.")
public class FMoverPollerCommand implements Callable<Integer> {
    private static final String DEFAULT_CONFIG = ".fmover.properties";

    @Parameters(description = "Source directory to watch for changes.",
                paramLabel = "<src-dir>")
    private Path srcDir;

    @Option(names = {"-b", "--base-dir"},
            description = "Destination base directory where to move all the changes.",
            paramLabel = "<dest-base-dir>")
    private Path baseDir;

    @Option(names = {"--co", "--copy-option"},
            description = "Action to take if conflict occurs while moving files - possible values: REPLACE,RENAME,FAIL",
            defaultValue = "RENAME",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private FMoverService.CopyOption copyOption;


    @Option(names = {"-c", "--config"},
            description = "Config file location.",
            paramLabel = "<config-file>")
    private final Path configPath = defaultConfig();

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

    private Path defaultConfig() {
        String parameter = System.getProperty("user.home") + File.separator + DEFAULT_CONFIG;
        Path path = Path.of(parameter);
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createFile(path);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to create config this should not be happening, report this bug to developer");
            }
        }
        return path;

    }

    private class WatchDirFileMover implements WatchDir.WatchDirListener {

        private final FMoverService fMoverService;
        private final List<Config.DirFileMapping> dirFileMappings;

        private WatchDirFileMover(FMoverService fMoverService) throws Exception {
            this.fMoverService = fMoverService;
            this.dirFileMappings = new Config(configPath, baseDir).get();
        }


        @Override
        public void fileCreated(Path path) {
            File file = path.toFile();
            String ext = getExt(file.getName());
            Optional<Config.DirFileMapping> mapping = dirFileMappings.stream().filter(e -> e.checkExt(ext)).findFirst();
            mapping.ifPresentOrElse(m -> fMoverService.moveFile(path, m.dir()), () -> Logger.log(() -> "No mapping found, ignoring : " + file.getName()));
        }


    }
}
