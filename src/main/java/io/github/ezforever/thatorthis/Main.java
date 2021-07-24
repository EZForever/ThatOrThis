package io.github.ezforever.thatorthis;

import io.github.ezforever.thatorthis.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        String modDirText;
        switch (modDirs.size()) {
            case 0:
                modDirText = "Loading mods from {} additional directories";
                break;
            case 1:
                modDirText = "Loading mods from {} additional directory:\n{}";
                break;
            default:
                modDirText = "Loading mods from {} additional directories:\n{}";
                break;
        }
        LOGGER.info("[ThatOrThis] " + modDirText,
                modDirs.size(), modDirs.keySet().stream().map((String x) -> "\t- " + x).collect(Collectors.joining("\n")));

        FabricInternals.hook(modDirs);
    }
}
