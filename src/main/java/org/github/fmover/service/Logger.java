package org.github.fmover.service;

import java.util.function.Supplier;

/**
 * @author iamsinghankit
 */
public class Logger {

    private static boolean debug = false;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void setDebug(boolean debugFlag) {
        debug = debugFlag;
    }

    public static void log(Supplier<String> message) {
        if (debug) {
            System.out.println(message.get());
        }
    }
}
