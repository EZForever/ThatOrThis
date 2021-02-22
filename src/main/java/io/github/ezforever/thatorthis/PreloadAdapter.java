package io.github.ezforever.thatorthis;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;

public class PreloadAdapter implements LanguageAdapter {
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new UnsupportedOperationException(String.format(
                "%s@%s trying to load ThatOrThis as a Language Adapter",
                mod.getMetadata().getId(),
                mod.getMetadata().getVersion().getFriendlyString()
        ));
    }

    static {
        Main.run();
    }
}
