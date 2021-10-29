package net.fabricmc.loader.impl.discovery;

import java.nio.file.Path;
import java.util.function.Predicate;

public class ThatOrThisDirectoryModCandidateFinder extends DirectoryModCandidateFinder {
    private static final ModDiscoverer DUMMY_DISCOVERER = new ModDiscoverer();

    private final Predicate<ModCandidate> predicate;

    public ThatOrThisDirectoryModCandidateFinder(Path path, boolean requiresRemap, Predicate<ModCandidate> predicate) {
        super(path, requiresRemap);
        this.predicate = predicate;
    }

    @Override
    public void findCandidates(ModCandidateConsumer out) {
        super.findCandidates((Path path, boolean requiresRemap) -> {
            ModCandidate candidate = DUMMY_DISCOVERER.new ModScanTask(path, requiresRemap).compute();
            if(predicate.test(candidate))
                out.accept(path, requiresRemap);
        });
    }
}
