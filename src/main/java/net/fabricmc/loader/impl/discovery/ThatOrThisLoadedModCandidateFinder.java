package net.fabricmc.loader.impl.discovery;

import net.fabricmc.loader.api.metadata.ModOrigin;
import net.fabricmc.loader.impl.ModContainerImpl;

import java.util.List;

public class ThatOrThisLoadedModCandidateFinder implements ModCandidateFinder {
    private final List<ModContainerImpl> containers;

    public ThatOrThisLoadedModCandidateFinder(List<ModContainerImpl> containers) {
        this.containers = containers;
    }

    @Override
    public void findCandidates(ModCandidateConsumer out) {
        containers.forEach((ModContainerImpl container) -> {
            // Built-in mods are added in ModResolver#resolve
            if (container.getOrigin().getKind().equals(ModOrigin.Kind.PATH))
                out.accept(container.getOrigin().getPaths(), false);
        });
    }
}
