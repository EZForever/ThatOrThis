package io.github.ezforever.thatorthis;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;

@SuppressWarnings("unused") // Used in fabric.mod.json
public class PreloadAdapter implements LanguageAdapter {
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    static {
        Main.run();
    }
}
