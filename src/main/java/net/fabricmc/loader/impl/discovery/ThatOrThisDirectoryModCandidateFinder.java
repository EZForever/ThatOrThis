package net.fabricmc.loader.impl.discovery;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.VersionOverrides;

import java.nio.file.Path;
import java.util.function.Predicate;

public class ThatOrThisDirectoryModCandidateFinder extends DirectoryModCandidateFinder {
    private final ModDiscoverer discoverer;

    private final Predicate<ModCandidate> predicate;

    public ThatOrThisDirectoryModCandidateFinder(Path path, boolean requiresRemap, Predicate<ModCandidate> predicate) {
        super(path, requiresRemap);
        this.predicate = predicate;
        this.discoverer = new ModDiscoverer(new VersionOverrides(), new DependencyOverrides(FabricLoader.getInstance().getConfigDir()));
    }

    @Override
    public void findCandidates(ModCandidateConsumer out) {
        super.findCandidates((final var path, final var requiresRemap) -> {
            ModCandidate candidate = discoverer.new ModScanTask(path, requiresRemap).compute();
            if (predicate.test(candidate))
                out.accept(path, requiresRemap);
        });
    }
}
