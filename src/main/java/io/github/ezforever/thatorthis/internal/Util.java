package io.github.ezforever.thatorthis.internal;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.discovery.ModCandidate;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

final class Util {
    public static final FabricLoaderImpl loader;
    public static final FabricLauncher launcher;

    public static final Method addModMethod;
    public static final Field modsField;
    public static final Field adapterMapField;

    static {
        loader = (FabricLoaderImpl) FabricLoader.getInstance();
        launcher = FabricLauncherBase.getLauncher();

        try {
            addModMethod = FabricLoaderImpl.class.getDeclaredMethod("addMod", ModCandidate.class);
            addModMethod.setAccessible(true);

            modsField = FabricLoaderImpl.class.getDeclaredField("mods");
            modsField.setAccessible(true);

            adapterMapField = FabricLoaderImpl.class.getDeclaredField("adapterMap");
            adapterMapField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            FabricGuiEntry.displayError("Failed to get reference to Fabric Loader internals", e, true);
            throw new IllegalStateException(); // Never reached
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public static Path getModsDir() {
        return loader.getGameDir().resolve("mods");
    }
}
