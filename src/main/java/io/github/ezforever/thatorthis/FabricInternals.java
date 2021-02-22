package io.github.ezforever.thatorthis;

import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.discovery.ClasspathModCandidateFinder;
import net.fabricmc.loader.discovery.DirectoryModCandidateFinder;
import net.fabricmc.loader.discovery.ModCandidate;
import net.fabricmc.loader.discovery.ModResolutionException;
import net.fabricmc.loader.discovery.ModResolver;
import net.fabricmc.loader.discovery.RuntimeModRemapper;
import net.fabricmc.loader.gui.FabricGuiEntry;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class FabricInternals {
    private static final Logger LOGGER = LogManager.getFormatterLogger("thatorthis/internals");

    private static final FabricLoader loader;
    private static final Method addModMethod;
    private static final Field modsField;

    static {
        try {
            // FabricLoader exposes getModsDir() while api.FabricLoader does not
            loader = (FabricLoader) net.fabricmc.loader.api.FabricLoader.getInstance();

            addModMethod = loader.getClass().getDeclaredMethod("addMod", ModCandidate.class);
            addModMethod.setAccessible(true);

            modsField = loader.getClass().getDeclaredField("mods");
            modsField.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            LOGGER.error("Failed to get reference to fabric-loader internals.", e);
            throw new IllegalStateException(e);
        }
    }

    static void hook(Set<String> modDirs) {
        try {
            List<ModContainer> original = (List<ModContainer>) modsField.get(loader);
            List<ModContainer> proxied = (List<ModContainer>) Proxy.newProxyInstance(
                    loader.getClass().getClassLoader(),
                    original.getClass().getInterfaces(),
                    new HookedModContainerList(original, modDirs)
            );
            modsField.set(loader, proxied);
        } catch (IllegalAccessException e) {
            LOGGER.error("Mods list hook failed", e);
            throw new IllegalStateException(e);
        }
    }

    static void unHook(HookedModContainerList hook) {
        try {
            modsField.set(loader, hook.list);
        } catch (IllegalAccessException e) {
            LOGGER.error("Mods list unhook failed", e);
            throw new IllegalStateException(e);
        }
    }

    static void onHook(HookedModContainerList hook) {
        unHook(hook);
        injectMods(hook.modDirs,
                hook.list.stream()
                    .map((ModContainer container) -> container.getInfo().getId())
                    .collect(Collectors.toSet())
        );
    }

    private static void injectMods(Set<String> modDirs, Set<String> loadedModIds) {
        try {
            ModResolver resolver = new ModResolver();

            // Keep original mods' directories only for resolving dependencies
            resolver.addCandidateFinder(new ClasspathModCandidateFinder());
            resolver.addCandidateFinder(new DirectoryModCandidateFinder(
                    loader.getModsDir(),
                    loader.isDevelopmentEnvironment()
            ));

            modDirs.forEach((String modDir) -> {
                Path dir = loader.getModsDir().resolve(modDir);
                if(Files.exists(dir) && Files.isDirectory(dir)) {
                    // Otherwise DirectoryModCandidateFinder creates it, which is not intended
                    resolver.addCandidateFinder(new DirectoryModCandidateFinder(
                            loader.getModsDir().resolve(dir),
                            loader.isDevelopmentEnvironment()
                    ));
                } else {
                    LOGGER.warn("Skipping missing/invalid directory: " + modDir);
                }
            });

            Map<String, ModCandidate> candidateMap = resolver.resolve(loader);
            Collection<ModCandidate> candidates = candidateMap.keySet().stream()
                    .filter((String modid) -> !loadedModIds.contains(modid))
                    .map(candidateMap::get)
                    .collect(Collectors.toList());

            String modText;
            switch (candidates.size()) {
                case 0:
                    modText = "Loading %d additional mods";
                    break;
                case 1:
                    modText = "Loading %d additional mod: %s";
                    break;
                default:
                    modText = "Loading %d additional mods: %s";
                    break;
            }

            LOGGER.info("[ThatOrThis] " + modText, candidates.size(), candidates.stream()
                    .map((ModCandidate candidate) -> String.format("%s@%s", candidate.getInfo().getId(), candidate.getInfo().getVersion().getFriendlyString()))
                    .collect(Collectors.joining(", ")));

            if (loader.isDevelopmentEnvironment())
                candidates = RuntimeModRemapper.remap(candidates, ModResolver.getInMemoryFs());
            for(ModCandidate candidate : candidates) {
                addModMethod.invoke(loader, candidate);
                FabricLauncherBase.getLauncher().propose(candidate.getOriginUrl());
            }
        } catch (ModResolutionException e) {
            FabricGuiEntry.displayCriticalError(e, true);
            //LOGGER.error("Unable to resolve additional mods", e);
            //throw new IllegalStateException(e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Failed to inject mod into fabric-loader", e);
            throw new IllegalStateException(e);
        }
    }
}
