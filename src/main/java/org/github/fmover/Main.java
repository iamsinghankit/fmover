package org.github.fmover;

import picocli.CommandLine;

/**
 * @author iamsinghankit
 */
public class Main {
    public static void main(String[] args) {
        var commandLine = new CommandLine(new FMoverCommand());
        commandLine.setExecutionExceptionHandler(new GlobalExceptionHandler());
        commandLine.execute(args);
    }
}
