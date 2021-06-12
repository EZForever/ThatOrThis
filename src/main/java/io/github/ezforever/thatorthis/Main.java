package io.github.ezforever.thatorthis;

import io.github.ezforever.thatorthis.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;

class Main {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/main");

    static void run() {
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
            case 1 -> "Loading mods from {} additional directory: {}";
            default -> "Loading mods from {} additional directories: {}";
        };
        LOGGER.info("[ThatOrThis] " + modDirText,
                modDirs.size(), String.join(", ", modDirs.keySet()));

        FabricInternals.hook(modDirs);
    }
}
