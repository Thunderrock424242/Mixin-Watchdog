package com.thunder.mixinwatchdog.data;

public class MixinReportEntry {
    public String mixinMod;
    public String mixinClass;
    public String mixinConfig;
    public String targetClass;
    public String targetMod;

    public MixinReportEntry(String mixinMod, String mixinClass, String mixinConfig, String targetClass, String targetMod) {
        this.mixinMod = mixinMod;
        this.mixinClass = mixinClass;
        this.mixinConfig = mixinConfig;
        this.targetClass = targetClass;
        this.targetMod = targetMod;
    }

    public String toLogLine() {
        return String.format(
                "[MixinWatchdog] Mod '%s' mixes '%s' (from %s) into '%s' (from mod '%s')",
                mixinMod, mixinClass, mixinConfig, targetClass, targetMod
        );
    }
}