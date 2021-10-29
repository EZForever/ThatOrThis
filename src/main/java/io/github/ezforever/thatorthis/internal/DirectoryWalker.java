package io.github.ezforever.thatorthis.internal;

import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.discovery.DirectoryModCandidateFinder;
import net.fabricmc.loader.impl.discovery.ModCandidate;
import net.fabricmc.loader.impl.discovery.ThatOrThisDirectoryModCandidateFinder;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class DirectoryWalker {
    @SuppressWarnings("unused") // Used in Bootstrap
    public static void walk(String modDir, Consumer<ModMetadata> callback) {
        Path path = Util.getModsDir().resolve(modDir);
        DirectoryModCandidateFinder finder = new ThatOrThisDirectoryModCandidateFinder(path, false, (ModCandidate candidate) -> {
            callback.accept(candidate.getMetadata());
            return false;
        });
        finder.findCandidates((x, y) -> {});
    }
}
