package io.github.ezforever.thatorthis.internal;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class Bootstrap {
    private static final Constructor<?> modInjectorConstructor;
    private static final Method modInjectorInstallMethod;

    private static final Method directoryWalkerWalkMethod;

    static {
        ClassLoader cl = FabricLoader.class.getClassLoader();
        try {
            Class<?> modInjectorClass = cl.loadClass("io.github.ezforever.thatorthis.internal.ModInjector");
            modInjectorConstructor = modInjectorClass.getConstructor(Map.class);
            modInjectorInstallMethod = modInjectorClass.getMethod("install");

            Class<?> directoryWalkerClass = cl.loadClass("io.github.ezforever.thatorthis.internal.DirectoryWalker");
            directoryWalkerWalkMethod = directoryWalkerClass.getMethod("walk", String.class, Consumer.class);
        } catch (ReflectiveOperationException e) {
            FabricGuiEntry.displayError("Failed to bootstrap classes", e, true);
            throw new IllegalStateException(); // Never reached
        }
    }

    public static void installInjector(Map<String, Set<String>> modDirs) {
        try {
            modInjectorInstallMethod.invoke(modInjectorConstructor.newInstance(modDirs));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Bootstrap class invocation failed", e);
        }
    }

    public static void walkDirectory(String modDir, Consumer<ModMetadata> callback) {
        try {
            directoryWalkerWalkMethod.invoke(null, modDir, callback);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Bootstrap class invocation failed", e);
        }
    }
}
