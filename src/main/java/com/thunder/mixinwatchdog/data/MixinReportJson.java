package com.thunder.mixinwatchdog.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MixinReportJson {
    public String timestamp = new Date().toString();
    public List<MixinReportEntry> entries = new ArrayList<>();
    public Map<String, Set<String>> conflicts = new LinkedHashMap<>();

    public void writeToFile(Path path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("[MixinWatchdog] Failed to write JSON report.");
            e.printStackTrace();
        }
    }
}