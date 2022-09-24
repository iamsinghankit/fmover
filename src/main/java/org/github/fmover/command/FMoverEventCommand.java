package org.github.fmover.command;

import org.github.fmover.Config;
import org.github.fmover.service.DefaultFMoverService;
import org.github.fmover.service.FMoverService;
import org.github.fmover.service.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;

/**
 * @author iamsinghankit
 */
@Command(name = "event",
         mixinStandardHelpOptions = true,
         description = "File mover event automatically copies files from source to destination using event triggered by OS.",
         version = "fmover v2.0")
public class FMoverEventCommand extends AbstractFMoverCommand {

    @Option(names = {"-c", "--config"},
            description = "Config file location.",
            paramLabel = "<config-file>")
    private final Path configPath = defaultConfig();

    @Parameters(description = "File which needs to be moved.",
                paramLabel = "<file>")
    private Path file;

    @Option(names = {"-b", "--base-dir"},
            description = "Destination base directory where to move all the changes.",
            paramLabel = "<dest-base-dir>")
    private Path baseDir;

    @Option(names = {"-m", "--move-option"},
            description = "Action to take if conflict occurs while moving files - possible values: REPLACE,RENAME,FAIL.",
            defaultValue = "RENAME",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private FMoverService.MoveOption moveOption;

    @Option(names = {"-d", "--debug"},
            description = "To print out debug statement.",
            paramLabel = "<debug>")
    private boolean debug;


    @Option(names = {"-i", "--ignore-suffix"},
            description = "Configured suffix if found in filename, file will be ignored.",
            defaultValue = "_i",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String ignoreSuffix;

    @Override
    public Integer call() throws Exception {
        Logger.setDebug(debug);
        String fname = FMoverService.removeExt(file.toFile().getName());
        //ignore the file
        if (fname.endsWith(ignoreSuffix)) {
            return 0;
        }
        var config = new Config<Path, String>(configPath, COMMENTS, defaultEntry());
        move(file, new DefaultFMoverService(moveOption), config.getMapping(pathMapper(baseDir)));
        return 0;
    }


}
