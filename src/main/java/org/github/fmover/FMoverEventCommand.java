package org.github.fmover;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * @author iamsinghankit
 */
@Command(name = "event",
         mixinStandardHelpOptions = true,
         description = "File mover event automatically copies files from source to destination whenever new file is created using event triggered by OS.")
public class FMoverEventCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return null;
    }
}
