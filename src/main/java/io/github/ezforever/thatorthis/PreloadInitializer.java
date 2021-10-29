package io.github.ezforever.thatorthis;

import io.github.ezforever.thatorthis.config.Config;
import io.github.ezforever.thatorthis.internal.Bootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class PreloadInitializer {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/initializer");

    public void onInitializing() {
        Config config;
        try {
            config = Config.getInstance();
        } catch(RuntimeException e) {
            LOGGER.error("Additional mods loading skipped due to config errors", e);
            return;
        }

        Map<String, Set<String>> modDirs = config.resolve();

        String modDirText = switch (modDirs.size()) {
            case 0 -> "Loading mods from {} additional directories";
            case 1 -> "Loading mods from {} additional directory:\n{}";
            default -> "Loading mods from {} additional directories:\n{}";
        };
        LOGGER.info("[ThatOrThis] " + modDirText,
                modDirs.size(), modDirs.keySet().stream().map((String x) -> "\t- " + x).collect(Collectors.joining("\n")));

        Bootstrap.installInjector(modDirs);
    }
}
