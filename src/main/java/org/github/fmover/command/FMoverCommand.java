package org.github.fmover.command;

import picocli.CommandLine.Command;

/**
 * @author iamsinghankit
 */
@Command(name = "fmover",
         mixinStandardHelpOptions = true,
         subcommands = {FMoverEventCommand.class, FMoverPollerCommand.class},
         description = "File mover automatically copies files from source to destination whenever new file is created.",
         version = "fmover v1.0")
public class FMoverCommand  {

}
