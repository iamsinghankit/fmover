package org.github.fmover.command;

import org.github.fmover.Config;
import org.github.fmover.service.DefaultFMoverService;
import org.github.fmover.service.FMoverService;
import org.github.fmover.service.Logger;
import org.github.fmover.service.WatchDir;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

/**
 * @author iamsinghankit
 */
@Command(name = "poll",
         mixinStandardHelpOptions = true,
         description = "File mover poll automatically copies files from source to destination by polling for changes.")
public class FMoverPollerCommand extends AbstractFMoverCommand {


    @Option(names = {"-c", "--config"},
            description = "Config file location.",
            paramLabel = "<config-file>")
    private final Path configPath = defaultConfig();

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

    @Option(names = {"-d", "--debug"},
            description = "To print out debug statement.",
            paramLabel = "<debug>")
    private boolean debug;


    @Override
    public Integer call() throws Exception {
        Logger.setDebug(debug);
        var watchDir = new WatchDir(srcDir, listener(), true);
        watchDir.processEvents();
        return 0;
    }

    private WatchDir.WatchDirListener listener() throws Exception {
        var config = new Config<Path, String>(configPath, COMMENTS, defaultEntry());
        return (file) -> move(file, new DefaultFMoverService(copyOption), config.getMapping(keyMapper(baseDir)));
    }
}
