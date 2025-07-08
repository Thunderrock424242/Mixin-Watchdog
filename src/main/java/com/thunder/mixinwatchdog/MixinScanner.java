package com.thunder.mixinwatchdog;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixins;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class MixinScanner {

    public static void scanAndReport() {
        List<Object> configs = getMixinConfigs();
        if (configs == null) return;

        for (Object config : configs) {
            List<String> mixinClasses = getField(config, "mixinClasses", List.class);
            Set<String> targets = getField(config, "targets", Set.class);

            if (mixinClasses == null || targets == null) continue;

            for (String mixinClass : mixinClasses) {
                String mixinMod = findModOwningClass(mixinClass);

                for (String target : targets) {
                    String targetMod = findModOwningClass(target);

                    if (!mixinMod.equals("unknown") && !targetMod.equals("unknown") && !mixinMod.equals(targetMod)) {
                        System.out.printf(
                                "[MixinWatchdog] Mod '%s' is mixing into '%s' from mod '%s'%n",
                                mixinMod, target, targetMod
                        );
                    }
                }
            }
        }
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
            Field field = Mixins.class.getDeclaredField("configs");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Object> configs = (List<Object>) field.get(null);
            return configs;
        } catch (Exception e) {
            System.err.println("[MixinWatchdog] Failed to access Mixin configs.");
            return null;
        }
    }

    private static String findModOwningClass(String className) {
        for (IModInfo mod : ModList.get().getMods()) {
            String modId = mod.getModId();
            if (className.toLowerCase().contains(modId.toLowerCase())) {
                return modId;
            }
        }
        return "unknown";
    }
}