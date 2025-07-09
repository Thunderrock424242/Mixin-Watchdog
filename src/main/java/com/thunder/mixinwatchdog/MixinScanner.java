package com.thunder.mixinwatchdog;

import com.thunder.mixinwatchdog.data.MixinReportEntry;
import com.thunder.mixinwatchdog.data.MixinReportJson;
import com.thunder.mixinwatchdog.util.ModResolver;
import com.thunder.mixinwatchdog.util.ThreadSafeLogger;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class MixinScanner {

    private static final Map<String, Set<String>> classMixers = new ConcurrentHashMap<>();
    private static final Queue<MixinReportEntry> reportEntries = new ConcurrentLinkedQueue<>();

    public static void scanAndReport() {
        List<Object> configs = getMixinConfigs();
        if (configs == null) return;

        for (Object config : configs) {
            List<String> mixinClasses = getField(config, "mixinClasses", List.class);
            Set<String> targets = getField(config, "targets", Set.class);
            String configName = getField(config, "name", String.class);

            if (mixinClasses == null || targets == null) continue;

            for (String mixinClass : mixinClasses) {
                String mixinMod = ModResolver.findModOwningClass(mixinClass);

                for (String target : targets) {
                    String targetMod = ModResolver.findModOwningClass(target);

                    MixinReportEntry entry = new MixinReportEntry(mixinMod, mixinClass, configName, target, targetMod);
                    reportEntries.add(entry);
                    ThreadSafeLogger.log(entry.toLogLine());

                    classMixers.computeIfAbsent(target, k -> new HashSet<>()).add(mixinMod);
                }
            }
        }

        detectConflicts();
        saveJsonReport();
        ThreadSafeLogger.flushToFile();
    }

    private static void detectConflicts() {
        for (Map.Entry<String, Set<String>> entry : classMixers.entrySet()) {
            if (entry.getValue().size() > 1) {
                String log = "[MixinWatchdog] WARNING: Multiple mods mix into " + entry.getKey() + ":\n - " +
                        String.join("\n - ", entry.getValue());
                ThreadSafeLogger.log(log);
            }
        }
    }

    private static void saveJsonReport() {
        MixinReportJson json = new MixinReportJson();
        json.entries.addAll(reportEntries);
        json.conflicts.putAll(classMixers.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        json.writeToFile(Path.of("logs/mixin_watchdog.json"));
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object obj, String fieldName, Class<T> type) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object val = f.get(obj);
            if (type.isInstance(val)) return (T) val;
        } catch (Exception ignored) {}
        return null;
    }

    private static List<Object> getMixinConfigs() {
        try {
            Field field = Mixins.class.getDeclaredField("config");
            field.setAccessible(true);
            return (List<Object>) field.get(null);
        } catch (Exception e) {
            ThreadSafeLogger.log("[MixinWatchdog] Failed to access Mixin config.");
            return null;
        }
    }
}