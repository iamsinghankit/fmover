package org.github.fmover;

import org.github.fmover.command.FMoverCommand;
import org.github.fmover.service.Logger;
import picocli.CommandLine;

/**
 * @author iamsinghankit
 */
public class Main {

    public static void main(String[] args) {
        var commandLine = new CommandLine(new FMoverCommand());
        commandLine.setExecutionExceptionHandler(globalExceptionHandler());
        commandLine.execute(args);
    }

    private static CommandLine.IExecutionExceptionHandler globalExceptionHandler() {
        return (ex, commandLine, parseResult) -> {
            System.out.println(Logger.ANSI_RED + ex.getMessage() + Logger.ANSI_RESET);
            return 0;
        };
    }


}

