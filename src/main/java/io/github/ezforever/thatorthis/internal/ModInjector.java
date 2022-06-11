package io.github.ezforever.thatorthis.internal;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.discovery.*;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import net.fabricmc.loader.impl.util.SystemProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ModInjector {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/injector");

    private final Map<String, Set<String>> modDirs;
    private List<ModContainerImpl> mods;
    private ModDiscoverer discoverer;
    private Map<String, Set<ModCandidate>> envDisabledMods = new HashMap<>();

    public ModInjector(Map<String, Set<String>> modDirs) {
        this.modDirs = Collections.unmodifiableMap(modDirs);
    }

    @SuppressWarnings("unused") // Used in Bootstrap
    public void install() {
        try {
            this.mods = Util.cast(Util.modsField.get(Util.loader));
            Util.modsField.set(Util.loader, ModContainerImplListProxy.create(this.mods, this::trigger));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Install mods hook failed", e);
        }
    }

    private void trigger() {
        try {
            Util.modsField.set(Util.loader, this.mods);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Uninstall mods hook failed", e);
        }

        Collection<ModCandidate> candidates;
        candidates = this.discover();
        candidates = this.resolve(candidates);

        assert candidates != null;
        String modText = switch (candidates.size()) {
            case 0 -> "Loading {} additional mods";
            case 1 -> "Loading {} additional mod:\n{}";
            default -> "Loading {} additional mods:\n{}";
        };

        LOGGER.info("[ThatOrThis] " + modText, candidates.size(), candidates.stream()
                .map((ModCandidate candidate) -> String.format("\t- %s@%s", candidate.getId(), candidate.getVersion().getFriendlyString()))
                .collect(Collectors.joining("\n")));

        this.inject(candidates);
        this.fixup(candidates);
    }

    private Collection<ModCandidate> discover() {
        this.discoverer = new ModDiscoverer(new VersionOverrides(), new DependencyOverrides(Util.loader.getConfigDir()));

        // Include loaded mods for resolving dependencies
        discoverer.addCandidateFinder(new ThatOrThisLoadedModCandidateFinder(this.mods));

        this.modDirs.forEach((String modDir, Set<String> blacklist) -> {
            Path path = Util.getModsDir().resolve(modDir);
            if (Files.exists(path) && Files.isDirectory(path)) {
                DirectoryModCandidateFinder finder;
                if (blacklist == null) {
                    finder = new DirectoryModCandidateFinder(path, Util.loader.isDevelopmentEnvironment());
                } else {
                    finder = new ThatOrThisDirectoryModCandidateFinder(path, Util.loader.isDevelopmentEnvironment(), (ModCandidate candidate) -> {
                        boolean blacklisted = blacklist.contains(candidate.getId());
                        if (blacklisted)
                            LOGGER.debug("Skipping mod {} as per user request", candidate.getId());
                        return !blacklisted;
                    });
                }
                discoverer.addCandidateFinder(finder);
            } else {
                LOGGER.warn("Skipping missing/invalid directory: " + modDir);
            }
        });

        try {
            envDisabledMods = new HashMap<>();
            return discoverer.discoverMods(Util.loader, envDisabledMods);
        } catch (ModResolutionException e) {
            FabricGuiEntry.displayCriticalError(e, true);
            return null; // Never reached
        }
    }

    private Collection<ModCandidate> resolve(Collection<ModCandidate> candidates) {
        Set<String> loadedMods = mods.stream()
                .map((ModContainerImpl container) -> container.getMetadata().getId())
                .collect(Collectors.toSet());
        try {
            candidates = ModResolver.resolve(candidates, Util.loader.getEnvironmentType(), envDisabledMods).stream()
                    .filter((ModCandidate candidate) -> !loadedMods.contains(candidate.getId()))
                    .collect(Collectors.toList());
        } catch (ModResolutionException e) {
            FabricGuiEntry.displayCriticalError(e, true);
            return null; // Never reached
        }

        if (Util.loader.isDevelopmentEnvironment() && System.getProperty(SystemProperties.REMAP_CLASSPATH_FILE) != null) {
            Path cacheDir = Util.loader.getGameDir().resolve(FabricLoaderImpl.CACHE_DIR_NAME);
            RuntimeModRemapper.remap(candidates, cacheDir.resolve("tmp"), cacheDir.resolve("processedMods"));
        }

        // XXX: Dev-time shuffling & "load late" options are not respected for now

        return candidates;
    }

    private void inject(Collection<ModCandidate> candidates) {
        for (ModCandidate candidate : candidates) {
            try {
                Util.addModMethod.invoke(Util.loader, candidate);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException("Failed to inject mod into Fabric Loader", e);
            }
            candidate.getPaths().forEach(Util.launcher::addToClassPath);
        }
    }

    private void fixup(Collection<ModCandidate> candidates) {
        // NOTE: Fix here after injecting all mods, so mods that "ping" other mods (e.g. GrossFabricHacks) can have a good time
        Map<String, LanguageAdapter> adapterMap;
        try {
            adapterMap = Util.cast(Util.adapterMapField.get(Util.loader));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to fix LanguageAdapter mods", e);
        }

        for (ModCandidate candidate : candidates) {
            Map<String, String> definitions = candidate.getMetadata().getLanguageAdapterDefinitions();
            if (definitions.isEmpty())
                continue;

            LOGGER.warn("LanguageAdapter found in mod {}! Trying to fix", candidate.getId());
            for (Map.Entry<String, String> entry : definitions.entrySet()) {
                if (adapterMap.containsKey(entry.getKey()))
                    throw new RuntimeException("Duplicate language adapter key: " + entry.getKey() + "! (" + entry.getValue() + ", " + adapterMap.get(entry.getKey()).getClass().getName() + ")");

                LanguageAdapter adapter;
                try {
                    adapter = (LanguageAdapter) Class.forName(entry.getValue(), true, Util.launcher.getTargetClassLoader())
                            .getConstructor()
                            .newInstance();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to instantiate language adapter: " + entry.getKey(), e);
                }

                adapterMap.put(entry.getKey(), adapter);
            }
        }
    }
}
