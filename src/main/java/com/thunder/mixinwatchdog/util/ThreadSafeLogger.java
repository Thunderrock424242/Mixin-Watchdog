package com.thunder.mixinwatchdog.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadSafeLogger {

    private static final Queue<String> logs = new ConcurrentLinkedQueue<>();

    public static void log(String message) {
        System.out.println(message);
        logs.add(message);
    }

    public static void flushToFile() {
        File logFile = new File("logs/mixin_watchdog.log");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, false))) {
            writer.write("[Mixin Watchdog Log]\n");
            for (String line : logs) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[MixinWatchdog] Failed to write log file.");
            e.printStackTrace();
        }
    }
}