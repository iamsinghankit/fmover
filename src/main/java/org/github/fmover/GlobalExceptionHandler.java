package org.github.fmover;

import picocli.CommandLine;

/**
 * @author iamsinghankit
 */
public class GlobalExceptionHandler implements CommandLine.IExecutionExceptionHandler {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Override
    public int handleExecutionException(Exception ex, CommandLine commandLine, CommandLine.ParseResult parseResult) throws Exception {
        System.err.println(ANSI_RED + ex.getMessage() + ANSI_RESET);
        return 0;
    }


}
