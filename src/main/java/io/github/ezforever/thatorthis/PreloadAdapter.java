package io.github.ezforever.thatorthis;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.impl.gui.FabricGuiEntry;

@SuppressWarnings("unused") // Used in fabric.mod.json
public class PreloadAdapter implements LanguageAdapter {
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    static {
        try {
            new PreloadInitializer().onInitializing();
        } catch(Exception e) {
            FabricGuiEntry.displayError("Exception occurred in pre-initialization", e, true);
        }
    }
}
