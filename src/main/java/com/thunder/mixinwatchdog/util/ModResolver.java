package com.thunder.mixinwatchdog.util;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

public class ModResolver {

    public static String findModOwningClass(String className) {
        return ModList.get().getMods().stream()
                .map(IModInfo::getModId)
                .filter(modId -> className.toLowerCase().contains(modId.toLowerCase()))
                .findFirst()
                .orElse("unknown");
    }
}