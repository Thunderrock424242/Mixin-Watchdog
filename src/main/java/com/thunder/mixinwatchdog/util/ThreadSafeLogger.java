package com.thunder.mixinwatchdog.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles writing tracked mixin information only to logs/mixin_watchdog.log
 */
public class ThreadSafeLogger {

    private static final Queue<String> logs = new ConcurrentLinkedQueue<>();
    private static final File LOG_FILE = new File("logs/mixin_watchdog.log");

    public static void log(String message) {
        logs.add(message);
    }

    public static void flushToFile() {
        if (logs.isEmpty()) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, false))) {
            writer.write("[Mixin Watchdog Log]\n");
            for (String line : logs) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            // Only for debugging file issues during development
            e.printStackTrace();
        }
    }
}