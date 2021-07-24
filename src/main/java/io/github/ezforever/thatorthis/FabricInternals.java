package io.github.ezforever.thatorthis;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.discovery.*;
import net.fabricmc.loader.gui.FabricGuiEntry;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.fabricmc.loader.metadata.LoaderModMetadata;
import net.fabricmc.loader.metadata.ModMetadataParser;
import net.fabricmc.loader.util.FileSystemUtil;
import net.fabricmc.loader.util.UrlUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FabricInternals {
    private static class HookedModContainerList implements InvocationHandler {
        final List<ModContainer> list;
        final Map<String, Set<String>> modDirs;

        HookedModContainerList(List<ModContainer> list, Map<String, Set<String>> modDirs) {
            this.list = Collections.synchronizedList(list);
            this.modDirs = modDirs;
        }

        // --- Implements InvocationHandler

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("iterator"))
                onHook(this);
            return method.invoke(list, args);
        }
    }

    private static class HookedDirectoryModCandidateFinder implements InvocationHandler {
        private final DirectoryModCandidateFinder finder;
        private final Function<LoaderModMetadata, Boolean> callback;

        HookedDirectoryModCandidateFinder(DirectoryModCandidateFinder finder, Function<LoaderModMetadata, Boolean> callback) {
            this.finder = finder;
            this.callback = callback;
        }

        // --- Implements InvocationHandler

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("findCandidates")) {
                return method.invoke(finder, args[0], (BiConsumer<URL, Boolean>)(URL url, Boolean requiresRemap) -> {
                    try {
                        // XXX: This is a copy of ModResolver routines, not a good idea
                        // NOTE: Assumed .jar mods as implied in findCandidates()
                        Path path = UrlUtil.asPath(url).normalize();
                        Path modJson = FileSystemUtil.getJarFileSystem(path, false).get().getPath("fabric.mod.json");
                        LoaderModMetadata info = ModMetadataParser.parseMetadata(LOGGER, modJson);
                        if(!callback.apply(info))
                            return;
                    } catch (Throwable ignored) {
                        // Ignored; any exception that might happen here will be processed later in ModResolver.resolve(), so nothing to do
                    }

                    BiConsumer<URL, Boolean> urlProposer = cast(args[1]);
                    urlProposer.accept(url, requiresRemap);
                });
            } else {
                return method.invoke(finder, args);
            }
        }
    }

    // ---

    private static final Logger LOGGER = LogManager.getLogger("thatorthis/internals");

    private static final FabricLoader loader;
    private static final Method addModMethod;
    private static final Field modsField;
    private static final Field adapterMapField;

    static {
        try {
            // FabricLoader exposes getModsDir() while api.FabricLoader does not
            loader = (FabricLoader) net.fabricmc.loader.api.FabricLoader.getInstance();

            addModMethod = loader.getClass().getDeclaredMethod("addMod", ModCandidate.class);
            addModMethod.setAccessible(true);

            modsField = loader.getClass().getDeclaredField("mods");
            modsField.setAccessible(true);

            adapterMapField = loader.getClass().getDeclaredField("adapterMap");
            adapterMapField.setAccessible(true);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException("Failed to get reference to fabric-loader internals.", e);
        }
    }


    public static void walkDirectory(String modDir, Consumer<LoaderModMetadata> callback) {
        Path dir = loader.getModsDir().resolve(modDir);
        // DirectoryModCandidateFinder creates missing directories, which is not intended
        if(Files.exists(dir) && Files.isDirectory(dir)) {
            ModCandidateFinder finder = new DirectoryModCandidateFinder(dir, loader.isDevelopmentEnvironment());
            finder = (ModCandidateFinder) Proxy.newProxyInstance(
                    loader.getClass().getClassLoader(),
                    finder.getClass().getInterfaces(),
                    new HookedDirectoryModCandidateFinder(
                            (DirectoryModCandidateFinder) finder,
                            (LoaderModMetadata info) -> {
                                callback.accept(info);
                                return false;
                            })
            );
            finder.findCandidates(loader, (URL url, Boolean requiresRemap) -> {});
        } else {
            LOGGER.warn("Skipping missing/invalid directory: " + modDir);
        }
    }

    // This has been used a lot
    @SuppressWarnings("unchecked")
    static <T> T cast(Object obj) {
        return (T)obj;
    }

    static void hook(Map<String, Set<String>> modDirs) {
        try {
            List<ModContainer> original = cast(modsField.get(loader));
            List<ModContainer> proxied = cast(Proxy.newProxyInstance(
                    loader.getClass().getClassLoader(),
                    original.getClass().getInterfaces(),
                    new HookedModContainerList(original, modDirs)
            ));
            modsField.set(loader, proxied);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Mods list hook failed", e);
        }
    }

    private static void unHook(HookedModContainerList hook) {
        try {
            modsField.set(loader, hook.list);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Mods list unhook failed", e);
        }
    }

    private static void onHook(HookedModContainerList hook) {
        unHook(hook);
        injectMods(hook.modDirs,
                hook.list.stream()
                    .map((ModContainer container) -> container.getInfo().getId())
                    .collect(Collectors.toSet())
        );
    }

    private static void injectMods(Map<String, Set<String>> modDirs, Set<String> loadedModIds) {
        ModResolver resolver = new ModResolver();

        // Keep original mods' directories only for resolving dependencies
        resolver.addCandidateFinder(new ClasspathModCandidateFinder());
        resolver.addCandidateFinder(new DirectoryModCandidateFinder(
                loader.getModsDir(),
                loader.isDevelopmentEnvironment()
        ));

        modDirs.forEach((String modDir, Set<String> blacklist) -> {
            Path dir = loader.getModsDir().resolve(modDir);
            // DirectoryModCandidateFinder creates missing directories, which is not intended
            if(Files.exists(dir) && Files.isDirectory(dir)) {
                ModCandidateFinder finder = new DirectoryModCandidateFinder(dir, loader.isDevelopmentEnvironment());
                if(blacklist != null) {
                    finder = (ModCandidateFinder) Proxy.newProxyInstance(
                            loader.getClass().getClassLoader(),
                            finder.getClass().getInterfaces(),
                            new HookedDirectoryModCandidateFinder(
                                    (DirectoryModCandidateFinder) finder,
                                    (LoaderModMetadata info) -> {
                                        if(blacklist.contains(info.getId())) {
                                            LOGGER.debug("Skipping mod {} as per user request", info.getId());
                                            return false;
                                        } else {
                                            return true;
                                        }
                                    }
                            )
                    );
                }
                resolver.addCandidateFinder(finder);
            } else {
                LOGGER.warn("Skipping missing/invalid directory: " + modDir);
            }
        });

        Collection<ModCandidate> candidates;
        try {
            candidates = resolver.resolve(loader)
                    .entrySet().stream()
                    .filter((Map.Entry<String, ModCandidate> candidate) -> !loadedModIds.contains(candidate.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            if(loader.isDevelopmentEnvironment())
                candidates = RuntimeModRemapper.remap(candidates, ModResolver.getInMemoryFs());
        } catch(ModResolutionException e) {
            FabricGuiEntry.displayCriticalError(e, true);
            return; // Never reached
        }

        String modText = switch (candidates.size()) {
            case 0 -> "Loading {} additional mods";
            case 1 -> "Loading {} additional mod:\n{}";
            default -> "Loading {} additional mods:\n{}";
        };

        LOGGER.info("[ThatOrThis] " + modText, candidates.size(), candidates.stream()
                .map((ModCandidate candidate) -> String.format("\t- %s@%s", candidate.getInfo().getId(), candidate.getInfo().getVersion().getFriendlyString()))
                .collect(Collectors.joining("\n")));

        List<ModCandidate> candidatesToFix = new ArrayList<>();
        for(ModCandidate candidate : candidates) {
            // Check for LanguageAdapter incompatibility
            if(!candidate.getInfo().getLanguageAdapterDefinitions().isEmpty()) {
                LOGGER.warn("LanguageAdapter found in mod {}! Will try to fix later", candidate.getInfo().getId());
                candidatesToFix.add(candidate);
            }

            try {
                addModMethod.invoke(loader, candidate);
            } catch(IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to inject mod into fabric-loader", e);
            }
            FabricLauncherBase.getLauncher().propose(candidate.getOriginUrl());
        }

        Map<String, LanguageAdapter> adapterMap;
        try {
            adapterMap = cast(adapterMapField.get(loader));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to fix LanguageAdapter mods", e);
        }

        // NOTE: Fix here after injecting all mods, so mods that "ping" other mods (e.g. GrossFabricHacks) can have a good time
        for(ModCandidate candidate : candidatesToFix) {
            for(Map.Entry<String, String> entry : candidate.getInfo().getLanguageAdapterDefinitions().entrySet()) {
                if(adapterMap.containsKey(entry.getKey()))
                    throw new RuntimeException("Duplicate language adapter key: " + entry.getKey() + "! (" + entry.getValue() + ", " + adapterMap.get(entry.getKey()).getClass().getName() + ")");

                try {
                    adapterMap.put(entry.getKey(), (LanguageAdapter)Class.forName(entry.getValue(), true, FabricLauncherBase.getLauncher().getTargetClassLoader()).getDeclaredConstructor().newInstance());
                } catch(Exception e) {
                    throw new RuntimeException("Failed to instantiate language adapter: " + entry.getKey(), e);
                }
            }
        }
    }
}
