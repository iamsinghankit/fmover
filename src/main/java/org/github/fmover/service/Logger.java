package org.github.fmover.service;

import java.util.function.Supplier;

/**
 * @author iamsinghankit
 */
public class Logger {

    private static boolean debug = false;

    public static void setDebug(boolean debugFlag) {
        debug = debugFlag;
    }

    public static void log(Supplier<String> message) {
        if (debug) {
            System.out.println(message.get());
        }
    }
}
